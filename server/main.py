import warnings
warnings.filterwarnings("ignore")
from fastapi import FastAPI
from modules.routes import image_routes, user, translation, summarization, smart_search,speech2text, genQA,createMindmap, get_seq2mcq
import os
app = FastAPI()
import sys

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

# Include routers
app.include_router(user.router)
app.include_router(translation.router)
app.include_router(summarization.router)
app.include_router(smart_search.router)
app.include_router(speech2text.router)
app.include_router(genQA.router)
app.include_router(image_routes.router)
app.include_router(createMindmap.router)
app.include_router(seq2mcq.router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
