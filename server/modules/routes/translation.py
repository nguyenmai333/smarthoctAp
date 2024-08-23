from fastapi import APIRouter
from modules.schemas.user import TextRequest
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
import pkg_resources
router = APIRouter()

envit5_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules.routes', 'pretrain_models/envit5-translation'))
envit5_model = AutoModelForSeq2SeqLM.from_pretrained(pkg_resources.resource_filename('modules.routes', 'pretrain_models/envit5-translation'))

@router.post("/translate-vi-to-en/")
async def translate_vi_to_en(request: TextRequest):
    input_text = request.text
    inputs = envit5_tokenizer.encode(input_text, return_tensors="pt", max_length=512, truncation=True)
    translated_outputs = envit5_model.generate(inputs, max_length=512)
    translated_text = envit5_tokenizer.decode(translated_outputs[0], skip_special_tokens=True)
    return {"translated_text": translated_text}

@router.post("/translate-en-to-vi/")
async def translate_en_to_vi(request: TextRequest):
    input_text = request.text
    summary_inputs = envit5_tokenizer.encode(input_text, return_tensors="pt", max_length=512, truncation=True)
    translated_summary_outputs = envit5_model.generate(summary_inputs, max_length=512)
    translated_summary_text = envit5_tokenizer.decode(translated_summary_outputs[0], skip_special_tokens=True)
    return {"translated_summary_text": translated_summary_text}