from fastapi import APIRouter, Depends, HTTPException
from modules.core.auth import get_current_user
from modules.models.user import UserInDB
from modules.services.process_func import re_rank_results
from modules.services.loader import Bert_tokenizer, Bert_model
router = APIRouter()

@router.get("/smart-search")
async def smartSearch(query: str, current_user: UserInDB = Depends(get_current_user)):
    saved_texts = [entry.text for entry in current_user.processed_texts]
    # print(saved_texts)
    
    if not saved_texts:
        raise HTTPException(status_code=404, detail="No saved texts found for the user.")
    
    reranked_results = re_rank_results(query, saved_texts, Bert_tokenizer, Bert_model)
    
    top_5_results = reranked_results[:5]
    
    return {"query": query, "top_5_texts": top_5_results}