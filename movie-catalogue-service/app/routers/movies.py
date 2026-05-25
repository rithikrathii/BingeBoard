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
    Args:
        page (int, optional): Current page. Defaults to 1.
        limit (int, optional): Number of movies per page. Defaults to 10, max 50.

    Returns:
        List: Movies paginated by limit and offset.
    """
    offset = (page - 1) * limit  # skips the first page-1 pages on the current page

    # fetches movies from the collection
    movies = movie_collection.find().skip(offset).limit(limit)
    return [serialize(m) for m in movies]  # serializes the movies and returns them


# search by query string
@router.get("/search")
def search(
    q: str = Query(..., min_length=1), page: int = 1, limit: int = Query(10, le=50)
):
    """
    Args:
        q (str): Search quesry string.
        page (int, optional): Page number to retrive. Defaults to 1.
        limit (int, optional): Number of movies per page. Defaults 10, max 50.

    Returns:
        List: List of movies matching the search query and sorted by relevance.
    """
    offset = (page - 1) * limit

    # filters with search string and relevance
    results_unsorted = movie_collection.find(
        {"$text": {"$search": q}}, {"score": {"$meta": "textScore"}}
    )
    # sorts results by relevance
    results_sorted = (
        results_unsorted.sort([("score", {"$meta": "textScore"})]))
    
    # displays results based on current page and limit
    results_paginated= results_sorted.skip(offset).limit(limit)
    return [serialize(m) for m in results_paginated]
