# from fastapi import APIRouter
# from fastapi.responses import FileResponse
# from modules.services.process_func import (
#     extract_key_sentences,
#     cluster_sentences,
#     summarize_paragraphs,
#     translate_paragraphs
# )
# from modules.services.loader import envit5_tokenizer, envit5_model
# from modules.schemas.user import TextRequest
# import json
# from tempfile import NamedTemporaryFile

# router = APIRouter()

# def create_json_data(clustered_paragraphs, topics):
#     # Prepare the data for JSON
#     data = [{"topic": topic, "paragraph": paragraph} for topic, paragraph in zip(topics, clustered_paragraphs)]
    
#     # Write to a temporary JSON file
#     tmp_file = NamedTemporaryFile(suffix=".json", delete=False, mode='w', encoding='utf-8')
#     with open(tmp_file.name, 'w', encoding='utf-8') as f:
#         json.dump(data, f, ensure_ascii=False, indent=4)
    
#     return tmp_file.name

# @router.post("/create_mindmap_json/")
# async def create_mindmap_json(request: TextRequest):
#     input_text = request.text

#     # Translate input text
#     inputs = envit5_tokenizer.encode(input_text, return_tensors="pt", max_length=512, truncation=True)
#     translated_outputs = envit5_model.generate(inputs, max_length=512)
#     translated_text = envit5_tokenizer.decode(translated_outputs[0], skip_special_tokens=True)

#     # Process translated text
#     sentences = extract_key_sentences(translated_text)
#     clustered_paragraphs = cluster_sentences(sentences)
#     summarized_paragraphs = summarize_paragraphs(clustered_paragraphs)
#     topics = classify_paragraphs(clustered_paragraphs)

#     # Create JSON file
#     json_file_path = create_json_data(summarized_paragraphs, topics)

#     return FileResponse(json_file_path, media_type="application/json", filename="paragraphs_with_topics.json")