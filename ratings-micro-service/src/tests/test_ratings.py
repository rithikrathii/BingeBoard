import pytest
from fastapi.testclient import TestClient
from src.main import app

client = TestClient(app)

MOVIE_ID = "573a1390f29313caabcd446f"
FAKE_ID = "aaaaaaaaaaaaaaaaaaaaaaaa"
TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1OWI5OWRiYmNmYTlhMzRkY2Q3ODg1YzMiLCJuYW1lIjoiVGhlb24gR3JleWpveSIsImV4cCI6MTc4MDE0NzkzM30.oaoLbEyGvp_bRZMMOlRF2riS2l8C9KmVloiiGu13238"
AUTH_HEADER = {"Authorization": f"Bearer {TEST_TOKEN}"}

def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"

def test_get_ratings_for_movie():
    response = client.get(f"/ratings/{MOVIE_ID}")
    assert response.status_code == 200
    assert isinstance(response.json(), list)

def test_get_ratings_invalid_id():
    response = client.get("/ratings/notanid")
    assert response.status_code == 400

def test_get_average_rating():
    response = client.get(f"/ratings/{MOVIE_ID}/average")
    assert response.status_code == 200
    data = response.json()
    assert "average" in data
    assert "total" in data

def test_get_average_no_ratings():
    response = client.get(f"/ratings/{FAKE_ID}")
    assert response.status_code == 200
    assert response.json() == []

def test_post_rating_no_auth():
    response = client.post("/ratings", json={
        "movie_id": MOVIE_ID,
        "rating": 3.0
    })
    assert response.status_code == 401

def test_post_rating_invalid_range():
    response = client.post("/ratings", json={
        "movie_id": MOVIE_ID,
        "rating": 9.0
    }, headers=AUTH_HEADER)
    assert response.status_code == 422

def test_post_rating_duplicate():
    response = client.post("/ratings", json={
        "movie_id": MOVIE_ID,
        "rating": 3.0
    }, headers=AUTH_HEADER)
    assert response.status_code == 409
