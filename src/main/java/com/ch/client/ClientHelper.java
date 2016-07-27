package com.ch.client;

import org.glassfish.jersey.internal.util.Base64;

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

  /**
   * Send json to the desired url.
   *
   * @param url  destination
   * @param json json to send
   * @return response from url
   */
  public Response postJson(String url, String json, String name, String password) {
    final WebTarget target = client.target(url);
    String encode = Base64.encodeAsString(name + ":" + password);
    return target.request().header("Authorization", "Basic " + encode).post(Entity.json(json));
  }
}


