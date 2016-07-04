package com.ch.resources;

import static com.ch.service.LoggingService.LoggingLevel.INFO;
import static com.ch.service.LoggingService.tag;

import com.ch.application.FormsServiceApplication;
import com.ch.client.ClientHelper;
import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.conversion.builders.JsonBuilder;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.service.LoggingService;
import com.codahale.metrics.Timer;
import io.dropwizard.auth.Auth;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 07/03/2016.
 */
@Path("/submission")
public class FormSubmissionResource {
  private static final Timer timer = FormsServiceApplication.registry.timer("FormSubmissionResource");

  private final ClientHelper client;
  private final CompaniesHouseConfiguration configuration;

  public FormSubmissionResource(ClientHelper client, CompaniesHouseConfiguration configuration) {
    this.client = client;
    this.configuration = configuration;
  }

  /**
   * Resource to post forms from Salesforce to CHIPS.
   *
   * @return json with response from CHIPS
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postForms(@Auth
                                FormDataMultiPart multi) {
    final Timer.Context context = timer.time();
    try {
      // convert input to json
      ITransformConfig config = new TransformConfig();
      JsonBuilder builder = new JsonBuilder(config, multi);
      String forms = builder.getJson();
      LoggingService.log(tag, INFO, "Transformation output: " + forms,
          FormSubmissionResource.class);

      // post to CHIPS
      Response response = client.postJson(configuration.getChipsApiUrl(), forms);
      LoggingService.log(tag, INFO, "Response from CHIPS: " + response.toString(),
          FormSubmissionResource.class);

      // return response from CHIPS
      return response;

    } finally {
      context.stop();
    }
  }
}