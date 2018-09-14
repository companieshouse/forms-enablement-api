package com.ch.cucumber;


import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ch.application.FormServiceConstants;
import com.ch.client.PresenterHelper;
import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.conversion.builders.JsonBuilder;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.PresenterAuthenticationException;
import com.ch.helpers.MongoHelper;
import com.ch.helpers.TestHelper;
import com.ch.model.ConfirmPaymentRequest;
import com.ch.model.FormsPackage;
import com.ch.model.PresenterAuthResponse;
import com.ch.resources.FormSubmissionResource;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;

import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by elliott.jenkins on 14/04/2016.
 */
public class ConfirmPaymentSteps extends TestHelper {

  private Response responseOne;
  private Response responseTwo;
  private DropwizardAppRule<FormsServiceConfiguration> rule = FormServiceTestSuiteIT.RULE;
  
  private MongoHelper helper;
  private PresenterHelper presenterHelper;
  private ITransformConfig config;
  
  private static final String TEST_PRESENTER_ACCOUNT_NUMBER = "1234567";

  @Before
  public void setUp() {
    config = new TransformConfig();
    MongoHelper.init(rule.getConfiguration());
    helper = MongoHelper.getInstance();
    presenterHelper = mock(PresenterHelper.class);
    when(presenterHelper.getPresenterResponse(PACKAGE_JSON_PRESENTER_ID, PACKAGE_JSON_PRESENTER_AUTH))
      .thenReturn(new PresenterAuthResponse(TEST_PRESENTER_ACCOUNT_NUMBER));
    when(presenterHelper.getPresenterResponse(PACKAGE_INVALID_CREDENTIALS_PRESENTER_ID, PACKAGE_INVALID_CREDENTIALS_PRESENTER_AUTH))
      .thenReturn(new PresenterAuthResponse(null));
    helper.dropCollection(FormServiceConstants.DATABASE_FORMS_COLLECTION_NAME);
    helper.dropCollection(FormServiceConstants.DATABASE_PACKAGES_COLLECTION_NAME);

  }
  @Given("^I submit a nonexisting package id confirm payment request to the forms API using the correct credentials$")
  public void i_submit_a_nonexisting_package_id_confirm_payment_request_to_the_forms_API_using_the_correct_credentials() throws IOException {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
        .using(rule.getConfiguration().getJerseyClientConfiguration())
        .build("Confirm payment client 1");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/confirmpaid", rule.getLocalPort());
    String confirmPaymentRequest = getStringFromFile(CONFIRM_PAYMENT_VALID_JSON_PATH);


    responseOne = client.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        .post(Entity.json(confirmPaymentRequest));
  }

  @Then("^I should receive a 404 response$")
  public void i_should_receive_a_404_response() throws Throwable {
    Assert.assertEquals("Correct HTTP status code.", 404, responseOne.getStatus());
  }
  
  @Given("^The queue contains a package with a status of unpaid$")
  public void the_queue_contains_a_package_with_a_status_of_unpaid() throws Throwable {

    //package one
    String packageOneString = getStringFromFile(PACKAGE_JSON_PATH);
    // valid forms
    String valid = getStringFromFile(FORM_ALL_JSON_PATH);
    List<String> valid_forms = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      valid_forms.add(valid);
    }
    FormsPackage formsPackage = new JsonBuilder(config, packageOneString, valid_forms).getTransformedPackage();
    // insert package one into db
    helper.storeFormsPackage(formsPackage);
  }
  
  @Then("^I submit a valid confirm payment request to the forms API using the correct credentials$")
  public void i_submit_a_valid_confirm_payment_request_to_the_forms_API_using_the_correct_credentials() throws IOException {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
        .using(rule.getConfiguration().getJerseyClientConfiguration())
        .build("Confirm payment client 2");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/confirmpaid", rule.getLocalPort());
    String confirmPaymentRequest = getStringFromFile(CONFIRM_PAYMENT_VALID_JSON_PATH);


    responseOne = client.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        .post(Entity.json(confirmPaymentRequest));
  }

  @Then("^I should receive a 200 response$")
  public void i_should_receive_a_response() throws Throwable {
    Assert.assertEquals("Correct HTTP status code.", 200, responseOne.getStatus());
  }

  @Given("^I submit an invalid media type to the forms confirm payment API using the correct credentials$")
  public void i_submit_an_invalid_media_type_to_the_forms_confirm_payment_API_using_the_correct_credentials() throws Throwable {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
        .using(rule.getConfiguration().getJerseyClientConfiguration())
        .build("Confirm payment client 3");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/barcode", rule.getLocalPort());
    String date = getStringFromFile(DATE_JSON_PATH);


    responseTwo = client.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        // wrong media type
        .post(Entity.text(date));
  }

  @Then("^I should receive an invalid media type error from the confirm payment api$")
  public void i_should_receive_an_invalid_media_type_error_from_the_confirm_payment_api() throws Throwable {
    Assert.assertEquals("Correct HTTP status code.", 415, responseTwo.getStatus());
  }
  
  @When("^I submit a confirm payment request with invalid presenter credentials$")
  public void i_submit_a_package_with_invalid_presenter_credentials() throws Throwable {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
    .using(rule.getConfiguration().getJerseyClientConfiguration())
    .build("Confirm payment client 3");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/confirmpaid", rule.getLocalPort());
    String confirmPaymentRequest = getStringFromFile(CONFIRM_PAYMENT_INVALID_PRESENTER_CREDENTIALS_JSON_PATH);

    responseOne = client.target(url)
            .request()
            .header("Authorization", "Basic " + encode)
            .post(Entity.json(confirmPaymentRequest));
  }

  @Then("^I should receive a 401 response$")
  public void i_should_receive_a_401_response() throws Throwable {
    Assert.assertEquals("Correct HTTP status code.", 401, responseOne.getStatus());
  }
}

