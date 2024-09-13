import torch

from modules.services.loader import sentence_tokenizer, mbart_tokenizer, mbart_model, Bert_tokenizer, Bert_model, envit5_tokenizer, envit5_model,roberta_tokenizer,roberta_model
from sklearn.cluster import KMeans
from collections import defaultdict
import torch
import torch.nn.functional as F

def extract_key_sentences(text): 
    doc = sentence_tokenizer(text)
    sentences = [sent.text.strip() for sent in doc.sents]
    return sentences

def cluster_sentences(sentences, n_clusters):
    X = torch.vstack([get_embedding(sent, Bert_tokenizer, Bert_model) for sent in sentences]).numpy()
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
    max_input_length = min(len(text),1024)
    inputs = tokenizer.encode(text, return_tensors="pt", max_length=max_input_length, truncation=True)
    outputs = model.generate(inputs, max_length=max_input_length, num_beams=4, early_stopping=True)
    return tokenizer.decode(outputs[0][3:], skip_special_tokens=True)



def summarize_text(text, ratio):
    max_input_length = min(len(text),1024)
    max_output_length = int(max_input_length * ratio) 
    min_output_length = min(max_output_length, 5)
    inputs = mbart_tokenizer.encode(text, return_tensors="pt", max_length=max_input_length, truncation=True)
    outputs = mbart_model.generate(inputs, max_length=max_output_length, min_length=min_output_length, length_penalty=2.0, num_beams=4, early_stopping=True)
    summary = mbart_tokenizer.decode(outputs[0], skip_special_tokens=True)
    return summary

def summarize_paragraphs(paragraphs):
    summaries = []
    for paragraph in paragraphs:
        summary = summarize_text(paragraph)
        summaries.append(summary)
    return summaries

def translate_paragraphs(paragraphs):
    translated_paragraphs = []
    for paragraph in paragraphs:
        translated_text = translate_text(paragraph, envit5_model, envit5_tokenizer)
        translated_paragraphs.append(translated_text)
    return translated_paragraphs





def answer_question(question: str, context: str) -> str:
    inputs = roberta_tokenizer(question, context, add_special_tokens=True, return_tensors='pt')
    with torch.no_grad():
        outputs = roberta_model(**inputs)
    answer_start_scores = outputs.start_logits
    answer_end_scores = outputs.end_logits
    answer_start = torch.argmax(answer_start_scores)
    answer_end = torch.argmax(answer_end_scores) + 1
    answer_tokens = roberta_tokenizer.convert_ids_to_tokens(inputs['input_ids'][0][answer_start:answer_end])
    answer = roberta_tokenizer.convert_tokens_to_string(answer_tokens)
    return answer

