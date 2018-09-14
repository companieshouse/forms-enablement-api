package com.ch.conversion.builders;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ch.client.PresenterHelper;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.MissingRequiredDataException;
import com.ch.helpers.TestHelper;
import com.ch.model.PresenterAuthResponse;
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
    FormJsonBuilder builder = new FormJsonBuilder(config, invalid, invalid);
    builder.getJson();
  }

  @Test(expected = MissingRequiredDataException.class)
  public void throwsMissingRequiredDataExceptionWithValidJsonMissingRequiredData() throws Exception {
    String valid = getStringFromFile(VALID_JSON_PATH);
    FormJsonBuilder builder = new FormJsonBuilder(config, valid, valid);
    builder.getJson();
  }

  @Test(expected = MissingRequiredDataException.class)
  public void throwsMissingRequiredDataExceptionWithValidJsonMissingAttachemnts() throws Exception {
    String validPackage = getStringFromFile(PACKAGE_JSON_PATH);
    String validForm = getStringFromFile(FORM_ALL_JSON_MISSING_ATTACHMENT);
    FormJsonBuilder builder = new FormJsonBuilder(config, validPackage, validForm);
    builder.getJson();
  }

  @Test
  public void createJSONObjectForValidJson() throws Exception {
    FormJsonBuilder builder = getValidFormJsonBuilder();
    JSONObject json = builder.getJson();
    Assert.assertNotNull(json);
  }

  @Test
  public void checkStatusSetForFeeForm() throws Exception {
    FormJsonBuilder builder = getValidFormJsonBuilder();
    JSONObject formJson = builder.getJson();
    String status = formJson.getString("status");
    Assert.assertEquals("UNPAID", status);
  }

  @Test
  public void checkStatusSetForNonFeeForm() throws Exception {
    String validPackage = getStringFromFile(PACKAGE_JSON_PATH);
    String nonPaidForm = getStringFromFile(FORM_ALL_JSON_NON_PAID);
    FormJsonBuilder builder = new FormJsonBuilder(config, validPackage, nonPaidForm);
    JSONObject formJson = builder.getJson();
    String status = formJson.getString("status");
    Assert.assertEquals("PENDING", status);
  }

  private FormJsonBuilder getValidFormJsonBuilder() throws Exception {
    // valid package data
    String package_string = getStringFromFile(PACKAGE_JSON_PATH);
    // valid form data
    String form_string = getStringFromFile(FORM_ALL_JSON_NO_ACC_NUMBER_PATH);
    // builder
    return new FormJsonBuilder(config, package_string, form_string);
  }
}
