from fastapi import APIRouter, Depends, HTTPException
from modules.core.auth import get_current_user
from modules.models.user import UserInDB
from modules.services.process_func import re_rank_results
from modules.schemas.user import TextRequest
import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)),'question_generation')))
from question_generation.pipelines import pipeline as qpl
from transformers import pipeline as tpl
import torch
import nltk
from nltk import pos_tag
from nltk.tokenize import word_tokenize
from modules.services.loader import mcq_generator as mcq_gen


##--init--
nltk.download('averaged_perceptron_tagger')
nltk.download('punkt')

router = APIRouter()

@route.post("/seq2mcq")
async def get_seq2mcq(TEXT: TextRequest):
    return  {
        'mcq_result' :   mcq_gen.distractors(TextRequest.text)
    }