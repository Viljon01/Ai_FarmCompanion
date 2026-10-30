<div align="center">

# 🌾 AI Farm Companion — Intelligent Agriculture & Leaf Health Screening

[![Android](https://img.shields.io/badge/Platform-Android_API_24+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin_2.2-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI_Framework-Jetpack_Compose_Material3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![TensorFlow Lite](https://img.shields.io/badge/On--Device_ML-LiteRT_/_TFLite_543KB-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white)](https://www.tensorflow.org/lite)
[![Gemini AI](https://img.shields.io/badge/GenAI-Google_Gemini-8E75B2?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev/)

*An end-to-end, privacy-focused, offline-first mobile agricultural assistant combining on-device computer vision, generative AI agronomists, regional weather intelligence, and farm management analytics.*

---

[📱 Key Features](#-key-features) • [🔬 ML Model Specs](#-on-device-ml-model-benchmarks) • [🏗️ Architecture](#%EF%B8%8F-architecture--tech-stack) • [🚀 Quick Start](#-quick-start) • [📦 APK Download](#-apk-download)

---

</div>

## 🌟 Executive Overview

**AI Farm Companion** empowers smallholder and commercial farmers with real-time, actionable agronomic intelligence right in the field. Powered by an ultra-lightweight **543 KB MobileNetV2 ($\alpha=0.35$)** quantized TensorFlow Lite model and **Google Gemini AI**, the application screens crop foliage for diseases instantly—even without an active internet connection—and delivers comprehensive preventive guidance, localized weather risk alerts, crop journey logs, and market pricing insights.

---

## 📱 Key Features

| Feature Module | Description & Capabilities |
| :--- | :--- |
| **🍃 On-Device ML Leaf Disease Monitor** | Instant optical screening for **Healthy Leaf**, **Powdery Mildew**, and **Plant Rust**. Displays dataset accuracy scores first before presenting agronomic treatment advice. |
| **🤖 Gemini AI Assistant** | Context-aware multimodal agricultural chat. Answers questions on crop nutrition, pest control, fertilizer application, and soil health. |
| **📈 Interactive Crop Journey** | Timeline log tracking crop growth stages from pre-sowing to harvest. Automatically records daily scan observations and health trends in Room DB. |
| **🌦️ Agri-Weather & Risk Outbreaks** | Micro-climate telemetry (temperature, humidity %, rain probability) correlated with disease precursor warnings (e.g. fungal humidity stress). |
| **💰 Smart Selling & Market Analytics** | Yield output prediction, cost breakdown, and distance-ranked local market mandi price comparisons. |
| **🌐 Regional Multilingual Support** | Full native UI localization in **English**, **Tamil (தமிழ்)**, **Telugu (తెలుగు)**, **Hindi (हिंदी)**, **Malayalam (മലയാളം)**, and **Kannada (ಕನ್ನಡ)**. |

---

## 🔬 On-Device ML Model Benchmarks

The leaf disease classification pipeline uses an ultra-lightweight MobileNetV2 architecture fine-tuned and quantized specifically for low-resource mobile edge execution:

```text
Input Image (Bitmap) ──► Resize [160x160 RGB] ──► Direct Float32 Tensor ──► LiteRT Interpreter (5ms)
                                                                                  │
    ┌─────────────────────────────────────────────────────────────────────────────┘
    ▼
[ Healthy Leaf | Powdery Mildew | Plant Rust ] ──► Confidence Threshold Check (≥ 0.70f)
                                                           │
                                                           ├── Confirmed ──► Display Accuracy % + Agronomic Guidance
                                                           └── Uncertain ──► Prompt for Clear Lighting Photo
```

### 📊 Model Performance Specifications

| Specification | Ultra-Lightweight Model (Embedded) | Standard Model Benchmark |
| :--- | :---: | :---: |
| **Backbone Architecture** | **MobileNetV2 ($\alpha=0.35$)** | MobileNetV2 ($\alpha=1.0$) |
| **Total Parameters** | **419,171 (82.1% fewer)** | 2,345,283 |
| **TFLite File Size** | ⚡ **543 KB (Quantized)** | 8.77 MB |
| **Inference Speed** | 🚀 **~5–8 ms (CPU)** | ~25 ms (CPU) |
| **Test Accuracy** | **90.0%** (135/150 test samples) | 96.67% |
| **Supported Classes** | `0: Healthy`, `1: Powdery`, `2: Rust` | `0: Healthy`, `1: Powdery`, `2: Rust` |
| **Input Shape** | `[1, 160, 160, 3]` Float32 | `[1, 160, 160, 3]` Float32 |

---

## 🏗️ Architecture & Tech Stack

The application follows modern Android Development best practices using a reactive, layered architecture:

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                          UI Layer (Jetpack Compose)                         │
│  [DashboardScreen]  [CropHealthMonitorScreen]  [CropJourneyScreen] [etc.]  │
└────────────────────────────────────┬────────────────────────────────────────┘
                                     │
┌────────────────────────────────────▼────────────────────────────────────────┐
│                        Engine / ViewModel Layer                             │
│                 [CropContextEngine]  [CropHealthService]                    │
└──────────────────┬─────────────────────────────────┬────────────────────────┘
                   │                                 │
┌──────────────────▼──────────────────┐   ┌──────────▼────────────────────────┐
│     Local ML & Data (Offline)       │   │       GenAI & Cloud APIs          │
│ • PlantDiseaseClassifier (LiteRT)   │   │ • Gemini AI Service               │
│ • Room SQLite DB (AppDatabase)      │   │ • OpenMeteo Weather API           │
└─────────────────────────────────────┘   └───────────────────────────────────┘
```

- **UI Framework**: Jetpack Compose with Material 3 design tokens.
- **Language**: Kotlin 2.2 with Coroutines & StateFlow.
- **On-Device Inference**: TensorFlow Lite / LiteRT runtime (`org.tensorflow:tensorflow-lite:2.16.1`).
- **GenAI Integration**: Google Gemini API via `GeminiService`.
- **Database & Persistence**: Room ORM with KSP annotation processing & SQLite.
- **Image Processing**: Coil Compose & Android Graphics Bitmaps.

---

## 🚀 Quick Start & Installation

### Prerequisites
- **Android Studio** Ladybug (2024.2.1+) or newer.
- **JDK**: Java 17 or JDK 21 (bundled with Android Studio JBR).
- **Android SDK**: Compile SDK `36`, Min SDK `24`.

### Building from Source

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Viljon01/Ai_FarmCompanion.git
   cd Ai_FarmCompanion
   ```

2. **Configure API Keys**:
   Copy `.env.example` to `.env` in the root directory and add your Gemini API key:
   ```env
   GEMINI_API_KEY=YOUR_GEMINI_API_KEY_HERE
   ```

3. **Build the Project**:
   ```powershell
   # Windows PowerShell
   .\gradlew.bat assembleDebug
   ```
   *or open directly in Android Studio and click **Run 'app'**.*

---

## 📦 APK Download

The compiled debug APK is pre-built and available in the project root:

- **Path**: [`E:\AIFarmapp\app-debug.apk`](file:///E:/AIFarmapp/app-debug.apk) or [`E:\AIFarmapp\APK\app-debug.apk`](file:///E:/AIFarmapp/APK/app-debug.apk)
- **Direct ADB Command**:
  ```powershell
  adb install -r "E:\AIFarmapp\app-debug.apk"
  ```

---

## 🤝 Model Training & Dataset Pipeline

The ML pipeline code, Jupyter training notebooks, and LiteRT converter scripts reside in the [`farm_companion_ml`](./farm_companion_ml/) folder:

- **Model Assets**: [`farm_companion_ml/models/`](./farm_companion_ml/models/)
- **Interactive Web Tester**: Run `python farm_companion_ml/test_interface.py` to test leaf images via a local web browser interface.

---

## 📄 License & Disclaimer

*Disclaimer: AI Farm Companion provides preliminary agricultural screening and educational decision support. It does not replace laboratory diagnostic assays, official plant pathology inspections, or regional Krishi Vigyan Kendra extension officer guidance.*
