import os
from pymongo import MongoClient
from dotenv import load_dotenv

load_dotenv()

MONGO_URI = os.getenv("MONGO_URI")
DB_NAME = os.getenv("DB_NAME", "sample_mflix")

client = MongoClient(MONGO_URI)
db = client[DB_NAME]

ratings_collection = db["ratings"]
reviews_collection = db["reviews"]

def test_connection():
    try:
        client.admin.command("ping")
        print("Connected to MongoDB successfully")
        print(f"Database: {DB_NAME}")
        print(f"Collections: {db.list_collection_names()}")
    except Exception as e:
        print(f"Connection failed: {e}")

if __name__ == "__main__":
    test_connection()
