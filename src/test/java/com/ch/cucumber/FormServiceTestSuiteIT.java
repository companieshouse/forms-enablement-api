package com.ch.cucumber;

import com.ch.application.FormsServiceApplication;
import com.ch.configuration.FormsServiceConfiguration;
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
@CucumberOptions(plugin = {"pretty", "json:target/cucumber.json"})
public class FormServiceTestSuiteIT {
    @ClassRule
    public static final DropwizardAppRule<FormsServiceConfiguration> RULE =
        new DropwizardAppRule<>(FormsServiceApplication.class, ResourceHelpers.resourceFilePath("test-configuration.yml"));
}
