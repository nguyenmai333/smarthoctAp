# import torch

# from modules.services.loader import mbart_tokenizer, mbart_model, Bert_tokenizer, Bert_model, envit5_tokenizer, envit5_model,roberta_tokenizer,roberta_model
# from sklearn.cluster import KMeans
# from collections import defaultdict
# import torch
# import torch.nn.functional as F

# import numpy as np
# import networkx as nx
# from collections import defaultdict
# from sklearn.cluster import SpectralClustering


# def extract_key_sentences(text): 
#     doc = sentence_tokenizer(text)
#     sentences = [sent.text.strip() for sent in doc.sents]
#     return sentences

# def cluster_sentences(sentences, n_clusters):
#     X = torch.vstack([get_embedding(sent, Bert_tokenizer, Bert_model) for sent in sentences]).numpy()
#     kmeans = KMeans(n_clusters=n_clusters, random_state=0)
#     clusters = kmeans.fit_predict(X)
#     clustered_sentences = defaultdict(list)
#     for sentence, cluster in zip(sentences, clusters):
#         clustered_sentences[cluster].append(sentence)
#     clustered_paragraphs = [" ".join(clustered_sentences[cluster]) for cluster in sorted(clustered_sentences)]
#     return clustered_paragraphs

# # def get_embedding(text, tokenizer, model):
# #     inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=512)
# #     with torch.no_grad():
# #         output = model(**inputs)
# #         embedding = output.last_hidden_state.mean(dim=1)
# #     return embedding

# # def calculate_similarity(query_embedding, doc_embedding):
# #     return torch.cosine_similarity(query_embedding, doc_embedding).item()

# # def re_rank_results(query, documents, tokenizer, model):
# #     query_embedding = get_embedding(query, tokenizer, model)
# #     reranked_docs = []
# #     for doc in documents:
# #         try:
# #             doc_embedding = get_embedding(doc, tokenizer, model)
# #             similarity = calculate_similarity(query_embedding, doc_embedding)
# #             reranked_docs.append((similarity, doc))
# #         except Exception as e:
# #             print(f"Error processing document: {e}")
# #     return sorted(reranked_docs, reverse=True, key=lambda x: x[0])

# #########################################################################

# def translate_to_english(text, tokenizer, model):
#     inputs = tokenizer.encode(text, return_tensors="pt", max_length=512, truncation=True)
#     translated_outputs = model.generate(inputs, max_length=512)
#     translated_text = tokenizer.decode(translated_outputs[0], skip_special_tokens=True)
#     return translated_text

# def get_embedding(text, tokenizer, model):
#     max_input_length = min(len(text),1024)
#     inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=max_input_length)
#     with torch.no_grad():
#         output = model(**inputs)
#         embedding = output.last_hidden_state.mean(dim=1)
#     return embedding



# def calculate_similarity(query_embedding, doc_embedding):
#     return torch.cosine_similarity(query_embedding, doc_embedding).item()

# def re_rank_results(query, documents, tokenizer, model):
#     query_english = translate_to_english(query, envit5_tokenizer, envit5_model)
#     query_embedding = get_embedding(query_english, tokenizer, model)
    
#     reranked_docs = []
#     for doc in documents:
#         try:
#             doc_english = translate_to_english(doc, envit5_tokenizer, envit5_model)
#             doc_embedding = get_embedding(doc_english, tokenizer, model)
#             similarity = calculate_similarity(query_embedding, doc_embedding)
#             reranked_docs.append((similarity, doc))
#         except Exception as e:
#             print(f"Error processing document: {e}")
    
#     return reranked_docs
# #########################################################################


# def translate_text(text, model, tokenizer):
#     max_input_length = min(len(text),1024)
#     inputs = tokenizer.encode(text, return_tensors="pt", max_length=max_input_length, truncation=True)
#     outputs = model.generate(inputs, max_length=max_input_length, num_beams=4, early_stopping=True)
#     return tokenizer.decode(outputs[0][3:], skip_special_tokens=True)





# def translate_paragraphs(paragraphs):
#     translated_paragraphs = []
#     for paragraph in paragraphs:
#         translated_text = translate_text(paragraph, envit5_model, envit5_tokenizer)
#         translated_paragraphs.append(translated_text)
#     return translated_paragraphs





# def answer_question(question: str, context: str) -> str:
#     inputs = roberta_tokenizer(question, context, add_special_tokens=True, return_tensors='pt')
#     with torch.no_grad():
#         outputs = roberta_model(**inputs)
#     answer_start_scores = outputs.start_logits
#     answer_end_scores = outputs.end_logits
#     answer_start = torch.argmax(answer_start_scores)
#     answer_end = torch.argmax(answer_end_scores) + 1
#     answer_tokens = roberta_tokenizer.convert_ids_to_tokens(inputs['input_ids'][0][answer_start:answer_end])
#     answer = roberta_tokenizer.convert_tokens_to_string(answer_tokens)
#     return answer



# def group_sentences_and_matrix(sentences, labels, similarity_matrix):
#     unique_labels = set(labels)
    
#     # Tách các câu theo nhãn
#     grouped_sentences = defaultdict(list)
#     for sentence, label in zip(sentences, labels):
#         grouped_sentences[label].append(sentence)
    
#     # Tách ma trận tương đồng theo nhãn
#     sub_matrices = {}
#     for label in unique_labels:
#         indices = [i for i, l in enumerate(labels) if l == label]
#         sub_matrix = similarity_matrix[np.ix_(indices, indices)]
#         sub_matrices[label] = sub_matrix
    
#     return grouped_sentences, sub_matrices

# def spectral_clustering_from_similarity(similarity_matrix, n_clusters):
#     spectral = SpectralClustering(n_clusters=n_clusters, affinity='precomputed', random_state=0)
#     labels = spectral.fit_predict(similarity_matrix)
    
#     return labels
# def calculate_similarity_matrix(embeddings):
#     embedding_matrix = torch.stack(embeddings).squeeze() 
#     # Tính cosine similarity giữa tất cả các embeddings
#     similarity_matrix = torch.mm(embedding_matrix, embedding_matrix.t())
    
#     # Chuẩn hóa ma trận tương đồng để có giá trị trong khoảng [0, 1]
#     norms = torch.norm(embedding_matrix, dim=1, keepdim=True)
#     similarity_matrix = similarity_matrix / (norms * norms.t())
    
#     # Đảm bảo rằng các giá trị trong ma trận tương đồng là hợp lý
#     similarity_matrix = torch.clamp(similarity_matrix, 0, 1)
    
#     return similarity_matrix.numpy()

# def text_rank(similarity_matrix, n_rank):
#     graph = nx.from_numpy_array(similarity_matrix)
    
#     # Áp dụng thuật toán PageRank trên đồ thị
#     scores = nx.pagerank(graph)
    
#     # Sắp xếp các câu dựa trên điểm số
#     ranked_sentences = sorted(((score, index) for index, score in scores.items()), reverse=True)
#     return sorted([x[1] for x in ranked_sentences[:n_rank]])

# def summarize_text(text, max_length):
#     inputs = mbart_tokenizer([text], return_tensors="pt")
#     summary_ids = mbart_model.generate(inputs["input_ids"], num_beams=2, min_length=0, max_length=max_length)
#     summary =  mbart_tokenizer.batch_decode(summary_ids, skip_special_tokens=True,clean_up_tokenization_spaces=False)[0]
#     return summary


# def create_node(sentences, similarity_matrix):
#     if sentences:
#         aNode = { "content": str,
#                  "childs": []
#                  }
        
#         n_sens = len(sentences)
#         if n_sens < 10:
#             content_summarized = summarize_text(" ".join(sentences),100)
            
#         else:
#             n_cluster = int(n_sens**(1/3))
#             indice = text_rank(similarity_matrix,7)
#             main_sentences =[sentences[i] for i in indice]
#             content_summarized = summarize_text(" ".join(main_sentences),50)
#             labels = spectral_clustering_from_similarity(similarity_matrix,n_cluster)
#             grp_sents, grp_matrixs  = group_sentences_and_matrix(sentences,labels,similarity_matrix) 
#             unique_labels = set(labels)
#             for lb in unique_labels:
#                 aNode["childs"].append(create_node(grp_sents[lb],grp_matrixs[lb]))
#         aNode["content"]= translate_text(content_summarized,envit5_model,envit5_tokenizer)
#         return aNode








