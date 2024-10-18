from langchain_core.pydantic_v1 import BaseModel
from typing import List, Optional,Field

class TextRequest(BaseModel):
    text: str

class TextRequestSummarize(BaseModel):
    text: str
    rito: float
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
    content: str = Field(description="Content after summarizing")
    

class Node(BaseModel):
    content : List[str] = Field(description="list of contents (level 2), explaining to parent node content")
class _1thNode(BaseModel):
    main_content: str = Field(description="Main content level 1")
    childs : Optional[List[Node]] = Field(description="list of subcontents")


class Mindmap(BaseModel):
    main_topic: str = Field(description="main topic of mindmap")
    Childs: List[_1thNode] =Field( description="list of subcontents")
    
class mcq(BaseModel):
    Question: str = Field(description="Question")
    Distractor: List[str] = Field(description="List of 3 wrong content, not true answer of the question")
    Answer : str = Field(description="Right answer of the question")
        
class mcqOb(BaseModel):
    result: List[mcq] = Field(description="list of question and multiple choice answers")

Node.model_rebuild()
