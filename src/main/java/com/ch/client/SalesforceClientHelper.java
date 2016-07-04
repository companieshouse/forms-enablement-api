package com.ch.client;

import com.ch.configuration.SalesforceConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 30/06/2016.
 */
public class SalesforceClientHelper {

  private final Client client;

  public SalesforceClientHelper(Client client) {
    this.client = client;
  }

  /**
   * Patches json verdict to Salesforce.
   *
   * @param clientUrl   salesforce client url
   * @param accessToken access token
   * @param verdict     json verdict from chips
   * @return response
   */
  public Response postJson(String clientUrl, String accessToken, String verdict) {
    final WebTarget target = client.target(clientUrl);
    return target.request(MediaType.APPLICATION_JSON_TYPE)
        .header("Authorization", "Bearer " + accessToken).method("PATCH", Entity.json(verdict));
  }

  /**
   * Gets token from Salesforce oauth client.
   *
   * @param configuration Salesforce configuration object
   * @return response from url
   */
  public Response getToken(SalesforceConfiguration configuration) {
    final WebTarget target = client.target(configuration.getAuthUrl())
        .queryParam("grant_type", configuration.getAuthGrantType())
        .queryParam("client_id", configuration.getAuthId())
        .queryParam("client_secret", configuration.getAuthSecret())
        .queryParam("username", configuration.getAuthUsername())
        .queryParam("password", configuration.getAuthPassword());
    return target.request().post(Entity.json(null));
  }
}
