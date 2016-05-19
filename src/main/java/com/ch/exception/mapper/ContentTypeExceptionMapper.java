package com.ch.exception.mapper;

import com.ch.exception.ContentTypeException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * This is an implementation of ExceptionMapper which is mapped to an ContentTypeException.
 * It will handle the exception and return an appropriate response.
 * Created by elliott.jenkins on 19/05/2016.
 */
public class ContentTypeExceptionMapper implements ExceptionMapper<ContentTypeException> {

  /**
   * Returns an HTTP response containing the appropriate error message.
   *
   * @param exception - the ContentTypeException that was thrown
   * @return an appropriate HTTP error response
   */
  public Response toResponse(ContentTypeException exception) {
    return Response.status(Response.Status.BAD_REQUEST)
        .header("Error", exception.getMessage())
        .build();
  }
}