package com.ch.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Created by elliott.jenkins on 05/04/2016.
 */
public class MissingRequiredDataException extends WebApplicationException {

  private final String property;
  private final String parent;

  /**
   * Exception thrown when required data is not present.
   *
   * @param exception error
   * @param property  property not found
   * @param parent    where it should be
   */
  public MissingRequiredDataException(Exception exception, String property, String parent) {
    super(exception);
    this.property = property;
    this.parent = parent;
  }

  /**
   * Exception thrown when required data is not present.
   *
   * @param property property not found
   * @param parent   where it should be
   */
  public MissingRequiredDataException(String property, String parent) {
    this.property = property;
    this.parent = parent;
  }

  @Override
  public String getMessage() {
    return String.format("Missing required data for transformation to occur. Missing: %s in %s",
      property, parent);
  }
}
