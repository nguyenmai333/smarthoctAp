import torch

from modules.services.loader import sentence_tokenizer,phoBert_model,phoBert_tokenizer
from sklearn.cluster import KMeans
from collections import defaultdict
import torch

def extract_key_sentences(text): 
    doc = sentence_tokenizer(text)
    sentences = [sent.text.strip() for sent in doc.sents]
    return sentences

def cluster_sentences(sentences, n_clusters):
    X = torch.vstack([get_embedding(sent, phoBert_tokenizer, phoBert_model) for sent in sentences]).numpy()
    kmeans = KMeans(n_clusters=n_clusters, random_state=0)
    clusters = kmeans.fit_predict(X)
    clustered_sentences = defaultdict(list)
    for sentence, cluster in zip(sentences, clusters):
        clustered_sentences[cluster].append(sentence)
    clustered_paragraphs = [" ".join(clustered_sentences[cluster]) for cluster in sorted(clustered_sentences)]
    return clustered_paragraphs
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


def translate_text(text, model, tokenizer):
    max_input_length = min(512, len(text) + 50) 
    max_output_length = int(max_input_length * 0.4) 
    inputs = tokenizer.encode(text, return_tensors="pt", max_length=max_input_length, truncation=True)
    outputs = model.generate(inputs, max_length=max_output_length, num_beams=4, early_stopping=True)
    return tokenizer.decode(outputs[0][3:], skip_special_tokens=True)