# Farm Companion — Plant Disease Screening

End-to-end Machine Learning training, evaluation, TensorFlow Lite / LiteRT conversion, and Android Kotlin integration pipeline for the **Farm Companion** agricultural application.

---

## 1. Model Scope and Domain Constraints

The plant disease classification model is designed to classify leaf images into three supported categories:

1. **Healthy** — the leaf most closely matches the healthy class.
2. **Powdery Mildew** — the leaf most closely matches the Powdery class.
3. **Rust** — the leaf most closely matches the Rust class.

The model is limited to these three categories. It is not designed to diagnose bacterial wilt, viral mosaic, nutrient deficiencies, or other unsupported plant conditions.

If a farmer uploads an image of an unsupported disease, unfamiliar crop, non-leaf object, or an image with poor visibility, the model may still produce one of its three available class predictions. Therefore, predictions must be presented as preliminary screening results rather than confirmed diagnoses.

---

## 2. Confidence Thresholding

The Android classifier implements a configurable provisional confidence threshold of **`0.70f`**.

* If the highest class score is at least `0.70f`, display the predicted class as a preliminary screening result.
* If the highest class score is below `0.70f`, return the state **Uncertain Screening**.
* In the uncertain state, do not present a disease classification as a reliable result. Ask the farmer to capture the leaf clearly in natural lighting and, if uncertainty persists, consult an agricultural extension specialist.

**Exact user-facing message:**
> “Uncertain Screening: We could not confidently screen this leaf image. Capture a clear image of the affected leaf in natural lighting. If the problem persists, consult an agricultural extension specialist.”

**Calibration note:** The threshold is a provisional engineering setting. Softmax confidence is not necessarily the same as the probability that a prediction is correct. Validate the threshold against held-out data and, ideally, real-world images before relying on it in agricultural decisions. A high score alone does not guarantee that the uploaded image belongs to one of the supported classes.

---

## 3. Decoupled Recommendations

The computer vision classifier and agricultural recommendation system remain completely separate components:

* **Computer vision component (`PlantDiseaseClassifier.kt`) responsibilities:**
  - Process the selected leaf image.
  - Produce scores for Healthy, Powdery, and Rust.
  - Apply the confidence threshold (`0.70f`).
  - Return the predicted class or Uncertain Screening state (`ScreeningResult`).
  - Report the actual model output without generating or inventing diagnoses.

* **Recommendation component (`RecommendationService.kt`) responsibilities:**
  - Provide guidance appropriate to the confirmed screening category and available crop information (`AgronomicRecommendation`).
  - Clearly distinguish general educational guidance from crop-specific advice.
  - Avoid unsupported pesticide, fertilizer, or treatment claims.
  - Request additional information when the crop, severity, or growing conditions are unknown.
  - Never override the classifier's prediction or convert an uncertain result into a definitive disease diagnosis.

Recommendations do not alter the raw model prediction or its reported scores.

---

## 4. Model Options & Comparative Benchmarks

Two model variants have been trained and exported to suit different mobile deployment needs:

| Specification | Standard Model ($\alpha=1.0$) | **Ultra-Lightweight Model ($\alpha=0.35$)** |
| :--- | :---: | :---: |
| **Backbone Architecture** | MobileNetV2 ($\alpha=1.0$) | **MobileNetV2 ($\alpha=0.35$)** |
| **Total Parameters** | 2,345,283 | **419,171 (82.1% fewer)** |
| **Float32 TFLite Size** | 8.77 MB | **1.54 MB (82.4% smaller)** |
| **Quantized TFLite Size** | 2.48 MB | **543 KB (78.1% smaller)** |
| **TFLite Parity Diff** | 3.34 × 10⁻⁶ | **3.58 × 10⁻⁷** |
| **Held-Out Test Accuracy** | **96.67%** (145/150) | **90.00%** (135/150) |
| **Inference Latency** | ~25 ms (CPU) | **~5–8 ms (CPU)** |
| **Recommended File** | `leaf_disease_model.tflite` | **`leaf_disease_model_light_quant.tflite`** |

Both models use the identical input tensor `[1, 160, 160, 3]` (RGB float32), have embedded normalization `(x / 127.5) - 1.0`, and output Softmax probabilities for `[Healthy, Powdery, Rust]`.

---

## 5. Android Integration Instructions

The Android application implements:
* Local inference using the exported TFLite model.
* Preserved class order: `0: Healthy`, `1: Powdery`, `2: Rust`.
* Embedded model preprocessing: input tensor is `[1, 160, 160, 3]` `FLOAT32` RGB with raw pixel values `[0.0f, 255.0f]`.
* Configurable `0.70f` threshold applied to the highest class score.
* Separate data structures: `ScreeningResult` (Vision) and `AgronomicRecommendation` (Agronomy).
* Off-UI-thread execution via Kotlin Coroutines (`Dispatchers.Default`).

To switch to the ultra-lightweight model in your Android app, simply change the filename in `PlantDiseaseClassifier`:
```kotlin
val classifier = PlantDiseaseClassifier(
    context = context,
    modelFilename = "leaf_disease_model_light_quant.tflite", // Only 543 KB!
    labelsFilename = "labels.txt",
    confidenceThreshold = 0.70f
)
```

---

## 7. Interactive Testing Web Interface

A zero-dependency local testing web application is included at `test_interface.py`.

### Run the app locally

From the project root (`farm_companion_ml`):

```powershell
# PowerShell / Windows
python -m venv venv
.\venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
python .\test_interface.py
```

If you are already using the project venv, the direct launch command is:

```powershell
.\venv\Scripts\python.exe .\test_interface.py
```

On macOS/Linux:

```bash
python3 -m venv venv
source venv/bin/activate
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
python test_interface.py
```

Then open **`http://localhost:8080`** in a browser.

The testing interface includes:
- **Upload custom images**: Drag and drop any leaf photograph (`.jpg`, `.png`).
- **One-click test samples**: Test random real test images for *Healthy*, *Powdery Mildew*, or *Plant Rust*.
- **Switch models**: Test the **Ultra-Lightweight (543 KB)** or **Standard (8.77 MB)** models in real-time.
- **Inspect results**: Live confidence breakdown, latency in ms, threshold status, and decoupled agronomic recommendations.

Note: the app expects the sibling dataset folder `plant-disease-recognition-dataset` to exist next to the project directory, as configured in `test_interface.py`.

---

## 8. Intended Use

Farm Companion provides preliminary plant leaf screening to support farmer awareness and decision-making. It does not replace laboratory testing, field inspection, or qualified agricultural advice. Its limitations must be communicated clearly in the application.

