package com.ch.conversion.config;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class TestTransformationConfig implements ITransformConfig {
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

  public String getFilingDetailsPropertyNameIn() {
    return "filingDetails";
  }

  public String getSubmissionNumberPropertyNameIn() {
    return "submissionnumber";
  }

  public String getSubmissionNumberElementNameOut() {
    return "submissionNumber";
  }

  public String getPackageCountPropertyNameIn() {
    return "count";
  }

  public String getPackageCountElementNameOut() {
    return "packageCount";
  }

  public String getPackageIdentifierPropertyNameIn() {
    return "id";
  }

  public String getPackageIdentifierElementNameOut() {
    return "packageIdentifier";
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
}
