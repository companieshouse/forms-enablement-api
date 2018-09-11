package com.ch.cucumber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ch.application.FormServiceConstants;
import com.ch.client.PresenterHelper;
import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.conversion.builders.JsonBuilder;
import com.ch.conversion.config.TransformConfig;
import com.ch.helpers.MongoHelper;
import com.ch.helpers.QueueHelper;
import com.ch.helpers.TestHelper;
import com.ch.model.FormStatus;
import com.ch.model.FormsPackage;
import com.ch.model.PresenterAuthResponse;
import com.ch.model.QueueRequest;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

/**
 * Created by Aaron.Witter on 19/07/2016.
 */
public class ActivationSteps extends TestHelper {
  private MongoHelper helper;
  private TransformConfig config = new TransformConfig();
  private DropwizardAppRule<FormsServiceConfiguration> rule = FormServiceTestSuiteIT.RULE;
  private QueueHelper queueHelper = new QueueHelper(config);
  private PresenterHelper presenterHelper;
  private String packageOneString;
  private String packageTwoString;
  private String packageThreeString;
  private String packageFourString;
  private String packageId;
  private static final String TEST_PRESENTER_ACCOUNT_NUMBER = "1234567";

  @Before
  public void setUp() {
    config = new TransformConfig();
    MongoHelper.init(rule.getConfiguration());
    helper = MongoHelper.getInstance();
    helper.dropCollection(FormServiceConstants.DATABASE_FORMS_COLLECTION_NAME);
    helper.dropCollection(FormServiceConstants.DATABASE_PACKAGES_COLLECTION_NAME);
    presenterHelper = mock(PresenterHelper.class);
    when(presenterHelper.getPresenterResponse(anyString(), anyString()))
      .thenReturn(new PresenterAuthResponse(TEST_PRESENTER_ACCOUNT_NUMBER));

  }

  @Given("^The queue contains (\\d+) packages all pending$")
  public void the_queue_contains_packages_all_pending(int arg1) throws Throwable {

    // package one
    packageOneString = getStringFromFile(PACKAGE_JSON_PATH);
    // valid forms
    String valid = getStringFromFile(FORM_ALL_JSON_PATH);
    List<String> valid_forms = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      valid_forms.add(valid);
    }
    FormsPackage formsPackage = new JsonBuilder(config, packageOneString, valid_forms).getTransformedPackage();
    // insert package one into db
    helper.storeFormsPackage(formsPackage);

    // package two
    packageTwoString = getStringFromFile(SINGLE_PACKAGE_JSON_PATH);
    // valid forms
    String valid2 = getStringFromFile(FORM_ALL_JSON_PATH);
    List<String> valid_forms2 = new ArrayList<>();
    for (int i = 0; i < 1; i++) {
      valid_forms2.add(valid2);
    }
    FormsPackage formsPackage2 = new JsonBuilder(config, packageTwoString, valid_forms2).getTransformedPackage();
    // insert package two into db
    TimeUnit.SECONDS.sleep(1);
    helper.storeFormsPackage(formsPackage2);

    // package three
    packageThreeString = getStringFromFile(FIVE_PACKAGE_JSON_PATH);
    // valid forms
    String valid3 = getStringFromFile(FORM_ALL_JSON_PATH);
    List<String> valid_forms3 = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      valid_forms3.add(valid3);
    }
    FormsPackage formsPackage3 = new JsonBuilder(config, packageThreeString, valid_forms3).getTransformedPackage();
    // insert package three into db
    TimeUnit.SECONDS.sleep(1);
    helper.storeFormsPackage(formsPackage3);
  }

  @When("^I request (\\d+) pending packages$")
  public void i_request_pending_packages(int arg1) throws Throwable {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
      .using(rule.getConfiguration().getJerseyClientConfiguration())
      .build("auth steps client 4");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/queue", rule.getLocalPort());
    client.target(url)
      .request()
      .header("Authorization", "Basic " + encode)
      .post(Entity.entity(new QueueRequest(FormStatus.PENDING.toString().toUpperCase(), 2), MediaType.APPLICATION_JSON));
  }

  @Then("^Two packages status should be changed$")
  public void two_packages_status_should_be_changed() throws Throwable {
    List<JSONObject> packs = queueHelper.getCompletePackagesByStatus(FormStatus.SUCCESS.toString().toUpperCase(), 0);
    Assert.assertTrue(packs.size() == 2);

    JSONObject packageOne = packs.get(0);
    JSONObject packageTwo = packs.get(1);

    Assert.assertTrue(packageOne.getInt(config.getPackageIdentifierElementNameOut()) == (new JSONObject(packageOneString)
      .getInt(config.getPackageIdentifierElementNameOut())));

    Assert.assertTrue(packageTwo.getInt(config.getPackageIdentifierElementNameOut()) == (new JSONObject(packageTwoString)
      .getInt(config.getPackageIdentifierElementNameOut())));

    Assert.assertTrue(queueHelper.getCompletePackagesByStatus(FormStatus.PENDING.toString().toUpperCase(), 0).size() == 1);
  }

  @Given("^The queue contains a failed package$")
  public void the_queue_contains_a_failed_package_where_only_one_of_the_forms_has_a_failed_status() throws Throwable {
    helper.dropCollection(FormServiceConstants.DATABASE_FORMS_COLLECTION_NAME);
    helper.dropCollection(FormServiceConstants.DATABASE_PACKAGES_COLLECTION_NAME);

    // package one
    packageFourString = getStringFromFile(PACKAGE_JSON_PATH);
    // valid forms
    String valid4 = getStringFromFile(FORM_ALL_JSON_PATH);
    List<String> valid_forms4 = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      valid_forms4.add(valid4);
    }
    FormsPackage formsPackage = new JsonBuilder(config, packageFourString, valid_forms4).getTransformedPackage();
    // insert package one into db
    helper.storeFormsPackage(formsPackage);

    packageId = formsPackage.getPackageMetaDataJson().getString(config
        .getPackageIdentifierElementNameOut());


    // check the db contains one package
    List<JSONObject> packs = queueHelper.getCompletePackagesByStatus(FormStatus.PENDING.toString().toUpperCase(), 0);

    Assert.assertTrue(packs.size() == 1);

    //and two forms each with a pending status
    JSONObject packageDoc = packs.get(0);

    Assert.assertTrue(packageDoc.getJSONArray(config.getFormsPropertyNameOut()).length() == 2);

    JSONObject formsDocOne = packageDoc.getJSONArray(config.getFormsPropertyNameOut()).getJSONObject(0);
    JSONObject formsDocTwo = packageDoc.getJSONArray(config.getFormsPropertyNameOut()).getJSONObject(1);

    Assert.assertTrue(packageDoc.getString(config.getFormStatusPropertyNameOut())
      .equals(FormStatus.PENDING.toString().toUpperCase()));


    //set package as failed
    helper.updatePackageStatusByPackageId(packageId, FormStatus.FAILED.toString().toUpperCase());

    List<JSONObject> failedPacks = queueHelper.getCompletePackagesByStatus(FormStatus.FAILED.toString().toUpperCase(), 0);

    Assert.assertTrue(failedPacks.size() == 1);


    //set the forms as failed
    ArrayList<Document> forms = helper.getFormsCollectionByPackageId(packageId).into(new ArrayList<Document>());

    Document formOne = forms.get(0);
    Document formTwo = forms.get(1);

    Assert.assertTrue(formOne.containsKey(FormServiceConstants.DATABASE_OBJECTID_KEY));

    ObjectId formOneId = formOne.getObjectId(FormServiceConstants.DATABASE_OBJECTID_KEY);
    ObjectId formTwoId = formTwo.getObjectId(FormServiceConstants.DATABASE_OBJECTID_KEY);

    helper.updateFormStatusByPackageId(formOneId, FormStatus.FAILED.toString().toUpperCase());
    helper.updateFormStatusByPackageId(formTwoId, FormStatus.FAILED.toString().toUpperCase());


    //check the db contains two failed forms is failed
    ArrayList<Document> failedForms = helper.getFormsCollectionByPackageIdAndStatus(packageId,FormStatus.FAILED.toString()
      .toUpperCase()).into(new ArrayList<Document>());

    Assert.assertTrue(failedForms.size() == 2);
  }

  @When("^I request the packages failed contents$")
  public void i_request_the_packages_failed_contents() throws Throwable {
    Client client = new JerseyClientBuilder(rule.getEnvironment())
      .using(rule.getConfiguration().getJerseyClientConfiguration())
      .build("auth steps client 5");

    CompaniesHouseConfiguration config = rule.getConfiguration().getCompaniesHouseConfiguration();
    String encode = Base64.encodeAsString(config.getName() + ":" + config.getApiKey());
    String url = String.format("http://localhost:%d/queue", rule.getLocalPort());
    client.target(url)
      .request()
      .header("Authorization", "Basic " + encode)
      .post(Entity.entity(new QueueRequest(FormStatus.FAILED.toString().toUpperCase(), 1), MediaType.APPLICATION_JSON));
  }

  @Then("^The failed packages status should be changed$")
  public void one_package_status_should_be_changed() throws Throwable {

    //check no failed forms remain
    ArrayList<Document> failedForms = helper.getFormsCollectionByPackageIdAndStatus(packageId,FormStatus.FAILED.toString()
      .toUpperCase()).into(new ArrayList<Document>());
    Assert.assertTrue(failedForms.size() == 0);

    //check no failed packages remain
    ArrayList<Document>  failedPackage = helper.getPackagesCollectionByStatus(FormStatus.FAILED.toString()
      .toUpperCase(),0).into(new ArrayList<Document>());
    Assert.assertTrue(failedPackage.size() == 0);

    //check the failed statuses have been replaced with success
    JSONObject packageMetaData = queueHelper.getCompletePackageById(packageId);
    JSONObject formOne = packageMetaData.getJSONArray(config.getFormsPropertyNameOut()).getJSONObject(0);
    JSONObject formTwo = packageMetaData.getJSONArray(config.getFormsPropertyNameOut()).getJSONObject(1);

    Assert.assertTrue(packageMetaData.getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.SUCCESS
      .toString().toUpperCase()));
    Assert.assertTrue(formOne.getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.SUCCESS.toString().toUpperCase()));
    Assert.assertTrue(formTwo.getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.SUCCESS.toString().toUpperCase()));
  }
}

