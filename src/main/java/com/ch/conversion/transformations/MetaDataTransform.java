package com.ch.conversion.transformations;


import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.helpers.XmlHelper;
import org.json.JSONObject;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class MetaDataTransform {

    private final ITransformConfig config;
    private final XmlHelper helper;

    private final String xml;
    private final JSONObject meta;

    /**
     * Apply transforms to meta data.
     *
     * @param config json and xml
     * @param xml    xml to transform
     * @param meta   form meta data
     */
    public MetaDataTransform(ITransformConfig config, String xml, JSONObject meta) {
        this.config = config;
        this.xml = xml;
        this.meta = meta;
        helper = XmlHelper.getInstance();
    }

    /**
     * Add xml root and and meta data.
     *
     * @return xml
     */
    public String getXml() {
        StringBuilder builder = new StringBuilder();
        builder
            .append("<?xml version='1.0'?>")
            .append(getOpenTag())
            .append(xml)
            .append(getCloseTag());
        return builder.toString();
    }

    private String getOpenTag() {
        StringBuilder builder = new StringBuilder();
        builder
            .append("<" + config.getRootElementNameOut())
            // form type
            .append(helper.createAttributeFromJson(meta,
                config.getMetaPropertyNameIn(),
                config.getFormTypePropertyNameIn(),
                config.getFormTypeAttributeNameOut()))
            // form version
            .append(helper.createAttributeFromJson(meta,
                config.getMetaPropertyNameIn(),
                config.getFormVersionPropertyNameIn(),
                config.getFormVersionAttributeNameOut()))
            .append(">");
        return builder.toString();
    }

    private String getCloseTag() {
        return "</" + config.getRootElementNameOut() + ">";
    }
}
