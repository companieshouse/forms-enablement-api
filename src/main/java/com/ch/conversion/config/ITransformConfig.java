package com.ch.conversion.config;

import java.util.List;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public interface ITransformConfig {

  String getPackageMultiPartName();

  String getMetaPropertyNameIn();

  String getFormPropertyNameIn();

  // output json
  String getAttachmentsPropertyNameIn();

  String getAttachmentsPropertyNameOut();

  String getBarcodePropertyNameIn();

  String getBarcodePropertyNameOut();

  String getXmlPropertyNameOut();

  String getFormsPropertyNameOut();

  // filing details
  String getFilingDetailsPropertyNameIn();

  String getSubmissionReferencePropertyNameIn();

  String getSubmissionReferenceElementNameOut();

  String getPackageCountPropertyNameIn();

  String getPackageCountElementNameOut();

  String getPackageIdentifierPropertyNameIn();

  String getPackageIdentifierElementNameOut();

  // meta data
  String getRootElementNameOut();

  String getFormTypePropertyNameIn();

  String getFormTypeAttributeNameOut();

  String getFormVersionPropertyNameIn();

  String getFormVersionAttributeNameOut();

  String getSchemasLocation();
  
  List<String> getCaseTransformExceptions();
}
