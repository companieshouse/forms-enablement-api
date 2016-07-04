package com.ch.cucumber;

import com.ch.application.FormsServiceApplication;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.helpers.TestHelper;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

/**
 * Created by Aaron.Witter on 09/03/2016.
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/", plugin = {"pretty",
    "json:target/cucumber.json"})
public class FormServiceTestSuiteIT {

    @ClassRule
    public static final DropwizardAppRule<FormsServiceConfiguration> RULE = new DropwizardAppRule<>(
        FormsServiceApplication.class, ResourceHelpers
        .resourceFilePath(getYmlfile()));

    private static String getYmlfile()  {
        String envYml = System.getenv().get(TestHelper.FORM_SERVICE_TEST_YML);
        return envYml != null ? envYml : "test-configuration.yml";
    }
}