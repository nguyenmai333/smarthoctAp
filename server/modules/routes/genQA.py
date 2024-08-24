from fastapi import APIRouter, HTTPException, Depends
from pydantic import BaseModel
from transformers import RobertaTokenizer, RobertaForQuestionAnswering
import torch
from modules.core.auth import get_current_user
from modules.models.user import UserInDB

roberta_model_name = "deepset/roberta-base-squad2"
roberta_tokenizer = RobertaTokenizer.from_pretrained(roberta_model_name)
roberta_model = RobertaForQuestionAnswering.from_pretrained(roberta_model_name)

router = APIRouter()

class Question(BaseModel):
    question: str
    context_id: str 

class QAResponse(BaseModel):
    answer: str

def answer_question(question: str, context: str) -> str:
    inputs = roberta_tokenizer(question, context, add_special_tokens=True, return_tensors='pt')
    with torch.no_grad():
        outputs = roberta_model(**inputs)
    answer_start_scores = outputs.start_logits
    answer_end_scores = outputs.end_logits
    answer_start = torch.argmax(answer_start_scores)
    answer_end = torch.argmax(answer_end_scores) + 1
    answer_tokens = roberta_tokenizer.convert_ids_to_tokens(inputs['input_ids'][0][answer_start:answer_end])
    answer = roberta_tokenizer.convert_tokens_to_string(answer_tokens)
    return answer

@router.post("/chatbotQA/", response_model=QAResponse)
async def get_answer(question: Question, current_user: UserInDB = Depends(get_current_user)):
    if not current_user.processed_texts:
      raise HTTPException(status_code=404, detail="No processed texts found for the user.")
    
    context_id = question.context_id
    context_text = next((item['text'] for item in current_user.processed_texts if item['id'] == context_id), None)
    
    if context == "Context not found.":
        raise HTTPException(status_code=404, detail="Context not found.")
    
    answer = answer_question(question.question, context)
    return QAResponse(answer=answer)
