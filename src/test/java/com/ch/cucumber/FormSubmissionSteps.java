package com.ch.cucumber;

import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.helpers.TestHelper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.json.JSONObject;
import org.junit.Assert;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 10/04/2016.
 */
public class FormSubmissionSteps extends TestHelper {

    private Response responseOne;
    private Response responseTwo;
    private DropwizardAppRule<FormsServiceConfiguration> rule = FormServiceTestSuiteIT.RULE;
    private ITransformConfig transformConfig = new TransformConfig();

    @Given("^I submit a valid form to the forms API using the correct credentials$")
    public void i_submit_a_valid_form_to_the_forms_API_using_the_correct_credentials() throws Throwable {
        Client client1 = new JerseyClientBuilder(rule.getEnvironment())
            .using(rule.getConfiguration().getJerseyClientConfiguration())
            .build("submission client 1");

        CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
        String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
        String url = String.format("http://localhost:%d/submission", rule.getLocalPort());

        FormDataMultiPart multi = new FormDataMultiPart();
        // forms package data
        String pack = getStringFromFile(PACKAGE_JSON_PATH);
        multi.field(transformConfig.getPackageMultiPartName(), pack, MediaType.APPLICATION_JSON_TYPE);
        // form json
        String form = getStringFromFile(FORM_ALL_JSON_PATH);
        multi.field("form1", form, MediaType.APPLICATION_JSON_TYPE);

        responseOne = client1.target(url)
            .register(MultiPartFeature.class)
            .request()
            .header("Authorization", "Basic " + encode)
            .post(Entity.entity(multi, MediaType.MULTIPART_FORM_DATA_TYPE));
    }

    @Then("^then I should receive a response from CHIPS$")
    public void then_I_should_receive_a_response_from_CHIPS() throws Throwable {
        Assert.assertEquals("Correct HTTP status code.", 202, responseOne.getStatus());
    }

    @Given("^I submit a invalid form to the forms API using the correct credentials$")
    public void i_submit_a_invalid_form_to_the_forms_API_using_the_correct_credentials() throws Throwable {
        Client client2 = new JerseyClientBuilder(rule.getEnvironment())
            .using(rule.getConfiguration().getJerseyClientConfiguration())
            .build("submission client 2");

        CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
        String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
        String url = String.format("http://localhost:%d/submission", rule.getLocalPort());

        FormDataMultiPart multiPart = new FormDataMultiPart();

        String formdata = getStringFromFile(INVALID_FORM_JSON_PATH);
        String packagemetadata = getStringFromFile(PACKAGE_JSON_PATH);

        multiPart.field("form1", formdata, MediaType.TEXT_PLAIN_TYPE);
        multiPart.field("packagemetadata", packagemetadata, MediaType.TEXT_PLAIN_TYPE);

        responseTwo = client2.target(url)
            .register(MultiPartFeature.class)
            .request()
            .header("Authorization", "Basic " + encode)
            .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE));
    }

    @Then("^then I should receive an error message from the API$")
    public void then_I_should_receive_an_error_message_from_the_API() throws Throwable {
        Assert.assertEquals("Correct HTTP status code.", 400, responseTwo.getStatus());
    }
}
