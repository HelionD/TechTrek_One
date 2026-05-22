import os
from pathlib import Path

from dotenv import load_dotenv

basedir = Path(__file__).resolve().parents[1]
load_dotenv(basedir / ".env")

os.environ.setdefault("DATABASE_URL", "sqlite+aiosqlite:///:memory:")
os.environ.setdefault("SECRET_KEY", "test-secret")
