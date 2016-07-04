package com.ch.cucumber;

import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.helpers.TestHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.internal.util.Base64;
import org.junit.Assert;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

/**
 * Created by elliott.jenkins on 19/05/2016.
 */
public class AuthenticationSteps extends TestHelper {

  private Response unAuthResponse;
  private Response validResponse;
  private DropwizardAppRule<FormsServiceConfiguration> rule = FormServiceTestSuiteIT.RULE;

  @Given("^I submit no auth header$")
  public void i_submit_no_auth_header() throws IOException {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
        .using(rule.getConfiguration().getJerseyClientConfiguration())
        .build("auth steps client 1");

    String url = String.format("http://localhost:%d/auth", rule.getLocalPort());
    unAuthResponse = client.target(url)
        .request()
        .get();
  }

  @Then("^I should receive an unauthorized response from the api$")
  public void i_should_receive_an_unauthorized_response_from_the_api() throws Throwable {
    Assert.assertEquals("Correct HTTP status code.", 401, unAuthResponse.getStatus());
  }

  @Given("^I submit a valid auth header$")
  public void i_submit_a_valid_auth_header() throws IOException {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
        .using(rule.getConfiguration().getJerseyClientConfiguration())
        .build("auth steps client 2");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/auth", rule.getLocalPort());
    validResponse = client.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        .get();

  }

  @Then("^I should receive a success response from the api$")
  public void i_should_receive_a_success_response_from_the_api() throws Throwable {
    Assert.assertEquals("Correct HTTP status code.", 200, validResponse.getStatus());
  }

  @Given("^I submit an invalid auth header$")
  public void i_submit_an_invalid_auth_header() throws IOException {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
        .using(rule.getConfiguration().getJerseyClientConfiguration())
        .build("auth steps client 3");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + "--WRONG--");
    String url = String.format("http://localhost:%d/auth", rule.getLocalPort());
    unAuthResponse = client.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        .get();

  }
}