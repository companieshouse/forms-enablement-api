package com.ch.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by elliott.jenkins on 05/04/2016.
 */
public final class ClientHelper {

  private final Client client;

  public ClientHelper(Client client) {
    this.client = client;
  }

  /**
   * Send json to the desired url.
   *
   * @param url  destination
   * @param json json to send
   * @return response from url
   */
  public Response postJson(String url, String json) {
    final WebTarget target = client.target(url);
    return target.request().post(Entity.json(json));
  }
}


