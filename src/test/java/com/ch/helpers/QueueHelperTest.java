package com.ch.helpers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ch.application.FormServiceConstants;
import com.ch.application.FormsServiceApplication;
import com.ch.client.PresenterHelper;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.conversion.builders.JsonBuilder;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.model.FormStatus;
import com.ch.model.FormsPackage;
import com.ch.model.PresenterAuthResponse;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Aaron.Witter on 18/07/2016.
 */
public class QueueHelperTest extends TestHelper {


  ITransformConfig config;
  private MongoHelper helper;
  private PresenterHelper presenterHelper;
  private static final String TEST_PRESENTER_ACCOUNT_NUMBER = "1234567";

  @ClassRule
  public static final DropwizardAppRule<FormsServiceConfiguration> RULE =
    new DropwizardAppRule<>(FormsServiceApplication.class, ResourceHelpers.resourceFilePath
      ("test-configuration.yml"));

  @Before
  public void setUp() {
    config = new TransformConfig();
    MongoHelper.init(RULE.getConfiguration());
    helper = MongoHelper.getInstance();
    helper.dropCollection(FormServiceConstants.DATABASE_FORMS_COLLECTION_NAME);
    helper.dropCollection(FormServiceConstants.DATABASE_PACKAGES_COLLECTION_NAME);
    presenterHelper = mock(PresenterHelper.class);
    when(presenterHelper.getPresenterResponse(anyString(), anyString()))
      .thenReturn(new PresenterAuthResponse(TEST_PRESENTER_ACCOUNT_NUMBER));
  }

  @Test
  public void shouldReturnCompletePackageWithFiveForms() throws IOException {

    // valid package data
    String package_string = getStringFromFile(FIVE_PACKAGE_JSON_PATH);
    // valid forms
    String valid = getStringFromFile(FORM_ALL_JSON_PATH);
    List<String> valid_forms = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      valid_forms.add(valid);
    }
    // builder
    FormsPackage formsPackage = new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

    helper.storeFormsPackage(formsPackage);

    TransformConfig config = new TransformConfig();

    QueueHelper queueHelper = new QueueHelper(config);

    List<JSONObject> forms = queueHelper.getCompletePackagesByStatus(FormStatus.UNPAID.toString().toUpperCase(Locale.ENGLISH),1);

    Assert.assertTrue(forms.size() == 1);
    Assert.assertTrue(forms.get(0).getInt(config.getPackageCountPropertyNameIn()) == 5);
    Assert.assertTrue(forms.get(0).getJSONArray(config.getFormsPropertyNameOut()).length() == 5);
  }

  @Test
  public void shouldReturnCompletePackageWithThreeForms() throws IOException {

    // valid package data
    String package_string = getStringFromFile(THREE_PACKAGE_JSON_PATH);
    // valid forms
    String valid = getStringFromFile(FORM_ALL_JSON_PATH);
    List<String> valid_forms = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      valid_forms.add(valid);
    }
    // builder
    FormsPackage formsPackage = new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

    helper.storeFormsPackage(formsPackage);

    ITransformConfig config = new TransformConfig();

    QueueHelper queueHelper = new QueueHelper(config);

    JSONObject form = queueHelper.getCompletePackageById("12435");

    Assert.assertTrue(form.getInt(config.getPackageCountPropertyNameIn()) == 3);
    Assert.assertTrue(form.getJSONArray(config.getFormsPropertyNameOut()).length() == 3);
    
    JSONArray array = form.getJSONArray(config.getFormsPropertyNameOut());
    String packageIdentifier = array.getJSONObject(0).getString(config.getPackageIdentifierElementNameOut());
    Assert.assertEquals(packageIdentifier, "12435");

  }

}
