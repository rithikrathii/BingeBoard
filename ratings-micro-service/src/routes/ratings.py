from fastapi import APIRouter, HTTPException, Depends
from bson import ObjectId
from src.db import ratings_collection
from src.models.rating import RatingCreate
from src.auth import get_current_user
from datetime import datetime, timezone




import logging
logger = logging.getLogger(__name__)

router = APIRouter()

def serialize_rating(r):
    return {
        "id": str(r["_id"]),
        "movie_id": str(r["movie_id"]),
        "user_id": str(r["user_id"]),
        "user_name": r.get("user_name", ""),
        "rating": r["rating"],
        "created_at": r["created_at"],
        "updated_at": r["updated_at"]
    }

@router.get("/{movie_id}")
def get_ratings(movie_id: str):
    logger.info(f"Fetching ratings for movie {movie_id}")
    try:
        mid = ObjectId(movie_id)
    except Exception:
        logger.error(f"Invalid movie_id format: {movie_id}")
        raise HTTPException(status_code=400, detail="Invalid movie_id format")

    ratings = list(ratings_collection.find({"movie_id": mid}))
    logger.info(f"Found {len(ratings)} ratings for movie {movie_id}")
    return [serialize_rating(r) for r in ratings]

@router.get("/{movie_id}/average")
def get_average_rating(movie_id: str):
    logger.info(f"Fetching average rating for movie {movie_id}")
    try:
        mid = ObjectId(movie_id)
    except Exception:
        logger.error(f"Invalid movie_id format: {movie_id}")
        raise HTTPException(status_code=400, detail="Invalid movie_id format")

    pipeline = [
        {"$match": {"movie_id": mid}},
        {"$group": {
            "_id": "$movie_id",
            "average": {"$avg": "$rating"},
            "total": {"$sum": 1}
        }}
    ]
    result = list(ratings_collection.aggregate(pipeline))
    logger.info(f"Average rating for movie {movie_id}: {result}")
    if not result:
        return {"movie_id": movie_id, "average": None, "total": 0}
    return {
        "movie_id": movie_id,
        "average": round(result[0]["average"], 2),
        "total": result[0]["total"]
    }

@router.post("")
def add_rating(data: RatingCreate, user=Depends(get_current_user)):
    logger.info(f"User {user['user_id']} adding rating for movie {data.movie_id}")
    try:
        mid = ObjectId(data.movie_id)
    except Exception:
        logger.error(f"Invalid movie_id format: {data.movie_id}")
        raise HTTPException(status_code=400, detail="Invalid movie_id format")

    existing = ratings_collection.find_one({
        "movie_id": mid,
        "user_id": ObjectId(user["user_id"])
    })
    if existing:
        logger.warning(f"User {user['user_id']} already rated movie {data.movie_id}")
        raise HTTPException(status_code=409, detail="You already rated this movie")

    doc = {
        "movie_id": mid,
        "user_id": ObjectId(user["user_id"]),
        "user_name": user["user_name"],
        "rating": data.rating,
        "created_at": datetime.now(timezone.utc),
        "updated_at": datetime.now(timezone.utc)
    }
    result = ratings_collection.insert_one(doc)
    doc["_id"] = result.inserted_id
    logger.info(f"Rating added successfully for movie {data.movie_id}")
    return serialize_rating(doc)

@router.put("/{rating_id}")
def update_rating(rating_id: str, data: RatingCreate, user=Depends(get_current_user)):
    logger.info(f"User {user['user_id']} updating rating {rating_id}")
    try:
        rid = ObjectId(rating_id)
    except Exception:
        logger.error(f"Invalid rating_id format: {rating_id}")
        raise HTTPException(status_code=400, detail="Invalid rating_id format")

    existing = ratings_collection.find_one({"_id": rid})
    if not existing:
        logger.warning(f"Rating {rating_id} not found")
        raise HTTPException(status_code=404, detail="Rating not found")
    if str(existing["user_id"]) != user["user_id"]:
        logger.warning(f"User {user['user_id']} tried to update rating they don't own")
        raise HTTPException(status_code=403, detail="Not your rating")

    ratings_collection.update_one(
        {"_id": rid},
        {"$set": {"rating": data.rating, "updated_at": datetime.now(timezone.utc)}}
    )
    updated = ratings_collection.find_one({"_id": rid})
    logger.info(f"Rating {rating_id} updated successfully")
    return serialize_rating(updated)

@router.delete("/{rating_id}")
def delete_rating(rating_id: str, user=Depends(get_current_user)):
    logger.info(f"User {user['user_id']} deleting rating {rating_id}")
    try:
        rid = ObjectId(rating_id)
    except Exception:
        logger.error(f"Invalid rating_id format: {rating_id}")
        raise HTTPException(status_code=400, detail="Invalid rating_id format")

    existing = ratings_collection.find_one({"_id": rid})
    if not existing:
        logger.warning(f"Rating {rating_id} not found")
        raise HTTPException(status_code=404, detail="Rating not found")
    if str(existing["user_id"]) != user["user_id"]:
        logger.warning(f"User {user['user_id']} tried to delete rating they don't own")
        raise HTTPException(status_code=403, detail="Not your rating")

    ratings_collection.delete_one({"_id": rid})
    logger.info(f"Rating {rating_id} deleted successfully")
    return {"message": "Rating deleted successfully"}
