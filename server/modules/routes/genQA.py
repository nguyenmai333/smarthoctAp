# from fastapi import APIRouter, HTTPException, Depends
# from modules.core.auth import get_current_user
# from modules.models.user import UserInDB
# from modules.schemas import QAResponse,Question
# from modules.services.process_func import answer_question
# router = APIRouter()



# @router.post("/chatbotQA/", response_model=QAResponse)
# async def get_answer(question: Question, current_user: UserInDB = Depends(get_current_user)):
#     if not current_user.processed_texts:
#       raise HTTPException(status_code=404, detail="No processed texts found for the user.")
    
#     context_id = question.context_id
#     context_text = next((item['text'] for item in current_user.processed_texts if item['id'] == context_id), None)
    
#     if context_text == "Context not found.":
#         raise HTTPException(status_code=404, detail="Context not found.")
    
#     answer = answer_question(question.question, context_text)
#     return QAResponse(answer=answer)
