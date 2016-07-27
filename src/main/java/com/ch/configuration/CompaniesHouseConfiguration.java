package com.ch.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Aaron.Witter on 09/03/2016.
 */
public class CompaniesHouseConfiguration {
  @JsonProperty
  @NotEmpty
  private String apiKey;

  @JsonProperty
  @NotEmpty
  private String chipsApiUrl;


  @JsonProperty
  @NotEmpty
  private String name;

  @JsonProperty
  @NotEmpty
  private String barcodeServiceUrl;

  @JsonProperty
  @NotEmpty
  private String jsonGatewayName;

  @JsonProperty
  @NotEmpty
  private String jsonGatewayPassword;

  public String getName() {
    return name;
  }

  public String getBarcodeServiceUrl() {
    return barcodeServiceUrl;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getChipsApiUrl() {
    return chipsApiUrl;
  }

  public String getJsonGatewayName() {
    return jsonGatewayName;
  }

  public String getJsonGatewayPassword() {
    return jsonGatewayPassword;
  }
}