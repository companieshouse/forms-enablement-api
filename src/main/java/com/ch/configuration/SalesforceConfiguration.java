package com.ch.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Aaron.Witter on 09/03/2016.
 */
public class SalesforceConfiguration {

  @JsonProperty
  @NotEmpty
  private String authId;

  @JsonProperty
  @NotEmpty
  private String authUsername;

  @JsonProperty
  @NotEmpty
  private String authPassword;

  @JsonProperty
  @NotEmpty
  private String authSecret;

  @JsonProperty
  @NotEmpty
  private String authUrl;

  @JsonProperty
  @NotEmpty
  private String authGrantType;

  @JsonProperty
  @NotEmpty
  private String clientUrl;

  @JsonProperty
  @NotEmpty
  private String apiUrl;

  @JsonProperty
  @NotEmpty
  private String apiKey;
  
  @JsonProperty
  @NotEmpty
  private boolean apiUseProxy;
  
  @JsonProperty
  @NotEmpty
  private String apiProxyHost;
  
  @JsonProperty
  @NotEmpty
  private int apiProxyPort;
  
  @JsonProperty
  @NotEmpty
  private String name;

  public String getName() {
    return name;
  }

  public String getAuthUsername() {
    return authUsername;
  }

  public String getAuthPassword() {
    return authPassword;
  }

  public String getAuthSecret() {
    return authSecret;
  }

  public String getAuthUrl() {
    return authUrl;
  }

  public String getAuthGrantType() {
    return authGrantType;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getAuthId() {
    return authId;
  }

  public String getClientUrl() {
    return clientUrl;
  }

  public boolean isApiUseProxy() {
    return apiUseProxy;
  }

  public String getApiProxyHost() {
    return apiProxyHost;
  }

  public int getApiProxyPort() {
    return apiProxyPort;
  }
}