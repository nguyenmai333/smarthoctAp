# from fastapi import FastAPI, UploadFile, File, HTTPException
# from pymongo import MongoClient
# from bson import ObjectId
# from pydantic import BaseModel
# import os
# from typing import Optional
# from fastapi.responses import FileResponse
# from ultils.ocr import OCRDetector
# import cv2
# import matplotlib.pyplot as plt

# app = FastAPI()

# client = MongoClient('mongodb://172.26.250.122:27017')
# db = client['text-preprocessing']
# images_collection = db['image']
# texts_collection = db['raw-text']

# UPLOAD_FOLDER = 'uploads'
# if not os.path.exists(UPLOAD_FOLDER):
#     os.makedirs(UPLOAD_FOLDER)

# class ImageResponse(BaseModel):
#     _id: str
#     message: str

# class TextDetectionResponse(BaseModel):
#     _id: str
#     image_id: str
#     detected_text: str
#     message: str

# def text_detect(image_id):
#     detector = OCRDetector()
#     image_path = os.path.join(UPLOAD_FOLDER, image_id + '.png')
#     image, raw_text = detector.process_image(image_path)
#     print("Detected Text:", raw_text)
    
#     # Lưu kết quả nhận diện vào MongoDB
#     text_doc = {
#         '_id': ObjectId(),
#         'image_id': ObjectId(image_id),
#         'detected_text': raw_text
#     }
#     texts_collection.insert_one(text_doc)
    
#     return raw_text

# @app.post('/upload', response_model=ImageResponse)
# async def upload_image(image: UploadFile = File(...)):
#     image_id = str(ObjectId())
#     image_path = os.path.join(UPLOAD_FOLDER, image_id + '.png')
#     with open(image_path, "wb") as buffer:
#         buffer.write(await image.read())
    
#     image_doc = {
#         '_id': ObjectId(image_id),
#         'filename': image.filename,
#         'path': image_path
#     }
#     images_collection.insert_one(image_doc)
    
#     return {"_id": image_id, "message": "Image uploaded successfully"}

# @app.get('/images/{image_id}')
# async def get_image(image_id: str):
#     image_doc = images_collection.find_one({'_id': ObjectId(image_id)})
#     if image_doc:
#         return FileResponse(image_doc['path'])
#     else:
#         raise HTTPException(status_code=404, detail="Image not found")

# @app.delete('/images/{image_id}')
# async def delete_image(image_id: str):
#     image_doc = images_collection.find_one({'_id': ObjectId(image_id)})
#     if image_doc:
#         os.remove(image_doc['path'])
#         images_collection.delete_one({'_id': ObjectId(image_id)})
#         return {"message": "Image deleted successfully"}
#     else:
#         raise HTTPException(status_code=404, detail="Image not found")

# @app.post('/detect-text/{image_id}', response_model=TextDetectionResponse)
# async def detect_text(image_id: str):
#     image_doc = images_collection.find_one({'_id': ObjectId(image_id)})
#     if image_doc:
#         detected_text = text_detect(image_id)
        
#         # Lấy thông tin văn bản từ MongoDB
#         text_doc = texts_collection.find_one({'image_id': ObjectId(image_id)})
        
#         return {
#             "_id": str(text_doc['_id']),
#             "image_id": image_id,
#             "detected_text": text_doc['detected_text'],
#             "message": "Text detected successfully"
#         }
#     else:
#         raise HTTPException(status_code=404, detail="Image not found")

# if __name__ == "__main__":
#     import uvicorn
#     uvicorn.run(app, host='0.0.0.0', port=3550)
