package com.ch.model;

public class Payment {

  private String referenceNumber;
  private String paymentMethod;
  private String accountNumber;

  /**
   * Constructs an empty confirm payment request.
   */
  public Payment() {
    //Empty constructor is needed for automatic deserialisation
  }

  /**
   * Constructs an empty confirm payment request.
   */
  public Payment(String referenceNumber, String paymentMethod, String accountNumber) {
    this.referenceNumber = referenceNumber;
    this.paymentMethod = paymentMethod;
    this.accountNumber = accountNumber;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }
  
  public String getPaymentMethod() {
    return paymentMethod;
  }

  public String getAccountNumber() {
    return accountNumber;
  }
}