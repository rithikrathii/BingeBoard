from pydantic import BaseModel, field_validator
from typing import Optional
from datetime import datetime

class RatingCreate(BaseModel):
    movie_id: str
    rating: float

    @field_validator("rating")
    @classmethod
    def rating_must_be_valid(cls, v):
        if v < 0 or v > 5:
            raise ValueError("Rating must be between 0 and 5")
        return round(v, 1)

class RatingResponse(BaseModel):
    id: str
    movie_id: str
    user_id: str
    user_name: str
    rating: float
    created_at: datetime
    updated_at: datetime
