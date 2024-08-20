import easyocr
import cv2
import matplotlib.pyplot as plt

class OCRDetector:
    def __init__(self, languages = ['vi'], gpu = True):
        """
        Initialize the OCRDetector with specified languages.
        """
        self.reader = easyocr.Reader(languages)
        self.raw_text = ''
    
    def detect_text(self, image_path):
        """
        Detect text in the image and return bounding boxes and text.
        """
        image = cv2.imread(image_path)
        results = self.reader.readtext(image_path)
        return results, image
    
    def draw_boxes(self, image, results):
        """
        Draw bounding boxes on the image based on detected text.
        """
        for (bbox, text, prob) in results:
            (top_left, top_right, bottom_right, bottom_left) = bbox
            top_left = tuple(map(int, top_left))
            bottom_right = tuple(map(int, bottom_right))
            cv2.rectangle(image, top_left, bottom_right, (0, 255, 0), 2)
            # Optionally draw text for debug (can be removed if only detection is needed)
            # cv2.putText(image, text, (top_left[0], top_left[1] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)
    
    def concat_text(self, results):
        """
        Concatenate detected text into a single raw text string.
        """
        text_list = [text for (_, text, _) in results]
        self.raw_text = ' '.join(text_list)
        return self.raw_text
    
    def process_image(self, image_path):
        """
        Process the image for text detection and return the result.
        """
        results, image = self.detect_text(image_path)
        self.draw_boxes(image, results)
        raw_text = self.concat_text(results)
        return image, raw_text
    
    def display_image(self, image):
        """
        Display the image with bounding boxes.
        """
        image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        plt.imshow(image_rgb)
        plt.axis('off')
        plt.show()

if __name__ == "__main__":
    pass
    # detector = OCRDetector()
    # image_path = 'test_folder/10.jpg'
    # image, raw_text = detector.process_image(image_path)
    # print("Detected Text:", raw_text)
    # detector.display_image(image)


    # import matplotlib.pyplot as plt
    # from PIL import Image
    # from vietocr.tool.predictor import Predictor
    # from vietocr.tool.config import Cfg
    # config = Cfg.load_config_from_name('vgg_transformer')

    # class ocr_detector():
    #     def __init__():
    #         self.config['device'] = 'cuda'
    #         self.config['predictor']['beamsearch'] = False
    #         self.detector = Predictor(config)
    #         self.true_raw_text = ''
    #     def text_detect(img_path, bbox):
    #         img = Image.open(img_path)
    #         #img.crop(bbox)
    #         result = self.detector.predict(img)
    #         return result
    #     def concat(list_):
    #         cc_str = ''
    #         for li in list_:
    #             cc_str = cc_str + ' ' + li
    #         return cc_str
    #     def core_func(bbox):
    #         list_ = []
    #         for bb in bbox:
    #             list_.append(text_detect(img_path, bbox))
    #         self.true_raw_text = self.concat(list_)
    #         pass
        

    # if __name__ == "__main__":
    #     detector = ocr_detector()
    #     pass