"""
train_model.py - Farm Companion Leaf Disease Classifier Training Pipeline
Trains a MobileNetV2 transfer learning model to classify plant leaves into:
  0: Healthy
  1: Powdery (Powdery Mildew)
  2: Rust
"""

import os
import sys
import json
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

# Limit CPU threading to avoid overloading system resources
import tensorflow as tf
tf.config.threading.set_inter_op_parallelism_threads(2)
tf.config.threading.set_intra_op_parallelism_threads(2)

from tensorflow.keras import layers, models, applications, callbacks, optimizers

# Set reproducible seeds
tf.keras.utils.set_random_seed(42)

# Configuration Constants
IMG_SIZE = (160, 160)
INPUT_SHAPE = (160, 160, 3)
BATCH_SIZE = 8
NUM_CLASSES = 3
CLASS_NAMES = ['Healthy', 'Powdery', 'Rust']

PHASE1_EPOCHS = 15
PHASE2_EPOCHS = 10
INITIAL_LR = 1e-3
FINE_TUNE_LR = 1e-5

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
DATASET_DIR = os.path.join(PROJECT_ROOT, 'plant-disease-recognition-dataset')

MODELS_DIR = os.path.join(SCRIPT_DIR, 'models')
REPORTS_DIR = os.path.join(SCRIPT_DIR, 'reports')
os.makedirs(MODELS_DIR, exist_ok=True)
os.makedirs(REPORTS_DIR, exist_ok=True)

MODEL_SAVE_PATH = os.path.join(MODELS_DIR, 'leaf_disease_model.keras')
LABELS_PATH = os.path.join(MODELS_DIR, 'labels.txt')
HISTORY_PLOT_PATH = os.path.join(REPORTS_DIR, 'training_history.png')
TRAINING_LOG_PATH = os.path.join(REPORTS_DIR, 'training_log.json')


def verify_paths():
    """Verify that dataset splits exist before starting."""
    train_dir = os.path.join(DATASET_DIR, 'Train', 'Train')
    val_dir = os.path.join(DATASET_DIR, 'Validation', 'Validation')
    test_dir = os.path.join(DATASET_DIR, 'Test', 'Test')

    for d, split in [(train_dir, 'Train'), (val_dir, 'Validation'), (test_dir, 'Test')]:
        if not os.path.isdir(d):
            raise FileNotFoundError(
                f"Dataset directory not found: {d}. Ensure dataset is extracted to {DATASET_DIR}."
            )
        for c in CLASS_NAMES:
            c_dir = os.path.join(d, c)
            if not os.path.isdir(c_dir):
                raise FileNotFoundError(f"Missing class folder: {c_dir}")

    return train_dir, val_dir, test_dir


def load_datasets(train_dir, val_dir, test_dir):
    """Load image datasets with consistent class names and streaming prefetch."""
    print("Loading datasets from disk...")

    train_ds = tf.keras.utils.image_dataset_from_directory(
        train_dir,
        labels='inferred',
        label_mode='categorical',
        class_names=CLASS_NAMES,
        image_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        shuffle=True,
        seed=42
    )

    val_ds = tf.keras.utils.image_dataset_from_directory(
        val_dir,
        labels='inferred',
        label_mode='categorical',
        class_names=CLASS_NAMES,
        image_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        shuffle=False
    )

    test_ds = tf.keras.utils.image_dataset_from_directory(
        test_dir,
        labels='inferred',
        label_mode='categorical',
        class_names=CLASS_NAMES,
        image_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        shuffle=False
    )

    print(f"Class mapping: { {i: name for i, name in enumerate(CLASS_NAMES)} }")

    # Data augmentation for training only
    data_augmentation = tf.keras.Sequential([
        layers.RandomFlip("horizontal_and_vertical", seed=42),
        layers.RandomRotation(0.15, fill_mode="reflect", seed=42),
        layers.RandomZoom(0.1, fill_mode="reflect", seed=42),
        layers.RandomContrast(0.1, seed=42)
    ], name="training_augmentation")

    train_ds = train_ds.map(
        lambda x, y: (data_augmentation(x, training=True), y),
        num_parallel_calls=tf.data.AUTOTUNE
    ).prefetch(buffer_size=tf.data.AUTOTUNE)

    val_ds = val_ds.prefetch(buffer_size=tf.data.AUTOTUNE)
    test_ds = test_ds.prefetch(buffer_size=tf.data.AUTOTUNE)

    return train_ds, val_ds, test_ds


def build_model():
    """
    Build MobileNetV2 architecture with embedded [0, 255] -> [-1, 1] normalization.
    Returns:
        model: tf.keras.Model
        base_model: MobileNetV2 feature extractor
    """
    print("\nBuilding MobileNetV2 model architecture...")
    inputs = layers.Input(shape=INPUT_SHAPE, name="input_image")

    # Embedded preprocessing: scale [0, 255] -> [-1, 1]
    # This simplifies deployment because the client sends raw RGB [0..255] pixels
    rescaled = layers.Rescaling(scale=1.0 / 127.5, offset=-1.0, name="rescaling_mobilenetv2")(inputs)

    # Base feature extractor pretrained on ImageNet
    base_model = applications.MobileNetV2(
        input_shape=INPUT_SHAPE,
        include_top=False,
        weights='imagenet'
    )
    base_model.trainable = False

    # Route through backbone
    x = base_model(rescaled, training=False)

    # Classification Head
    x = layers.GlobalAveragePooling2D(name="global_average_pooling")(x)
    x = layers.BatchNormalization(name="batch_norm")(x)
    x = layers.Dropout(0.3, seed=42, name="dropout")(x)
    x = layers.Dense(64, activation="relu", name="dense_64")(x)
    x = layers.Dropout(0.2, seed=42, name="head_dropout")(x)
    outputs = layers.Dense(NUM_CLASSES, activation="softmax", name="disease_probabilities")(x)

    model = models.Model(inputs=inputs, outputs=outputs, name="farm_companion_leaf_disease_mobilenetv2")
    return model, base_model


def plot_and_save_history(all_history, save_path):
    """Plot and save combined training and validation loss and accuracy curves."""
    epochs_range = range(1, len(all_history['accuracy']) + 1)

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))

    # Accuracy Plot
    ax1.plot(epochs_range, all_history['accuracy'], 'b-o', label='Training Accuracy', linewidth=2)
    ax1.plot(epochs_range, all_history['val_accuracy'], 'r--s', label='Validation Accuracy', linewidth=2)
    ax1.set_title('Training & Validation Accuracy', fontsize=13, fontweight='bold')
    ax1.set_xlabel('Epoch', fontsize=11)
    ax1.set_ylabel('Accuracy', fontsize=11)
    ax1.set_ylim([0.0, 1.05])
    ax1.grid(True, linestyle='--', alpha=0.6)
    ax1.legend(loc='lower right', fontsize=10)

    # Loss Plot
    ax2.plot(epochs_range, all_history['loss'], 'b-o', label='Training Loss', linewidth=2)
    ax2.plot(epochs_range, all_history['val_loss'], 'r--s', label='Validation Loss', linewidth=2)
    ax2.set_title('Training & Validation Loss', fontsize=13, fontweight='bold')
    ax2.set_xlabel('Epoch', fontsize=11)
    ax2.set_ylabel('Loss', fontsize=11)
    ax2.grid(True, linestyle='--', alpha=0.6)
    ax2.legend(loc='upper right', fontsize=10)

    plt.tight_layout()
    plt.savefig(save_path, dpi=200)
    plt.close()
    print(f"Training history curves saved to: {save_path}")


def main():
    print("=" * 70)
    print("  FARM COMPANION - PLANT DISEASE DETECTION TRAINING PIPELINE")
    print("=" * 70)

    train_dir, val_dir, test_dir = verify_paths()
    train_ds, val_ds, test_ds = load_datasets(train_dir, val_dir, test_dir)

    # Save canonical labels.txt file
    with open(LABELS_PATH, 'w', encoding='utf-8') as f:
        for name in CLASS_NAMES:
            f.write(f"{name}\n")
    print(f"Labels saved to: {LABELS_PATH}")

    model, base_model = build_model()
    model.summary(print_fn=print)

    # Compile for Phase 1: Feature Extraction
    model.compile(
        optimizer=optimizers.Adam(learning_rate=INITIAL_LR),
        loss=tf.keras.losses.CategoricalCrossentropy(label_smoothing=0.05),
        metrics=['accuracy']
    )

    checkpoint_cb = callbacks.ModelCheckpoint(
        filepath=MODEL_SAVE_PATH,
        monitor='val_loss',
        save_best_only=True,
        verbose=1
    )

    early_stopping_cb = callbacks.EarlyStopping(
        monitor='val_loss',
        patience=4,
        restore_best_weights=True,
        verbose=1
    )

    print("\n" + "=" * 70)
    print(">>> PHASE 1: Training Classification Head (Backbone Frozen)")
    print("=" * 70)

    history1 = model.fit(
        train_ds,
        validation_data=val_ds,
        epochs=PHASE1_EPOCHS,
        callbacks=[checkpoint_cb, early_stopping_cb]
    )

    # Phase 2: Fine-Tuning top layers of MobileNetV2
    print("\n" + "=" * 70)
    print(">>> PHASE 2: Fine-Tuning Top Layers of MobileNetV2")
    print("=" * 70)

    base_model.trainable = True
    # Freeze all layers except the last 24 layers of MobileNetV2
    fine_tune_at = len(base_model.layers) - 24
    for layer in base_model.layers[:fine_tune_at]:
        layer.trainable = False

    trainable_count = sum(len(layer.weights) for layer in model.layers if layer.trainable)
    print(f"Unfroze last {24} layers of MobileNetV2 for gentle fine-tuning.")
    print(f"Trainable weights count: {trainable_count}")

    model.compile(
        optimizer=optimizers.Adam(learning_rate=FINE_TUNE_LR),
        loss=tf.keras.losses.CategoricalCrossentropy(label_smoothing=0.05),
        metrics=['accuracy']
    )

    history2 = model.fit(
        train_ds,
        validation_data=val_ds,
        epochs=PHASE2_EPOCHS,
        callbacks=[checkpoint_cb, early_stopping_cb]
    )

    # Combine training history
    combined_history = {
        'accuracy': history1.history['accuracy'] + history2.history['accuracy'],
        'val_accuracy': history1.history['val_accuracy'] + history2.history['val_accuracy'],
        'loss': history1.history['loss'] + history2.history['loss'],
        'val_loss': history1.history['val_loss'] + history2.history['val_loss'],
    }

    # Save training curves
    plot_and_save_history(combined_history, HISTORY_PLOT_PATH)

    # Save log
    with open(TRAINING_LOG_PATH, 'w', encoding='utf-8') as f:
        json.dump(combined_history, f, indent=2)
    print(f"Training history metrics logged to: {TRAINING_LOG_PATH}")

    # Confirm best model saved
    if os.path.exists(MODEL_SAVE_PATH):
        model_size_mb = os.path.getsize(MODEL_SAVE_PATH) / (1024 * 1024)
        print(f"\nBest trained model saved successfully!")
        print(f"Path: {MODEL_SAVE_PATH}")
        print(f"Size: {model_size_mb:.2f} MB")
    else:
        raise RuntimeError("Model checkpoint file was not found after training!")

    print("\nTraining completed successfully! Next step: evaluate_model.py")


if __name__ == '__main__':
    main()
