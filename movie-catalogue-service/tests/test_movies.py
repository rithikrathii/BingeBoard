from fastapi.testclient import TestClient  
from app.main import app 

client = TestClient(app)  # creates a test client that sends requests directly to our app


def test_health():
    # verifies the health endpoint is reachable and returns the expected response
    r = client.get("/health")
    assert r.status_code == 200  # 200 means OK
    assert r.json()["status"] == "ok"  # confirms the response body has correct value


def test_list_movies():
    # verifies the list endpoint returns a valid paginated list of movies
    r = client.get("/movies/?limit=5")
    assert r.status_code == 200
    data = r.json()
    assert isinstance(data, list)  # response must be a list
    assert len(data) <= 5  # must respect the limit parameter
    assert "title" in data[0]  # each movie must have a title field


def test_list_movies_pagination():
    # verifies pagination works: page 1 and page 2 must return different movies
    page1 = client.get("/movies/?page=1&limit=5").json()
    page2 = client.get("/movies/?page=2&limit=5").json()
    assert page1[0]["_id"] != page2[0]["_id"]  # first movie on each page must be different


def test_search_movies():
    # verifies search returns results for a known movie title
    r = client.get("/movies/search?q=godfather")
    assert r.status_code == 200
    data = r.json()
    assert isinstance(data, list)  # response must be a list
    assert len(data) > 0  # godfather exists in the dataset so results must not be empty


def test_search_requires_query():
    # verifies search fails gracefully when no query string is provided
    r = client.get("/movies/search")
    assert r.status_code == 422  # 422 means validation error for missing required parameter


def test_filter_by_genre():
    # verifies filter correctly returns only movies matching the requested genre
    r = client.get("/movies/filter?genre=Drama")
    assert r.status_code == 200
    data = r.json()
    # every returned movie must have Drama in its genres array
    assert all("Drama" in m["genres"] for m in data if "genres" in m)


def test_filter_by_year():
    # verifies year range filter returns only movies within the specified range
    r = client.get("/movies/filter?year_min=2000&year_max=2005")
    assert r.status_code == 200
    data = r.json()
    # every returned movie must have a year between 2000 and 2005 inclusive
    assert all(2000 <= m["year"] <= 2005 for m in data if "year" in m)


def test_list_genres():
    # verifies genres endpoint returns a list containing known genres
    r = client.get("/movies/genres")
    assert r.status_code == 200
    data = r.json()
    assert isinstance(data, list)  
    assert "Drama" in data  # Drama is a known genre in sample_mflix so it must appear


def test_get_movie_by_id():
    # verifies a movie can be fetched by its ID
    movies = client.get("/movies/?limit=1").json()  # fetch one movie to get a real ID
    movie_id = movies[0]["_id"]  # extract its ID
    r = client.get(f"/movies/{movie_id}")  # fetch that specific movie
    assert r.status_code == 200
    assert r.json()["_id"] == movie_id  # response must be the same movie we asked for


def test_get_movie_invalid_id():
    # verifies an invalid ID returns 400 Bad Request
    r = client.get("/movies/invalidid123")
    assert r.status_code == 400  # ObjectId conversion fails


def test_get_movie_not_found():
    # verifies a valid but nonexistent ID returns 404 Not Found
    r = client.get("/movies/000000000000000000000000")  # valid ObjectId format but no movie has this ID
    assert r.status_code == 404