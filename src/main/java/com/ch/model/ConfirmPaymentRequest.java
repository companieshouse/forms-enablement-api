package com.ch.model;

public class ConfirmPaymentRequest {

  private String packageIdentifier;
  private Payment payment;

  /**
   * Constructs an empty confirm payment request.
   */
  public ConfirmPaymentRequest() {
    //Empty constructor is needed for automatic deserialisation
  }

  /**
   * Constructs an empty confirm payment request.
   */
  public ConfirmPaymentRequest(String packageIdentifier, Payment payment) {
    this.packageIdentifier = packageIdentifier;
    this.payment = payment;
  }

  public String getPackageIdentifier() {
    return packageIdentifier;
  }

  public Payment getPayment() {
    return payment;
  }
}