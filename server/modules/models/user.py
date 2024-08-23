from pydantic import BaseModel
from datetime import datetime

class ProcessedText(BaseModel):
    text: str
    date: datetime

class User(BaseModel):
    username: str
    email: str
    full_name: str
    hashed_password: str

class UserInDB(User):
    hashed_password: str
    processed_texts: list[ProcessedText] = []
