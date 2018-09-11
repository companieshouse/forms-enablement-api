package com.ch.client;

import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.model.PresenterAuthResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 01/08/2016.
 */
public class PresenterHelper {

  private final Client client;
  private final CompaniesHouseConfiguration configuration;

  public PresenterHelper(Client client, CompaniesHouseConfiguration configuration) {
    this.configuration = configuration;
    this.client = client;
  }

  /**
   * Send json to the desired url.
   *
   * @return response from url
   */
  public PresenterAuthResponse getPresenterResponse(String presenterId, String presenterAuth) {
    String authUrl = configuration.getPresenterAuthUrl() + "?id=" + presenterId + "&auth=" + presenterAuth;
    final WebTarget target = client.target(authUrl);
    Response response = target.request().get();
    return response.readEntity(PresenterAuthResponse.class);

  }
}
