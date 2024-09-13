from pymongo import MongoClient
from config import settings

client = MongoClient(settings.database_url, username=settings.mongo_initdb_root_username, password=settings.mongo_initdb_root_password, uuidRepresentation='standard')

db = client.mydatabase
users_collection = db.users
