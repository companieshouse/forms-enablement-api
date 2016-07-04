package com.ch.cucumber;

import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.helpers.MongoHelper;
import com.ch.helpers.TestHelper;
import com.mongodb.client.FindIterable;
import com.sun.research.ws.wadl.Doc;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.bson.Document;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Assert;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 10/04/2016.
 */
public class FormSubmissionSteps extends TestHelper {

    private Response validResponse;
    private Response invalidResponse;
    private DropwizardAppRule<FormsServiceConfiguration> rule = FormServiceTestSuiteIT.RULE;
    private ITransformConfig transformConfig = new TransformConfig();
    private MongoHelper mongoHelper;

    @Before
    public void beforeScenario() {
        mongoHelper = new MongoHelper(rule.getConfiguration());
        // clear database before
        mongoHelper.getDatabase().drop();
    }

    // Invalid
    @Given("^I submit a invalid form to the forms API using the correct credentials$")
    public void i_submit_a_invalid_form_to_the_forms_API_using_the_correct_credentials() throws Throwable {
        Client client2 = new JerseyClientBuilder(rule.getEnvironment())
            .using(rule.getConfiguration().getJerseyClientConfiguration())
            .build("submission client 2");

        CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
        String encode = Base64.encodeAsString(config.getName() + ":" + config.getSecret());
        String url = String.format("http://localhost:%d/submission", rule.getLocalPort());

        FormDataMultiPart multiPart = new FormDataMultiPart();

        String formdata = getStringFromFile(INVALID_FORM_JSON_PATH);
        String packagemetadata = getStringFromFile(PACKAGE_JSON_PATH);

        multiPart.field("form1", formdata, MediaType.TEXT_PLAIN_TYPE);
        multiPart.field("form2", formdata, MediaType.TEXT_PLAIN_TYPE);
        multiPart.field("packagemetadata", packagemetadata, MediaType.TEXT_PLAIN_TYPE);

        invalidResponse = client2.target(url)
            .register(MultiPartFeature.class)
            .request()
            .header("Authorization", "Basic " + encode)
            .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE));
    }

    @Then("^the database should contain (\\d+) packages and (\\d+) forms$")
    public void the_database_should_contain_packages_and_forms(int arg1, int arg2) throws Throwable {
        // packages
        long packagesCount = mongoHelper.getPackagesCollection().count();
        Assert.assertEquals(arg1, packagesCount);

        // forms
        long formsCount = mongoHelper.getFormsCollection().count();
        Assert.assertEquals(arg2, formsCount);
    }

    @Then("^I should receive an error response from the API$")
    public void i_should_receive_an_error_response_from_the_API() throws Throwable {
        Assert.assertEquals("Correct HTTP status code.", 400, invalidResponse.getStatus());
    }

    // Valid
    @Given("^I submit a valid form to the forms API using the correct credentials$")
    public void i_submit_a_valid_form_to_the_forms_API_using_the_correct_credentials() throws Throwable {
        Client client1 = new JerseyClientBuilder(rule.getEnvironment())
            .using(rule.getConfiguration().getJerseyClientConfiguration())
            .build("submission client 1");

        CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
        String encode = Base64.encodeAsString(config.getName() + ":" + config.getSecret());
        String url = String.format("http://localhost:%d/submission", rule.getLocalPort());

        FormDataMultiPart multiPart = new FormDataMultiPart();

        String formdata = getStringFromFile(FORM_ALL_JSON_PATH);
        String packagemetadata = getStringFromFile(PACKAGE_JSON_PATH);

        multiPart.field("form1", formdata, MediaType.TEXT_PLAIN_TYPE);
        multiPart.field("form2", formdata, MediaType.TEXT_PLAIN_TYPE);
        multiPart.field("packagemetadata", packagemetadata, MediaType.TEXT_PLAIN_TYPE);

        validResponse = client1.target(url)
            .register(MultiPartFeature.class)
            .request()
            .header("Authorization", "Basic " + encode)
            .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE));
    }

    @Then("^the package and forms should have the correct info$")
    public void the_package_and_forms_should_have_the_correct_info() throws Throwable {
        // ensure the package has a date property
        Document storedPackage = mongoHelper.getPackagesCollection().find().first();
        Object date = storedPackage.get(transformConfig.getPackageDatePropertyNameOut());
        Object packageIdentifier = storedPackage.get(transformConfig.getPackageIdentifierPropertyNameIn());
        Assert.assertNotNull(date);

        FindIterable<Document> forms = mongoHelper.getFormsCollection().find();
        for(Document formDoc : forms){
            // make sure each form has the packageIdentifier
            Object formPackageIdentifier = formDoc.get(transformConfig.getPackageIdentifierPropertyNameIn());
            Assert.assertEquals(packageIdentifier, formPackageIdentifier);
        }
    }

    @Then("^I should get a success response from the API$")
    public void i_should_get_a_success_response_from_the_API() throws Throwable {
        Assert.assertEquals("Correct HTTP status code.", 200, validResponse.getStatus());
    }
}
