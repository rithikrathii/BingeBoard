from fastapi import APIRouter, Query, HTTPException
from app.database import movie_collection
from typing import List
from bson import ObjectId

# API endpoints
router = APIRouter()


# helper function that converts ObjectId _id into str
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
def search_movies(
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
    results_sorted = results_unsorted.sort([("score", {"$meta": "textScore"})])

    # displays results based on current page and limit
    results_paginated = results_sorted.skip(offset).limit(limit)
    return [serialize(m) for m in results_paginated]


# filters movies
@router.get("/filter")
def filter_movies(
    genre: str = None,
    year_min: int = None,
    year_max: int = None,
    rated: str = None,
    language: str = None,
    page: int = 1,
    limit: int = Query(10, le=50),
):
    """
    Args:
        genre (str, optional): Genre of the movie. Defaults to None.
        year_min (int, optional): Minimum release year. Defaults to None.
        year_max (int, optional): Maximum release year. Defaults to None.
        rated (str, optional): Motion picture rating. Defaults to None.
        language (str, optional): Language of the movie. Defaults to None.
        page (int, optional): Current page. Defaults to 1.
        limit (int, optional): Number of movies per page. Defaults to 10, max 50.

    Returns:
        List: List of movies matching the filters. If none is given, acts like list_movies().
    """
    query = {}
    year_condition = {}  # to check if both year_min and year_max is given

    # searches each parameter in corresponding field in sample_mflix and adds them to query
    if genre is not None:
        query["genres"] = {
            "$in": [genre]
        }  # selects only the arrays that contain genre in genres keyword

    if year_min is not None:
        year_condition["$gte"] = (
            year_min  # greater than or equal to year_min, query={"year":{"$gte": year_min}}
        )
    if year_max is not None:
        year_condition["$lte"] = (
            year_max  # less than or equal to year_max, query={"year":{"$lte": year_max}}
        )
    if year_condition:
        query["year"] = (
            year_condition  # query={"year": {"$gte": year_min, "$lte": year_max}}
        )

    if rated is not None:
        query["rated"] = rated

    if language is not None:
        query["languages"] = {"$in": [language]}

    offset = (page - 1) * limit
    results = movie_collection.find(query).skip(offset).limit(limit)
    return [serialize(m) for m in results]
