from app.core.security import create_access_token, decode_access_token


def test_create_and_decode_access_token():
    token = create_access_token("user-123")
    payload = decode_access_token(token)

    assert payload["sub"] == "user-123"
    assert "exp" in payload


def test_decode_invalid_token_raises():
    invalid_token = "not-a-valid-token"

    try:
        decode_access_token(invalid_token)
        assert False, "Expected JWTError for invalid token"
    except Exception as exc:
        assert "Could not validate credentials" in str(exc)
