FROM ollama/ollama:latest AS ollama-base
FROM python:3.11-slim

COPY --from=ollama-base /bin/ollama /usr/local/bin/ollama

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt || true
COPY server.py .

ENV OLLAMA_MODELS=/root/.ollama/models
ENV OLLAMA_MODEL=qwen2.5:3b

RUN ollama serve & \
    sleep 5 && \
    ollama pull qwen2.5:3b && \
    pkill ollama || true

EXPOSE 11434
CMD ["python", "server.py"]