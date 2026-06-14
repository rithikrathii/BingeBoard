import pytest
from fastapi.testclient import TestClient
from src.main import app

client = TestClient(app)

MOVIE_ID = "573a1390f29313caabcd446f"
FAKE_ID = "aaaaaaaaaaaaaaaaaaaaaaaa"
TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1OWI5OWRiYmNmYTlhMzRkY2Q3ODg1YzMiLCJuYW1lIjoiVGhlb24gR3JleWpveSIsImV4cCI6MTc4MDE0NzkzM30.oaoLbEyGvp_bRZMMOlRF2riS2l8C9KmVloiiGu13238"
AUTH_HEADER = {"Authorization": f"Bearer {TEST_TOKEN}"}

def test_get_reviews_for_movie():
    response = client.get(f"/reviews/{MOVIE_ID}")
    assert response.status_code == 200
    assert isinstance(response.json(), list)

def test_get_reviews_invalid_id():
    response = client.get("/reviews/notanid")
    assert response.status_code == 400

def test_get_single_review_not_found():
    response = client.get(f"/reviews/single/{FAKE_ID}")
    assert response.status_code == 404

def test_post_review_no_auth():
    response = client.post("/reviews", json={
        "movie_id": MOVIE_ID,
        "text": "Great movie!"
    })
    assert response.status_code == 401

def test_post_review_empty_text():
    response = client.post("/reviews", json={
        "movie_id": MOVIE_ID,
        "text": ""
    }, headers=AUTH_HEADER)
    assert response.status_code == 422

def test_post_review_duplicate():
    response = client.post("/reviews", json={
        "movie_id": MOVIE_ID,
        "text": "Another review attempt"
    }, headers=AUTH_HEADER)
    assert response.status_code == 409

def test_delete_review_not_found():
    response = client.delete(f"/reviews/{FAKE_ID}", headers=AUTH_HEADER)
    assert response.status_code == 404

def test_delete_review_no_auth():
    response = client.delete(f"/reviews/{FAKE_ID}")
    assert response.status_code == 401
