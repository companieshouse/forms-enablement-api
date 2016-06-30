package com.ch.conversion.helpers;

import com.ch.exception.MissingRequiredDataException;
import com.ch.exception.XmlException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public final class XmlHelper {

    private static XmlHelper instance = new XmlHelper();

    private XmlHelper() {
    }

    public static XmlHelper getInstance() {
        return instance;
    }

    /**
     * Create an xml document from a string.
     *
     * @param xml xml string
     * @return xml document
     * @throws XmlException error parsing string to document
     */
    public Document createDocumentFromString(String xml) throws XmlException {
        try {
            // create dom from xml string
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new InputSource(new StringReader(xml)));

        } catch (Exception ex) {
            throw new XmlException(ex, ex.getMessage());
        }
    }

    /**
     * Add a json property as an element to an xml document.
     *
     * @param xml          xml document
     * @param xmlLocation  where to add element
     * @param json         json object to get property from
     * @param propertyName json property to add
     * @param elementName  name of the xml element to add
     * @throws XmlException                 error parsing xml
     * @throws MissingRequiredDataException error getting property from json
     */
    public void addJsonValueAsElementToXml(Document xml, JSONObject json, String xmlLocation, String parentName,
                                           String propertyName,
                                           String
                                               elementName) throws XmlException, MissingRequiredDataException {
        try {
            // get where to add element
            NodeList nodes = xml.getElementsByTagName(xmlLocation);

            // get value from json
            Object value = json.get(propertyName);
            // insert element
            insert(xml, nodes, elementName, value);

        } catch (JSONException ex) {
            throw new MissingRequiredDataException(ex, propertyName, parentName);

        } catch (Exception ex) {
            throw new XmlException(ex, ex.getMessage());
        }
    }

    /**
     * Add an element with a value to xml document.
     *
     * @param xml          document
     * @param xmlLocation  where to add element in xml
     * @param elementName  name of element to add
     * @param elementValue value of the element
     */
    public void addElementToXml(Document xml, String xmlLocation, String elementName, String elementValue) throws XmlException {
        try {
            // get where to add element
            NodeList nodes = xml.getElementsByTagName(xmlLocation);
            insert(xml, nodes, elementName, elementValue);

        } catch (Exception ex) {
            throw new XmlException(ex, ex.getMessage());
        }
    }

    /**
     * Insert xml element into document.
     *
     * @param xml          xml document
     * @param nodes        where to insert
     * @param elementName  xml element name
     * @param elementValue xml element value
     */
    public void insert(Document xml, NodeList nodes, String elementName, Object elementValue) throws XmlException {
        try {
            // create xml element e.g. <name>value</name>
            Element element = xml.createElement(elementName);
            if (elementValue != null) {
                Text text = xml.createTextNode(elementValue.toString());
                element.appendChild(text);
            }
            // append to nodes
            Node node = nodes.item(0);
            node.appendChild(element);

        } catch (Exception ex) {
            throw new XmlException(ex, ex.getMessage());
        }
    }

    /**
     * Create an xml attribute from a json property value.
     *
     * @param json          json object
     * @param propertyName  json property containing value
     * @param attributeName what to call the attribute
     * @return attribute string
     */
    public String createAttributeFromJson(JSONObject json, String parentName, String propertyName, String attributeName) {
        JsonHelper jsonHelper = JsonHelper.getInstance();
        Object value = jsonHelper.getValueFromJson(json, parentName, propertyName);
        return createAttribute(attributeName, value);
    }

    /**
     * Create the string for an xml attribute.
     *
     * @param attributeName  xml attribute name
     * @param attributeValue xml attribute value
     * @return xml attribute string
     */
    public String createAttribute(String attributeName, Object attributeValue) {
        // e.g. attributeName='value'
        // if value is null: attributeName=''
        StringBuilder builder = new StringBuilder();
        builder
            .append(" ")
            .append(attributeName)
            .append("=")
            .append("'");

        if (attributeValue != null && !attributeValue.toString().equals("null")) {
            builder.append(attributeValue);
        }

        builder.append("'");
        return builder.toString();
    }

    /**
     * Convert an xml document into a string.
     *
     * @param doc xml document
     * @return string representation
     * @throws XmlException error parsing xml
     */
    public String getStringFromDocument(Document doc) throws XmlException {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();

        } catch (Exception ex) {
            throw new XmlException(ex, ex.getMessage());
        }
    }

    /**
     * Get the value of an attribute from an element.
     *
     * @param doc           xml
     * @param elementName   xml element
     * @param attributeName xml attribute
     * @return attribute value
     */
    public String getAttributeValueFromDocument(Document doc, String elementName, String attributeName) {
        NodeList nodeList = doc.getElementsByTagName(elementName);
        // if no elements, throw exception
        if (nodeList.getLength() == 0) {
            throw new XmlException(String.format("Element %s, not present in xml document.", elementName));
        }
        // get the attribute from first element
        Node attribute = nodeList.item(0).getAttributes().getNamedItem(attributeName);
        if (attribute == null) {
            throw new XmlException(String.format("Attribute '%s', not present in Element '%s' in the xml document.", attributeName,
                elementName));
        }
        return attribute.getNodeValue();
    }
}
