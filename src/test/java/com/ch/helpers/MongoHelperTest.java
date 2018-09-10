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
import org.bson.Document;
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
public class MongoHelperTest extends TestHelper{

    private MongoHelper helper;
    private static final String TEST_PRESENTER_ACCOUNT_NUMBER = "1234567";
    ITransformConfig config;
    PresenterHelper presenterHelper;


    @ClassRule
    public static final DropwizardAppRule<FormsServiceConfiguration> RULE =
        new DropwizardAppRule<>(FormsServiceApplication.class, ResourceHelpers.resourceFilePath
            ("test-configuration.yml"));

    @Before
    public void setUp() {
        config = new TransformConfig();
        MongoHelper.init(RULE.getConfiguration());
        helper =  MongoHelper.getInstance();
        helper.dropCollection(FormServiceConstants.DATABASE_FORMS_COLLECTION_NAME);
        helper.dropCollection(FormServiceConstants.DATABASE_PACKAGES_COLLECTION_NAME);
        presenterHelper = mock(PresenterHelper.class);
        when(presenterHelper.getPresenterResponse(anyString(), anyString()))
          .thenReturn(new PresenterAuthResponse(TEST_PRESENTER_ACCOUNT_NUMBER));
    }

    @Test
    public void shouldReturnResultsOldestFirst() throws IOException {

        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid forms
        String valid = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            valid_forms.add(valid);
        }
        // builder
        FormsPackage formsPackage = new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

        helper.storeFormsPackage(formsPackage);

        // valid package data
        String package_string2 = getStringFromFile(SINGLE_PACKAGE_JSON_PATH);
        // valid forms
        String valid2 = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms2 = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            valid_forms2.add(valid2);
        }
        // builder
        FormsPackage formsPackage2 = new JsonBuilder(config, package_string2, valid_forms2).getTransformedPackage();

        helper.storeFormsPackage(formsPackage2);

        ArrayList<Document> documents = helper.getPackagesCollectionByStatus(FormStatus.PENDING.toString()
          .toUpperCase(Locale.ENGLISH), 2).into(new ArrayList<Document>());
        
        String packageIdentifier = documents.get(0).getString(config.getPackageIdentifierPropertyNameIn());
        Assert.assertEquals(packageIdentifier,  PACKAGE_JSON_ID);
        
        Integer count = documents.get(0).getInteger(config.getPackageCountPropertyNameIn());
        Assert.assertTrue(count == 2);
    }

    @Test
    public void shouldReturnAllPackages() throws IOException {

        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid forms
        String valid = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            valid_forms.add(valid);
        }
        // builder
        FormsPackage formsPackage = new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

        helper.storeFormsPackage(formsPackage);

        // valid package data
        String package_string2 = getStringFromFile(SINGLE_PACKAGE_JSON_PATH);
        // valid forms
        String valid2 = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms2 = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            valid_forms2.add(valid2);
        }
        // builder
        FormsPackage formsPackage2 = new JsonBuilder(config, package_string2, valid_forms2).getTransformedPackage();

        helper.storeFormsPackage(formsPackage2);

        ArrayList<Document> documents = helper.getPackagesCollectionByStatus(FormStatus.PENDING.toString()
          .toUpperCase(Locale.ENGLISH), 0).into(new ArrayList<Document>());

        Assert.assertTrue(documents.size() == 2);
    }

    @Test
    public void shouldReturnOnePackage() throws IOException {

        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid forms
        String valid = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            valid_forms.add(valid);
        }
        // builder
        FormsPackage formsPackage = new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

        helper.storeFormsPackage(formsPackage);

        // valid package data
        String package_string2 = getStringFromFile(SINGLE_PACKAGE_JSON_PATH);
        // valid forms
        String valid2 = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms2 = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            valid_forms2.add(valid2);
        }
        // builder
        FormsPackage formsPackage2 = new JsonBuilder(config, package_string2, valid_forms2).getTransformedPackage();

        helper.storeFormsPackage(formsPackage2);

        ArrayList<Document> documents = helper.getPackagesCollectionByStatus(FormStatus.PENDING.toString()
          .toUpperCase(Locale.ENGLISH), 1).into(new ArrayList<Document>());

        Assert.assertTrue(documents.size() == 1);
    }

    @Test
    public void shouldUpdatePackageStatus() throws IOException {

        // valid package data
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        // valid forms
        String valid = getStringFromFile(FORM_ALL_JSON_PATH);
        List<String> valid_forms = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            valid_forms.add(valid);
        }
        // builder
        FormsPackage formsPackage = new JsonBuilder(config, package_string, valid_forms).getTransformedPackage();

        helper.storeFormsPackage(formsPackage);

        helper.updatePackageStatusByPackageId(PACKAGE_JSON_ID, FormStatus.SUCCESS.toString().toUpperCase(Locale.ENGLISH));

        Document pack = helper.getPackageByPackageId(PACKAGE_JSON_ID);

        Assert.assertTrue(pack.getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.SUCCESS.toString()
          .toUpperCase(Locale.ENGLISH)));
    }

    @Test
    public void shouldUpdateAllFormsStatus() throws IOException {

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

        helper.updateFormsStatusByPackageId(FIVE_PACKAGE_JSON_ID, FormStatus.FAILED.toString().toUpperCase(Locale.ENGLISH));

        ArrayList<Document> forms = helper.getFormsCollectionByPackageId(FIVE_PACKAGE_JSON_ID).into(new ArrayList<Document>());

        Assert.assertTrue(forms.get(0).getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.FAILED.toString()
          .toUpperCase(Locale.ENGLISH)));
        Assert.assertTrue(forms.get(1).getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.FAILED.toString()
          .toUpperCase(Locale.ENGLISH)));
        Assert.assertTrue(forms.get(2).getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.FAILED.toString()
          .toUpperCase(Locale.ENGLISH)));
        Assert.assertTrue(forms.get(3).getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.FAILED.toString()
          .toUpperCase(Locale.ENGLISH)));
        Assert.assertTrue(forms.get(4).getString(config.getFormStatusPropertyNameOut()).equals(FormStatus.FAILED.toString()
          .toUpperCase(Locale.ENGLISH)));
    }


}
