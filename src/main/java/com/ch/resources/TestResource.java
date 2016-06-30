package com.ch.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
}
