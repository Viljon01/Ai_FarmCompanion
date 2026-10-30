"""
test_interface.py - Farm Companion Interactive Model Testing Interface
A standalone local web app for testing the plant disease classification models:
  - Drag-and-drop or browse leaf photographs
  - One-click testing with test dataset samples (Healthy, Powdery, Rust)
  - Select between Ultra-Lightweight (543 KB) and Standard (8.77 MB) models
  - Real-time confidence scores, latency metrics, and decoupled agronomic guidance
  - Zero external web dependencies: runs using Python's built-in http.server
"""

import os
import io
import time
import json
import base64
import random
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import urlparse, parse_qs
from PIL import Image
import numpy as np

import tensorflow as tf
tf.config.threading.set_inter_op_parallelism_threads(2)
tf.config.threading.set_intra_op_parallelism_threads(2)

PORT = 8080
HOST = "127.0.0.1"

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
DATASET_TEST_DIR = os.path.join(PROJECT_ROOT, 'plant-disease-recognition-dataset', 'Test', 'Test')
MODELS_DIR = os.path.join(SCRIPT_DIR, 'models')

# Available models
MODEL_PATHS = {
    "ultralight_quant": {
        "name": "Ultra-Lightweight Quantized (543 KB, ~5ms latency)",
        "file": os.path.join(MODELS_DIR, "leaf_disease_model_light_quant.tflite")
    },
    "ultralight_fp32": {
        "name": "Ultra-Lightweight Float32 (1.54 MB)",
        "file": os.path.join(MODELS_DIR, "leaf_disease_model_light.tflite")
    },
    "standard_fp32": {
        "name": "Standard Float32 (8.77 MB, 96.67% accuracy)",
        "file": os.path.join(MODELS_DIR, "leaf_disease_model.tflite")
    },
    "standard_quant": {
        "name": "Standard Quantized (2.48 MB)",
        "file": os.path.join(MODELS_DIR, "leaf_disease_model_quant.tflite")
    }
}

CLASS_NAMES = ["Healthy", "Powdery", "Rust"]
CONFIDENCE_THRESHOLD = 0.70

# Cache loaded interpreters
interpreters = {}

def get_interpreter(model_key):
    if model_key not in MODEL_PATHS:
        model_key = "ultralight_quant"
    if model_key not in interpreters:
        path = MODEL_PATHS[model_key]["file"]
        if not os.path.exists(path):
            raise FileNotFoundError(f"Model file not found: {path}")
        interp = tf.lite.Interpreter(model_path=path)
        interp.allocate_tensors()
        interpreters[model_key] = interp
    return interpreters[model_key], MODEL_PATHS[model_key]["name"]


def run_inference(image_pil, model_key):
    interpreter, model_name = get_interpreter(model_key)
    input_details = interpreter.get_input_details()[0]
    output_details = interpreter.get_output_details()[0]

    # Preprocess image: resize to 160x160 RGB
    img = image_pil.convert('RGB').resize((160, 160), Image.Resampling.BILINEAR)
    img_array = np.array(img, dtype=np.float32)
    # Shape [1, 160, 160, 3] in raw [0.0, 255.0]
    input_tensor = np.expand_dims(img_array, axis=0)

    # Benchmark latency
    start_time = time.perf_counter()
    interpreter.set_tensor(input_details['index'], input_tensor)
    interpreter.invoke()
    output_tensor = interpreter.get_tensor(output_details['index'])
    latency_ms = round((time.perf_counter() - start_time) * 1000, 2)

    raw_scores = output_tensor[0]
    scores_dict = {CLASS_NAMES[i]: round(float(raw_scores[i]), 4) for i in range(len(CLASS_NAMES))}
    
    max_idx = int(np.argmax(raw_scores))
    top_class = CLASS_NAMES[max_idx]
    top_score = float(raw_scores[max_idx])
    is_uncertain = top_score < CONFIDENCE_THRESHOLD

    # Agricultural Mapping & Guidance
    if is_uncertain:
        status = "UNCERTAIN_SCREENING"
        display_title = "Uncertain Screening"
        user_msg = (
            "Uncertain Screening: We could not confidently screen this leaf image. "
            "Capture a clear image of the affected leaf in natural lighting. "
            "If the problem persists, consult an agricultural extension specialist."
        )
        recommendation = {
            "title": "Uncertain Screening — Manual Verification Advised",
            "summary": "The model could not classify this photograph with sufficient certainty.",
            "practices": [
                "Photograph a single crop leaf against a neutral, shaded background.",
                "Ensure sharp focus on leaf lesions or discoloration without hand blur.",
                "Check whether your crop is among supported species."
            ],
            "disclaimer": "Preliminary screening only. Does not replace accredited agronomist inspection."
        }
    else:
        status = "CONFIRMED_PRELIMINARY"
        if top_class == "Healthy":
            display_title = "Normal / Healthy"
            user_msg = f"Preliminary Screening: Normal / Healthy foliage ({round(top_score * 100, 1)}% confidence)."
            recommendation = {
                "title": "Healthy Crop Maintenance",
                "summary": "No powdery mildew or rust symptoms detected.",
                "practices": [
                    "Maintain routine crop scouting and canopy monitoring.",
                    "Ensure adequate row spacing to optimize airflow and reduce humidity.",
                    "Avoid overhead irrigation late in the evening."
                ],
                "disclaimer": "Regular field observation remains essential for early pathogen detection."
            }
        elif top_class == "Powdery":
            display_title = "Powdery Mildew"
            user_msg = f"Preliminary Screening: Symptoms match Powdery Mildew ({round(top_score * 100, 1)}% confidence)."
            recommendation = {
                "title": "Powdery Mildew Cultural Management",
                "summary": "Talcum-like white/gray fungal spots identified.",
                "practices": [
                    "Prune dense canopy shoots to improve sunlight penetration.",
                    "Moderate nitrogen applications to limit excessive lush foliage.",
                    "Sanitize cutting tools between rows to prevent spore transport."
                ],
                "disclaimer": "Verify with a local agricultural extension specialist before applying commercial fungicides."
            }
        else: # Rust
            display_title = "Plant Rust"
            user_msg = f"Preliminary Screening: Symptoms match Plant Rust ({round(top_score * 100, 1)}% confidence)."
            recommendation = {
                "title": "Plant Rust Cultural Management",
                "summary": "Reddish-orange spore pustules identified on leaf surface.",
                "practices": [
                    "Remove and destroy severely affected leaves to slow windborne spread.",
                    "Switch to drip or ground-level irrigation to keep foliage dry.",
                    "Inquire with local agricultural extension for rust-resistant seed varieties."
                ],
                "disclaimer": "Rust spreads rapidly in warm, damp weather. Prompt agronomist consultation is recommended."
            }

    return {
        "status": status,
        "is_uncertain": is_uncertain,
        "top_class": top_class,
        "display_name": display_title,
        "confidence": round(top_score * 100, 1),
        "user_message": user_msg,
        "class_scores": scores_dict,
        "latency_ms": latency_ms,
        "model_used": model_name,
        "recommendation": recommendation
    }


def get_random_sample(class_name):
    """Retrieve a random test image from the held-out test dataset."""
    folder = os.path.join(DATASET_TEST_DIR, class_name)
    if not os.path.isdir(folder):
        return None
    files = [f for f in os.listdir(folder) if f.lower().endswith(('.jpg', '.jpeg', '.png'))]
    if not files:
        return None
    sample_file = os.path.join(folder, random.choice(files))
    with open(sample_file, 'rb') as f:
        img_bytes = f.read()
    b64_str = base64.b64encode(img_bytes).decode('ascii')
    return f"data:image/jpeg;base64,{b64_str}"


HTML_PAGE = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Farm Companion — Plant Disease Testing Interface</title>
  <style>
    :root {
      --bg: #0d1310;
      --card-bg: rgba(20, 31, 24, 0.85);
      --border: #233c2a;
      --primary: #10b981;
      --primary-hover: #059669;
      --healthy: #22c55e;
      --powdery: #f59e0b;
      --rust: #ef4444;
      --text: #f0fdf4;
      --text-muted: #94a3b8;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
    body {
      background: radial-gradient(circle at 50% 0%, #15291b 0%, var(--bg) 80%);
      color: var(--text);
      min-height: 100vh;
      padding: 24px;
      display: flex;
      flex-direction: column;
      align-items: center;
    }
    .container {
      width: 100%;
      max-width: 860px;
    }
    header {
      text-align: center;
      margin-bottom: 24px;
    }
    header h1 {
      font-size: 28px;
      font-weight: 800;
      background: linear-gradient(135deg, #4ade80, #10b981);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      margin-bottom: 6px;
    }
    header p { color: var(--text-muted); font-size: 14px; }
    .card {
      background: var(--card-bg);
      backdrop-filter: blur(12px);
      border: 1px solid var(--border);
      border-radius: 16px;
      padding: 24px;
      margin-bottom: 20px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.4);
    }
    .model-selector {
      display: flex;
      gap: 12px;
      align-items: center;
      margin-bottom: 16px;
      flex-wrap: wrap;
    }
    .model-selector label { font-size: 13px; font-weight: 600; color: #a7f3d0; }
    select {
      background: #0f1f14;
      border: 1px solid var(--border);
      color: #ecfdf5;
      padding: 8px 14px;
      border-radius: 8px;
      font-size: 13px;
      outline: none;
      flex: 1;
    }
    .drop-zone {
      border: 2px dashed #2d5537;
      border-radius: 12px;
      padding: 32px 16px;
      text-align: center;
      cursor: pointer;
      transition: all 0.2s ease;
      background: rgba(16, 185, 129, 0.03);
    }
    .drop-zone:hover, .drop-zone.dragover {
      border-color: var(--primary);
      background: rgba(16, 185, 129, 0.08);
    }
    .drop-zone svg { width: 44px; height: 44px; stroke: #34d399; margin-bottom: 8px; }
    .drop-zone p { font-size: 14px; color: #cbd5e1; }
    .drop-zone span { font-size: 12px; color: var(--text-muted); }
    input[type="file"] { display: none; }
    .sample-buttons {
      display: flex;
      gap: 10px;
      margin-top: 14px;
      flex-wrap: wrap;
      align-items: center;
    }
    .sample-buttons span { font-size: 12px; color: var(--text-muted); }
    .btn-sample {
      background: #172c1c;
      border: 1px solid var(--border);
      color: #d1fae5;
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 12px;
      cursor: pointer;
      transition: background 0.2s;
    }
    .btn-sample:hover { background: #22432a; border-color: #34d399; }
    .result-section { display: none; margin-top: 20px; }
    .result-grid {
      display: grid;
      grid-template-columns: 240px 1fr;
      gap: 20px;
    }
    @media (max-width: 650px) {
      .result-grid { grid-template-columns: 1fr; }
    }
    .preview-box {
      width: 100%;
      height: 240px;
      border-radius: 10px;
      overflow: hidden;
      border: 1px solid var(--border);
      background: #000;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .preview-box img { width: 100%; height: 100%; object-fit: cover; }
    .status-badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 6px 14px;
      border-radius: 30px;
      font-size: 14px;
      font-weight: 700;
      margin-bottom: 12px;
    }
    .badge-healthy { background: rgba(34, 197, 94, 0.2); color: #4ade80; border: 1px solid #22c55e; }
    .badge-powdery { background: rgba(245, 158, 11, 0.2); color: #fbbf24; border: 1px solid #f59e0b; }
    .badge-rust { background: rgba(239, 68, 68, 0.2); color: #f87171; border: 1px solid #ef4444; }
    .badge-uncertain { background: rgba(217, 119, 6, 0.25); color: #fbbf24; border: 1px solid #f59e0b; }
    .latency-pill {
      font-size: 11px;
      background: #112215;
      padding: 3px 8px;
      border-radius: 12px;
      color: #6ee7b7;
      display: inline-block;
      margin-left: 8px;
    }
    .progress-bar-group { margin: 12px 0; }
    .progress-row {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      margin-bottom: 4px;
      color: #cbd5e1;
    }
    .progress-track {
      background: #1a2e20;
      height: 8px;
      border-radius: 4px;
      overflow: hidden;
      margin-bottom: 10px;
    }
    .progress-fill { height: 100%; border-radius: 4px; transition: width 0.4s ease; }
    .fill-healthy { background: var(--healthy); }
    .fill-powdery { background: var(--powdery); }
    .fill-rust { background: var(--rust); }
    .guidance-box {
      background: #112216;
      border-left: 4px solid var(--primary);
      padding: 12px;
      border-radius: 4px;
      margin-top: 14px;
    }
    .guidance-box h4 { font-size: 13px; color: #a7f3d0; margin-bottom: 6px; }
    .guidance-box ul { padding-left: 18px; font-size: 12px; color: #cbd5e1; }
    .guidance-box li { margin-bottom: 4px; }
    .disclaimer { font-size: 11px; color: var(--text-muted); margin-top: 10px; line-height: 1.4; }
    .loading { display: none; text-align: center; padding: 20px; font-size: 13px; color: #6ee7b7; }
  </style>
</head>
<body>
  <div class="container">
    <header>
      <h1>Farm Companion</h1>
      <p>Plant Leaf Disease Screening & Testing Interface</p>
    </header>

    <div class="card">
      <div class="model-selector">
        <label for="modelSelect">Engine:</label>
        <select id="modelSelect">
          <option value="ultralight_quant">Ultra-Lightweight Quantized (543 KB, ~5ms) [Recommended]</option>
          <option value="ultralight_fp32">Ultra-Lightweight Float32 (1.54 MB)</option>
          <option value="standard_fp32">Standard Float32 (8.77 MB, 96.67% Acc)</option>
          <option value="standard_quant">Standard Quantized (2.48 MB)</option>
        </select>
      </div>

      <div class="drop-zone" id="dropZone">
        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <p><strong>Click to browse</strong> or drag & drop a plant leaf photograph</p>
        <span>Supports JPG, PNG, WebP (Automatically resized to 160x160)</span>
      </div>
      <input type="file" id="fileInput" accept="image/*">

      <div class="sample-buttons">
        <span>Quick Test with Dataset Samples:</span>
        <button class="btn-sample" onclick="loadSample('Healthy')">Test Healthy Sample</button>
        <button class="btn-sample" onclick="loadSample('Powdery')">Test Powdery Mildew</button>
        <button class="btn-sample" onclick="loadSample('Rust')">Test Plant Rust</button>
      </div>

      <div class="loading" id="loadingIndicator">
        Running inference on local model...
      </div>

      <div class="result-section" id="resultSection">
        <div class="result-grid">
          <div>
            <div class="preview-box">
              <img id="previewImg" src="" alt="Foliage Preview">
            </div>
            <div style="font-size: 11px; color: var(--text-muted); text-align: center; margin-top: 6px;">
              Resized to model input: 160 x 160 (RGB)
            </div>
          </div>
          <div>
            <div id="statusBadge" class="status-badge badge-healthy">
              <span id="badgeIcon">●</span>
              <span id="badgeText">Normal / Healthy</span>
              <span class="latency-pill" id="latencyPill">6 ms</span>
            </div>

            <div style="font-size: 13px; color: #cbd5e1; margin-bottom: 12px;" id="userMsg">
              Preliminary Screening result summary.
            </div>

            <div class="progress-bar-group">
              <div class="progress-row">
                <span>Healthy</span>
                <span id="scoreHealthy">0.0%</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill fill-healthy" id="barHealthy" style="width: 0%"></div>
              </div>

              <div class="progress-row">
                <span>Powdery Mildew</span>
                <span id="scorePowdery">0.0%</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill fill-powdery" id="barPowdery" style="width: 0%"></div>
              </div>

              <div class="progress-row">
                <span>Plant Rust</span>
                <span id="scoreRust">0.0%</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill fill-rust" id="barRust" style="width: 0%"></div>
              </div>
            </div>

            <div class="guidance-box" id="guidanceBox">
              <h4 id="guidanceTitle">Agronomic Advisory:</h4>
              <ul id="guidanceList"></ul>
            </div>

            <p class="disclaimer" id="disclaimerText">
              Preliminary screening tool. Softmax confidence threshold: 70%. Does not replace accredited field agronomist inspection.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>

  <script>
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const modelSelect = document.getElementById('modelSelect');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const resultSection = document.getElementById('resultSection');
    const previewImg = document.getElementById('previewImg');

    dropZone.addEventListener('click', () => fileInput.click());
    dropZone.addEventListener('dragover', (e) => { e.preventDefault(); dropZone.classList.add('dragover'); });
    dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));
    dropZone.addEventListener('drop', (e) => {
      e.preventDefault();
      dropZone.classList.remove('dragover');
      if (e.dataTransfer.files.length > 0) {
        processFile(e.dataTransfer.files[0]);
      }
    });

    fileInput.addEventListener('change', () => {
      if (fileInput.files.length > 0) {
        processFile(fileInput.files[0]);
      }
    });

    function processFile(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        sendImageForPrediction(e.target.result);
      };
      reader.readAsDataURL(file);
    }

    async function loadSample(className) {
      loadingIndicator.style.display = 'block';
      resultSection.style.display = 'none';
      try {
        const resp = await fetch(`/sample?class=${className}`);
        const data = await resp.json();
        if (data.image_b64) {
          sendImageForPrediction(data.image_b64);
        }
      } catch (err) {
        alert('Failed to load sample: ' + err);
        loadingIndicator.style.display = 'none';
      }
    }

    async function sendImageForPrediction(b64DataUrl) {
      previewImg.src = b64DataUrl;
      loadingIndicator.style.display = 'block';
      resultSection.style.display = 'none';

      try {
        const resp = await fetch('/predict', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            image_b64: b64DataUrl,
            model_key: modelSelect.value
          })
        });

        const result = await resp.json();
        displayResults(result);
      } catch (err) {
        alert('Prediction error: ' + err);
      } finally {
        loadingIndicator.style.display = 'none';
      }
    }

    function displayResults(res) {
      resultSection.style.display = 'block';

      // Status Badge
      const badge = document.getElementById('statusBadge');
      const badgeText = document.getElementById('badgeText');
      const latencyPill = document.getElementById('latencyPill');

      latencyPill.textContent = `${res.latency_ms} ms (${res.model_used.split(' ')[0]})`;

      if (res.is_uncertain) {
        badge.className = 'status-badge badge-uncertain';
        badgeText.textContent = 'Uncertain Screening (<70%)';
      } else if (res.top_class === 'Healthy') {
        badge.className = 'status-badge badge-healthy';
        badgeText.textContent = `Normal / Healthy (${res.confidence}%)`;
      } else if (res.top_class === 'Powdery') {
        badge.className = 'status-badge badge-powdery';
        badgeText.textContent = `Powdery Mildew (${res.confidence}%)`;
      } else {
        badge.className = 'status-badge badge-rust';
        badgeText.textContent = `Plant Rust (${res.confidence}%)`;
      }

      document.getElementById('userMsg').textContent = res.user_message;

      // Probability bars
      const hScore = (res.class_scores.Healthy * 100).toFixed(1);
      const pScore = (res.class_scores.Powdery * 100).toFixed(1);
      const rScore = (res.class_scores.Rust * 100).toFixed(1);

      document.getElementById('scoreHealthy').textContent = `${hScore}%`;
      document.getElementById('barHealthy').style.width = `${hScore}%`;

      document.getElementById('scorePowdery').textContent = `${pScore}%`;
      document.getElementById('barPowdery').style.width = `${pScore}%`;

      document.getElementById('scoreRust').textContent = `${rScore}%`;
      document.getElementById('barRust').style.width = `${rScore}%`;

      // Decoupled Guidance
      const rec = res.recommendation;
      document.getElementById('guidanceTitle').textContent = rec.title;
      const list = document.getElementById('guidanceList');
      list.innerHTML = '';
      rec.practices.forEach(p => {
        const li = document.createElement('li');
        li.textContent = p;
        list.appendChild(li);
      });

      document.getElementById('disclaimerText').textContent = rec.disclaimer;
    }
  </script>
</body>
</html>
"""


class RequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        parsed = urlparse(self.path)
        if parsed.path == "/" or parsed.path == "/index.html":
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.end_headers()
            self.wfile.write(HTML_PAGE.encode('utf-8'))
        elif parsed.path == "/sample":
            params = parse_qs(parsed.query)
            cls_name = params.get('class', ['Healthy'])[0]
            if cls_name not in CLASS_NAMES:
                cls_name = 'Healthy'
            b64_url = get_random_sample(cls_name)
            if b64_url:
                self.send_response(200)
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"image_b64": b64_url}).encode('utf-8'))
            else:
                self.send_response(404)
                self.end_headers()
        else:
            self.send_response(404)
            self.end_headers()

    def do_POST(self):
        parsed = urlparse(self.path)
        if parsed.path == "/predict":
            content_length = int(self.headers.get('Content-Length', 0))
            post_data = self.rfile.read(content_length)
            try:
                payload = json.loads(post_data.decode('utf-8'))
                b64_str = payload.get('image_b64', '')
                model_key = payload.get('model_key', 'ultralight_quant')

                # Strip base64 header if present
                if ',' in b64_str:
                    b64_str = b64_str.split(',', 1)[1]

                img_bytes = base64.b64decode(b64_str)
                image_pil = Image.open(io.BytesIO(img_bytes))

                res = run_inference(image_pil, model_key)
                self.send_response(200)
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps(res).encode('utf-8'))
            except Exception as e:
                self.send_response(500)
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"error": str(e)}).encode('utf-8'))
        else:
            self.send_response(404)
            self.end_headers()

    def log_message(self, format, *args):
        # Concise logging
        pass


def main():
    print("=" * 70)
    print("  FARM COMPANION - INTERACTIVE TESTING INTERFACE")
    print("=" * 70)
    # Pre-warm default ultra-lightweight model
    print("Pre-warming Ultra-Lightweight model...")
    get_interpreter("ultralight_quant")
    print(f"Server running at: http://{HOST}:{PORT}")
    print("Open http://localhost:8080 in your browser to test leaf photographs!")
    print("Press Ctrl+C to stop.")

    server = HTTPServer((HOST, PORT), RequestHandler)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nServer stopped.")


if __name__ == '__main__':
    main()
