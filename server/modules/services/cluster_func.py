
from modules.services.loader import sentence_tokenizer,phoBert_model,phoBert_tokenizer
from modules.services.smart_search_func import get_embedding
from sklearn.cluster import KMeans
import torch

def extract_key_sentences(text): 
    doc = sentence_tokenizer(text)
    sentences = [sent.text.strip() for sent in doc.sents]
    return sentences

def cluster_sentences(sentences, n_clusters):
    X = torch.vstack([get_embedding(sent, phoBert_tokenizer, phoBert_model) for sent in sentences]).numpy()
    kmeans = KMeans(n_clusters=n_clusters, random_state=0)
    clusters = kmeans.fit_predict()

    
    
    
    
    return 