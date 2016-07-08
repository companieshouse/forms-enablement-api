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
}