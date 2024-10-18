from langchain_core.pydantic_v1 import BaseModel,Field
from langchain_core.pydantic_v1 import BaseModel as BaseModelv1
from pydantic import BaseModel
from typing import List, Optional

class TextRequest(BaseModel):
    text: str

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
class TestResponse(BaseModelv1):
    content: str = Field(description="Content after summarizing")
    

class _1thNode(BaseModelv1):
    main_content: str = Field(description="subtopic of mindmap, concise, comprehensive")
    childs : Optional[List[str]] = Field(description="list of subcontents, explaining subtopic")



class Mindmap(BaseModelv1):
    main_topic: str = Field(description="main topic of mindmap")
    Childs: List[_1thNode] =Field( description="list of subcontents")
    
class mcq(BaseModelv1):
    Question: str = Field(description="Question")
    Distractor: List[str] = Field(description="List of 3 wrong content, not true answer of the question")
    Answer : str = Field(description="Right answer of the question")
        
class mcqOb(BaseModelv1):
    result: List[mcq] = Field(description="list of question and multiple choice answers")

