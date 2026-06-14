# Ratings & Reviews Service

Microservice for movie ratings and reviews. Part of the Distributed Movie Review System project, Group 5, Summer Semester 2026.

## Stack
- Python + FastAPI
- MongoDB
- Docker
- JWT authentication

## Run locally
cp .env.sample .env
uvicorn src.main:app --reload --port 8001

## Run with Docker
cp .env.sample .env
docker compose up -d --build

## Endpoints
GET    /ratings/{movie_id}           all ratings for a movie
GET    /ratings/{movie_id}/average   average rating
POST   /ratings                      add a rating (auth required)
PUT    /ratings/{id}                 update your rating (auth required)
DELETE /ratings/{id}                 delete your rating (auth required)

GET    /reviews/{movie_id}           all reviews for a movie
GET    /reviews/single/{id}          single review
POST   /reviews                      write a review (auth required)
PUT    /reviews/{id}                 edit your review (auth required)
DELETE /reviews/{id}                 delete your review (auth required)

GET    /health                       health check

## Tests
pytest src/tests/ -v

## Contribution
| Name           | Component                       |
|----------------|---------------------------------|
| Farhan Tahmid  | Ratings & Reviews Microservice  |
