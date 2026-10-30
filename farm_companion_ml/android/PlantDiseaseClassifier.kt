package com.farmcompanion.ml

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

/**
 * Status of the computer vision screening output.
 */
enum class ScreeningStatus {
    /** Top confidence >= threshold (provisional default 0.70f). Preliminary screening result. */
    CONFIRMED_PRELIMINARY,

    /** Top confidence < threshold (0.70f). Do not present as reliable diagnosis. */
    UNCERTAIN_SCREENING
}

/**
 * Pure Computer Vision screening result data model.
 *
 * Decoupled from recommendations, treatment advice, or farming actions.
 * Contains only the raw model outputs and calibrated confidence evaluation.
 *
 * @property topClass Canonical predicted class name ("Healthy", "Powdery", "Rust")
 * @property classIndex Canonical class index (0: Healthy, 1: Powdery, 2: Rust)
 * @property topScore Highest predicted softmax score (0.0 .. 1.0)
 * @property classScores Complete probability distribution across all supported classes
 * @property status CONFIRMED_PRELIMINARY or UNCERTAIN_SCREENING
 * @property screeningMessage User-facing preliminary screening summary
 * @property thresholdApplied The confidence threshold used for this evaluation (default 0.70f)
 */
data class ScreeningResult(
    val topClass: String,
    val classIndex: Int,
    val topScore: Float,
    val classScores: Map<String, Float>,
    val status: ScreeningStatus,
    val screeningMessage: String,
    val thresholdApplied: Float
)

/**
 * Android LiteRT / TensorFlow Lite Leaf Disease Classifier for Farm Companion.
 *
 * Pure Computer Vision Component:
 * - Model: leaf_disease_model.tflite (MobileNetV2, 160x160x3 RGB)
 * - Input Tensor: Float32 [1, 160, 160, 3] with pixel values [0.0f, 255.0f]
 * - Preprocessing: Model contains embedded Rescaling layer ((x / 127.5) - 1.0)
 * - Output Tensor: Float32 [1, 3] Softmax probabilities
 * - Canonical Class Order: 0: Healthy, 1: Powdery, 2: Rust
 * - Configurable Threshold: 0.70f
 */
class PlantDiseaseClassifier(
    private val context: Context,
    private val modelFilename: String = "leaf_disease_model.tflite",
    private val labelsFilename: String = "labels.txt",
    var confidenceThreshold: Float = 0.70f
) : AutoCloseable {

    companion object {
        const val INPUT_WIDTH = 160
        const val INPUT_HEIGHT = 160
        const val CHANNELS = 3
        const val BYTES_PER_FLOAT = 4
        const val NUM_CLASSES = 3

        const val UNCERTAIN_USER_MESSAGE =
            "Uncertain Screening: We could not confidently screen this leaf image. " +
            "Capture a clear image of the affected leaf in natural lighting. " +
            "If the problem persists, consult an agricultural extension specialist."
    }

    private var interpreter: Interpreter? = null
    private val labels = mutableListOf<String>()

    init {
        loadModelAndLabels()
    }

    @Synchronized
    private fun loadModelAndLabels() {
        // 1. Load canonical labels (Healthy, Powdery, Rust)
        context.assets.open(labelsFilename).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                lines.forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isNotEmpty()) labels.add(trimmed)
                }
            }
        }

        if (labels.size != NUM_CLASSES) {
            throw IllegalStateException(
                "Invalid labels count: expected $NUM_CLASSES labels in $labelsFilename, found ${labels.size}: $labels"
            )
        }

        // 2. Memory-map TFLite model from assets
        val modelBuffer = loadModelFile(context, modelFilename)
        val options = Interpreter.Options().apply {
            setNumThreads(2)
            setCancellable(true)
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    private fun loadModelFile(context: Context, filename: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }

    /**
     * Executes leaf health screening asynchronously off the main UI thread.
     *
     * @param bitmap Input leaf photograph as an Android Bitmap
     * @return Result containing pure computer vision ScreeningResult
     */
    suspend fun classify(bitmap: Bitmap?): Result<ScreeningResult> = withContext(Dispatchers.Default) {
        if (bitmap == null) {
            return@withContext Result.failure(IllegalArgumentException("Input bitmap is null."))
        }

        val currentInterpreter = interpreter ?: return@withContext Result.failure(
            IllegalStateException("Classifier is closed or interpreter is not initialized.")
        )

        try {
            // 1. Resize bitmap to required input dimensions (160x160)
            val resizedBitmap = if (bitmap.width == INPUT_WIDTH && bitmap.height == INPUT_HEIGHT) {
                bitmap
            } else {
                Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, true)
            }

            // 2. Populate direct ByteBuffer [1, 160, 160, 3] Float32
            val inputBuffer = ByteBuffer.allocateDirect(1 * INPUT_WIDTH * INPUT_HEIGHT * CHANNELS * BYTES_PER_FLOAT)
            inputBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(INPUT_WIDTH * INPUT_HEIGHT)
            resizedBitmap.getPixels(intValues, 0, INPUT_WIDTH, 0, 0, INPUT_WIDTH, INPUT_HEIGHT)

            var pixelIndex = 0
            for (i in 0 until INPUT_WIDTH * INPUT_HEIGHT) {
                val pixel = intValues[pixelIndex++]
                // Extract raw RGB float values in [0.0f, 255.0f]
                // Normalization is embedded internally inside the exported model
                inputBuffer.putFloat(((pixel shr 16) and 0xFF).toFloat())
                inputBuffer.putFloat(((pixel shr 8) and 0xFF).toFloat())
                inputBuffer.putFloat((pixel and 0xFF).toFloat())
            }

            // 3. Prepare output tensor buffer [1, 3] Float32
            val outputBuffer = Array(1) { FloatArray(NUM_CLASSES) }

            // 4. Execute inference
            currentInterpreter.run(inputBuffer, outputBuffer)

            val rawScores = outputBuffer[0]
            val scoresMap = mutableMapOf<String, Float>()
            var maxIndex = 0
            var maxScore = -1.0f

            for (i in rawScores.indices) {
                val className = labels.getOrElse(i) { "Class_$i" }
                val score = rawScores[i]
                scoresMap[className] = score
                if (score > maxScore) {
                    maxScore = score
                    maxIndex = i
                }
            }

            val topClass = labels.getOrElse(maxIndex) { "Unknown" }
            val isAboveThreshold = maxScore >= confidenceThreshold

            val status = if (isAboveThreshold) {
                ScreeningStatus.CONFIRMED_PRELIMINARY
            } else {
                ScreeningStatus.UNCERTAIN_SCREENING
            }

            val userMessage = if (isAboveThreshold) {
                "Preliminary Screening: Leaf symptoms most closely match '$topClass' (${String.format("%.1f", maxScore * 100)}% confidence)."
            } else {
                UNCERTAIN_USER_MESSAGE
            }

            Result.success(
                ScreeningResult(
                    topClass = topClass,
                    classIndex = maxIndex,
                    topScore = maxScore,
                    classScores = scoresMap,
                    status = status,
                    screeningMessage = userMessage,
                    thresholdApplied = confidenceThreshold
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Releases native interpreter resources.
     */
    @Synchronized
    override fun close() {
        interpreter?.close()
        interpreter = null
    }
}
