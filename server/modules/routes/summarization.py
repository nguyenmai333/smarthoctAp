from fastapi import APIRouter
from modules.schemas.user import TextRequest
from modules.services.loader import envit5_tokenizer,envit5_model,mbart_tokenizer,mbart_model

router = APIRouter()


@router.post("/summarize/")
async def summarize(request: TextRequest):
    input_text = request.text
    
    inputs = envit5_tokenizer.encode(input_text, return_tensors="pt", max_length=512, truncation=True)
    translated_outputs = envit5_model.generate(inputs, max_length=512)
    translated_text = envit5_tokenizer.decode(translated_outputs[0], skip_special_tokens=True)
    
    summary_inputs = mbart_tokenizer.encode(translated_text, return_tensors="pt", max_length=1024, truncation=True)
    summary_outputs = mbart_model.generate(summary_inputs, max_length=150, min_length=30, length_penalty=2.0, num_beams=4, early_stopping=True)
    summary_text = mbart_tokenizer.decode(summary_outputs[0], skip_special_tokens=True)
    
    summary_inputs_vi = envit5_tokenizer.encode(summary_text, return_tensors="pt", max_length=512, truncation=True)
    translated_summary_outputs = envit5_model.generate(summary_inputs_vi, max_length=512)
    translated_summary_text = envit5_tokenizer.decode(translated_summary_outputs[0], skip_special_tokens=True)
    
    return {"summary_text": translated_summary_text}
