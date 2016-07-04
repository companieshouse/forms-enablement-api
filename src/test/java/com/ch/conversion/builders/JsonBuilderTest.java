package com.ch.conversion.builders;

import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.MissingRequiredDataException;
import com.ch.helpers.TestHelper;
import com.ch.model.FormsPackage;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class JsonBuilderTest extends TestHelper {

    ITransformConfig config;

    @Before
    public void setUp() {
        config = new TransformConfig();
    }

    @Test(expected = JSONException.class)
    public void throwsJSONExceptionWithInvalidJson() throws Exception {
        String invalid = getStringFromFile(INVALID_JSON_PATH);
        List<String> invalid_forms = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            invalid_forms.add(invalid);
        }
        JsonBuilder builder = new JsonBuilder(config, invalid, invalid_forms);
        builder.getTransformedPackage();
    }

    // TODO: is this the desired behaviour?
    @Test(expected = MissingRequiredDataException.class)
    public void throwsMissingRequiredDataExceptionWithValidJsonMissingRequiredData() throws Exception {
        String valid = getStringFromFile(VALID_JSON_PATH);
        List<String> valid_json_forms = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            valid_json_forms.add(valid);
        }
        JsonBuilder builder = new JsonBuilder(config, valid, valid_json_forms);
        builder.getTransformedPackage();
    }

    // TODO: what to assert?
    @Test
    public void createStringForValidJson() throws Exception {
        JsonBuilder builder = getValidJsonBuilder();
        FormsPackage transformedPackage = builder.getTransformedPackage();
        Assert.assertNotNull(transformedPackage);
    }

    @Test(expected = Exception.class)
    public void throwsExceptionWithEmptyMultiPart() throws Exception {
        FormDataMultiPart multi = new FormDataMultiPart();
        JsonBuilder builder = new JsonBuilder(config, multi);
        builder.getTransformedPackage();
    }

    @Test
    public void createStringForValidMultiPart() throws Exception {
        FormDataMultiPart multi = getValidMultiPart();
        JsonBuilder builder = new JsonBuilder(config, multi);
        FormsPackage transformedPackage = builder.getTransformedPackage();
        Assert.assertNotNull(transformedPackage);
    }

    private JsonBuilder getValidJsonBuilder() throws Exception {
        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid forms
        String valid = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            valid_forms.add(valid);
        }
        // builder
        return new JsonBuilder(config, package_string, valid_forms);
    }

    private FormDataMultiPart getValidMultiPart() throws IOException {
        FormDataMultiPart multi = new FormDataMultiPart();
        // forms package data
        String pack = getStringFromFile(PACKAGE_JSON_PATH);
        multi.field(config.getPackageMultiPartName(), pack, MediaType.APPLICATION_JSON_TYPE);
        // form json
        String form = getStringFromFile(FORM_ALL_JSON_PATH);
        multi.field("form1", form, MediaType.APPLICATION_JSON_TYPE);
        return multi;
    }
}
