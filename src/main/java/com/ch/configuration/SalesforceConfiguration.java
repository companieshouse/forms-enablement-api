package com.ch.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Aaron.Witter on 09/03/2016.
 */
public class SalesforceConfiguration {
    @JsonProperty
    @NotEmpty
    private String secret;

    @JsonProperty
    @NotEmpty
    private String apiUrl;

    @JsonProperty
    @NotEmpty
    private String name;

    public String getSecret() {
        return secret;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getName() {
        return name;
    }
}