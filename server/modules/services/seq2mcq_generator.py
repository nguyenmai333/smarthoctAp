import sys
import os
# from question_generation.question_generation.pipelines import pipeline as qpl
from transformers import pipeline as tpl
import torch
import nltk
from nltk import pos_tag
from nltk.tokenize import word_tokenize

class mcq:
    '''
        This Object Allow User Give a sentences through 2 pipelines:
        - Question Generate And Answer Generate
        - Create distractors to misleading reader (maybe 3)
    '''
    def __init__(self):
        self.mcq_generator = qpl('question-generation', model="valhalla/t5-base-qg-hl") #available
        self.model_path = 'google-bert/bert-base-cased'
        if torch.cuda.is_available:
            self.device = 0 
            self.unmasker = tpl('fill-mask', model=self.model_path, device=self.device) ##add cuda
        else:
            self.unmasker = tpl('fill-mask', model=self.model_path)
    

    def extract_nouns(self, segment):
        words = word_tokenize(segment)
        pos_tags = pos_tag(words)
        nouns = [word for word, pos in pos_tags if pos in ['NN', 'NNS', 'NNP', 'NNPS']]
        return nouns

    def genQA(self, text):
        return self.mcq_generator(text)

    def distractors(self, sample):
        Dict = self.genQA(sample)
        Distractors = []
        for dict_ in Dict:
            answer, question = dict_['answer'], dict_['question']
            mask_text = ''
            sight_word = ''
            if sample.find(answer) == -1:
                #print('oops, maybe your model have a wrong prediction')
                return None
            if len(answer.split()) > 1:
                sight_word = self.extract_nouns(answer)
                if sight_word != []:
                    mask_text = answer.replace(sight_word[0], '[MASK]')
                else:
                    mask_text = sample.replace(answer, '[MASK]')
            else: 
                mask_text = sample.replace(answer, '[MASK]')
            # print(mask_text)
            D = self.unmasker(mask_text, top_k = 20)
            distractors = [] 
            cnt = 0
            for d in D:
                if d['token_str'] != answer and len(d['token_str']) > 1:
                    cnt += 1
                    if len(sight_word) != 0:
                        distractors.append(answer.replace(sight_word[0], d['token_str']))
                    else:
                        distractors.append(d['token_str'])
                if cnt == 3:
                    break
            Distractors.append({
                'Question': question,
                'Answer': answer,
                'Distractor': distractors
            })
        return Distractors ###list (if segments have multi sentences so it's can split 2 ,3 .... 100 sentences contain distractors)

