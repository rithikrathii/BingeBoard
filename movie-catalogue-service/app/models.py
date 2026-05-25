from pydantic import BaseModel, Field
from typing import Optional, List

class Movie(BaseModel):
    id: Optional[str] = Field(default=None, alias="_id")
    title: str
    year: int
    genres: Optional[List[str]] = None
    cast: Optional[List[str]] = None
    poster: Optional[str] = None
    directors: Optional[List[str]] = None
    runtime: Optional[int] = None
    languages: Optional[List[str]] = None
    rated: Optional[str] = None
    plot: Optional[str] = None
    
    class Config:
        populate_by_name = True  
  

  