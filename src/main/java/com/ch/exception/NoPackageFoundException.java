package com.ch.exception;

import javax.ws.rs.WebApplicationException;

public class NoPackageFoundException extends WebApplicationException {

  private final String packageIdentifier;
  private final String status;

  /**
   * Exception thrown when required package is not present.
   *
   * @param packageIdentifier identifier for package
   * @param status the status that was searched for
   */
  public NoPackageFoundException(String packageIdentifier, String status) {
    this.packageIdentifier = packageIdentifier;
    this.status = status;
  }

  @Override
  public String getMessage() {
    return String.format("Package with identifier %s and status %s was not found",
      packageIdentifier, status);
  }
}
