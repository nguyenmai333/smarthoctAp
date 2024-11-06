from fastapi import APIRouter
from modules.schemas.user import TextRequestTranslate,TestResponseTranslate


from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import JsonOutputParser
from modules.services.loader import llm
router = APIRouter()

@router.post("/translate/")
async def translate(request: TextRequestTranslate):
    
    
    parser = JsonOutputParser(pydantic_object=TestResponseTranslate)

    template = (
            "Bạn là một trợ lý học tập giúp dịch nội dung."
            "Sử dụng nội dung cho sẳn để dịch sang ngôn ngữ cần dịch."
        )

    prompt = PromptTemplate(
        template=template + "nội dung: {content}\n Ngôn ngữ cần dịch: {lang}\n {format_instructions}",
        input_variables=["content", "lang"],
        partial_variables={"format_instructions": parser.get_format_instructions()},
    )
    chain = prompt | llm | parser
    return  chain.invoke({"content": request.text,
                            "lang":request.lang
                            })