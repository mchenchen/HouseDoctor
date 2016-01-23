import requests

class TopicExtractor():
    
    def __init__(self):
        self.apikey = open('bluemixapikey.txt').read()
        #self.endpoint = "http://gateway-a.watsonplatform.net/calls/text/TextGetRankedKeywords"
        self.endpoint = "http://access.alchemyapi.com/calls/text/TextGetRelations"
    def extract(self, text):
        """Extract keywords using IBM bluemix
        """
        if len(text.split(' ')) < 3:
            return text
        parameters = {
            'apikey': self.apikey,
            'text': text,
            'outputMode': 'json',
        }
        response = requests.get(self.endpoint, params=((k,v) for k, v in parameters.iteritems())).json()
        keywords = ' '.join([relation['action']['text'] + " " + relation['object']['text']  for relation in response['relations']])     
        return keywords 