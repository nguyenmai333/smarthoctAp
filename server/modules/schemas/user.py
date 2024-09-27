from pydantic import BaseModel
from typing import List, Optional

class TextRequest(BaseModel):
    text: str
class TextListRequest(BaseModel):
    texts: list


class Question(BaseModel):
    question: str
    context_id: str 

class QAResponse(BaseModel):
    answer: str
    
class Node(BaseModel):
    content: str
    childs: List["Node"] = [] 

class MindMapResponse(BaseModel):
    root: Node

Node.model_rebuild()