package com.ch.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Created by elliott.jenkins on 05/04/2016.
 */
public class XmlException extends WebApplicationException {

    private final String message;

    public XmlException(String message) {
        this.message = message;
    }

    public XmlException(Exception exception, String message) {
        super(exception);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("Error handling xml. %s",
            message);
    }
}
