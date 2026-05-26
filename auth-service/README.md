# Authentication Service

This service handles user registration, login, JWT access tokens, and basic role-based access control for the Distributed Movie Review System.

## Tech Stack

- FastAPI
- PostgreSQL
- SQLAlchemy
- PyJWT
- passlib + bcrypt
- Docker

## Base URL

http://localhost:8000

## Endpoints

### GET /health

Returns service health status.

Example response:
{
  "status": "ok",
  "service": "auth-service"
}

### POST /auth/register

Creates a new user account.

Request body:
{
  "email": "test@example.com",
  "password": "password123"
}

Example response:
{
  "id": 1,
  "email": "test@example.com",
  "role": "user",
  "is_active": true,
  "created_at": "2026-05-26T01:53:18.151066Z"
}

Possible errors:
- 409 Conflict: email already registered
- 422 Unprocessable Entity: invalid email or password

### POST /auth/login

Logs in an existing user and returns a JWT access token.

Request body:
{
  "email": "test@example.com",
  "password": "password123"
}

Example response:
{
  "access_token": "JWT_TOKEN_HERE",
  "token_type": "bearer"
}

Possible errors:
- 401 Unauthorized: invalid email or password
- 403 Forbidden: inactive user account

### GET /auth/me

Returns the currently authenticated user.

Required header:
Authorization: Bearer JWT_TOKEN_HERE

Example response:
{
  "id": 1,
  "email": "test@example.com",
  "role": "user",
  "is_active": true,
  "created_at": "2026-05-26T01:53:18.151066Z"
}

Possible errors:
- 401 Unauthorized: missing, invalid or expired token

### GET /auth/admin-check

Checks whether the authenticated user has the admin role.

Required header:
Authorization: Bearer JWT_TOKEN_HERE

Example response for admin users:
{
  "status": "ok",
  "message": "Admin access granted",
  "user": "admin@example.com"
}

Possible errors:
- 403 Forbidden: admin role required

## Run with Docker Compose

From the project root:

docker compose up --build -d auth-db auth-service

Check containers:

docker compose ps

Health check:

Invoke-RestMethod http://localhost:8000/health

## Run Tests Locally

From inside the auth-service folder:

pytest -q

Expected result:

2 passed

## Notes

- Passwords are never stored as plain text.
- Passwords are hashed with bcrypt before being saved.
- JWT tokens include the user email as sub and the user role as role.
- The default user role after registration is user.
- Real secrets should be provided through environment variables and should not be committed to Git.
