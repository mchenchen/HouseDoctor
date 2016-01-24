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
    if status == 1:
        json_response['formatted_response'] = response
    else:
        json_response['response'] = response
    resp.message(json.dumps(json_response))    
    return str(resp)
 
if __name__ == "__main__":
    port = int(os.environ.get('PORT', 33507))
    print("Binding to port: " + str(port))
    app.run(host='0.0.0.0', port=port, debug=True)