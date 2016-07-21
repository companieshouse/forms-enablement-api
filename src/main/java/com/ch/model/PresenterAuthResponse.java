package com.ch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Aaron.Witter on 20/07/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PresenterAuthResponse {

  private String presenterAccountNumber;

  public PresenterAuthResponse() {// empty constructor for de serialisation
  }

  public PresenterAuthResponse(String presenterAccountNumber) {
    this.presenterAccountNumber = presenterAccountNumber;
  }

  @JsonProperty
  public String getPresenterAccountNumber() {
    return presenterAccountNumber;
  }
}