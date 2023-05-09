package asg.dev;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
//        SAXLocalNameCount.processXMLFile("personal-schema.xml");
        DOMLocalNameCount.processXMLFile("personal-schema.xml");
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

    static class DOMLocalNameCount implements ErrorHandler {
        static final String JAXP_SCHEMA_LANGUAGE =
                "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

        static final String W3C_XML_SCHEMA =
                "http://www.w3.org/2001/XMLSchema";

        static final String JAXP_SCHEMA_SOURCE =
                "http://java.sun.com/xml/jaxp/properties/schemaSource";
        private PrintStream out;

        private int indent;

        private DOMLocalNameCount(PrintStream out) {
            this.out = out;

        }
        public static void processXMLFile(String xmlFileName) throws Exception{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(true);

            //simple XML data processing
            dbf.setCoalescing(true);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setExpandEntityReferences(true);
            dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            dbf.setAttribute(JAXP_SCHEMA_SOURCE, new File("personal.xsd"));

            DocumentBuilder db = dbf.newDocumentBuilder();
            DOMLocalNameCount dln = new DOMLocalNameCount(System.out);
            db.setErrorHandler(dln);
            BufferedReader br = new BufferedReader(new InputStreamReader(dln.getClass().getClassLoader().getResourceAsStream(xmlFileName)));
            org.w3c.dom.Document doc = db.parse(new InputSource(br));
            dln.echo(doc);
            System.out.println("#".repeat(50));

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            Node node = doc.getElementById("one.worker");
//            DOMSource ds = new DOMSource(doc);
            DOMSource ds = new DOMSource(node);
            StreamResult sr = new StreamResult(System.out);
            t.transform(ds,sr);

        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            out.println("warning: " + e);
            throw new SAXException(e);
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            out.println("error: " +e);
            throw new SAXException(e);
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            out.println("fatal-error: " + e);
            throw new SAXException(e);
        }

        private void printlnCommon(Node n) {
            out.print(" nodeName=\"" + n.getNodeName() + "\"");

            String val = n.getNamespaceURI();
            if (val != null) {
                out.print(" uri=\"" + val + "\"");
            }

            val = n.getPrefix();

            if (val != null) {
                out.print(" pre=\"" + val + "\"");
            }

            val = n.getLocalName();
            if (val != null) {
                out.print(" local=\"" + val + "\"");
            }

            val = n.getNodeValue();
            if (val != null) {
                out.print(" nodeValue=");
                if (val.trim().equals("")) {
                    // Whitespace
                    out.print("[WS]");
                }
                else {
                    out.print("\"" + n.getNodeValue() + "\"");
                }
            }
            out.println();
        }

        private void outputIndentation() {
            for (int i = 0; i < indent; i++) {
                out.print(" ");
            }
        }

        private void echo(Node n) {
            outputIndentation();
            int type = n.getNodeType();

            switch (type) {
                case Node.ATTRIBUTE_NODE -> {
                    out.print("ATTR:");
                    printlnCommon(n);
                }
                case Node.CDATA_SECTION_NODE -> {
                    out.print("CDATA:");
                    printlnCommon(n);
                }
                case Node.COMMENT_NODE -> {
                    out.print("COMM:");
                    printlnCommon(n);
                }
                case Node.DOCUMENT_FRAGMENT_NODE -> {
                    out.print("DOC_FRAG:");
                    printlnCommon(n);
                }
                case Node.DOCUMENT_NODE -> {
                    out.print("DOC:");
                    printlnCommon(n);
                }
                case Node.DOCUMENT_TYPE_NODE -> {
                    out.print("DOC_TYPE:");
                    printlnCommon(n);
                    NamedNodeMap nodeMap = ((DocumentType) n).getEntities();
                    indent += 2;
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Entity entity = (Entity) nodeMap.item(i);
                        echo(entity);
                    }
                    indent -= 2;
                }
                case Node.ELEMENT_NODE -> {
                    out.print("ELEM:");
                    printlnCommon(n);
                    NamedNodeMap atts = n.getAttributes();
                    indent += 2;
                    for (int i = 0; i < atts.getLength(); i++) {
                        Node att = atts.item(i);
                        echo(att);
                    }
                    indent -= 2;
                }
                case Node.ENTITY_NODE -> {
                    out.print("ENT:");
                    printlnCommon(n);
                }
                case Node.ENTITY_REFERENCE_NODE -> {
                    out.print("ENT_REF:");
                    printlnCommon(n);
                }
                case Node.NOTATION_NODE -> {
                    out.print("NOTATION:");
                    printlnCommon(n);
                }
                case Node.PROCESSING_INSTRUCTION_NODE -> {
                    out.print("PROC_INST:");
                    printlnCommon(n);
                }
                case Node.TEXT_NODE -> {
                    out.print("TEXT:");
                    printlnCommon(n);
                }
                default -> {
                    out.print("UNSUPPORTED NODE: " + type);
                    printlnCommon(n);
                }
            }

            indent++;
            for (Node child = n.getFirstChild(); child != null;
                 child = child.getNextSibling()) {
                echo(child);
            }
            indent--;
        }

        public Node findSubNode(String name, Node node) {
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                System.err.println("Error: Search node not of element type");
                System.exit(22);
            }

            if (! node.hasChildNodes()) return null;

            NodeList list = node.getChildNodes();
            for (int i=0; i < list.getLength(); i++) {
                Node subnode = list.item(i);
                if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                    if (subnode.getNodeName().equals(name))
                        return subnode;
                }
            }
            return null;
        }

        public String getText(Node node) {
            StringBuffer result = new StringBuffer();
            if (! node.hasChildNodes()) return "";

            NodeList list = node.getChildNodes();
            for (int i=0; i < list.getLength(); i++) {
                Node subnode = list.item(i);
                if (subnode.getNodeType() == Node.TEXT_NODE) {
                    result.append(subnode.getNodeValue());
                }
                else if (subnode.getNodeType() == Node.CDATA_SECTION_NODE) {
                    result.append(subnode.getNodeValue());
                }
                else if (subnode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                    // Recurse into the subtree for text
                    // (and ignore comments)
                    result.append(getText(subnode));
                }
            }

            return result.toString();
        }


    }
}