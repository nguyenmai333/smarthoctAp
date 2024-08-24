import spacy
import pkg_resources
from transformers import AutoTokenizer,AutoModel,AutoModelForSeq2SeqLM,pipeline


phoBert_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules.pretrain_models', 'phobert-base'))
phoBert_model = AutoModel.from_pretrained(pkg_resources.resource_filename('modules.pretrain_models', 'phobert-base'))

phoWhisper_transcriber = pipeline("automatic-speech-recognition", model=pkg_resources.resource_filename('modules.pretrain_models', 'PhoWhisper-small'))


mbart_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules.pretrain_models', 'bart-large-cnn'))
mbart_model = AutoModelForSeq2SeqLM.from_pretrained(pkg_resources.resource_filename('modules.pretrain_models', 'bart-large-cnn'))

envit5_tokenizer = AutoTokenizer.from_pretrained(pkg_resources.resource_filename('modules.pretrain_models', 'envit5-translation'))
envit5_model = AutoModelForSeq2SeqLM.from_pretrained(pkg_resources.resource_filename('modules.pretrain_models', 'envit5-translation'))

sentence_tokenizer = spacy.load(pkg_resources.resource_filename('modules.pretrain', 'PhoWhisper-small'))
phoBertTokenizer = AutoTokenizer.from_pretrained("phobert-base-v2")
phoBertmodel = AutoModel.from_pretrained("phobert-base-v2")