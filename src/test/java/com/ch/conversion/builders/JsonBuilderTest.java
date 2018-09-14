package com.ch.conversion.builders;

import com.ch.client.PresenterHelper;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.MissingRequiredDataException;
import com.ch.helpers.TestHelper;
import com.ch.model.FormsPackage;
import com.ch.model.PresenterAuthResponse;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


import javax.ws.rs.core.MediaType;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class JsonBuilderTest extends TestHelper {

    ITransformConfig config;
    PresenterHelper helper;

    @Before
    public void setUp() {

        config = new TransformConfig();
        helper = mock(PresenterHelper.class);
        when(helper.getPresenterResponse(anyString(), anyString())).thenReturn(new PresenterAuthResponse("1234567"));

    }

    @Test(expected = JSONException.class)
    public void throwsJSONExceptionWithInvalidJson() throws Exception {
        String invalid = getStringFromFile(INVALID_JSON_PATH);
        List<String> invalid_forms = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            invalid_forms.add(invalid);
        }
        JsonBuilder builder = new JsonBuilder(config, invalid, invalid_forms);
        builder.getTransformedPackage();
    }

    @Test(expected = MissingRequiredDataException.class)
    public void throwsMissingRequiredDataExceptionWithValidJsonMissingRequiredData() throws Exception {
        String valid = getStringFromFile(VALID_JSON_PATH);
        List<String> valid_json_forms = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            valid_json_forms.add(valid);
        }
        JsonBuilder builder = new JsonBuilder(config, valid, valid_json_forms);
        builder.getTransformedPackage();
    }

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
        for (int i = 0; i < 2; i++) {
            valid_forms.add(valid);
        }
        // builder
        return new JsonBuilder(config, package_string, valid_forms);
    }

    @Test
    public void shouldAddValidSubmissionNumberToAllElements() throws Exception {
        //Given a valid package is submitted
        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid forms
        String valid = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms = new ArrayList<>();
        valid_forms.add(valid);
        valid_forms.add(valid.replaceAll("J53W9DA1", "J53W9DA2"));

        // when this package is transformed
        FormsPackage pack =  new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

        //all elements should contain a unique submissionReference
        String formOneBarcode = pack.getFormsJSon().get(0).getString(config.getBarcodePropertyNameOut());
        String formTwoBarcode = pack.getFormsJSon().get(1).getString(config.getBarcodePropertyNameOut());
        
        String formOnePackageIdentifier = pack.getFormsJSon().get(0).getString(config.getPackageIdentifierElementNameOut());
        String formTwoPackageIdentifier = pack.getFormsJSon().get(1).getString(config.getPackageIdentifierElementNameOut());

        String formOneSubmissionReference = pack.getFormsJSon().get(0).getString(config.getSubmissionReferenceElementNameOut());
        String formTwoSubmissionReference = pack.getFormsJSon().get(1).getString(config.getSubmissionReferenceElementNameOut());

        Assert.assertTrue(formOneSubmissionReference.equals(formOnePackageIdentifier + "-" + formOneBarcode));
        Assert.assertTrue(formTwoSubmissionReference.equals(formTwoPackageIdentifier + "-" + formTwoBarcode));
        Assert.assertTrue(!formOneSubmissionReference.equals(formTwoSubmissionReference));
        
        String formOneBase64Xml = pack.getFormsJSon().get(0).getString(config.getXmlPropertyNameOut());
        String formOneXml =  new String (Base64.decodeBase64(formOneBase64Xml));
        
        String formTwoBase64Xml = pack.getFormsJSon().get(1).getString(config.getXmlPropertyNameOut());
        String formTwoXml =  new String (Base64.decodeBase64(formTwoBase64Xml));
        
        Assert.assertTrue(formOneXml.contains("<submissionReference>12345-J53W9DA1</submissionReference>"));
        Assert.assertTrue(formTwoXml.contains("<submissionReference>12345-J53W9DA2</submissionReference>"));
    }
    
    @Test
    public void packageShouldHaveStausPendingIfNoFeeFormsArePresent() throws Exception {
        //Given a valid package is submitted
        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid forms
        String valid = getStringFromFile(FORM_ALL_JSON_NON_PAID);
        List<String> valid_forms = new ArrayList<>();
        valid_forms.add(valid);
        valid_forms.add(valid.replaceAll("J53W9DA1", "J53W9DA2"));

        // when this package is transformed
        FormsPackage pack =  new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

        //all elements should contain a correct status
        Assert.assertEquals("PENDING", pack.getPackageMetaDataJson().getString("status"));
        Assert.assertEquals("PENDING", pack.getFormsJSon().get(0).getString("status"));
    }
    
    @Test
    public void packageShouldHaveStausUnpaidIfFeeFormPresent() throws Exception {
        //Given a valid package is submitted
        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid forms
        String valid = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms = new ArrayList<>();
        valid_forms.add(valid);
        valid_forms.add(valid.replaceAll("J53W9DA1", "J53W9DA2"));

        // when this package is transformed
        FormsPackage pack =  new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

        //all elements should contain a correct status
        Assert.assertEquals("UNPAID", pack.getPackageMetaDataJson().getString("status"));
        Assert.assertEquals("UNPAID", pack.getFormsJSon().get(0).getString("status"));

    }

    private FormDataMultiPart getValidMultiPart() throws IOException {
        FormDataMultiPart multi = new FormDataMultiPart();
        // forms package data
        String pack = getStringFromFile(SINGLE_PACKAGE_JSON_PATH);
        multi.field(config.getPackageMultiPartName(), pack, MediaType.APPLICATION_JSON_TYPE);
        // form json
        String form = getStringFromFile(FORM_ALL_JSON_PATH);
        multi.field("form1", form, MediaType.APPLICATION_JSON_TYPE);
        return multi;
    }


}
