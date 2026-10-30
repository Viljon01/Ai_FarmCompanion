# Farm Companion - Android LiteRT / TFLite Integration Guide

This guide describes how to integrate the real trained leaf disease classifier into your native Kotlin + Jetpack Compose Android app.

---

## 1. Asset Placement

Copy the exported model and labels into your Android app's `assets/` folder:

```text
FarmCompanionApp/
└── app/
    └── src/
        └── main/
            └── assets/
                ├── leaf_disease_model.tflite   <-- from farm_companion_ml/models/
                └── labels.txt                  <-- from farm_companion_ml/models/
```

* Ensure `leaf_disease_model.tflite` (8.77 MB) or `leaf_disease_model_quant.tflite` (2.48 MB) is placed inside `app/src/main/assets/`.
* `labels.txt` must contain:
  ```text
  Healthy
  Powdery
  Rust
  ```

---

## 2. Dependencies (`app/build.gradle.kts`)

Add the LiteRT / TensorFlow Lite dependency to your `app/build.gradle.kts`:

```kotlin
dependencies {
    // Official Standalone LiteRT / TensorFlow Lite runtime
    implementation("org.tensorflow:tensorflow-lite:2.16.1")

    // Kotlin Coroutines (if not already added)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Jetpack Compose & Material 3
    implementation("androidx.compose.material3:material3")
}
```

### Prevent Asset Compression
TensorFlow Lite uses memory-mapped file access (`FileChannel.map`), which requires uncompressed `.tflite` files. Add this to your `android { ... }` block in `app/build.gradle.kts`:

```kotlin
android {
    ...
    androidResources {
        noCompress += "tflite"
    }
}
```

*(If using Groovy `build.gradle`, use `aaptOptions { noCompress 'tflite' }`)*.

---

## 3. Integration Source Files

The integration files are ready in `farm_companion_ml/android/`:

1. **`PlantDiseaseClassifier.kt`**:
   - Manages memory-mapped model loading and native interpreter lifecycle (`AutoCloseable`).
   - Normalization: The model **already contains an internal Rescaling layer**, so pixels are passed as raw floats in `[0.0f, 255.0f]`.
   - Thread safety: Executes inference asynchronously on `Dispatchers.Default`.
   - Safety thresholds: Identifies predictions below `0.70 (70%)` as `isUncertain = true`.

2. **`CropHealthScreen.kt`**:
   - Complete Jetpack Compose screening screen.
   - Manages classifier disposal using `DisposableEffect { onDispose { classifier.close() } }`.
   - Visualizes confidence progress bars and uncertainty warnings.

---

## 4. Minimal Code Patch for Existing Crop Health Feature

If your application already has a ViewModel or repository for crop health, wire `PlantDiseaseClassifier` directly into your existing ViewModel:

```kotlin
class CropHealthViewModel(application: Application) : AndroidViewModel(application) {
    private val classifier = PlantDiseaseClassifier(
        context = application.applicationContext,
        modelFilename = "leaf_disease_model.tflite",
        confidenceThreshold = 0.70f
    )

    private val _prediction = MutableStateFlow<DiseasePrediction?>(null)
    val prediction: StateFlow<DiseasePrediction?> = _prediction.asStateFlow()

    fun analyzeLeafImage(bitmap: Bitmap) {
        viewModelScope.launch {
            classifier.classify(bitmap).onSuccess { result ->
                _prediction.value = result
            }.onFailure { error ->
                Log.e("CropHealth", "Classification failed", error)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        classifier.close()
    }
}
```
