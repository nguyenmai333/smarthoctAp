import sys
import os
sys.path.append(os.path.abspath("question_generation"))
from question_generation.pipelines import pipeline
from transformers import pipeline

class mcq:
    '''
        This Object Allow User Give a sentences through 2 pipelines:
        - Question Generate And Answer Generate
        - Create distractors to misleading reader (maybe 3)
    '''
    def __init__(self):
        self.mcq_generator = pipeline('question-generation', model="valhalla/t5-base-qg-hl")
        self.unmasker = pipeline('fill-mask', model='bert-base-uncased')
    def genQA(self, text):
        return self.mcq_generator(text)
    def distractors(self, sample):
        dict_ = self.genQA(sample)
        answer, question = dict_['answer'], dict_['question']
        if sample.find(answer) == -1:
            print('oops, maybe your model have a wrong prediction')
            return None
        mask_text = sample.replace(answer, '[MASK]')
        D = self.unmasker(mask_text)
        distractors = [] 
        cnt = 0
        for d in D:
            if d['token_str'] != answer and len(d['token_str'] > 1):
                cnt += 1
                distractors.append(d['token_str'])
            if cnt == 3:
                break
        return  {
            'Question': question,
            'Answer': answer,
            'Distractor': distractors
        }

if __name__ == "__main__":
    mcq_generator = mcq()
    print('generator loaded')
    sample = 'Machine Learning is teach robot how to know teach another one'
    result = mcq_generator.distractors(sample)
    print(result)
