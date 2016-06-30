package com.ch.resources;

import com.ch.model.FormsApiUser;
import io.dropwizard.auth.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Aaron.Witter on 11/03/2016.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HomeResource {

    @GET
    public String getHome() {
        return "Hello World";
    }

    @Path("auth")
    @GET
    public String getAuth(@Auth FormsApiUser user) {
        return "Hello Authorised World";
    }
}
