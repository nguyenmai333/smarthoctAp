from fastapi import APIRouter, Depends, HTTPException
from modules.core.auth import get_current_user
from modules.models.user import UserInDB
from modules.services.smart_search_func import re_rank_results
from transformers import AutoTokenizer, AutoModel
import pkg_resources
router = APIRouter()
tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules.pretrain_models', 'phobert-base'))
model = AutoModel.from_pretrained(pkg_resources.resource_filename('modules.pretrain_models', 'phobert-base'))

@router.post("/smart-search/")
async def smartSearch(query: str, current_user: UserInDB = Depends(get_current_user)):
    saved_texts = [entry.text for entry in current_user.processed_texts]
    
    if not saved_texts:
        raise HTTPException(status_code=404, detail="No saved texts found for the user.")
    
    reranked_results = re_rank_results(query, saved_texts, tokenizer, model)
    
    top_5_results = reranked_results[:5]
    
    return {"query": query, "top_5_texts": top_5_results}
