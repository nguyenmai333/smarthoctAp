from fastapi import APIRouter,HTTPException
from modules.schemas.user import TextRequestSummarize


# from modules.services.loader import envit5_tokenizer,envit5_model,mbart_tokenizer,mbart_model
# from modules.services.process_func import summarize_text,translate_text

from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import JsonOutputParser
from modules.services.loader import llm
from modules.schemas.user import TestResponse

router = APIRouter()


@router.post("/summarize/")
async def summarize(request: TextRequestSummarize):
    # translated_text = translate_text(input_text,envit5_model, envit5_tokenizer)
    # length_out = (len(input_text)*input_riot)
    # summary_text = summarize_text(translated_text,length_out)
    # translated_summary_text = translate_text(summary_text,envit5_model, envit5_tokenizer)
    
    parser = JsonOutputParser(pydantic_object=TestResponse)

    template = (
        "Bạn là một trợ lý học tập giúp tóm tắt nội dung."
        "Sử dụng nội dung cho sẳn để tóm tắt, gom ý, chịu ảnh hưởng tỉ lệ từ 1 đến 10, 1 là dài nhất, 10 là ngắn nhất."
    )

    prompt = PromptTemplate(
    template=template + "nội dung: {content}\n tỉ lệ: {ratio}\n{format_instructions}",
    input_variables=["content", "ratio"],
    partial_variables={"format_instructions": parser.get_format_instructions()},
)

    chain = prompt | llm | parser
    
    
    return chain.invoke({"content": request.text,
                            "ratio":request.ratio
                            })