from pydantic import BaseModel
from datetime import datetime
from uuid import UUID, uuid4

class ProcessedText(BaseModel):
    id: UUID = uuid4()
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
