from fastapi import FastAPI
from pydantic import BaseModel
import uvicorn
#pip install fastapi uvicorn
app = FastAPI()

class TextRequest(BaseModel):
    text: str

class QAResponse(BaseModel):
    answer: str

@app.post("/translate/")
async def translate(request: TextRequest):
    return {"translated_text": "This is a mock translation."}

@app.post("/chatbotQA/", response_model=QAResponse)
async def get_answer(question: TextRequest):
    return QAResponse(answer="This is a mock answer.")

@app.post("/detect-text/")
async def detect_text():
    return {"text": "Mock detected text from an image."}

@app.get("/smart-search/")
async def smart_search(query: str):
    return {"query": query, "top_5_texts": ["Mock result 1", "Mock result 2", "Mock result 3", "Mock result 4", "Mock result 5"]}

@app.post("/transcribe/")
async def transcribe_audio():
    return {"text": "Mock transcription text."}

@app.post("/summarize/")
async def summarize(request: TextRequest):
    return {"summary_text": "This is a mock summary."}

if __name__ == "__main__":
    print('hello world! world is u and me is u u is u. Virtual Server Stared')
    uvicorn.run("virtualAPI:app", host="0.0.0.0", port = 8000, reload = True)