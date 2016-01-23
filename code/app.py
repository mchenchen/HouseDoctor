from flask import Flask, request, redirect
import twilio.twiml
import os
 
app = Flask(__name__)
 
@app.route("/", methods=['GET', 'POST'])
def hello_monkey():
    """Respond to incoming calls with a simple text message."""
 
    resp = twilio.twiml.Response()
    resp.message("Hello, Mobile Monkey")
    return str(resp)
 
if __name__ == "__main__":
    port = int(os.environ.get('PORT', 33507))
    print("Binding to port: " + str(port))
    app.run(host='0.0.0.0', port=port, debug=True)