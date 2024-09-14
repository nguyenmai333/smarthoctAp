from pymongo import MongoClient
from config import settings

client = MongoClient(f'mongodb://{settings.username}:{settings.password}@{settings.host}:{settings.port}', uuidRepresentation='standard')

db = client.mydatabase
users_collection = db.users
