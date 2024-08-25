from fastapi import APIRouter
from modules.schemas.user import TextRequest
from modules.services.loader import envit5_tokenizer,envit5_model
from modules.services.process_func import translate_text
router = APIRouter()

@router.post("/translate/")
async def translate(request: TextRequest):
    return {"translated_text": translate_text(request.text, envit5_model, envit5_tokenizer)}
