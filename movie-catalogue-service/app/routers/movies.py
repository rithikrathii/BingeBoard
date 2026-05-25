from fastapi import APIRouter, Query, HTTPException
from app.database import movie_collection
from bson import ObjectId
from typing import List

router = APIRouter()

def serialize(movie) -> dict:
    movie["_id"] = str(movie["_id"])
    return movie

@router.get("/")
def list_movies(page: int = 1, limit: int = Query(20, le=100)):
    skip = (page - 1) * limit
    movies = movie_collection.find().skip(skip).limit(limit)
    return [serialize(m) for m in movies]

@router.get("/search")
def search_movies(
    q: str = Query(..., min_length=1),
    page: int = 1,
    limit: int = Query(20, le=100)
):
    skip = (page - 1) * limit
    results = movie_collection.find(
        {"$text": {"$search": q}},
        {"score": {"$meta": "textScore"}}
    ).sort([("score", {"$meta": "textScore"})]).skip(skip).limit(limit)
    return [serialize(m) for m in results]

@router.get("/filter")
def filter_movies(
    genre: str = None,
    year_min: int = None,
    year_max: int = None,
    rated: str = None,
    page: int = 1,
    limit: int = Query(20, le=100)
):
    query = {}
    if genre:
        query["genres"] = {"$in": [genre]}
    if year_min or year_max:
        query["year"] = {}
        if year_min:
            query["year"]["$gte"] = year_min
        if year_max:
            query["year"]["$lte"] = year_max
    if rated:
        query["rated"] = rated
    skip = (page - 1) * limit
    results = movie_collection.find(query).skip(skip).limit(limit)
    return [serialize(m) for m in results]

@router.get("/genres")
def list_genres():
    genres = movie_collection.distinct("genres")
    return sorted([g for g in genres if g])

@router.get("/{movie_id}")
def get_movie(movie_id: str):
    try:
        oid = ObjectId(movie_id)
    except Exception:
        raise HTTPException(status_code=400, detail="Invalid movie ID")
    movie = movie_collection.find_one({"_id": oid})
    if not movie:
        raise HTTPException(status_code=404, detail="Movie not found")
    return serialize(movie)