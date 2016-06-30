package com.ch.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Created by elliott.jenkins on 05/04/2016.
 */
public class ConnectionException extends WebApplicationException {

    private final String message;

    public ConnectionException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("Unable to connect to: %s", message);
    }
}
