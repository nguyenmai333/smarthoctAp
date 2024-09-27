from fastapi import APIRouter, HTTPException
from modules.services.loader import envit5_model,envit5_tokenizer,Bert_model,Bert_tokenizer
from modules.services.process_func import (
    extract_key_sentences,
    create_node,
    translate_text,
    calculate_similarity_matrix,
    get_embedding
)
from modules.schemas.user import TextListRequest, MindMapResponse

router = APIRouter()

@router.post("/create_mindmap/", response_model=MindMapResponse)
async def create_mindmap(request: TextListRequest):
    try:
        input_texts = request.texts
        
        # Translate texts
        translated_texts = [translate_text(text,envit5_model,envit5_tokenizer) for text in input_texts]
        translated_text = " ".join(translated_texts)

        # Extract key sentences
        sentences = extract_key_sentences(translated_text)

        # Get embeddings
        embs = [get_embedding(sent,Bert_tokenizer,Bert_model) for sent in sentences]

        # Calculate similarity matrix
        sim_matrix = calculate_similarity_matrix(embs)

        # Create mind map
        mindmap = create_node(sentences, sim_matrix)
        
        # Wrap in the MindMapResponse model
        return MindMapResponse(root=mindmap)

    except Exception as e:
        # Handle specific exceptions or log the error
        raise HTTPException(status_code=500, detail=str(e))