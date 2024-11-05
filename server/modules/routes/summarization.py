from fastapi import APIRouter,HTTPException
from modules.schemas.user import TextRequestSummarize


# from modules.services.loader import envit5_tokenizer,envit5_model,mbart_tokenizer,mbart_model
# from modules.services.process_func import summarize_text,translate_text

from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import JsonOutputParser
from modules.services.loader import llm
from modules.schemas.user import TestResponse
from langdetect import detect

router = APIRouter()


@router.post("/summarize/")
async def summarize(request: TextRequestSummarize):
    # translated_text = translate_text(input_text,envit5_model, envit5_tokenizer)
    # length_out = (len(input_text)*input_riot)
    # summary_text = summarize_text(translated_text,length_out)
    # translated_summary_text = translate_text(summary_text,envit5_model, envit5_tokenizer)

    parser = JsonOutputParser(pydantic_object=TestResponse)

    template = (
        "You are a study assistant that helps summarize content."
        "Use the provided content to summarize and condense the information, with a scale from 1 to 10, where 1 is the longest and 10 is the shortest."
        "Output the result as a valid JSON object."
    )

    prompt = PromptTemplate(
        template=template + "Content: {content}\n ratio: {ratio}Output Language: {output_language}\n{format_instructions}",
        input_variables=["content","ratio", "output_language"],
        partial_variables={"format_instructions": parser.get_format_instructions()},
    )

    chain = prompt | llm | parser


    return chain.invoke({"content": request.text,
                            "ratio":request.ratio,
                            "output_language":detect( request.text)
                            })