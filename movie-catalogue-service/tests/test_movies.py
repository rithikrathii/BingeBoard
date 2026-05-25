from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health():
    r = client.get("/health")
    assert r.status_code == 200
    assert r.json()["status"] == "ok"

def test_list_movies():
    r = client.get("/movies/?limit=5")
    assert r.status_code == 200
    data = r.json()
    assert isinstance(data, list)
    assert len(data) <= 5
    assert "title" in data[0]

def test_list_movies_pagination():
    page1 = client.get("/movies/?page=1&limit=5").json()
    page2 = client.get("/movies/?page=2&limit=5").json()
    assert page1[0]["_id"] != page2[0]["_id"]

def test_search_movies():
    r = client.get("/movies/search?q=godfather")
    assert r.status_code == 200
    data = r.json()
    assert isinstance(data, list)
    assert len(data) > 0

def test_search_requires_query():
    r = client.get("/movies/search")
    assert r.status_code == 422

def test_filter_by_genre():
    r = client.get("/movies/filter?genre=Drama")
    assert r.status_code == 200
    data = r.json()
    assert all("Drama" in m["genres"] for m in data if "genres" in m)

def test_filter_by_year():
    r = client.get("/movies/filter?year_min=2000&year_max=2005")
    assert r.status_code == 200
    data = r.json()
    assert all(2000 <= m["year"] <= 2005 for m in data if "year" in m)

def test_list_genres():
    r = client.get("/movies/genres")
    assert r.status_code == 200
    data = r.json()
    assert isinstance(data, list)
    assert "Drama" in data

def test_get_movie_by_id():
    movies = client.get("/movies/?limit=1").json()
    movie_id = movies[0]["_id"]
    r = client.get(f"/movies/{movie_id}")
    assert r.status_code == 200
    assert r.json()["_id"] == movie_id

def test_get_movie_invalid_id():
    r = client.get("/movies/invalidid123")
    assert r.status_code == 400

def test_get_movie_not_found():
    r = client.get("/movies/000000000000000000000000")
    assert r.status_code == 404