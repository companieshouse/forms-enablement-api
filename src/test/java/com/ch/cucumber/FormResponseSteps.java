package com.ch.cucumber;

import com.ch.configuration.SalesforceConfiguration;
import com.ch.helpers.TestHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.dropwizard.client.JerseyClientBuilder;
import org.glassfish.jersey.internal.util.Base64;
import org.junit.Assert;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * Created by elliott.jenkins on 14/04/2016.
 */
// TODO: look at refactoring
public class FormResponseSteps extends TestHelper {
  private Response responseOne;
  private Response responseTwo;

  @Given("^I submit a valid verdict to the response forms API using the correct credentials$")
  public void i_submit_a_valid_verdict_to_the_response_forms_API_using_the_correct_credentials() throws Throwable {

    Client client1 = new JerseyClientBuilder(FormServiceTestSuiteIT.RULE.getEnvironment())
        .using(FormServiceTestSuiteIT.RULE.getConfiguration().getJerseyClientConfiguration())
        .build("response client 1");

    SalesforceConfiguration config = FormServiceTestSuiteIT.RULE.getConfiguration().getSalesforceConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getSecret());
    String url = String.format("http://localhost:%d/response", FormServiceTestSuiteIT.RULE.getLocalPort());

    String response = getStringFromFile(RESPONSE_JSON_PATH);
    responseOne = client1.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        .post(Entity.json(response));
  }

  @Then("^I should receive a response from Salesforce$")
  public void i_should_receive_a_response_from_Salesforce() throws Throwable {
    Assert.assertEquals("Correct HTTP status code.", 202, responseOne.getStatus());
  }

  @Given("^I submit an invalid media type to the response forms API using the correct credentials$")
  public void i_submit_an_invalid_media_type_to_the_response_forms_API_using_the_correct_credentials() throws Throwable {

    Client client2 = new JerseyClientBuilder(FormServiceTestSuiteIT.RULE.getEnvironment())
        .using(FormServiceTestSuiteIT.RULE.getConfiguration().getJerseyClientConfiguration())
        .build("response client 2");

    SalesforceConfiguration config = FormServiceTestSuiteIT.RULE.getConfiguration().getSalesforceConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getSecret());
    String url = String.format("http://localhost:%d/response", FormServiceTestSuiteIT.RULE.getLocalPort());

    String response = getStringFromFile(RESPONSE_JSON_PATH);
    // wrong media type
    responseTwo = client2.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        .post(Entity.text(response));
  }

  @Then("^I should receive an invalid media type error from the response api$")
  public void i_should_receive_an_invalid_media_type_error_from_the_response_api() throws Throwable {
    Assert.assertEquals("Correct HTTP status code.", 500, responseTwo.getStatus());
  }
}
