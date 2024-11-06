from pydantic import BaseModel,Field
from typing import List, Optional

class TextRequest(BaseModel):
    text: str
    
class TextRequestTranslate(BaseModel):
    text: str
    lang:str

class TextRequestSummarize(BaseModel):
    text: str
    ratio: float
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
class TestResponse(BaseModel):
    content: str = Field(description="Content after summarization")
class TestResponseTranslate(BaseModel):
    content: str = Field(description="Nội dung sau khi dich")
    
class _1thNode(BaseModel):
    main_content: str = Field(description="Topic phụ của mindmap, ngắn gọn")
    childs : Optional[List[str]] = Field(description="Danh sách các ý mang đầy đủ nghĩa, diễn giải, giải thích cho topic phụ")


class Mindmap(BaseModel):
    main_topic: str = Field(description="Topic chính của mindmap, ngắn gọn")
    Childs: List[_1thNode] =Field( description="Danh sách các nội dung con diễn giải, giải thích cho topic chính")

    


class mcq(BaseModel):
    Question: str = Field(description="Question")
    Distractor: List[str] = Field(description="List of 3 incorrect answers for the question")
    Answer: str = Field(description="Correct answer for the question")

class mcqOb(BaseModel):
    result: List[mcq] = Field(description="List of multiple-choice questions")
