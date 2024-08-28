from fastapi import FastAPI
from modules.routes import image_routes, user, translation, summarization, smart_search,speech2text, genQA
import os
app = FastAPI()
import sys
import warnings

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

# Include routers
app.include_router(user.router)
# app.include_router(translation.router)
# app.include_router(summarization.router)
# app.include_router(smart_search.router)
# app.include_router(speech2text.router)
# app.include_router(genQA.router)
app.include_router(image_routes.router)


if __name__ == "__main__":
    import uvicorn
    warnings.filterwarnings("ignore")
    uvicorn.run(app, host="0.0.0.0", port=8000)
