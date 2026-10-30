"""
train_lightweight_model.py - Ultra-Lightweight Plant Disease Classifier
Builds, trains, and exports an ultra-compact MobileNetV2 (alpha=0.35) model:
  - Parameters: ~412,000 (82% reduction from baseline)
  - Float32 TFLite: ~1.6 MB
  - Quantized TFLite: ~500 KB
  - Evaluates on the 150 held-out test images
"""

import os
import json
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

import tensorflow as tf
tf.config.threading.set_inter_op_parallelism_threads(2)
tf.config.threading.set_intra_op_parallelism_threads(2)

from tensorflow.keras import layers, models, applications, callbacks, optimizers
from sklearn.metrics import classification_report, confusion_matrix

tf.keras.utils.set_random_seed(42)

IMG_SIZE = (160, 160)
INPUT_SHAPE = (160, 160, 3)
BATCH_SIZE = 8
NUM_CLASSES = 3
CLASS_NAMES = ['Healthy', 'Powdery', 'Rust']

PHASE1_EPOCHS = 15
PHASE2_EPOCHS = 8
INITIAL_LR = 1e-3
FINE_TUNE_LR = 1e-5

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
DATASET_DIR = os.path.join(PROJECT_ROOT, 'plant-disease-recognition-dataset')
MODELS_DIR = os.path.join(SCRIPT_DIR, 'models')
REPORTS_DIR = os.path.join(SCRIPT_DIR, 'reports')

LIGHT_KERAS_PATH = os.path.join(MODELS_DIR, 'leaf_disease_model_light.keras')
LIGHT_TFLITE_PATH = os.path.join(MODELS_DIR, 'leaf_disease_model_light.tflite')
LIGHT_TFLITE_QUANT_PATH = os.path.join(MODELS_DIR, 'leaf_disease_model_light_quant.tflite')
LIGHT_REPORT_TXT_PATH = os.path.join(REPORTS_DIR, 'lightweight_model_report.txt')
LIGHT_PLOT_PATH = os.path.join(REPORTS_DIR, 'lightweight_training_history.png')


def get_datasets():
    train_dir = os.path.join(DATASET_DIR, 'Train', 'Train')
    val_dir = os.path.join(DATASET_DIR, 'Validation', 'Validation')
    test_dir = os.path.join(DATASET_DIR, 'Test', 'Test')

    train_ds = tf.keras.utils.image_dataset_from_directory(
        train_dir, labels='inferred', label_mode='categorical',
        class_names=CLASS_NAMES, image_size=IMG_SIZE, batch_size=BATCH_SIZE,
        shuffle=True, seed=42
    )
    val_ds = tf.keras.utils.image_dataset_from_directory(
        val_dir, labels='inferred', label_mode='categorical',
        class_names=CLASS_NAMES, image_size=IMG_SIZE, batch_size=BATCH_SIZE,
        shuffle=False
    )
    test_ds = tf.keras.utils.image_dataset_from_directory(
        test_dir, labels='inferred', label_mode='categorical',
        class_names=CLASS_NAMES, image_size=IMG_SIZE, batch_size=BATCH_SIZE,
        shuffle=False
    )

    augmentation = tf.keras.Sequential([
        layers.RandomFlip("horizontal_and_vertical", seed=42),
        layers.RandomRotation(0.15, fill_mode="reflect", seed=42),
        layers.RandomZoom(0.1, fill_mode="reflect", seed=42),
        layers.RandomContrast(0.1, seed=42)
    ])

    train_ds = train_ds.map(
        lambda x, y: (augmentation(x, training=True), y),
        num_parallel_calls=tf.data.AUTOTUNE
    ).prefetch(tf.data.AUTOTUNE)

    val_ds = val_ds.prefetch(tf.data.AUTOTUNE)
    test_ds = test_ds.prefetch(tf.data.AUTOTUNE)
    return train_ds, val_ds, test_ds


def build_lightweight_model():
    inputs = layers.Input(shape=INPUT_SHAPE, name="input_image")
    # Embedded preprocessing: [0, 255] -> [-1, 1]
    rescaled = layers.Rescaling(scale=1.0 / 127.5, offset=-1.0, name="rescaling_mobilenetv2")(inputs)

    # Ultra-compact MobileNetV2 (alpha = 0.35)
    base_model = applications.MobileNetV2(
        input_shape=INPUT_SHAPE,
        alpha=0.35,
        include_top=False,
        weights='imagenet'
    )
    base_model.trainable = False

    x = base_model(rescaled, training=False)
    x = layers.GlobalAveragePooling2D(name="global_avg_pool")(x)
    x = layers.BatchNormalization(name="batch_norm")(x)
    x = layers.Dropout(0.2, seed=42, name="dropout")(x)
    outputs = layers.Dense(NUM_CLASSES, activation="softmax", name="disease_probabilities")(x)

    model = models.Model(inputs=inputs, outputs=outputs, name="farm_companion_light_mobilenetv2")
    return model, base_model


def main():
    print("=" * 70)
    print("  BUILDING ULTRA-LIGHTWEIGHT MODEL (MobileNetV2 alpha=0.35)")
    print("=" * 70)

    train_ds, val_ds, test_ds = get_datasets()
    model, base_model = build_lightweight_model()
    model.summary(print_fn=print)

    # Compile Phase 1
    model.compile(
        optimizer=optimizers.Adam(learning_rate=INITIAL_LR),
        loss=tf.keras.losses.CategoricalCrossentropy(label_smoothing=0.05),
        metrics=['accuracy']
    )

    checkpoint_cb = callbacks.ModelCheckpoint(
        filepath=LIGHT_KERAS_PATH,
        monitor='val_loss',
        save_best_only=True,
        verbose=1
    )
    early_stopping_cb = callbacks.EarlyStopping(
        monitor='val_loss', patience=4, restore_best_weights=True, verbose=1
    )

    print("\n>>> Phase 1: Training Classification Head (Backbone Frozen)")
    h1 = model.fit(
        train_ds, validation_data=val_ds,
        epochs=PHASE1_EPOCHS,
        callbacks=[checkpoint_cb, early_stopping_cb]
    )

    # Fine-tuning
    print("\n>>> Phase 2: Gentle Fine-Tuning Top 20 Layers")
    base_model.trainable = True
    fine_tune_at = len(base_model.layers) - 20
    for layer in base_model.layers[:fine_tune_at]:
        layer.trainable = False

    model.compile(
        optimizer=optimizers.Adam(learning_rate=FINE_TUNE_LR),
        loss=tf.keras.losses.CategoricalCrossentropy(label_smoothing=0.05),
        metrics=['accuracy']
    )

    h2 = model.fit(
        train_ds, validation_data=val_ds,
        epochs=PHASE2_EPOCHS,
        callbacks=[checkpoint_cb, early_stopping_cb]
    )

    # Evaluate on held-out test set
    print("\n" + "=" * 70)
    print("  EVALUATING ULTRA-LIGHTWEIGHT MODEL ON HELD-OUT TEST SET (150 Images)")
    print("=" * 70)
    best_model = tf.keras.models.load_model(LIGHT_KERAS_PATH)
    eval_res = best_model.evaluate(test_ds, verbose=0)
    test_loss, test_acc = eval_res[0], eval_res[1]

    y_true_all = []
    y_pred_all = []
    for imgs, lbls in test_ds:
        preds = best_model.predict(imgs, verbose=0)
        y_true_all.extend(np.argmax(lbls.numpy(), axis=1))
        y_pred_all.extend(np.argmax(preds, axis=1))

    cm = confusion_matrix(y_true_all, y_pred_all)
    clf_rep = classification_report(y_true_all, y_pred_all, target_names=CLASS_NAMES, digits=4)

    # Convert to TFLite Float32
    print("\nExporting Float32 TFLite model...")
    conv_fp32 = tf.lite.TFLiteConverter.from_keras_model(best_model)
    tflite_fp32_bytes = conv_fp32.convert()
    with open(LIGHT_TFLITE_PATH, 'wb') as f:
        f.write(tflite_fp32_bytes)
    fp32_size_mb = len(tflite_fp32_bytes) / (1024 * 1024)

    # Convert to TFLite Dynamic Range Quantized
    print("Exporting Dynamic-Range Quantized TFLite model...")
    conv_quant = tf.lite.TFLiteConverter.from_keras_model(best_model)
    conv_quant.optimizations = [tf.lite.Optimize.DEFAULT]
    tflite_quant_bytes = conv_quant.convert()
    with open(LIGHT_TFLITE_QUANT_PATH, 'wb') as f:
        f.write(tflite_quant_bytes)
    quant_size_mb = len(tflite_quant_bytes) / (1024 * 1024)

    # Verify parity
    interp = tf.lite.Interpreter(model_content=tflite_fp32_bytes)
    interp.allocate_tensors()
    inp_idx = interp.get_input_details()[0]['index']
    out_idx = interp.get_output_details()[0]['index']
    dummy = np.random.uniform(0, 255, (1, 160, 160, 3)).astype(np.float32)
    kp = best_model.predict(dummy, verbose=0)[0]
    interp.set_tensor(inp_idx, dummy)
    interp.invoke()
    tp = interp.get_tensor(out_idx)[0]
    parity_diff = float(np.max(np.abs(kp - tp)))

    summary_text = (
        "========================================================================\n"
        "     ULTRA-LIGHTWEIGHT MODEL (MobileNetV2 alpha=0.35) REPORT             \n"
        "========================================================================\n\n"
        f"Keras Model File:     {LIGHT_KERAS_PATH} ({os.path.getsize(LIGHT_KERAS_PATH)/(1024*1024):.2f} MB)\n"
        f"TFLite Float32:       {LIGHT_TFLITE_PATH} ({fp32_size_mb:.2f} MB)\n"
        f"TFLite Quantized:     {LIGHT_TFLITE_QUANT_PATH} ({quant_size_mb:.2f} MB / {len(tflite_quant_bytes)//1024} KB)\n"
        f"TFLite Parity Diff:   {parity_diff:.6e}\n\n"
        f"Held-Out Test Samples: 150 images\n"
        f"Overall Test Loss:    {test_loss:.4f}\n"
        f"Overall Test Accuracy: {test_acc * 100:.2f}%\n\n"
        "Confusion Matrix:\n"
        f"                Healthy   Powdery      Rust\n"
        f"Healthy:      {cm[0, 0]:9d} {cm[0, 1]:9d} {cm[0, 2]:9d}\n"
        f"Powdery:      {cm[1, 0]:9d} {cm[1, 1]:9d} {cm[1, 2]:9d}\n"
        f"Rust:         {cm[2, 0]:9d} {cm[2, 1]:9d} {cm[2, 2]:9d}\n\n"
        "Classification Report:\n"
        f"{clf_rep}\n"
        "========================================================================\n"
    )

    with open(LIGHT_REPORT_TXT_PATH, 'w', encoding='utf-8') as f:
        f.write(summary_text)

    print("\n" + summary_text)


if __name__ == '__main__':
    main()
