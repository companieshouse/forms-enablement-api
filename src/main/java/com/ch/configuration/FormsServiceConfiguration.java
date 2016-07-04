package com.ch.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Created by Aaron.Witter on 07/03/2016.
 */
public class FormsServiceConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private final JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @JsonProperty
    private int rateLimit;

    @JsonProperty
    private SalesforceConfiguration salesforceConfiguration;

    @JsonProperty
    private CompaniesHouseConfiguration companiesHouseConfiguration;

    @JsonProperty
    private FluentLoggingConfiguration fluentLogging;

    @NotNull
    @JsonProperty
    private Log4jConfiguration log4jConfiguration;

    @NotNull
    @JsonProperty
    private String mongoDbUri;

    @NotNull
    @JsonProperty
    private String mongoDbName;

    @NotNull
    @JsonProperty
    private String mongoDbPackagesCollectionName;

    @NotNull
    @JsonProperty
    private String mongoDbFormsCollectionName;

    public int getRateLimit() {
        return rateLimit;
    }

    public SalesforceConfiguration getSalesforceConfiguration() {
        return salesforceConfiguration;
    }

    public CompaniesHouseConfiguration getCompaniesHouseConfiguration() {
        return companiesHouseConfiguration;
    }

    public FluentLoggingConfiguration getFluentLoggingConfiguration() {
        return fluentLogging;
    }

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    public Log4jConfiguration getLog4jConfiguration() {
        return log4jConfiguration;
    }

    public String getMongoDbUri() {
        return mongoDbUri;
    }

    public String getMongoDbName() {
        return mongoDbName;
    }

    public String getMongoDbPackagesCollectionName() {
        return mongoDbPackagesCollectionName;
    }

    public String getMongoDbFormsCollectionName() {
        return mongoDbFormsCollectionName;
    }
}