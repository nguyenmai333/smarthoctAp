from fastapi import APIRouter
from modules.schemas.user import TextRequestSummarize
from modules.services.loader import envit5_tokenizer,envit5_model,mbart_tokenizer,mbart_model
from modules.services.process_func import summarize_text,translate_text
router = APIRouter()


@router.post("/summarize/")
async def summarize(request: TextRequestSummarize):
    input_text = request.text
    input_riot = request.rito
    
    translated_text = translate_text(input_text,envit5_model, envit5_tokenizer)
    length_out = (len(input_text)*input_riot)
    translated_summary_text = summarize_text(translated_text,length_out)
    return {"summary_text": translated_summary_text}