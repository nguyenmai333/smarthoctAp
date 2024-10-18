# from modules.services.process_func import re_rank_results
# from modules.services.loader import Bert_tokenizer, Bert_model


from fastapi import APIRouter, Depends, HTTPException
from modules.core.auth import get_current_user
from modules.models.user import UserInDB
from langchain_core.prompts import ChatPromptTemplate
from langchain.chains import create_retrieval_chain
from langchain.chains.combine_documents import create_stuff_documents_chain
from modules.services.process_func import llm
from langchain.vectorstores import FAISS
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.vectorstores.utils import DistanceStrategy
from modules.services.process_func import embedding_model

from langchain.schema.document import Document
router = APIRouter()

@router.get("/smart-search")
async def smartSearch(query: str, current_user: UserInDB = Depends(get_current_user)):
    saved_texts = [entry.text for entry in current_user.processed_texts]
    # print(saved_texts)
    
    if not saved_texts:
        raise HTTPException(status_code=404, detail="No saved texts found for the user.")
    
    # reranked_results = re_rank_results(query, saved_texts, Bert_tokenizer, Bert_model)
    
    # top_5_results = reranked_results[:5]
    docs = [Document(text) for text in saved_texts]
    text_splitter = RecursiveCharacterTextSplitter(chunk_size=300, chunk_overlap=0)
    splits = text_splitter.split_documents(docs)
    vector_store = FAISS.from_documents(
        splits, embedding_model, distance_strategy=DistanceStrategy.COSINE
    )
    retriever = vector_store.as_retriever()
    system_prompt = (
        "You are vietnamese assistance."
        "Use the given context to answer the question. "
        "Câu trả lời cần ngắn gọn và là tiếng việt"
        "Context: {context}"
    )
    prompt = ChatPromptTemplate.from_messages(
        [
            ("system", system_prompt),
            ("human", "{input}"),
        ]
    )
    
    question_answer_chain = create_stuff_documents_chain(llm, prompt)
    chain = create_retrieval_chain(retriever, question_answer_chain)
    res = chain.invoke({"input":query})
    
    return {"result": res['answer'], "Contents": [x.page_content for x in res['context']]}