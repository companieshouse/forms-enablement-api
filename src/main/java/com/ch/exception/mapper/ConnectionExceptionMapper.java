package com.ch.exception.mapper;

import com.ch.exception.ConnectionException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * This is an implementation of ExceptionMapper which is mapped to an ConnectionException.
 * It will handle the exception and return an appropriate response.
 * Created by elliott.jenkins on 19/05/2016.
 */
public class ConnectionExceptionMapper implements ExceptionMapper<ConnectionException> {

  /**
   * Returns an HTTP response containing the appropriate error message.
   *
   * @param exception - the ConnectionException that was thrown
   * @return an appropriate HTTP error response
   */
  public Response toResponse(ConnectionException exception) {
    return Response.status(Response.Status.NOT_FOUND)
        .header("Error", exception.getMessage())
        .build();
  }
}