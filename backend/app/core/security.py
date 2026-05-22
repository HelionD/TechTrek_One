from datetime import datetime, timedelta
from typing import Any, Mapping

from jose import JWTError, jwt

from app.config import settings


def create_access_token(subject: str, expires_delta: timedelta | None = None) -> str:
    to_encode: dict[str, Any] = {"sub": str(subject)}
    now = datetime.utcnow()
    if expires_delta:
        expire = now + expires_delta
    else:
        expire = now + timedelta(minutes=60)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(
        to_encode, settings.SECRET_KEY, algorithm=settings.ALGORITHM
    )
    return encoded_jwt


def decode_access_token(token: str) -> Mapping[str, Any]:
    try:
        payload = jwt.decode(
            token, settings.SECRET_KEY, algorithms=[settings.ALGORITHM]
        )
        return payload
    except JWTError as exc:
        raise JWTError("Could not validate credentials") from exc
