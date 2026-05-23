
# Stage 1: install dependencies
FROM python:3.11-slim AS builder
 
WORKDIR /app
 
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
 
 
# Stage 2: runtime image
FROM python:3.11-slim AS runtime
 
WORKDIR /app
 
# copy installed packages from builder
COPY --from=builder /usr/local/lib/python3.11 /usr/local/lib/python3.11
COPY --from=builder /usr/local/bin /usr/local/bin
 
# copy app code
COPY main.py .
COPY app/ ./app/
 
EXPOSE 8000
 
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]