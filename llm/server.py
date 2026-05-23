import json
import os
import subprocess
import signal
import sys
import time
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse

HOST = "0.0.0.0"
PORT = 11434
OLLAMA_BIN = os.environ.get("OLLAMA_BIN", "/usr/local/bin/ollama")
MODEL = os.environ.get("OLLAMA_MODEL", "tinyllama")

ollama_proc = None


def _ollama_installed() -> bool:
    try:
        result = subprocess.run(
            [OLLAMA_BIN, "--version"],
            capture_output=True,
            timeout=30,
            check=True,
        )
        return result.returncode == 0
    except (subprocess.CalledProcessError, FileNotFoundError, OSError):
        return False


def _install_ollama() -> None:
    print("Installing Ollama CLI...", flush=True)
    try:
        subprocess.run(
            ["sh", "-c", "curl -fsSL https://ollama.com/install.sh | sh"],
            capture_output=True,
            timeout=300,
            check=True,
        )
        print("Ollama CLI installed", flush=True)
    except Exception as e:
        print(f"Ollama install failed: {e}", file=sys.stderr, flush=True)
        sys.exit(1)


def _ensure_model_pulled() -> None:
    print(f"Checking for model {MODEL}...", flush=True)
    try:
        result = subprocess.run(
            [OLLAMA_BIN, "list"],
            capture_output=True,
            timeout=60,
            check=True,
            text=True,
        )
        if MODEL in result.stdout:
            print(f"Model {MODEL} already present", flush=True)
            return
    except Exception:
        pass

    print(f"Pulling model {MODEL}...", flush=True)
    try:
        subprocess.run(
            [OLLAMA_BIN, "pull", MODEL],
            capture_output=True,
            timeout=600,
            check=True,
        )
        print(f"Model {MODEL} ready", flush=True)
    except Exception as e:
        print(f"Failed to pull model {MODEL}: {e}", file=sys.stderr, flush=True)
        sys.exit(1)


def start_ollama():
    global ollama_proc

    if not _ollama_installed():
        _install_ollama()

    try:
        ollama_proc = subprocess.Popen(
            [OLLAMA_BIN, "serve"],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )
        print(f"Ollama server started (pid={ollama_proc.pid})", flush=True)
        time.sleep(5)
        _ensure_model_pulled()
    except Exception as e:
        print(f"Ollama init error: {e}", file=sys.stderr, flush=True)
        sys.exit(1)


def stop_ollama():
    global ollama_proc
    if ollama_proc:
        ollama_proc.terminate()
        try:
            ollama_proc.wait(timeout=10)
        except subprocess.TimeoutExpired:
            ollama_proc.kill()
        print("Ollama server stopped", flush=True)


class LLMProxyHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        parsed = urlparse(self.path)
        if parsed.path != "/api/generate":
            self.send_error(404, "Not Found")
            return

        content_length = int(self.headers.get("Content-Length", 0))
        raw_body = self.rfile.read(content_length)
        try:
            payload = json.loads(raw_body.decode("utf-8"))
        except json.JSONDecodeError:
            self.send_error(400, "Invalid JSON")
            return

        prompt = payload.get("prompt", "")
        model = payload.get("model", MODEL)

        # Call Ollama CLI with the prompt
        result = subprocess.run(
            [OLLAMA_BIN, "run", model],
            input=prompt.encode("utf-8"),
            capture_output=True,
            timeout=120,
        )
        response_text = result.stdout.decode("utf-8") if result.stdout else "[]"
        response_body = json.dumps({"text": response_text}).encode("utf-8")

        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(response_body)))
        self.end_headers()
        self.wfile.write(response_body)

    def do_GET(self):
        if self.path == "/api/tags":
            result = subprocess.run(
                [OLLAMA_BIN, "list"], capture_output=True, timeout=30
            )
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            body = json.dumps({"models": result.stdout.decode("utf-8").strip()}).encode(
                "utf-8"
            )
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)
        else:
            self.send_error(404)

    def log_message(self, format, *args):
        print(f"[LLM Proxy] {args[0]}", file=sys.stderr, flush=True)


if __name__ == "__main__":
    signal.signal(signal.SIGTERM, lambda *a: stop_ollama())
    signal.signal(signal.SIGINT, lambda *a: (stop_ollama(), sys.exit(0)))
    start_ollama()
    server = HTTPServer((HOST, PORT), LLMProxyHandler)
    print(f"LLM proxy running at http://{HOST}:{PORT} (model={MODEL})", flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        stop_ollama()
