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
    content: str = Field(description="Nội dung sau khi tóm tắt")
class TestResponseTranslate(BaseModel):
    content: str = Field(description="Nội dung sau khi dich")
    
class _1thNode(BaseModel):
    main_content: str = Field(description="Topic phụ của mindmap, ngắn gọn")
    childs : Optional[List[str]] = Field(description="Danh sách các ý mang đầy đủ nghĩa, diễn giải, giải thích cho topic phụ")


class Mindmap(BaseModel):
    main_topic: str = Field(description="Topic chính của mindmap, ngắn gọn")
    Childs: List[_1thNode] =Field( description="Danh sách các nội dung con diễn giải, giải thích cho topic chính")

    


class mcq(BaseModel):
    Question: str = Field(description="Câu hỏi")
    Distractor: List[str] = Field(description="danh sách 3 câu trả lời sai của câu hỏi")
    Answer : str = Field(description="Câu trả lời đúng của câu hỏi")
    
class mcqOb(BaseModel):
    result: List[mcq] = Field(description="Danh sách các câu hỏi trắc nghiệm")

