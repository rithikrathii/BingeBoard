from app.security import (
    create_access_token,
    decode_access_token,
    hash_password,
    verify_password,
)


def test_password_hashing_and_verification():
    hashed_password = hash_password("password123")

    assert hashed_password != "password123"
    assert verify_password("password123", hashed_password) is True
    assert verify_password("wrongpassword", hashed_password) is False


def test_jwt_token_contains_subject_and_role():
    token = create_access_token("test@example.com", "user")
    payload = decode_access_token(token)

    assert payload["sub"] == "test@example.com"
    assert payload["role"] == "user"
    assert "exp" in payload
