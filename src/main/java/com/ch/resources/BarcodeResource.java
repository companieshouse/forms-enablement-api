package com.ch.resources;

import static com.ch.service.LoggingService.LoggingLevel.INFO;
import static com.ch.service.LoggingService.tag;

import com.ch.application.FormsServiceApplication;
import com.ch.client.ClientHelper;
import com.ch.configuration.CompaniesHouseConfiguration;
import com.ch.service.LoggingService;
import com.codahale.metrics.Timer;
import io.dropwizard.auth.Auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 01/04/2016.
 */
@Path("/barcode")
public class BarcodeResource {
    private static final Timer timer = FormsServiceApplication.registry.timer("BarcodeResource");

    private final ClientHelper client;
    private final CompaniesHouseConfiguration configuration;

    public BarcodeResource(ClientHelper client, CompaniesHouseConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    /**
     * Retrieves unique barcode from CHIPS.
     *
     * @param dateReceived date received json object.
     * @return response from chips.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBarcode(@Auth
                               String dateReceived) {
        final Timer.Context context = timer.time();
        try {
            LoggingService.log(tag, INFO, "Barcode request from Salesforce: " + dateReceived,
                BarcodeResource.class);

            // POST to Barcode Service
            Response response = client.postJson(configuration.getBarcodeServiceUrl(), dateReceived);
            LoggingService.log(tag, INFO, "Response from Barcode Service " + response,
                BarcodeResource.class);
            return response;

        } finally {
            context.stop();
        }
    }
}
