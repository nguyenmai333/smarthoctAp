from fastapi import APIRouter, UploadFile, File, Depends,HTTPException
from modules.core.auth import get_current_user
from modules.models.user import UserInDB
from modules.core.database import users_collection
from modules.models.user import ProcessedText
from datetime import datetime
from modules.services.loader import phoWhisper_transcriber
from uuid import uuid4
import os


router = APIRouter()


@router.post("/transcribe/")
async def transcribe_audio(file: UploadFile = File(...), current_user: UserInDB = Depends(get_current_user)):
    try:
        audio_data = await file.read()
        
        temp_audio_path = "temp_audio.wav"
        with open(temp_audio_path, "wb") as f:
            f.write(audio_data)
        output = phoWhisper_transcriber(temp_audio_path)
        processed_entry = ProcessedText(id=uuid4(), text=output['text'], date=datetime.now())
        current_user.processed_texts.append(processed_entry)
        
        await users_collection.update_one(
            {"username": current_user.username},
            {"$set": {"processed_texts": [entry.model_dump() for entry in current_user.processed_texts]}}
        )
        os.remove(temp_audio_path)
        
        return {"text": output['text']}
    
    except Exception as e:
        # Đảm bảo xóa tệp âm thanh tạm thời nếu có lỗi
        if os.path.exists(temp_audio_path):
            os.remove(temp_audio_path)
        raise HTTPException(status_code=500, detail=str(e))