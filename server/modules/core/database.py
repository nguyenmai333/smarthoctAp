from pymongo import MongoClient
from config import settings

client = MongoClient(f'mongodb://{settings.mongo_initdb_root_username}:{settings.mongo_initdb_root_password}@{settings.host}:{settings.port}', uuidRepresentation='standard')

db = client.mydatabase
users_collection = db.users
