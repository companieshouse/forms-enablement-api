package com.ch.resources;

import static com.ch.service.LoggingService.LoggingLevel.INFO;
import static com.ch.service.LoggingService.tag;

import com.ch.application.FormsServiceApplication;
import com.ch.client.SalesforceClientHelper;
import com.ch.configuration.SalesforceConfiguration;
import com.ch.service.LoggingService;
import com.codahale.metrics.Timer;
import io.dropwizard.auth.Auth;
import org.json.simple.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 09/03/2016.
 */
@Path("/response")
public class FormResponseResource {

  private static final Timer timer = FormsServiceApplication.registry.timer("FormResponseResource");

  private final SalesforceClientHelper client;
  private final SalesforceConfiguration configuration;

  public FormResponseResource(SalesforceClientHelper client, SalesforceConfiguration configuration) {
    this.client = client;
    this.configuration = configuration;
  }

  /**
   * Resource to post response from CHIPS to Salesforce.
   *
   * @param verdict JSON to forward to Salesforce
   * @return What to return to CHIPS to be confirmed
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postResponse(@Auth
                               String verdict) {
    final Timer.Context context = timer.time();
    try {
      LoggingService.log(tag, INFO, "Verdict from CHIPS: " + verdict,
        FormResponseResource.class);

      //Get Token from Salesforce
      Response tokenReponse = getToken();

      JSONObject accessTokenJson = tokenReponse.readEntity(JSONObject.class);

      LoggingService.log(tag, INFO, "Token from Salesforce: " + accessTokenJson.toJSONString(), FormResponseResource.class);

      String accessToken = (String) accessTokenJson.get("access_token");

      // POST to Salesforce
      Response response = client.postJson(configuration.getClientUrl(), accessToken, verdict);
      LoggingService.log(tag, INFO, "Response from Salesforce " + response,
        FormResponseResource.class);
      return response;

    } finally {
      context.stop();
    }
  }

  private Response getToken() {
    return client.getToken(configuration);
  }
}