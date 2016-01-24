import requests
from lxml import html
from lxml import etree

PAGE_URL = "http://www.webmd.com/first-aid/frostbite-treatment"
#http://www.webmd.com/first-aid/bruises-treatment
#http://www.webmd.com/first-aid/frostbite-treatment

def extract_instructions(info):

    # in html tree need to go to div textArea
    instructions = []
    headers = []
    doctree = html.fromstring(page.content) # doctree can be parsed with xpath

    # find headers
    find = '//div[@id="textArea"]//*[not(name()="a") and starts-with(text(),"1.")]'
    header_node = str((doctree.xpath(find))[0])
    header_elem = (header_node.split(' '))[1]
    header_path = '//div[@id="textArea"]//'+header_elem+'[string-length(text())>0 and text()!=" "]/text()'
    headers = doctree.xpath(header_path)

    for i in range(len(headers)):
        
        # find siblings i.e. content nodes
        if header_elem == 'b':
            content_path = '//div[@id="textArea"]/p[(b/text())="'+headers[i]+'"]/following-sibling::*[1]/descendant-or-self::*[name()="li"]'
        else:
            content_path = '//div[@id="textArea"]//'+header_elem+'[text()="'+headers[i]+'"]/following-sibling::*[position()=1 or position()=2]/descendant-or-self::*[name()="li" or name()="p"]'
        content_elem = doctree.xpath(content_path) # not a string, this is the node itself

        # get contents
        body_elems = []
        for elem in content_elem:
            etree.strip_tags(elem,'a')  # flatten links inside <a href>
            current_text = elem.text
            if not current_text.startswith('\r\n'):
                body_elems.append(current_text)
        body = '\n'.join(body_elems)    # create body string

        # create response object
        current_instruction = {"heading": headers[i], "body": body}
        instructions.append(current_instruction)
        
    return instructions

if __name__ == "__main__":
    page = requests.get(PAGE_URL)
    print extract_instructions(page)
    
