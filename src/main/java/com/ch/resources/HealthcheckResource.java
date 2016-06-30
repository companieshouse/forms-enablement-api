package com.ch.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 15/03/2016.
 */
@Path("/healthcheck")
public class HealthcheckResource {

    @GET
    public Response returnSuccess() {
        return Response.ok().build();
    }
}

