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

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 20/07/2016.
 */
public class PresenterAuthSteps extends TestHelper{

  private Response responseOne;
  private Response responseTwo;
  private DropwizardAppRule<FormsServiceConfiguration> rule = FormServiceTestSuiteIT.RULE;

  @Given("^I submit a valid credentials to the presenter auth endpoint$")
  public void i_submit_a_valid_credentials_to_the_presenter_auth_endpoint() throws Throwable {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
        .using(rule.getConfiguration().getJerseyClientConfiguration())
        .build("presenter client 1");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/presenterauth?id=12423&auth=PSW52889885", rule.getLocalPort());

    responseOne = client.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        .get();
  }

  @Then("^I should receive a successful response from the presenter auth endpoint$")
  public void i_should_receive_a_successful_response_from_the_presenter_auth_endpoint() throws Throwable {
    Assert.assertTrue(responseOne.getStatus() == 200);
  }

  @Given("^I submit invalid credentials to the presenter auth endpoint$")
  public void i_submit_invalid_credentials_to_the_presenter_auth_endpoint() throws Throwable {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
        .using(rule.getConfiguration().getJerseyClientConfiguration())
        .build("presenter client 2");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/presenterauth?id=124823&auth=PSW52889885", rule.getLocalPort());

    responseTwo = client.target(url)
        .request()
        .header("Authorization", "Basic " + encode)
        .get();
  }

  @Then("^I should receive a unsuccessful response from the presenter auth endpoint$")
  public void i_should_receive_a_unsuccessful_response_from_the_presenter_auth_endpoint() throws Throwable {
    Assert.assertTrue(responseTwo.getStatus() == 401);
  }
}
