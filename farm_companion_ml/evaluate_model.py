"""
evaluate_model.py - Farm Companion Leaf Disease Model Evaluation
Evaluates the trained Keras model on the held-out test dataset (150 images).
Generates:
  - Test loss and accuracy
  - Confusion matrix and heatmap visualization
  - Precision, Recall, and F1-score classification report
  - Sample predictions with confidence breakdown
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

from sklearn.metrics import classification_report, confusion_matrix

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
DATASET_DIR = os.path.join(PROJECT_ROOT, 'plant-disease-recognition-dataset')
TEST_DIR = os.path.join(DATASET_DIR, 'Test', 'Test')

MODELS_DIR = os.path.join(SCRIPT_DIR, 'models')
REPORTS_DIR = os.path.join(SCRIPT_DIR, 'reports')
MODEL_PATH = os.path.join(MODELS_DIR, 'leaf_disease_model.keras')
LABELS_PATH = os.path.join(MODELS_DIR, 'labels.txt')

CM_PLOT_PATH = os.path.join(REPORTS_DIR, 'confusion_matrix.png')
REPORT_TXT_PATH = os.path.join(REPORTS_DIR, 'classification_report.txt')

IMG_SIZE = (160, 160)
BATCH_SIZE = 8
CLASS_NAMES = ['Healthy', 'Powdery', 'Rust']


def plot_confusion_matrix(cm, class_names, save_path):
    """Plot and save confusion matrix with annotations."""
    fig, ax = plt.subplots(figsize=(7, 6))
    im = ax.imshow(cm, interpolation='nearest', cmap=plt.cm.Greens)
    ax.figure.colorbar(im, ax=ax)

    ax.set(
        xticks=np.arange(cm.shape[1]),
        yticks=np.arange(cm.shape[0]),
        xticklabels=class_names,
        yticklabels=class_names,
        title='Confusion Matrix - Held-Out Test Set (150 Images)',
        ylabel='True Label (Ground Truth)',
        xlabel='Predicted Label'
    )

    plt.setp(ax.get_xticklabels(), rotation=30, ha="right", rotation_mode="anchor")

    thresh = cm.max() / 2.0
    for i in range(cm.shape[0]):
        for j in range(cm.shape[1]):
            ax.text(
                j, i, format(cm[i, j], 'd'),
                ha="center", va="center",
                color="white" if cm[i, j] > thresh else "black",
                fontweight='bold', fontsize=12
            )

    plt.tight_layout()
    plt.savefig(save_path, dpi=200)
    plt.close()
    print(f"Confusion matrix plot saved to: {save_path}")


def main():
    print("=" * 70)
    print("  FARM COMPANION - MODEL EVALUATION (HELD-OUT TEST SET)")
    print("=" * 70)

    if not os.path.exists(MODEL_PATH):
        raise FileNotFoundError(
            f"Trained model not found at: {MODEL_PATH}. Run train_model.py first."
        )

    print(f"Loading trained model: {MODEL_PATH}")
    model = tf.keras.models.load_model(MODEL_PATH)

    print(f"Loading held-out test dataset from: {TEST_DIR}")
    test_ds = tf.keras.utils.image_dataset_from_directory(
        TEST_DIR,
        labels='inferred',
        label_mode='categorical',
        class_names=CLASS_NAMES,
        image_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        shuffle=False
    )

    # 1. Evaluate Overall Test Loss & Accuracy
    print("\nEvaluating test set performance...")
    eval_results = model.evaluate(test_ds, verbose=1)
    test_loss = eval_results[0]
    test_acc = eval_results[1]

    # 2. Gather All True Labels and Model Predictions
    y_true_all = []
    y_pred_probs_all = []

    for images, labels in test_ds:
        preds = model.predict(images, verbose=0)
        y_true_all.extend(np.argmax(labels.numpy(), axis=1))
        y_pred_probs_all.extend(preds)

    y_true_all = np.array(y_true_all)
    y_pred_probs_all = np.array(y_pred_probs_all)
    y_pred_all = np.argmax(y_pred_probs_all, axis=1)

    # 3. Compute Confusion Matrix
    cm = confusion_matrix(y_true_all, y_pred_all)
    plot_confusion_matrix(cm, CLASS_NAMES, CM_PLOT_PATH)

    # 4. Classification Report
    clf_report_text = classification_report(
        y_true_all, y_pred_all,
        target_names=CLASS_NAMES,
        digits=4
    )

    full_report = (
        "========================================================================\n"
        "           FARM COMPANION - MODEL EVALUATION REPORT                     \n"
        "========================================================================\n\n"
        f"Model File: {MODEL_PATH}\n"
        f"Input Resolution: 160 x 160 x 3 (RGB)\n"
        f"Evaluated Test Samples: {len(y_true_all)} images\n"
        f"Overall Test Loss: {test_loss:.4f}\n"
        f"Overall Test Accuracy: {test_acc * 100:.2f}%\n\n"
        "------------------------------------------------------------------------\n"
        "CONFUSION MATRIX (Rows: True, Columns: Predicted):\n"
        "------------------------------------------------------------------------\n"
        f"                Healthy   Powdery      Rust\n"
        f"Healthy:      {cm[0, 0]:9d} {cm[0, 1]:9d} {cm[0, 2]:9d}\n"
        f"Powdery:      {cm[1, 0]:9d} {cm[1, 1]:9d} {cm[1, 2]:9d}\n"
        f"Rust:         {cm[2, 0]:9d} {cm[2, 1]:9d} {cm[2, 2]:9d}\n\n"
        "------------------------------------------------------------------------\n"
        "DETAILED CLASSIFICATION METRICS (Precision, Recall, F1-Score):\n"
        "------------------------------------------------------------------------\n"
        f"{clf_report_text}\n"
        "------------------------------------------------------------------------\n"
        "AGRICULTURAL & OPERATIONAL REALITY CAVEAT:\n"
        "High test accuracy on this benchmark dataset reflects clean, curated test\n"
        "photographs. In field conditions, variations in lighting, background foliage,\n"
        "unsupported plant species, or multiple co-occurring diseases will impact\n"
        "predictions. The Android application MUST surface confidence scores and\n"
        "provide an 'Uncertain / Inspect Manually' warning if confidence is below threshold.\n"
        "========================================================================\n"
    )

    with open(REPORT_TXT_PATH, 'w', encoding='utf-8') as f:
        f.write(full_report)
    print(f"Classification report saved to: {REPORT_TXT_PATH}")
    print("\n" + full_report)

    # 5. Display Sample Predictions
    print("------------------------------------------------------------------------")
    print("SAMPLE PREDICTIONS (First 10 Test Images):")
    print("------------------------------------------------------------------------")
    print(f"{'Index':<6} {'True Class':<12} {'Predicted Class':<16} {'Confidence':<12} {'Result'}")
    for idx in range(min(10, len(y_true_all))):
        true_name = CLASS_NAMES[y_true_all[idx]]
        pred_name = CLASS_NAMES[y_pred_all[idx]]
        confidence = y_pred_probs_all[idx][y_pred_all[idx]] * 100
        match_str = "CORRECT" if true_name == pred_name else "MISMATCH"
        print(f"{idx:<6} {true_name:<12} {pred_name:<16} {confidence:6.2f}%     {match_str}")
    print("------------------------------------------------------------------------")


if __name__ == '__main__':
    main()
