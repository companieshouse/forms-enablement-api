package com.ch.conversion.builders;

import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.MissingRequiredDataException;
import com.ch.helpers.TestHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class FormJsonBuilderTest extends TestHelper {

    ITransformConfig config;

    @Before
    public void setUp() {
        config = new TransformConfig();
    }

    @Test(expected = JSONException.class)
    public void throwsJSONExceptionWithInvalidJson() throws Exception {
        String invalid = getStringFromFile(INVALID_JSON_PATH);
        FormJsonBuilder builder = new FormJsonBuilder(config, invalid, invalid);
        builder.getJson();
    }

    @Test(expected = MissingRequiredDataException.class)
    public void throwsMissingRequiredDataExceptionWithValidJsonMissingRequiredData() throws Exception {
        String valid = getStringFromFile(VALID_JSON_PATH);
        FormJsonBuilder builder = new FormJsonBuilder(config, valid, valid);
        builder.getJson();
    }

    @Test
    public void createJSONObjectForValidJson() throws Exception {
        FormJsonBuilder builder = getValidFormJsonBuilder();
        JSONObject json = builder.getJson();
        Assert.assertNotNull(json);
    }

    private FormJsonBuilder getValidFormJsonBuilder() throws Exception {
        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid form data
        String form_string = getStringFromFile(FORM_ALL_JSON_PATH);
        // builder
        return new FormJsonBuilder(config, package_string, form_string);
    }
}
