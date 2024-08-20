from pydantic import BaseModel

class ImageResponse(BaseModel):
    _id: str
    message: str

class TextDetectionResponse(BaseModel):
    _id: str
    image_id: str
    detected_text: str
    message: str
