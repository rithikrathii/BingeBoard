from jose import jwt
from datetime import datetime, timedelta, timezone

SECRET = "secrettesting123"
ALGORITHM = "HS256"

payload = {
    "sub": "59b99dbbcfa9a34dcd7885c3",
    "name": "Theon Greyjoy",
    "exp": datetime.now(timezone.utc) + timedelta(days=7)
}

token = jwt.encode(payload, SECRET, algorithm=ALGORITHM)
print("Your test token:")
print(token)
