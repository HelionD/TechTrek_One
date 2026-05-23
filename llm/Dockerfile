FROM python:3.11-slim
 
WORKDIR /app
<<<<<<< Updated upstream
 
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
 
||||||| Stash base

<<<<<<< Updated upstream
=======

# Install Ollama
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://ollama.ai/install.sh | sh && \
    rm -rf /var/lib/apt/lists/*

>>>>>>> Stashed changes
||||||| Stash base
=======
# Install Ollama
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://ollama.ai/install.sh | sh && \
    rm -rf /var/lib/apt/lists/*

>>>>>>> Stashed changes
COPY server.py .
 
EXPOSE 11434
 
CMD ["python", "server.py"]