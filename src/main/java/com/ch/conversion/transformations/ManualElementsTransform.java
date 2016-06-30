package com.ch.conversion.transformations;

import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.helpers.XmlHelper;
import org.w3c.dom.Document;

/**
 * Created by elliott.jenkins on 04/04/2016.
 * Transform for manually adding elements into the xml.
 */
public class ManualElementsTransform {

    private final ITransformConfig config;
    private final XmlHelper helper;

    private final Document xml;

    /**
     * Transform to add elements manually to the xml.
     *
     * @param config json and xml
     * @param xml    xml to add to
     * @throws Exception error
     */
    public ManualElementsTransform(ITransformConfig config, String xml) {
        this.config = config;
        helper = XmlHelper.getInstance();
        this.xml = helper.createDocumentFromString(xml);
    }

    /**
     * Get the transformed xml.
     *
     * @return xml
     * @throws Exception error
     */
    public String getXml() {
        // 1. add method to filingDetails
        addMethodElement();
        // return xml
        return helper.getStringFromDocument(xml);
    }

    private void addMethodElement() {
        // <method>enablement</method>
        String location = config.getFilingDetailsPropertyNameIn();
        String elementName = "method";
        String elementValue = "enablement";
        helper.addElementToXml(xml, location, elementName, elementValue);
    }
}
