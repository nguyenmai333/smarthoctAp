from pydantic import BaseModel

class TextRequest(BaseModel):
    text: str


class Question(BaseModel):
    question: str
    context_id: str 

class QAResponse(BaseModel):
    answer: str