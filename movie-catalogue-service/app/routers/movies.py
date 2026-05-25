from fastapi import APIRouter, Query, HTTPException
from app.database import movie_collection
from typing import List
from bson import ObjectId

router = APIRouter()


# converts ObjectId _id into str
def serialize(movie):
    movie["_id"] = str(movie["_id"])
    return movie


# lists movies
@router.get("/")
def list_movies(page: int = 1, limit: int = Query(10, le=50)):
    """
    page: page shown
    limit: number of movies listed per page, default=10, max=50
    """
    offset = (page - 1) * limit  # skips the first page-1 pages on the current page

    # fetches movies from the collection
    movies = movie_collection.find().skip(offset).limit(limit)
    return [serialize(m) for m in movies]  # serializes the movies and returns them
