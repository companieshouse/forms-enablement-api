package com.ch.resources;

import static com.ch.service.LoggingService.LoggingLevel.INFO;
import static com.ch.service.LoggingService.tag;

import com.ch.application.FormsServiceApplication;
import com.ch.exception.NoPackageFoundException;
import com.ch.helpers.MongoHelper;
import com.ch.model.ConfirmPaymentRequest;
import com.ch.model.FormStatus;
import com.ch.service.LoggingService;
import com.codahale.metrics.Timer;

import io.dropwizard.auth.Auth;

import org.bson.Document;

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


  /**
   * Checks for existing packageReference with a status of PENDING and updates the payment information
   * and the status to PAID.
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
      
      // Try to find an existing package with the supplied identifier and status of PENDING
      ArrayList<Document> packages = MongoHelper.getInstance().getPackagesCollectionByPackageIdAndStatus(
        confirmPaymentRequest.getPackageIdentifier(), FormStatus.PENDING.toString()).into(new ArrayList<Document>());
      if ( packages.size() == 1 ) {
        return Response.ok("Payment confirmed for package " + confirmPaymentRequest.getPackageIdentifier()).build();
      } else {
        LoggingService.log(tag, INFO, "ConfirmPaymentRequest from Salesforce - no packages found: " + confirmPaymentRequest,
          ConfirmPaymentResource.class);
        throw new NoPackageFoundException(confirmPaymentRequest.getPackageIdentifier(), FormStatus.PENDING.toString());
      }
      
      

    } finally {
      context.stop();
    }
  }
}
