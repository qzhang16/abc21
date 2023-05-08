package asg.dev;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws Exception{
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");
        System.out.println(StringUtils.capitalize("hello, world !"));
//
//        // Press Shift+F10 or click the green arrow button in the gutter to run the code.
//        for (int i = 1; i <= 5; i++) {
//
//            // Press Shift+F9 to start debugging your code. We have set one breakpoint
//            // for you, but you can always add more by pressing Ctrl+F8.
//            System.out.println("i = " + i);
//        }
        SAXLocalNameCount.processXMLFile("personal-schema.xml");
    }

    static class SAXLocalNameCount extends DefaultHandler {
        static final String JAXP_SCHEMA_LANGUAGE =
                "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

        static final String W3C_XML_SCHEMA =
                "http://www.w3.org/2001/XMLSchema";

        static final String JAXP_SCHEMA_SOURCE =
                "http://java.sun.com/xml/jaxp/properties/schemaSource";
        HashMap<String, Integer> tags;
        ArrayList<String> textData;
        public static void processXMLFile(String xmlFileName) throws Exception{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setValidating(true);
            SAXParser sp = spf.newSAXParser();
            sp.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            XMLReader xr = sp.getXMLReader();
            SAXLocalNameCount sln = new SAXLocalNameCount();
            xr.setContentHandler(sln);
            xr.setErrorHandler(sln);
//            xr.setErrorHandler()
            BufferedReader br = new BufferedReader(new InputStreamReader(sln.getClass().getClassLoader().getResourceAsStream(xmlFileName)));
            xr.parse(new InputSource(br));

        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            System.out.println("warning: " + e);
            throw new SAXException(e);
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            System.out.println("error: " +e);
            throw new SAXException(e);
        }

        @Override
        public void startDocument() throws SAXException {
            tags = new HashMap<String, Integer>();
            textData = new ArrayList<String>();
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException{

            Integer value = tags.get(localName);

            tags.put(localName, (value == null ) ? 1 : value + 1);

        }

        @Override
        public void characters(char[] ch, int start, int length) {
            String str = new String(ch,start,length);
            if ( ! ( str.isEmpty() || str.isBlank() ) )  textData.add(str);
        }

        @Override
        public void endDocument() throws SAXException{
            if (tags.size() > 0 ) {
                tags.forEach((k,v) -> System.out.println("localName \"" + k + "\" occurs " + v + " time(s)"));
            }

            if (textData.size() > 0) {
                textData.forEach(e -> System.out.println("Text: " + e));
            }
        }



    }
}