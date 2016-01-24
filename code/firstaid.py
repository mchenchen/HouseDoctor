import requests
from lxml import html

PAGE_URL = "http://www.webmd.com/first-aid/bruises-treatment"



def extract_instructions(info):

    # in html tree need to go to div textArea, exclude div callout911
    instr = []
    headers = []
    i = 1
    doctree = html.fromstring(page.content) # doctree can be parsed with xpath

    # find header elem
    find = '//div[@id="textArea"]//*[not(name()="a") and starts-with(text(),"1.")]'
    this = doctree.xpath(find)
    


    return this

if __name__ == "__main__":
    page = requests.get(PAGE_URL)
    print extract_instructions(page)
    
