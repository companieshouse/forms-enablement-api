package com.ch.resources;

import io.dropwizard.jersey.PATCH;
import org.json.simple.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 10/04/2016.
 */
@Path("test")
public class TestResource {

  @POST
  @Path("/submission")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postSubmission(String forms) {
    return Response.status(202).entity(forms).build();
  }

  @POST
  @Path("/response")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postResponse(String verdict) {
    return Response.status(202).entity(verdict).build();
  }

  @POST
  @Path("/barcode")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postBarcode(String date) {
    return Response.status(202).entity(date).build();
  }

  /**
   * Method to simulate the auth token response from salsforce.
   * @return Response object.
   */
  @POST
  @Path("/salesforce/auth")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postAuth() {

    JSONObject jsonObject = new JSONObject();

    jsonObject.put("access_token", "abcdefghijklmnopqrstuvwxyz");

    return Response.status(202).entity(jsonObject).build();
  }

  /**
   * Mock endpoint for Salesforce client.
   * @param accessToken takes accesstoken as a header.
   * @return acceptance response.
   */
  @PATCH
  @Path("/salesforce/client")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postVerdict(@HeaderParam("Authorization") String accessToken) {
    JSONObject jsonObject = new JSONObject();

    jsonObject.put("access_token", accessToken);
    return Response.status(202).entity(jsonObject).build();
  }
}
