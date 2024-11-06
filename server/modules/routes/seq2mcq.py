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
from langdetect import detect
router = APIRouter()

@router.post("/seq2mcq")
async def get_seq2mcq(TEXT: TextRequest):

    parser = JsonOutputParser(pydantic_object=mcqOb)

    template = (
        "You are a study assistant that creates multiple-choice questions with 3 incorrect answers and 1 correct answer. "
        "Keep the answers concise. The output language should match the language of the content. "
        "The number of questions should depend on the length of the content."
    )

    prompt = PromptTemplate(
        template=template + "Content: {content}\nOutput Language: {output_language}\n{format_instructions}",
        input_variables=["content", "output_language"],
        partial_variables={"format_instructions": parser.get_format_instructions()},
    )

    chain = prompt | llm | parser

    return  chain.invoke({"content": TEXT.text, "output_language":  detect( TEXT.text)})
