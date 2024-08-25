import spacy
import pkg_resources
from transformers import AutoTokenizer,AutoModel,AutoModelForSeq2SeqLM,pipeline,RobertaForQuestionAnswering,RobertaTokenizer,pipeline






cached_path = pkg_resources.resource_filename('modules', 'pretrain_models')


roberta_tokenizer = RobertaTokenizer.from_pretrained("deepset/roberta-base-squad2",cache_dir=cached_path)
roberta_model = RobertaForQuestionAnswering.from_pretrained("deepset/roberta-base-squad2",cache_dir=cached_path)


phoBert_tokenizer = AutoTokenizer.from_pretrained('vinai/phobert-base',cache_dir=cached_path)
phoBert_model = AutoModel.from_pretrained('vinai/phobert-base',cache_dir=cached_path)

phoWhisper_transcriber = pipeline("automatic-speech-recognition", model=pkg_resources.resource_filename('modules', 'pretrain_models/PhoWhisper-small'))
mbart_tokenizer = AutoTokenizer.from_pretrained('facebook/bart-large-cnn',cache_dir=cached_path)
mbart_model = AutoModelForSeq2SeqLM.from_pretrained('facebook/bart-large-cnn',cache_dir=cached_path)

envit5_tokenizer = AutoTokenizer.from_pretrained('VietAI/envit5-translation',cache_dir=cached_path)
envit5_model = AutoModelForSeq2SeqLM.from_pretrained('VietAI/envit5-translation',cache_dir=cached_path)

sentence_tokenizer = spacy.load(pkg_resources.resource_filename('modules', 'pretrain_models/en_core_web_sm'))

Bert_tokenizer = AutoTokenizer.from_pretrained('google-bert/bert-base-uncased',cache_dir=cached_path)
Bert_model = AutoModel.from_pretrained('google-bert/bert-base-uncased',cache_dir=cached_path)