from fastapi import FastAPI
from modules.routes import user, translation, summarization, smart_search,speech2text, genQA
import os
app = FastAPI()
import sys

# Add the parent directory to sys.path
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

# Include routers
app.include_router(user.router)
app.include_router(translation.router)
app.include_router(summarization.router)
app.include_router(smart_search.router)
app.include_router(speech2text.router)app.include_router(genQA.router)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
