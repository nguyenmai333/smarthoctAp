FROM python:3.9-slim

RUN apt-get update && \
apt-get install -y git && \
apt-get install git-lfs -y && \
apt-get install -y libgl1-mesa-glx && \
apt-get install -y libglib2.0-0 && \
apt-get clean

RUN git lfs install

# Clone mã nguồn từ GitHub

RUN git clone https://github.com/nguyenmai333/smarthoctAp.git /smarthoctAp
WORKDIR /smarthoctAp/server/modules/pretrain_models
RUN git clone  https://huggingface.co/vinai/PhoWhisper-small PhoWhisper-small
WORKDIR /smarthoctAp/server
# Cài đặt các yêu cầu từ requirements.txt
RUN pip install --upgrade pip
RUN pip install --no-cache-dir -r requirements.txt
RUN python -m spacy download en_core_web_sm 
RUN pip install -U "huggingface_hub[cli]"
RUN huggingface-cli download facebook/bart-large-cnn --cache-dir /modules/pretrain_models
RUN huggingface-cli download deepset/roberta-base-squad2 --cache-dir /modules/pretrain_models
RUN huggingface-cli download vinai/phobert-base --cache-dir /modules/pretrain_models
RUN huggingface-cli download VietAI/envit5-translation --cache-dir /modules/pretrain_models
RUN huggingface-cli download google-bert/bert-base-uncased --cache-dir /modules/pretrain_models

# Chạy ứng dụng chính
CMD ["python", "main.py"]
