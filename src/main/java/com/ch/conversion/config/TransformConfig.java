package com.ch.conversion.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class TransformConfig implements ITransformConfig {

  public String getPackageMultiPartName() {
    return "packagemetadata";
  }

  public String getMetaPropertyNameIn() {
    return "metadata";
  }

  public String getFormPropertyNameIn() {
    return "formdata";
  }

  public String getAttachmentsPropertyNameIn() {
    return "attachments";
  }

  public String getAttachmentsPropertyNameOut() {
    return "attachments";
  }

  public String getBarcodePropertyNameIn() {
    return "barcode";
  }

  public String getBarcodePropertyNameOut() {
    return "barcode";
  }

  public String getXmlPropertyNameOut() {
    return "xml";
  }

  public String getFormsPropertyNameOut() {
    return "forms";
  }

  public String getFormStatusPropertyNameOut() {
    return "status";
  }

  public String getPackageDatePropertyNameOut() {
    return "date";
  }

  public String getFilingDetailsPropertyNameIn() {
    return "filingDetails";
  }

  public String getSubmissionReferencePropertyNameIn() {
    return "submissionReference";
  }

  public String getSubmissionReferenceElementNameOut() {
    return "submissionReference";
  }

  public String getPackageCountPropertyNameIn() {
    return "count";
  }

  public String getPackageCountElementNameOut() {
    return "packageCount";
  }

  public String getPackageIdentifierPropertyNameIn() {
    return "packageIdentifier";
  }

  public String getPackageIdentifierElementNameOut() {
    return "packageIdentifier";
  }


  public String getPaymentPropertyNameIn() {
    return "payment";
  }

  public String getPaymentElementNameOut() {
    return "payment";
  }

  public String getPaymentMethodPropertyNameIn() {
    return "paymentMethod";
  }

  public String getPaymentMethodElementNameOut() {
    return "paymentMethod";
  }

  public String getAccountNumberPropertyNameIn() {
    return "accountNumber";
  }

  public String getAccountNumberElementNameOut() {
    return "accountNumber";
  }
  
  public String getReferenceNumberPropertyNameIn() {
    return "referenceNumber";
  }

  public String getReferenceNumberPlaceholderValueOut() {
    return ":placeholderReferenceNumber:";
  }
  
  public String getPaymentMethodPlaceholderValueOut() {
    return ":placeholderPaymentMethod:";
  }

  public String getAccountNumberPlaceholderValueOut() {
    return ":placeholderAccountNumber:";
  }
  
  public String getRootElementNameOut() {
    return "form";
  }

  public String getFormTypePropertyNameIn() {
    return "type";
  }

  public String getFormTypeAttributeNameOut() {
    return "type";
  }

  public String getFormVersionPropertyNameIn() {
    return "version";
  }

  public String getFormVersionAttributeNameOut() {
    return "version";
  }

  public String getPresenterIdPropertyNameIn() {
    return "presenterId";
  }
  
  public String getPresenterAuthPropertyNameIn() {
    return "presenterAuth";
  }

  public String getSchemasLocation() {
    return "schemas";
  }

  /**
   * Return a list of JSON paths that should not be made uppercase.
   *
   * @return List
   */
  public List<String> getCaseTransformExceptions() {
    List<String> exceptions = new ArrayList<String>();

    exceptions.add("/filingDetails/payment/paymentMethod");
    exceptions.add("/filingDetails/presenterDocumentReference");
    exceptions.add("/filingDetails/submissionReference");
    exceptions.add("/filingDetails/presenterDetails/presenterEmailIn");
    exceptions.add("/filingDetails/presenterDetails/presenterEmailOut");

    return exceptions;
  }
  
  /**
   * Return a list of forms that require payment.
   *
   * @return List
   */
  public List<String> getPaidFormList() {
    List<String> paidFormTypes = new ArrayList<String>();
    paidFormTypes.add("DS01");
    paidFormTypes.add("LLDS01");
    return paidFormTypes;
  }
}
