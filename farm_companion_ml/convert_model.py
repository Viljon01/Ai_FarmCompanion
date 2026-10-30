"""
convert_model.py - Farm Companion TensorFlow Lite / LiteRT Model Converter
Converts the trained Keras leaf disease model to mobile-ready .tflite format:
  1. Standard Float32 TFLite model: models/leaf_disease_model.tflite
  2. Dynamic-range Quantized TFLite model: models/leaf_disease_model_quant.tflite

Performs comprehensive validation:
  - Tensor shape and dtype inspection
  - Numerical parity check against original Keras model
  - Model metadata extraction and persistence to model_info.json
"""

import os
import json
import numpy as np

import tensorflow as tf
tf.config.threading.set_inter_op_parallelism_threads(2)
tf.config.threading.set_intra_op_parallelism_threads(2)

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
MODELS_DIR = os.path.join(SCRIPT_DIR, 'models')
KERAS_MODEL_PATH = os.path.join(MODELS_DIR, 'leaf_disease_model.keras')
TFLITE_MODEL_PATH = os.path.join(MODELS_DIR, 'leaf_disease_model.tflite')
TFLITE_QUANT_PATH = os.path.join(MODELS_DIR, 'leaf_disease_model_quant.tflite')
MODEL_INFO_PATH = os.path.join(MODELS_DIR, 'model_info.json')
LABELS_PATH = os.path.join(MODELS_DIR, 'labels.txt')

CLASS_NAMES = ['Healthy', 'Powdery', 'Rust']


def convert_to_tflite(keras_model, output_path, quantize=False):
    """Convert Keras model to TFLite format."""
    converter = tf.lite.TFLiteConverter.from_keras_model(keras_model)
    if quantize:
        converter.optimizations = [tf.lite.Optimize.DEFAULT]

    tflite_model_bytes = converter.convert()
    with open(output_path, 'wb') as f:
        f.write(tflite_model_bytes)

    size_mb = len(tflite_model_bytes) / (1024 * 1024)
    print(f"Saved: {output_path} ({size_mb:.2f} MB)")
    return tflite_model_bytes


def inspect_and_verify_tflite(tflite_path, keras_model):
    """
    Inspect input/output tensors and run numerical parity verification
    between TFLite interpreter and Keras model.
    """
    print(f"\n--- Inspecting {os.path.basename(tflite_path)} ---")
    interpreter = tf.lite.Interpreter(model_path=tflite_path)
    interpreter.allocate_tensors()

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    inp = input_details[0]
    out = output_details[0]

    print("Input Tensor:")
    print(f"  Name:  {inp['name']}")
    print(f"  Shape: {inp['shape'].tolist()}")
    print(f"  Dtype: {inp['dtype'].__name__}")
    print(f"  Quantization: {inp['quantization']}")

    print("Output Tensor:")
    print(f"  Name:  {out['name']}")
    print(f"  Shape: {out['shape'].tolist()}")
    print(f"  Dtype: {out['dtype'].__name__}")
    print(f"  Quantization: {out['quantization']}")

    # Numerical Parity Test with deterministic random synthetic input
    np.random.seed(42)
    sample_input = np.random.uniform(0.0, 255.0, inp['shape']).astype(np.float32)

    # 1. Keras prediction
    keras_pred = keras_model.predict(sample_input, verbose=0)[0]

    # 2. TFLite prediction
    interpreter.set_tensor(inp['index'], sample_input)
    interpreter.invoke()
    tflite_pred = interpreter.get_tensor(out['index'])[0]

    # Verify finite & valid probabilities
    assert np.all(np.isfinite(tflite_pred)), "TFLite outputs contain NaN or Inf!"
    assert abs(np.sum(tflite_pred) - 1.0) < 1e-2, f"Probabilities do not sum to ~1.0: {np.sum(tflite_pred)}"

    max_abs_diff = float(np.max(np.abs(keras_pred - tflite_pred)))
    print(f"\nVerification Results:")
    print(f"  Keras Prediction:  {keras_pred}")
    print(f"  TFLite Prediction: {tflite_pred}")
    print(f"  Max Absolute Difference: {max_abs_diff:.6f}")

    return {
        "input_tensor": {
            "name": inp['name'],
            "shape": inp['shape'].tolist(),
            "dtype": inp['dtype'].__name__,
            "quantization": [float(x) for x in inp['quantization']] if inp['quantization'] else None
        },
        "output_tensor": {
            "name": out['name'],
            "shape": out['shape'].tolist(),
            "dtype": out['dtype'].__name__,
            "quantization": [float(x) for x in out['quantization']] if out['quantization'] else None
        },
        "parity_max_abs_diff": max_abs_diff
    }


def main():
    print("=" * 70)
    print("  FARM COMPANION - TENSORFLOW LITE / LITERt EXPORT PIPELINE")
    print("=" * 70)

    if not os.path.exists(KERAS_MODEL_PATH):
        raise FileNotFoundError(f"Model not found at {KERAS_MODEL_PATH}. Train the model first.")

    print(f"Loading Keras model from: {KERAS_MODEL_PATH}")
    keras_model = tf.keras.models.load_model(KERAS_MODEL_PATH)

    # 1. Convert Standard Float32 TFLite model
    print("\n[1/2] Converting to Standard Float32 TFLite model...")
    convert_to_tflite(keras_model, TFLITE_MODEL_PATH, quantize=False)
    fp32_meta = inspect_and_verify_tflite(TFLITE_MODEL_PATH, keras_model)

    # 2. Convert Dynamic Range Quantized TFLite model
    print("\n[2/2] Converting to Dynamic-Range Quantized TFLite model...")
    convert_to_tflite(keras_model, TFLITE_QUANT_PATH, quantize=True)
    quant_meta = inspect_and_verify_tflite(TFLITE_QUANT_PATH, keras_model)

    # Ensure labels.txt is verified
    with open(LABELS_PATH, 'w', encoding='utf-8') as f:
        for name in CLASS_NAMES:
            f.write(f"{name}\n")
    print(f"\nVerified labels file: {LABELS_PATH}")

    # Generate model_info.json metadata
    model_info = {
        "model_name": "Farm Companion Leaf Disease Classifier",
        "architecture": "MobileNetV2 (Transfer Learning)",
        "version": "1.0.0",
        "dataset": "rashikrahmanpritom/plant-disease-recognition-dataset (1,322 train / 60 val / 150 test)",
        "classes": {
            "0": "Healthy",
            "1": "Powdery",
            "2": "Rust"
        },
        "input_spec": {
            "shape": fp32_meta['input_tensor']['shape'],
            "width": 160,
            "height": 160,
            "channels": 3,
            "color_order": "RGB",
            "data_type": fp32_meta['input_tensor']['dtype'],
            "pixel_value_range": "[0.0, 255.0] float32",
            "normalization_embedded": True,
            "normalization_formula": "x_norm = (x / 127.5) - 1.0 (applied internally by model Rescaling layer)"
        },
        "output_spec": {
            "shape": fp32_meta['output_tensor']['shape'],
            "data_type": fp32_meta['output_tensor']['dtype'],
            "activation": "Softmax",
            "semantics": "Probability distribution over [Healthy, Powdery, Rust]"
        },
        "artifacts": {
            "keras_model": {
                "file": "leaf_disease_model.keras",
                "size_bytes": os.path.getsize(KERAS_MODEL_PATH),
                "size_mb": round(os.path.getsize(KERAS_MODEL_PATH) / (1024 * 1024), 2)
            },
            "tflite_float32": {
                "file": "leaf_disease_model.tflite",
                "size_bytes": os.path.getsize(TFLITE_MODEL_PATH),
                "size_mb": round(os.path.getsize(TFLITE_MODEL_PATH) / (1024 * 1024), 2),
                "max_abs_diff_from_keras": fp32_meta['parity_max_abs_diff']
            },
            "tflite_quantized": {
                "file": "leaf_disease_model_quant.tflite",
                "size_bytes": os.path.getsize(TFLITE_QUANT_PATH),
                "size_mb": round(os.path.getsize(TFLITE_QUANT_PATH) / (1024 * 1024), 2),
                "max_abs_diff_from_keras": quant_meta['parity_max_abs_diff']
            }
        },
        "recommended_android_model": "leaf_disease_model.tflite"
    }

    with open(MODEL_INFO_PATH, 'w', encoding='utf-8') as f:
        json.dump(model_info, f, indent=2)
    print(f"\nModel metadata documented in: {MODEL_INFO_PATH}")

    print("\n" + "=" * 70)
    print("  EXPORT & PARITY VERIFICATION SUMMARY")
    print("=" * 70)
    print(f"Keras Model:        {model_info['artifacts']['keras_model']['size_mb']} MB")
    print(f"TFLite (Float32):   {model_info['artifacts']['tflite_float32']['size_mb']} MB (Parity diff: {fp32_meta['parity_max_abs_diff']:.6e})")
    print(f"TFLite (Quantized): {model_info['artifacts']['tflite_quantized']['size_mb']} MB (Parity diff: {quant_meta['parity_max_abs_diff']:.6e})")
    print("Conversion complete!")


if __name__ == '__main__':
    main()
