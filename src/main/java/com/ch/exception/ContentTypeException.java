package com.ch.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Created by elliott.jenkins on 05/04/2016.
 */
public class ContentTypeException extends WebApplicationException {

    private final String message;

    public ContentTypeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("Incorrect content type specified. Should be text/plain or application/json. Sent: %s",
            message);
    }
}