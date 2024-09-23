import sys
import os
sys.path.append(os.path.abspath("question_generation"))
from question_generation.pipelines import pipeline

class mcq:
    def __init__(self, module, model_size):
        self.mcq_generator = pipeline(module, model=model_size)

    def genText(self, text):
        return self.mcq_generator(text)

if __name__ == "__main__":
    mcq_generator = mcq('question-generation', "valhalla/t5-base-qg-hl")
    print('generator loaded')
    sample = 'if you have 2 penis, the best choice is big penis'
    result = mcq_generator.genText(sample)
    print(result)
