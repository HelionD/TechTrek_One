FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    iptables \
    curl \
    procps \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

#CHANGE THIS WHEN HAVING LLM SCRIPTS.
CMD ["python", "ids-ips-llm.py"] 