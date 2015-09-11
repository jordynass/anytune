package jordangreenblatt.anytuneusa;

import java.net.URL;

class RetrieveFeedTask extends AsyncTask<URL, Void, RSSFeed> {

    protected RSSFeed doInBackground(URL... urls) {
        try {
            URL url= new URL(urls[0]);
            SAXParserFactory factory =SAXParserFactory.newInstance();
            SAXParser parser=factory.newSAXParser();
            XMLReader xmlreader=parser.getXMLReader();
            RssHandler theRSSHandler=new RssHandler();
            xmlreader.setContentHandler(theRSSHandler);
            InputSource is=new InputSource(url.openStream());
            xmlreader.parse(is);
            return theRSSHandler.getFeed();
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(RSSFeed feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}