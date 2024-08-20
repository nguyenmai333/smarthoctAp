import warnings
warnings.filterwarnings("ignore")

import torch
from transformers import T5ForConditionalGeneration, T5Tokenizer
from sense2vec import Sense2Vec
from sentence_transformers import SentenceTransformer
import random
import numpy as np
import nltk
from nltk.tokenize import sent_tokenize
from nltk.corpus import stopwords, wordnet as wn
import pke
import string
from sklearn.metrics.pairwise import cosine_similarity
from similarity.normalized_levenshtein import NormalizedLevenshtein
import pickle
import time
import os

# Set up NLTK
nltk.download('punkt')
nltk.download('stopwords')
nltk.download('wordnet')
nltk.download('omw-1.4')

# Check GPU
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(device)

# Load models and tokenizers
def load_model(model_name, model_path, tokenizer_name, tokenizer_path):
    if os.path.exists(model_path):
        with open(model_path, 'rb') as f:
            model = pickle.load(f)
        print(f"{model_name} found in the disc, model is loaded successfully.")
    else:
        print(f"{model_name} does not exist, downloading...")
        start_time = time.time()
        model = T5ForConditionalGeneration.from_pretrained(model_name)
        end_time = time.time()
        print(f"Downloaded the {model_name} model in {(end_time-start_time)/60:.2f} min, saving to disc...")
        with open(model_path, 'wb') as f:
            pickle.dump(model, f)
        print("Done. Saved the model to disc.")
    
    if os.path.exists(tokenizer_path):
        with open(tokenizer_path, 'rb') as f:
            tokenizer = pickle.load(f)
        print(f"{tokenizer_name} found in the disc and is loaded successfully.")
    else:
        print(f"{tokenizer_name} does not exist, downloading...")
        start_time = time.time()
        tokenizer = T5Tokenizer.from_pretrained(tokenizer_name)
        end_time = time.time()
        print(f"Downloaded the {tokenizer_name} tokenizer in {(end_time-start_time)/60:.2f} min, saving to disc...")
        with open(tokenizer_path, 'wb') as f:
            pickle.dump(tokenizer, f)
        print("Done. Saved the tokenizer to disc.")
    
    return model.to(device), tokenizer

# Load models
summary_model, summary_tokenizer = load_model('t5-base', 't5_summary_model.pkl', 't5_summary_tokenizer', 't5_summary_tokenizer.pkl')
question_model, question_tokenizer = load_model('ramsrigouthamg/t5_squad_v1', 't5_question_model.pkl', 'ramsrigouthamg/t5_squad_v1', 't5_question_tokenizer.pkl')

# Load sentence transformer model
if os.path.exists("sentence_transformer_model.pkl"):
    with open("sentence_transformer_model.pkl", 'rb') as f:
        sentence_transformer_model = pickle.load(f)
    print("Sentence transformer model found in the disc, model is loaded successfully.")
else:
    print("Sentence transformer model does not exist, downloading...")
    start_time = time.time()
    sentence_transformer_model = SentenceTransformer("sentence-transformers/msmarco-distilbert-base-v2")
    end_time = time.time()
    print(f"Downloaded the sentence transformer model in {(end_time-start_time)/60:.2f} min, saving to disc...")
    with open("sentence_transformer_model.pkl", 'wb') as f:
        pickle.dump(sentence_transformer_model, f)
    print("Done saving to disc.")

# Load Sense2Vec model
s2v = Sense2Vec().from_disk('s2v_old')

# Utility functions
def set_seed(seed: int):
    random.seed(seed)
    np.random.seed(seed)
    torch.manual_seed(seed)
    torch.cuda.manual_seed_all(seed)

def postprocesstext(content):
    final = ""
    for sent in sent_tokenize(content):
        sent = sent.capitalize()
        final += " " + sent
    return final.strip()

def summarizer(text, model, tokenizer):
    text = text.strip().replace("\n", " ")
    text = "summarize: " + text
    max_len = 512
    encoding = tokenizer.encode_plus(text, max_length=max_len, pad_to_max_length=False, truncation=True, return_tensors="pt").to(device)
    input_ids, attention_mask = encoding["input_ids"], encoding["attention_mask"]
    outs = model.generate(input_ids=input_ids, attention_mask=attention_mask, early_stopping=True, num_beams=3, num_return_sequences=1, no_repeat_ngram_size=2, min_length=75, max_length=300)
    dec = [tokenizer.decode(ids, skip_special_tokens=True) for ids in outs]
    summary = dec[0]
    return postprocesstext(summary)

def get_nouns_multipartite(content):
    out = []
    try:
        extractor = pke.unsupervised.MultipartiteRank()
        extractor.load_document(input=content, language='en')
        pos = {'PROPN', 'NOUN', 'ADJ', 'VERB', 'ADP', 'ADV', 'DET', 'CONJ', 'NUM', 'PRON', 'X'}
        stoplist = list(string.punctuation) + ['-lrb-', '-rrb-', '-lcb-', '-rcb-', '-lsb-', '-rsb-'] + stopwords.words('english')
        extractor.candidate_selection(pos=pos)
        extractor.candidate_weighting(alpha=1.1, threshold=0.75, method='average')
        keyphrases = extractor.get_n_best(n=15)
        for val in keyphrases:
            out.append(val[0])
    except:
        out = []
    return out

def get_keywords(originaltext):
    return get_nouns_multipartite(originaltext)

def get_question(context, answer, model, tokenizer):
    text = "context: {} answer: {}".format(context, answer)
    encoding = tokenizer.encode_plus(text, max_length=384, pad_to_max_length=False, truncation=True, return_tensors="pt").to(device)
    input_ids, attention_mask = encoding["input_ids"], encoding["attention_mask"]
    outs = model.generate(input_ids=input_ids, attention_mask=attention_mask, early_stopping=True, num_beams=5, num_return_sequences=1, no_repeat_ngram_size=2, max_length=72)
    dec = [tokenizer.decode(ids, skip_special_tokens=True) for ids in outs]
    Question = dec[0].replace("question:", "").strip()
    return Question

def filter_same_sense_words(original, wordlist):
    filtered_words = []
    base_sense = original.split('|')[1]
    for eachword in wordlist:
        if eachword[0].split('|')[1] == base_sense:
            filtered_words.append(eachword[0].split('|')[0].replace("_", " ").title().strip())
    return filtered_words

def get_highest_similarity_score(wordlist, wrd):
    score = []
    normalized_levenshtein = NormalizedLevenshtein()
    for each in wordlist:
        score.append(normalized_levenshtein.similarity(each.lower(), wrd.lower()))
    return max(score)

def sense2vec_get_words(word, s2v, topn, question):
    output = []
    try:
        sense = s2v.get_best_sense(word, senses=["NOUN", "PERSON", "PRODUCT", "LOC", "ORG", "EVENT", "NORP", "WORK OF ART", "FAC", "GPE", "NUM", "FACILITY"])
        most_similar = s2v.most_similar(sense, n=topn)
        output = filter_same_sense_words(sense, most_similar)
    except:
        output = []
    threshold = 0.6
    final = [word]
    checklist = question.split()
    for x in output:
        if get_highest_similarity_score(final, x) < threshold and x not in final and x not in checklist:
            final.append(x)
    return final[1:]

def mmr(doc_embedding, word_embeddings, words, top_n, lambda_param):
    word_doc_similarity = cosine_similarity(word_embeddings, doc_embedding)
    word_similarity = cosine_similarity(word_embeddings)
    keywords_idx = [np.argmax(word_doc_similarity)]
    candidates_idx = [i for i in range(len(words)) if i != keywords_idx[0]]
    for _ in range(top_n - 1):
        candidate_similarities = word_doc_similarity[candidates_idx, :]
        target_similarities = np.max(word_similarity[candidates_idx][:, keywords_idx], axis=1)
        mmr = (lambda_param) * candidate_similarities - (1-lambda_param) * target_similarities.reshape(-1, 1)
        mmr_idx = candidates_idx[np.argmax(mmr)]
        keywords_idx.append(mmr_idx)
        candidates_idx.remove(mmr_idx)
    return [words[idx] for idx in keywords_idx]

def get_distractors_wordnet(word):
    distractors = []
    try:
        syn = wn.synsets(word, 'n')[0]
        word = word.lower()
        orig_word = word
        if len(word.split()) > 0:
            word = word.replace(" ", "_")
        hypernym = syn.hypernyms()
        if len(hypernym) == 0:
            return distractors
        for item in hypernym[0].hyponyms():
            if item.name().split('.')[0] != word:
                distractors.append(item.name().split('.')[0].replace("_", " "))
        distractors = list(set(distractors))
    except:
        distractors = []
    return distractors

def get_distractors(words, keyword):
    wordnet_distractors = get_distractors_wordnet(keyword)
    sense2vec_distractors = sense2vec_get_words(keyword, s2v, 20, words)
    all_distractors = list(set(wordnet_distractors + sense2vec_distractors))
    final_distractors = [x for x in all_distractors if x.lower() not in keyword.lower()]
    return final_distractors

def generate_mcq(content):
    summary = summarizer(content, summary_model, summary_tokenizer)
    keywords = get_keywords(summary)
    mcqs = []
    for keyword in keywords:
        question = get_question(summary, keyword, question_model, question_tokenizer)
        distractors = get_distractors(keywords, keyword)
        if len(distractors) < 3:
            continue
        choices = [keyword] + distractors[:3]
        random.shuffle(choices)
        mcqs.append({
            "question": question,
            "answers": [keyword],
            "choices": choices
        })
    return mcqs

# Example usage
content = "Your text content here"
mcqs = generate_mcq(content)
for mcq in mcqs:
    print("Question:", mcq["question"])
    print("Choices:")
    for choice in mcq["choices"]:
        print("-", choice)
    print("Correct Answers:", mcq["answers"])
    print()
