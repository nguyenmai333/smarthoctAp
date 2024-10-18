from fastapi import APIRouter, HTTPException
# from modules.services.loader import envit5_model,envit5_tokenizer,Bert_model,Bert_tokenizer
# from modules.services.process_func import (
#     extract_key_sentences,
#     create_node,
#     translate_text,
#     calculate_similarity_matrix,
#     get_embedding
# )
from modules.services.process_func import llm
from modules.schemas.user import TextListRequest, MindMapResponse, Mindmap
router = APIRouter()
from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import JsonOutputParser


@router.post("/create_mindmap/", response_model=Mindmap)
async def create_mindmap(request: TextListRequest):
    try:
        input_texts =  " ".join(request.texts)
        
        # Translate texts
        # translated_texts = [translate_text(text,envit5_model,envit5_tokenizer) for text in input_texts]
        # translated_text = " ".join(translated_texts)

        # # Extract key sentences
        # sentences = extract_key_sentences(translated_text)

        # # Get embeddings
        # embs = [get_embedding(sent,Bert_tokenizer,Bert_model) for sent in sentences]

        # # Calculate similarity matrix
        # sim_matrix = calculate_similarity_matrix(embs)

        # # Create mind map
        # mindmap = create_node(sentences, sim_matrix)
        
        
           
        parser = JsonOutputParser(pydantic_object=Mindmap)

        template = (
            "Use the given content to create vietnamese mindmap. "
            "Dịch lại tiếng việt"
        )

        prompt = PromptTemplate(
            template=template + "content: {content}\n{format_instructions}",
            input_variables=["content"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )

        chain = prompt | llm | parser


        return chain.invoke({"content": input_texts})

    except Exception as e:
        # Handle specific exceptions or log the error
        raise HTTPException(status_code=500, detail=str(e))