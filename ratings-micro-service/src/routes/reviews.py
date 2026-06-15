from fastapi import APIRouter, HTTPException, Depends
from bson import ObjectId
from src.db import reviews_collection
from src.models.review import ReviewCreate
from src.auth import get_current_user
from datetime import datetime, timezone

import logging
logger = logging.getLogger(__name__)

router = APIRouter()

def serialize_review(r):
    return {
        "id": str(r["_id"]),
        "movie_id": str(r["movie_id"]),
        "user_id": str(r["user_id"]),
        "user_name": r.get("user_name", ""),
        "text": r["text"],
        "rating": r.get("rating", 0),
        "created_at": r["created_at"],
        "updated_at": r["updated_at"]
    }

@router.get("/{movie_id}")
def get_reviews(movie_id: str):
    logger.info(f"Fetching reviews for movie {movie_id}")
    try:
        mid = ObjectId(movie_id)
    except Exception:
        logger.error(f"Invalid movie_id format: {movie_id}")
        raise HTTPException(status_code=400, detail="Invalid movie_id format")

    reviews = list(reviews_collection.find({"movie_id": mid}))
    logger.info(f"Found {len(reviews)} reviews for movie {movie_id}")
    return [serialize_review(r) for r in reviews]

@router.get("/single/{review_id}")
def get_review(review_id: str):
    logger.info(f"Fetching single review {review_id}")
    try:
        rid = ObjectId(review_id)
    except Exception:
        logger.error(f"Invalid review_id format: {review_id}")
        raise HTTPException(status_code=400, detail="Invalid review_id format")

    review = reviews_collection.find_one({"_id": rid})
    if not review:
        logger.warning(f"Review {review_id} not found")
        raise HTTPException(status_code=404, detail="Review not found")
    return serialize_review(review)

@router.post("")
def add_review(data: ReviewCreate, user=Depends(get_current_user)):
    logger.info(f"User {user['user_id']} adding review for movie {data.movie_id}")
    try:
        mid = ObjectId(data.movie_id)
    except Exception:
        logger.error(f"Invalid movie_id format: {data.movie_id}")
        raise HTTPException(status_code=400, detail="Invalid movie_id format")

    existing = reviews_collection.find_one({
        "movie_id": mid,
        "user_id": user["user_id"]
    })
    if existing:
        logger.warning(f"User {user['user_id']} already reviewed movie {data.movie_id}")
        raise HTTPException(status_code=409, detail="You already reviewed this movie")

    doc = {
        "movie_id": mid,
        "user_id": user["user_id"],
        "user_name": data.user_name if data.user_name else user.get("user_name", "Anonymous"),
        "text": data.text,
        "rating": data.rating if data.rating else 0,
        "created_at": datetime.now(timezone.utc),
        "updated_at": datetime.now(timezone.utc)
    }
    result = reviews_collection.insert_one(doc)
    doc["_id"] = result.inserted_id
    logger.info(f"Review added successfully for movie {data.movie_id}")
    return serialize_review(doc)

@router.put("/{review_id}")
def update_review(review_id: str, data: ReviewCreate, user=Depends(get_current_user)):
    logger.info(f"User {user['user_id']} updating review {review_id}")
    try:
        rid = ObjectId(review_id)
    except Exception:
        logger.error(f"Invalid review_id format: {review_id}")
        raise HTTPException(status_code=400, detail="Invalid review_id format")

    existing = reviews_collection.find_one({"_id": rid})
    if not existing:
        logger.warning(f"Review {review_id} not found")
        raise HTTPException(status_code=404, detail="Review not found")
    if str(existing["user_id"]) != user["user_id"]:
        logger.warning(f"User {user['user_id']} tried to update review they don't own")
        raise HTTPException(status_code=403, detail="Not your review")

    reviews_collection.update_one(
        {"_id": rid},
        {"$set": {"text": data.text, "updated_at": datetime.now(timezone.utc)}}
    )
    updated = reviews_collection.find_one({"_id": rid})
    logger.info(f"Review {review_id} updated successfully")
    return serialize_review(updated)

@router.delete("/{review_id}")
def delete_review(review_id: str, user=Depends(get_current_user)):
    logger.info(f"User {user['user_id']} deleting review {review_id}")
    try:
        rid = ObjectId(review_id)
    except Exception:
        logger.error(f"Invalid review_id format: {review_id}")
        raise HTTPException(status_code=400, detail="Invalid review_id format")

    existing = reviews_collection.find_one({"_id": rid})
    if not existing:
        logger.warning(f"Review {review_id} not found")
        raise HTTPException(status_code=404, detail="Review not found")
    if str(existing["user_id"]) != user["user_id"]:
        logger.warning(f"User {user['user_id']} tried to delete review they don't own")
        raise HTTPException(status_code=403, detail="Not your review")

    reviews_collection.delete_one({"_id": rid})
    logger.info(f"Review {review_id} deleted successfully")
    return {"message": "Review deleted successfully"}