package com.ch.resources;

import static com.ch.service.LoggingService.LoggingLevel.INFO;
import static com.ch.service.LoggingService.tag;

import com.ch.application.FormServiceConstants;
import com.ch.application.FormsServiceApplication;
import com.ch.client.PresenterHelper;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.DatabaseException;
import com.ch.exception.NoPackageFoundException;
import com.ch.exception.PresenterAuthenticationException;
import com.ch.helpers.ConfirmPaymentHelper;
import com.ch.helpers.MongoHelper;
import com.ch.model.ConfirmPaymentRequest;
import com.ch.model.FormStatus;
import com.ch.model.Payment;
import com.ch.model.PresenterAuthResponse;
import com.ch.service.LoggingService;
import com.codahale.metrics.Timer;

import io.dropwizard.auth.Auth;

import org.apache.commons.codec.binary.Base64;
import org.bson.Document;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Confirm Payment Resource to update status and payment details for previous submission.
 */
@Path("/confirmpaid")
public class ConfirmPaymentResource {
  private static final Timer timer = FormsServiceApplication.registry.timer("ConfirmPaymentResource");
  
  private final ConfirmPaymentHelper confirmHelper;

  public ConfirmPaymentResource(ConfirmPaymentHelper confirmHelper) {
    this.confirmHelper = confirmHelper;
  }

  /**
   * Checks for existing packageReference with a status of UNPAID and updates the payment information
   * and the status to PENDING.
   *
   * @return response - 200 or 400
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response confirmPaid(@Auth ConfirmPaymentRequest confirmPaymentRequest) {
    final Timer.Context context = timer.time();
    try {
      LoggingService.log(tag, INFO, "ConfirmPaymentRequest from Salesforce: " + confirmPaymentRequest,
        ConfirmPaymentResource.class);
      
      confirmHelper.confirm(confirmPaymentRequest);
      return Response.ok("Payment confirmed for package " + confirmPaymentRequest.getPackageIdentifier()).build();

    } finally {
      context.stop();
    }
  } 
}
