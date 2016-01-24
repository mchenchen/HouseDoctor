from py_bing_search import PyBingSearch
from Summarizer import Summarizer
import requests
import urllib2
from bs4 import BeautifulSoup

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

    def extractUrlStructuredText(self, url):
        """Extracts data from webmd url and provides a list of objects containing the heading and body
        """
        html = self.getUrl(url)    
        Soup = BeautifulSoup(html)
        soup = Soup.find('div', {'class':'hwDefinition_fmt'}) # better condition but doesn't always exist
        if soup == None:
            soup = Soup.find('div', {'id':'textArea'}) # generally always exists
        body = ""
        blocks = [] # list of objects containing heading and body
        heading = ""
        body = ""
        startNew = False
        skip = False
        for child in soup.recursiveChildGenerator():
            name = getattr(child, "name", None)
            if skip:
                skip = False
                continue
            if startNew:
                heading = child
                body = ""
                startNew = False
                continue
            if name in ['script', 'style']:
                skip = True
                continue
            if name in ['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'b']:
                blocks.append({'heading':heading, 'body':body})
                startNew = True
            if name is not None:
                pass
            elif not child.isspace(): # leaf node, don't print spaces
                body = body + " " + child
        if len(blocks)>1:
            return blocks[1::]
        return []
        
    def extractUrlText(self, url):
        """Extracts content text from webmd url
        """
        html = self.getUrl(url)    
        Soup = BeautifulSoup(html)
        soup = Soup.find('div', {'class':'hwDefinition_fmt'}) # better condition but doesn't always exist
        if soup == None:
            soup = Soup.find('div', {'id':'textArea'}) # generally always exists
        skipNext = False
        body = ""
        for child in soup.recursiveChildGenerator():
            if skipNext:
                skipNext = False
                continue
            name = getattr(child, "name", None)
            if name in ["script", "style"]:
                skipNext = True
            if name is not None:
                pass
            elif not child.isspace(): # leaf node, don't print spaces
                body = body + child
        return body
                   
    def getUrl(self, url):
        """Attempts to summarize webpage contents (assuming webmd url) 
        """
        hdr = {'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11',
               'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
               'Accept-Charset': 'ISO-8859-1,utf-8;q=0.7,*;q=0.3',
               'Accept-Encoding': 'none',
               'Accept-Language': 'en-US,en;q=0.8',
               'Connection': 'keep-alive'}
        req = urllib2.Request(url, headers=hdr)
        response = urllib2.urlopen(req).read()
        #response = requests.get(test_url)
        #response = urllib2.urlopen(test_url).read()        
        return response

    def isFirstAidPage(self, url):
        
        #########
        
        return False
        
        
    def search(self, s, limit=3):
        """Searches top limit number of bing searches.
           Returns the summarized/unsummarized data and the format code (0=no format, 1=formatted)
        """
        result_list, next_uri = self.bing.search(s + " treatment webmd", limit=limit, format='json')
        
        ########### Xiuyan's processing. First Aid type instruction format ##########
        for result in result_list:
            print(result.url)
            if self.isFirstAidPage(result.url):
                
                print("First Aid WebMd Page")
                
                page = requests.get(result.url)
                return extract_instructions(page)
                
        ########## Rahman's processing. Returns structured data representing all of first link #############
        try:
            blocks = self.extractUrlStructuredText(result_list[0].url)
            return (blocks, 1)
        except:
            print("Able to structure into headers and body")

        ########### Rahman's processing for 'other' pages. Attempts to summarize all first three links ###########  
        content = ""      
        for result in result_list:
            try:
                content = content + self.extractUrlText(result.url)
            except Exception, e:
                print(e)
                pass
        if content == "":
            print("Other WebMd Page")
            return (self.summarizer.summarizeText(content), 0)
        
        ########### Worst case: summarize first url ################
        print("Summarizing first")
        return (self.summarizer.summarizeUrl(result_list[0].url), 0)
        
