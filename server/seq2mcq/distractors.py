from transformers import BertTokenizer, BertForMaskedLM
import torch

# Tải mô hình và tokenizer BERT
model_name = "bert-base-uncased"  # Bạn có thể thay đổi mô hình nếu muốn
tokenizer = BertTokenizer.from_pretrained(model_name)
model = BertForMaskedLM.from_pretrained(model_name)

# Hàm dự đoán từ bị che giấu
def predict_masked_word(sentence):
    # Thay thế từ cần che giấu bằng [MASK]
    masked_sentence = sentence.replace("___", "[MASK]")
    
    # Tokenize câu
    inputs = tokenizer(masked_sentence, return_tensors="pt")
    
    # Dự đoán
    with torch.no_grad():
        outputs = model(**inputs)
        predictions = outputs.logits
    
    # Lấy chỉ số vị trí của từ [MASK]
    mask_index = torch.where(inputs['input_ids'] == tokenizer.mask_token_id)[1]
    
    # Lấy 10 từ dự đoán tốt nhất
    predicted_indices = predictions[0, mask_index].topk(10).indices.tolist()
    predicted_words = [tokenizer.decode([idx]) for idx in predicted_indices]
    
    return masked_sentence, predicted_words

# Câu gốc với từ bị che giấu
input_sentence = "The bird flew over the ___."
# Bạn có thể thay thế từ cần che giấu bằng ___

# Dự đoán từ bị che giấu
masked_sentence, predicted_words = predict_masked_word(input_sentence)

# In kết quả
print(f"Masked Sentence: {masked_sentence}")
print("Predicted words:", predicted_words)
