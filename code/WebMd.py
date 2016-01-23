from py_bing_search import PyBingSearch
from Summarizer import Summarizer

class WebMd:
    
    def __init__(self):
        self.APIKEY = open("apikey.txt").read()
        self.bing = PyBingSearch(self.APIKEY)
        self.headers = {'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11',
       'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
       'Accept-Charset': 'ISO-8859-1,utf-8;q=0.7,*;q=0.3',
       'Accept-Encoding': 'none',
       'Accept-Language': 'en-US,en;q=0.8',
       'Connection': 'keep-alive'}
        self.summarizer = Summarizer()
        
    def search(self, s, limit=1):
        """Returns top limit number of html summarized page results from searching s using bing
        """
        result_list, next_uri = self.bing.search(s + " treatment webmd", limit=limit, format='json')
        summary = ""
        for result in result_list:
            summary = summary + self.summarizer.summarizeUrl(result.url)
        return summary