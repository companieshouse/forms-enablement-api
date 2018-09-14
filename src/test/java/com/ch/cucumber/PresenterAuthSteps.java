package com.ch.cucumber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ch.application.FormServiceConstants;
import com.ch.client.PresenterHelper;
import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.PresenterAuthenticationException;
import com.ch.helpers.MongoHelper;
import com.ch.helpers.QueueHelper;
import com.ch.helpers.TestHelper;
import com.ch.model.PresenterAuthResponse;
import com.ch.resources.FormSubmissionResource;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 20/07/2016.
 */
public class PresenterAuthSteps extends TestHelper{

  private Response responseOne;
  private Response responseTwo;
  private MongoHelper helper;
  private FormDataMultiPart multi;
  private TransformConfig config = new TransformConfig();
  private QueueHelper queueHelper = new QueueHelper(config);
  private PresenterHelper presenterHelper;
  private DropwizardAppRule<FormsServiceConfiguration> rule = FormServiceTestSuiteIT.RULE;
  private ITransformConfig transformConfig = new TransformConfig();
  public Client client;
  private static final String TEST_PRESENTER_ACCOUNT_NUMBER = "1234567";

  @Before
  public void setUp() {
    MongoHelper.init(rule.getConfiguration());
    helper = MongoHelper.getInstance();
    helper.dropCollection(FormServiceConstants.DATABASE_FORMS_COLLECTION_NAME);
    helper.dropCollection(FormServiceConstants.DATABASE_PACKAGES_COLLECTION_NAME);
    presenterHelper = mock(PresenterHelper.class);
    when(presenterHelper.getPresenterResponse(PACKAGE_JSON_PRESENTER_ID, PACKAGE_JSON_PRESENTER_AUTH))
      .thenReturn(new PresenterAuthResponse(TEST_PRESENTER_ACCOUNT_NUMBER));
    when(presenterHelper.getPresenterResponse(PACKAGE_INVALID_CREDENTIALS_PRESENTER_ID, PACKAGE_INVALID_CREDENTIALS_PRESENTER_AUTH))
      .thenReturn(new PresenterAuthResponse(null));
  }


  @Given("^I submit a valid credentials to the presenter auth endpoint$")
  public void i_submit_a_valid_credentials_to_the_presenter_auth_endpoint() throws Throwable {
    client = new JerseyClientBuilder(rule.getEnvironment())
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
    client = new JerseyClientBuilder(rule.getEnvironment())
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

  @Then("^I should receive an unsuccessful response from the presenter auth endpoint$")
  public void i_should_receive_a_unsuccessful_response_from_the_presenter_auth_endpoint() throws Throwable {
    Assert.assertTrue(responseTwo.getStatus() == 401);
  }

  @Given("^I submit a package with no presenter credentials$")
  public void i_submit_a_package_with_no_presenter_credentials() throws Throwable {
    helper.dropCollection(FormServiceConstants.DATABASE_FORMS_COLLECTION_NAME);
    helper.dropCollection(FormServiceConstants.DATABASE_PACKAGES_COLLECTION_NAME);

    FormDataMultiPart multi = new FormDataMultiPart();
    // forms package data
    String pack = getStringFromFile(PACKAGE_NO_CREDENTIALS_PATH);
    multi.field(transformConfig.getPackageMultiPartName(), pack, MediaType.APPLICATION_JSON_TYPE);
    // form json
    String form = getStringFromFile(FORM_ALL_JSON_NO_ACC_NUMBER_PATH);
    multi.field("form1", form, MediaType.APPLICATION_JSON_TYPE);

    // form json 2
    String form2 = getStringFromFile(FORM_ALL_JSON_NO_ACC_NUMBER_PATH);
    multi.field("form2", form2, MediaType.APPLICATION_JSON_TYPE);

    // form json 3
    String form3 = getStringFromFile(FORM_ALL_JSON_NO_ACC_NUMBER_PATH);
    multi.field("form3", form3, MediaType.APPLICATION_JSON_TYPE);

    FormSubmissionResource resource = new FormSubmissionResource();
    resource.postForms(multi);
  }

  @Then("^The forms should have placeholder account numbers$")
  public void the_forms_should_have_no_account_numbers() throws Throwable {
    TimeUnit.SECONDS.sleep(1);

    JSONObject formPackage = queueHelper.getCompletePackageById(PACKAGE_NO_CREDENTIALS_ID);
    String xml1 = formPackage.getJSONArray(config.getFormsPropertyNameOut()).getJSONObject(0).getString(config
      .getXmlPropertyNameOut());
    String xml2 = formPackage.getJSONArray(config.getFormsPropertyNameOut()).getJSONObject(1).getString(config
      .getXmlPropertyNameOut());
    String xml3 = formPackage.getJSONArray(config.getFormsPropertyNameOut()).getJSONObject(2).getString(config
      .getXmlPropertyNameOut());

    String decodedXml1 = decode(xml1);
    String decodedXml2 = decode(xml2);
    String decodedXml3 = decode(xml3);

    Assert.assertTrue(decodedXml1.contains("<accountNumber>:placeholderAccountNumber:</accountNumber>"));
    Assert.assertTrue(decodedXml2.contains("<accountNumber>:placeholderAccountNumber:</accountNumber>"));
    Assert.assertTrue(decodedXml3.contains("<accountNumber>:placeholderAccountNumber:</accountNumber>"));

  }

  private String decode(String xml) {
    byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(xml.getBytes());
    return new String(decoded);
  }
}
