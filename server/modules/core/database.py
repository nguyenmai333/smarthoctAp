from motor.motor_asyncio import AsyncIOMotorClient
import os

client = AsyncIOMotorClient(os.getenv("DATABASE_URL"))
db = client.mydatabase
users_collection = db.users
