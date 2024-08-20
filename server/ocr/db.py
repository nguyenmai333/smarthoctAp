from pymongo import MongoClient

client = MongoClient('mongodb://172.26.250.122:27017')
db = client['text-preprocessing']

images_collection = db['image']
texts_collection = db['raw-text']
