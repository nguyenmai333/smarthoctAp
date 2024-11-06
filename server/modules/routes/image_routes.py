from fastapi import APIRouter, UploadFile, File, Depends,HTTPException
from modules.core.auth import get_current_user
from modules.models.user import UserInDB,ProcessedText
from modules.core.database import users_collection
from modules.services.ocr import OCRDetector
from datetime import datetime
import io 
from uuid import uuid4
from PIL import Image,ImageOps
import numpy as np
router = APIRouter()

@router.post('/detect-text/')
async def detect_text(image: UploadFile = File(...), current_user: UserInDB = Depends(get_current_user)):
    try:
        # Đọc và mở ảnh từ UploadFile
        image_data = await image.read()
        image = Image.open(io.BytesIO(image_data))
        image = np.array(ImageOps.exif_transpose((image)))
        
        # Khởi tạo OCRDetector và xử lý ảnh
        detector = OCRDetector(gpu=True)
        _, text_resp = detector.process_image(image)
        
        # Thêm kết quả vào processed_texts của user
        processed_entry = ProcessedText(id=uuid4(), text=text_resp, date=datetime.now())
        current_user.processed_texts.append(processed_entry)
        
        # Cập nhật thông tin người dùng trong cơ sở dữ liệu
        users_collection.update_one(
            {"username": current_user.username},
            {"$set": {"processed_texts": [entry.model_dump() for entry in current_user.processed_texts]}}
        )
        
        return {"text": text_resp}
    
    except Exception as e:
        # Xử lý lỗi và trả về thông báo lỗi
        raise HTTPException(status_code=500, detail=str(e))