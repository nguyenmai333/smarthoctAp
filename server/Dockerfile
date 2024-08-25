FROM python:3.9-slim

RUN apt-get update && \
apt-get install -y git && \
apt-get clean

# Clone mã nguồn từ GitHub

RUN git clone https://github.com/nguyenmai333/smarthoctAp.git /smarthoctAp
WORKDIR /smarthoctAp/server
# Cài đặt các yêu cầu từ requirements.txt
RUN pip install --no-cache-dir -r requirements.txt
RUN python -m spacy download en_core_web_sm 
RUN git clone https://huggingface.co/vinai/PhoWhisper-small modules/pretrain_models/PhoWhisper-small

# Chạy ứng dụng chính
CMD ["python", "main.py"]
