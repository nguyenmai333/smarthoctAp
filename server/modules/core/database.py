from pymongo import MongoClient
from bson.codec_options import CodecOptions

from bson.binary import UuidRepresentation
import os

client = MongoClient(os.getenv("DATABASE_URL"), uuidRepresentation='standard')
db = client.mydatabase
users_collection = db.users
