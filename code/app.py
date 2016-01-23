from flask import Flask, request, redirect
import twilio.twiml
import os
from WebMd import WebMd
from TopicExtractor import TopicExtractor
 
 
app = Flask(__name__)

webmd = WebMd() 
topic_extractor = TopicExtractor()
 
@app.route("/", methods=['GET', 'POST'])
def hello_monkey():
    """Respond to incoming calls with a simple text message."""
    incoming_message = ""
    print request.form
    resp = twilio.twiml.Response()
    resp.message('Hello World')
    #resp.message(webmd.search(topic_extractor.extract(incoming_message)))
    return str(resp)
 
if __name__ == "__main__":
    port = int(os.environ.get('PORT', 33507))
    print("Binding to port: " + str(port))
    app.run(host='0.0.0.0', port=port, debug=True)