#House Doctor

House Doctor uses the twilio API to enable access to medical and first aid information without the use of wifi.

With the tech boom occurring all over the world, but most notably in places like India, China, Pakistan and countries in Africa, people have access to affordable smart phones but not health services and internet. HealthApp's original purpose was to help save lives by making medical information more reliably accessible to blue collar workers removed from social services and communication.

HouseDoctor can be used by hikers, adventurers, explorers, and workers to get medical and first aid information even in the toughest circumstances.

## Usage

1. Download ntlk data into code/nltk_data.
2. Deploy the code directory on a web server. Copy down the domain. 
3. Create a twilio account. Add the new twilio number in MainActivity.java, build the application and install on your phone. Add the domain to the messaging url in twilio.

## How It Works

When a query comes in, House Doctor uses **IBM Blumix** to extract the verb-object relations, and in essence, get the key words. Users can enter phrases such as, 'I cut my hand on a knife.'

The backend uses the **bing API** to scrape the first three page results and preferences webmd. 

For processing the webpages, there is a cascaded system where it attempts to extract data based on a format and return a list of objects containing headings and bodies using **lxml** and **BeautifulSoup**. This works for first-aid pages which have the most consistency. If it cannot detect a format, it scrapes all the information and runs it through a summarizer, **sumy**, returning a body of text instead.

## Challenges

The biggest challenge was extracting data from WebMd. WebMd doesn't have a consistent format between pages, even between the first-aid pages. Processing the page for the content and then processing the content itself was the biggest challenge we faced.