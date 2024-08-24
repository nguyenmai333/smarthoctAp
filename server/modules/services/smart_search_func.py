import torch

def get_embedding(text, tokenizer, model):
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=512)
    with torch.no_grad():
        output = model(**inputs)
        embedding = output.last_hidden_state.mean(dim=1)
    return embedding

def calculate_similarity(query_embedding, doc_embedding):
    return torch.cosine_similarity(query_embedding, doc_embedding).item()

def re_rank_results(query, documents, tokenizer, model):
    query_embedding = get_embedding(query, tokenizer, model)
    reranked_docs = []
    for doc in documents:
        try:
            doc_embedding = get_embedding(doc, tokenizer, model)
            similarity = calculate_similarity(query_embedding, doc_embedding)
            reranked_docs.append((similarity, doc))
        except Exception as e:
            print(f"Error processing document: {e}")
    return sorted(reranked_docs, reverse=True, key=lambda x: x[0])
