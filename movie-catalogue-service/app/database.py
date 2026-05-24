from pymongo import MongoClient
from dotenv import load_dotenv
import os

# locates .env file
load_dotenv()

# reads uri string
uri=os.getenv("MONGO_URI")

# connects to dataabase
client = MongoClient(uri)
database = client["sample_mflix"]
movie_collection = database["movies"]
