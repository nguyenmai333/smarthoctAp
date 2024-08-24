import spacy
import pkg_resources
from transformers import AutoTokenizer,AutoModel,AutoModelForSeq2SeqLM,pipeline, AutoModelForSequenceClassification


phoBert_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/phobert-base'))
phoBert_model = AutoModel.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/phobert-base'))

phoWhisper_transcriber = pipeline("automatic-speech-recognition", model=pkg_resources.resource_filename('modules', 'pretrain_models/PhoWhisper-small'))


mbart_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/bart-large-cnn'))
mbart_model = AutoModelForSeq2SeqLM.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/bart-large-cnn'))

envit5_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/envit5-translation'))
envit5_model = AutoModelForSeq2SeqLM.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/envit5-translation'))

sentence_tokenizer = spacy.load(pkg_resources.resource_filename('modules', 'pretrain_models/en_core_web_sm'))

mnli_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/bart-large-mnli'))
mnli_model = AutoModelForSequenceClassification.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/bart-large-mnli'))

Bert_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/bert-base-uncased'))
Bert_model = AutoModel.from_pretrained(pkg_resources.resource_filename('modules', 'pretrain_models/bert-base-uncased'))