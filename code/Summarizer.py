from __future__ import absolute_import
from __future__ import division, print_function, unicode_literals
from sumy.parsers.html import HtmlParser
from sumy.parsers.plaintext import PlaintextParser
from sumy.nlp.tokenizers import Tokenizer
from sumy.summarizers.lsa import LsaSummarizer as SumySummarizer
from sumy.nlp.stemmers import Stemmer
from sumy.utils import get_stop_words

class Summarizer:
    
    def __init__(self):
        self.LANG = "english"
        pass
    
    def summarizeText(self, body, numSentences = 10):
        """Summarizes body of text to numSentences
        """
        #parser = PlaintextParser.from_string(body, Tokenizer(self.LANG))
        parser = PlaintextParser.from_string(body, Tokenizer(self.LANG))        
        stemmer = Stemmer(self.LANG)
        summarizer = SumySummarizer(stemmer)
        summarizer.stop_words = get_stop_words(self.LANG)
        summary = ' '.join([str(sentence).decode('utf-8') for sentence in summarizer(parser.document, numSentences)])
        return summary
            

    def summarizeUrl(self, url, numSentences = 10):
        """Summarizes text at a given url to numSentences
        """
        #parser = PlaintextParser.from_string(body, Tokenizer(self.LANG))
        parser = HtmlParser.from_url(url, Tokenizer(self.LANG))        
        stemmer = Stemmer(self.LANG)
        summarizer = SumySummarizer(stemmer)
        summarizer.stop_words = get_stop_words(self.LANG)
        summary = ' '.join([str(sentence).decode('utf-8') for sentence in summarizer(parser.document, numSentences)])
        return summary
        