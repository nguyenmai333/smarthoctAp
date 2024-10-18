from fastapi import APIRouter,HTTPException
from modules.schemas.user import TextRequestSummarize


# from modules.services.loader import envit5_tokenizer,envit5_model,mbart_tokenizer,mbart_model
# from modules.services.process_func import summarize_text,translate_text

from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import JsonOutputParser
from modules.services.process_func import llm
from modules.schemas.user import TestResponse

router = APIRouter()


@router.post("/summarize/",response_model=TestResponse)
async def summarize(request: TextRequestSummarize):
    try:
        
        # translated_text = translate_text(input_text,envit5_model, envit5_tokenizer)
        # length_out = (len(input_text)*input_riot)
        # summary_text = summarize_text(translated_text,length_out)
        # translated_summary_text = translate_text(summary_text,envit5_model, envit5_tokenizer)
        
        parser = JsonOutputParser(pydantic_object=TestResponse)

        template = (
                "Use the given content to summarize affected by ratio."
                "Dịch lại tiếng việt"
            )

        prompt = PromptTemplate(
            template=template + "content: {content}\n ratio: {input_ratio}/10\n{format_instructions}",
            input_variables=["content"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )

        chain = prompt | llm | parser
        
        
        return chain.invoke({"content": request.text,
                             "ratio":request.ratio
                             })
    
        
    except Exception as e:
        # Handle specific exceptions or log the error
        raise HTTPException(status_code=500, detail=str(e))