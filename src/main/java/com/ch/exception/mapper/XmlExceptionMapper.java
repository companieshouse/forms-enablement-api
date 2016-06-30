package com.ch.exception.mapper;

import static com.ch.service.LoggingService.LoggingLevel.ERROR;
import static com.ch.service.LoggingService.tag;

import com.ch.exception.XmlException;
import com.ch.service.LoggingService;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * This is an implementation of ExceptionMapper which is mapped to an XmlException.
 * It will handle the exception and return an appropriate response.
 * Created by elliott.jenkins on 19/05/2016.
 */
public class XmlExceptionMapper implements ExceptionMapper<XmlException> {

    /**
     * Returns an HTTP response containing the appropriate error message.
     *
     * @param exception - the XmlException that was thrown
     * @return an appropriate HTTP error response
     */
    public Response toResponse(XmlException exception) {
        LoggingService.log(tag, ERROR, exception.getMessage(), XmlException.class);
        String error = ExceptionHelper.getInstance().getJsonError(exception);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(error)
            .build();
    }
}