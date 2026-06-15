from pydantic import BaseModel, field_validator
from typing import Optional
from datetime import datetime

class ReviewCreate(BaseModel):
    movie_id: str
    text: str
    user_name: Optional[str] = None
    rating: Optional[int] = None

    @field_validator("text")
    @classmethod
    def text_must_not_be_empty(cls, v):
        if not v or not v.strip():
            raise ValueError("Review text cannot be empty")
        return v.strip()

class ReviewResponse(BaseModel):
    id: str
    movie_id: str
    user_id: str
    user_name: str
    text: str
    rating: Optional[int] = None
    created_at: datetime
    updated_at: datetime