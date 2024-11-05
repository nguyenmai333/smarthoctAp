# from fastapi import APIRouter, Depends, HTTPException
# from modules.core.auth import get_current_user
# from modules.models.user import UserInDB
# from modules.services.process_func import re_rank_results
# from modules.schemas.user import TextRequest
# import sys
# import os
# sys.path.append(os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)),'question_generation')))
# # from question_generation.pipelines import pipeline as qpl
# from transformers import pipeline as tpl
# import torch
# import nltk
# from nltk import pos_tag
# from nltk.tokenize import word_tokenize
# from modules.services.loader import mcq_generator as mcq_gen


# ##--init--
# nltk.download('averaged_perceptron_tagger')
# nltk.download('punkt')

# router = APIRouter()

# @router.post("/seq2mcq")
# async def get_seq2mcq(TEXT: TextRequest):
#     return  {
#         'mcq_result' :   mcq_gen.distractors(TextRequest.text)
#     }



from fastapi import APIRouter,HTTPException
from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import JsonOutputParser
from modules.services.loader import llm
from modules.schemas.user import TextRequest,mcqOb

router = APIRouter()

@router.post("/seq2mcq")
async def get_seq2mcq(TEXT: TextRequest):
        
    parser = JsonOutputParser(pydantic_object=mcqOb)

    template = (
    "Bạn là trợ lý hỗ trợ học tập, bạn tạo ra một vài câu hỏi trắc nghiệm với 3 câu trả lời sai và 1 câu trả lời đúng. Câu trả lời ngắn gọn."
    "Số lượng câu hỏi dựa vào độ dài nôi dung."
)
    prompt = PromptTemplate(
        template=template + "content: {content}\n{format_instructions}",
        input_variables=["content"],
        partial_variables={"format_instructions": parser.get_format_instructions()},
    )

    chain = prompt | llm | parser

    return chain.invoke({"content":TEXT.text})
