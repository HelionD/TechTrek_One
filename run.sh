#!/usr/bin/env bash
set -e

echo "=== TechTrek One - Auto Start Script ==="

echo ""
echo "1. Building DB image..."
cd db
docker build -t techtrek-db .
cd ..

echo ""
echo "2. Stopping old DB container (if exists)..."
docker stop techtrek-db 2>/dev/null || true
docker rm techtrek-db 2>/dev/null || true

echo ""
echo "3. Starting DB container..."
docker run -d --name techtrek-db -p 5432:5432 \
  -e POSTGRES_USER=techtrek \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=techtrek \
  techtrek-db

echo "   Waiting for DB to be ready..."
sleep 5

echo ""
echo "4. Creating demo user..."
# Create a temporary Python script to create the demo user
cd backend
python3 -c "
import asyncio
from app.database import get_session_factory
from app.crud.users import get_or_create_user

async def main():
    sf = get_session_factory()
    async with sf() as db:
        user = await get_or_create_user(db, external_id='demo_user', name='Demo', surname='User', email='demo@one.al')
        print(f'Demo user created: {user.id} (plan_type={user.plan_type})')
    await sf.engine.dispose()

asyncio.run(main())
"
cd ..

echo ""
echo "5. Building backend image..."
docker build -t techtrek-backend backend/

echo ""
echo "6. Stopping old backend container (if exists)..."
docker stop techtrek-backend 2>/dev/null || true
docker rm techtrek-backend 2>/dev/null || true

echo ""
echo "7. Starting backend container..."
docker run -d --name techtrek-backend -p 8000:8000 \
  --add-host host.docker.internal:host-gateway \
  -e DATABASE_URL=postgresql+asyncpg://techtrek:secret@172.17.0.1:5432/techtrek \
  techtrek-backend

echo "   Waiting for backend to be ready..."
sleep 3

echo ""
echo "8. Checking backend health..."
curl -s http://localhost:8000/health && echo ""

echo ""
echo "9. Starting frontend dev server..."
cd frontend && npm run dev -- --host 0.0.0.0 --port 5173 &
cd ..

echo ""
echo "=== All services started! ==="
echo "   - DB:      localhost:5432"
echo "   - Backend: http://localhost:8000"
echo "   - Frontend: http://localhost:5173"
echo "   - API Docs: http://localhost:8000/docs"
