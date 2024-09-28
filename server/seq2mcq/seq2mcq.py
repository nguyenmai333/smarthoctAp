import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)),'question_generation')))
from question_generation.pipelines import pipeline as qpl
from transformers import pipeline as tpl
import torch

class mcq:
    '''
        This Object Allow User Give a sentences through 2 pipelines:
        - Question Generate And Answer Generate
        - Create distractors to misleading reader (maybe 3)
    '''
    def __init__(self):
        self.mcq_generator = qpl('question-generation', model="valhalla/t5-base-qg-hl") #available
        if torch.cuda.is_available:
            self.device = 0
            self.unmasker = tpl('fill-mask', model='google-bert/bert-large-cased-whole-word-masking', device=self.device) ##add cuda
        else:
            self.unmasker = tpl('fill-mask', model='google-bert/bert-large-cased-whole-word-masking')
    
    def genQA(self, text):
        return self.mcq_generator(text)

    def distractors(self, sample):
        Dict = self.genQA(sample)
        Distractors = []
        for dict_ in Dict:
            answer, question = dict_['answer'], dict_['question']
            if sample.find(answer) == -1:
                #print('oops, maybe your model have a wrong prediction')
                return None
            mask_text = sample.replace(answer, '[MASK]')
            # print(mask_text)
            D = self.unmasker(mask_text, top_k = 20)
            distractors = [] 
            cnt = 0
            for d in D:
                print()
                print(d)
                if d['token_str'] != answer and len(d['token_str']) > 1:
                    cnt += 1
                    distractors.append(d['token_str'])
                if cnt == 3:
                    break
            Distractors.append({
                'Question': question,
                'Answer': answer,
                'Distractor': distractors
            })
        return Distractors ###list (if segments have multi sentences so it's can split 2 ,3 .... 100 sentences contain distractors)

if __name__ == "__main__":
    mcq_generator = mcq()
    print('generator loaded')
    samples = [
    'Neural networks are one of many types of ML algorithms that are used to model complex patterns in data. They are composed of three layers — input layer, hidden layer, and output layer.',
    'Deep learning is a subfield of machine learning that focuses on the development of artificial neural networks with multiple layers, also known as deep neural networks. These networks are particularly effective in modeling complex, hierarchical patterns and representations in data. Deep learning is inspired by the structure and function of the human brain, specifically the biological neural networks that make up the brain.',
    'TensorFlow is an open-source platform developed by Google designed primarily for high-performance numerical computation. It offers a collection of workflows that can be used to develop and train models to make machine learning robust and efficient. TensorFlow is customizable, and thus, helps developers create experiential learning architectures and work on the same to produce desired results.'
    ]
    for s in samples:
        result = mcq_generator.distractors(s)
        print(result)
