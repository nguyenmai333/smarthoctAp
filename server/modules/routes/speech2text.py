from fastapi import APIRouter, UploadFile, File, Depends
from modules.core.auth import get_current_user
from modules.models.user import UserInDB
from modules.core.database import users_collection
from modules.models.user import ProcessedText
from datetime import datetime
from modules.services.loader import phoWhisper_transcriber


router = APIRouter()


@router.post("/transcribe/")
async def transcribe_audio(file: UploadFile = File(...),current_user: UserInDB = Depends(get_current_user)):
    audio_data = await file.read()
    with open("temp_audio.wav", "wb") as f:
        f.write(audio_data)
    
    output = phoWhisper_transcriber("temp_audio.wav")
    
    processed_entry = ProcessedText(text=output, date=datetime.now())
    current_user.processed_texts.append(processed_entry)
    await users_collection.update_one(
        {"username": current_user.username},
        {"$set": {"processed_texts": [entry.model_dump() for entry in current_user.processed_texts]}}
    )
    
    return {"text": output['text']}