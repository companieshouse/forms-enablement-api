package com.ch.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Created by elliott.jenkins on 21/04/2016.
 */
public class XsdValidationException extends WebApplicationException {

    private final String formType;
    private final String schema;

    /**
     * Exception thrown when xml does not validate against its schema.
     *
     * @param exception error
     * @param formType  xml form type
     * @param schema    schema used
     */
    public XsdValidationException(Exception exception, String formType, String schema) {
        super(exception);
        this.formType = formType;
        this.schema = schema;
    }

    @Override
    public String getMessage() {
        return String.format("XML for form type %s, could not be validated against schema %s. %s",
            formType, schema, getCause().getMessage());
    }
}
