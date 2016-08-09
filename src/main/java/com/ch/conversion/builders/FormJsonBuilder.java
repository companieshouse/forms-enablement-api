package com.ch.conversion.builders;


import com.ch.application.FormServiceConstants;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.helpers.JsonHelper;
import com.ch.conversion.validation.XmlValidatorImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class FormJsonBuilder {

  private final JsonHelper helper;

  private final ITransformConfig config;
  private final JSONObject pack;
  private final JSONObject meta;
  private final JSONObject form;
  private final JSONArray attachments;

  /**
   * Builder to convert one form into the required json object.
   *
   * @param config      json and xml
   * @param packageJson package data json
   * @param formJson    form data json
   */
  public FormJsonBuilder(ITransformConfig config, String packageJson, String formJson, String presenterAccountNumber, 
    String packageIdentifier) {
    this.config = config;
    this.helper = JsonHelper.getInstance();
    this.pack = new JSONObject(packageJson);

    JSONObject form = new JSONObject(formJson);

    this.meta = helper.getObjectFromJson(form, "parent json object (form body part)", config.getMetaPropertyNameIn());
    this.form = helper.getObjectFromJson(form, "parent json object (form body part)", config.getFormPropertyNameIn());
    this.attachments = helper.getArrayFromJson(form, "parent json object (form body part)",
      config.getAttachmentsPropertyNameIn());

    if (presenterAccountNumber != null) {
      addAccountNumber(presenterAccountNumber);
    }
    
    addSubmissionReference(packageIdentifier);
    
  }

  /**
   * Get the json object for a single form.
   *
   * @return transformed json
   */
  public JSONObject getJson() {
    // 1. create empty json object
    JSONObject output = new JSONObject();

    // 2. add attachments
    output.put(config.getAttachmentsPropertyNameOut(), attachments);

    // 3. add barcode
    Object barcode = getFormBarcode();
    output.put(config.getBarcodePropertyNameOut(), barcode);
    
    // 4. add package identifier
    Object packageIdentifier = getPackageIdentifier();
    output.put(config.getPackageIdentifierPropertyNameIn(), packageIdentifier);
    
    // 5. add submissionReference
    String submissionReference = getSubmissionReference(packageIdentifier.toString(), barcode.toString());
    output.put(config.getSubmissionReferenceElementNameOut(), submissionReference);

    // 6. add default status
    Object status = getDefaultStatus();
    output.put(config.getFormStatusPropertyNameOut(), status);

    // 7. transform package and form json into base64 xml
    String xml = getFormXML();
    output.put(config.getXmlPropertyNameOut(), xml);
    return output;
  }


  private Object getFormBarcode() {
    JSONObject details = helper.getObjectFromJson(form, config.getFormPropertyNameIn(),
      config.getFilingDetailsPropertyNameIn());
    return helper.getValueFromJson(details, config.getFilingDetailsPropertyNameIn(), config.getBarcodePropertyNameIn());
  }

  private Object getPackageIdentifier() {
    return helper.getValueFromJson(pack, "", config.getPackageIdentifierPropertyNameIn());
  }

  /**
   * Gets the form as xml. Change the validation here if required. It must implement XmlValidator Interface.
   *
   * @return XML string.
   */
  private String getFormXML() {
    FormXmlBuilder builder = new FormXmlBuilder(config, pack, meta, form, new XmlValidatorImpl());
    return builder.getXML();
  }

  private String getDefaultStatus() {
    return FormServiceConstants.PACKAGE_STATUS_DEFAULT;
  }

  /**
   * Adds account number to json object if payment number is account.
   *
   * @param accountNumber account number as string.
   * @return Form as string.
   */
  protected JSONObject addAccountNumber(String accountNumber) {

    try {

      JSONObject paymentProperty = form.getJSONObject(config.getFilingDetailsPropertyNameIn())
        .getJSONObject(config.getPaymentPropertyNameIn());

      if ("account".equals(paymentProperty.get(config.getPaymentMethodPropertyNameIn()))) {
        paymentProperty.put(config.getAccountNumberPropertyNameIn(), accountNumber);

        return form;
      }
    } catch (JSONException ex) {
      return form;
    }
    return form;
  }
  
  /**
   * Adds submission reference to json object
   *
   * @param packageIdentifier as string.
   * @return Form as JSONObject.
   */
  protected JSONObject addSubmissionReference(String packageIdentifier) {
    try {

      String barcode = (String) getFormBarcode();
      
      JSONObject filingDetails = form.getJSONObject(config.getFilingDetailsPropertyNameIn());
      
      filingDetails.put(config.getSubmissionReferencePropertyNameIn(), 
        getSubmissionReference(packageIdentifier, barcode));

      return form;
        
    } catch (JSONException ex) {
      return form;
    }
  }  

  protected JSONObject getForm() {
    return form;
  }
  
  protected String getSubmissionReference(String packageId, String barcode) {
    return packageId + "-" + barcode;
  }

}
