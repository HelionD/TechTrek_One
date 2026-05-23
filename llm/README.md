docker run -d \
  --name ollama \
  --network llm-net \
  -p 11434:11434 \
  ollama/ollama