from fastapi import FastAPI
from dotenv import load_dotenv
import logging
import os

load_dotenv()

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s — %(message)s"
)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="Ratings & Reviews Service",
    description="Microservice for movie ratings and reviews",
    version="1.0.0"
)

from src.routes import ratings, reviews
app.include_router(ratings.router, prefix="/ratings", tags=["ratings"])
app.include_router(reviews.router, prefix="/reviews", tags=["reviews"])

@app.get("/health")
def health_check():
    logger.info("Health check called")
    return {"status": "ok", "service": "ratings-reviews"}
