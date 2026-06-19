import logging
from fastapi import FastAPI
from app.routers.movies import router as movies_router
from pythonjsonlogger import json as jsonlogger

logger = logging.getLogger()    # gets the root
handler = logging.StreamHandler()   # sends log output to stdout
formatter = jsonlogger.JsonFormatter(
    '%(asctime)s %(name)s %(levelname)s %(message)s'
)
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.INFO)

# registers all movie endpoints under the /movies
app = FastAPI(
    title="Movie Catalogue Service",
    description="Manages movie metadata, search and filtering",
    version="1.0.0"
)

app.include_router(movies_router, prefix="/movies", tags=["movies"])

@app.get("/health")
def health_check():
    # used by docker-compose healthcheck to verify the service is running
    return {"status": "ok", "service": "movie-catalogue"}