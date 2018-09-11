package com.ch.exception.mapper;

import static com.ch.service.LoggingService.LoggingLevel.ERROR;
import static com.ch.service.LoggingService.tag;

import com.ch.exception.NoPackageFoundException;
import com.ch.service.LoggingService;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * This is an implementation of ExceptionMapper which is mapped to an NoPackageFoundException.
 * It will handle the exception and return an appropriate response.
 */
public class NoPackageFoundExceptionMapper implements ExceptionMapper<NoPackageFoundException> {

  /**
   * Returns an HTTP response containing the appropriate error message.
   *
   * @param exception - the MissingRequiredDataException that was thrown
   * @return an appropriate HTTP error response
   */
  public Response toResponse(NoPackageFoundException exception) {
    LoggingService.log(tag, ERROR, exception.getMessage(), NoPackageFoundException.class);
    String error = ExceptionHelper.getInstance().getJsonError(exception);
    return Response.status(Response.Status.NOT_FOUND)
      .entity(error)
      .build();
  }
}
