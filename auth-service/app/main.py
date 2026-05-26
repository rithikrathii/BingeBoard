import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from pythonjsonlogger import json as jsonlogger

from app.database import Base, engine
from app.routers.auth import router as auth_router


logger = logging.getLogger()
handler = logging.StreamHandler()
formatter = jsonlogger.JsonFormatter(
    "%(asctime)s %(name)s %(levelname)s %(message)s"
)
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.INFO)


@asynccontextmanager
async def lifespan(app: FastAPI):
    Base.metadata.create_all(bind=engine)
    logger.info("Authentication Service started and database tables checked")
    yield


app = FastAPI(
    title="Authentication Service",
    description="Handles user registration, login, JWT tokens and role-based access control",
    version="1.0.0",
    lifespan=lifespan
)

app.include_router(auth_router, prefix="/auth", tags=["auth"])


@app.get("/health")
def health_check():
    return {
        "status": "ok",
        "service": "auth-service"
    }
