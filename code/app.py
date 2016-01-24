from flask import Flask, request, redirect, jsonify
import twilio.twiml
import os
from WebMd import WebMd
from TopicExtractor import TopicExtractor
import nltk
import json
 
app = Flask(__name__)
nltk.data.path.append('./nltk_data/')
webmd = WebMd() 
topic_extractor = TopicExtractor()
 
@app.route("/", methods=['GET', 'POST'])
def hello_monkey():
    """Respond to incoming calls with a simple text message."""
    incoming_message = request.args.get('Body')
    resp = twilio.twiml.Response()
    response, status = webmd.search(topic_extractor.extract(incoming_message))
    json_response = {
        'formatted': status,
        'formatted_response':[],
        'response':''
    }
    count = 10 # avoid infinite while-loop
    if status == 1:
        while(count>0):
            json_response['formatted_response'] = response
            if(len(str(json_response))<1600):
                break
            response = response[0:len(response)-1]
            count = count - 1
    else:
        if len(response) > 1500:
            response = response[0:1500]
        json_response['response'] = response
    resp.message(json.dumps(json_response))    
    return str(resp)
 
if __name__ == "__main__":
    port = int(os.environ.get('PORT', 33507))
    print("Binding to port: " + str(port))
    app.run(host='0.0.0.0', port=port, debug=True)