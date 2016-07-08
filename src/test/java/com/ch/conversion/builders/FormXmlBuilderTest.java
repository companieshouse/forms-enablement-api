package com.ch.conversion.builders;

import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.MissingRequiredDataException;
import com.ch.helpers.TestHelper;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class FormXmlBuilderTest extends TestHelper {

    ITransformConfig config;

    @Before
    public void setUp() {
        config = new TransformConfig();
    }

    @Test(expected = MissingRequiredDataException.class)
    public void throwsMissingRequiredDataExceptionWithValidJsonMissingRequiredData() throws Exception {
        String valid = getStringFromFile(VALID_JSON_PATH);
        JSONObject json = new JSONObject(valid);
        FormXmlBuilder builder = new FormXmlBuilder(config, json, json, json);
        builder.getXML();
    }

    @Test
    public void createEncodedXmlForValidJson() throws Exception {
        FormXmlBuilder builder = getValidFormXmlBuilder();
        String xml = builder.getXML();
        Assert.assertNotNull(xml);
    }

    private FormXmlBuilder getValidFormXmlBuilder() throws Exception {
        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        JSONObject package_json = new JSONObject(package_string);
        // valid meta data
        String meta_string = getStringFromFile(META_PATH);
        JSONObject meta_json = new JSONObject(meta_string);
        // valid form data
        String form_string = getStringFromFile(FORM_JSON_PATH);
        JSONObject form_json = new JSONObject(form_string);
        // builder
        return new FormXmlBuilder(config, package_json, meta_json, form_json);
    }
}
