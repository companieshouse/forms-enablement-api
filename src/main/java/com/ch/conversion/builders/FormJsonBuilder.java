package com.ch.conversion.builders;

import com.ch.application.FormServiceConstants;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.helpers.JsonHelper;
import com.ch.conversion.validation.XmlValidatorImpl;
import com.ch.exception.MissingRequiredDataException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
  public FormJsonBuilder(ITransformConfig config, String packageJson, String formJson) {
    this.config = config;
    this.helper = JsonHelper.getInstance();
    this.pack = new JSONObject(packageJson);

    JSONObject form = new JSONObject(formJson);

    this.meta = helper.getObjectFromJson(form, "parent json object (form body part)", config.getMetaPropertyNameIn());
    this.form = helper.getObjectFromJson(form, "parent json object (form body part)", config.getFormPropertyNameIn());
    this.attachments = helper.getArrayFromJson(form, "parent json object (form body part)",
      config.getAttachmentsPropertyNameIn());
    
    if (attachments.length() == 0) {
      throw new MissingRequiredDataException(config.getAttachmentsPropertyNameIn() + " length is 0", "(form body part)");
    }
  }

  /**
   * Get the json object for a single form.
   *
   * @return transformed json
   */
  public JSONObject getJson() {
    // Create empty json object
    JSONObject output = new JSONObject();

    // Add attachments
    output.put(config.getAttachmentsPropertyNameOut(), attachments);

    // Add barcode
    Object barcode = getFormBarcode();
    output.put(config.getBarcodePropertyNameOut(), barcode);
    
    // Add package identifier
    Object packageIdentifier = getPackageIdentifier();
    output.put(config.getPackageIdentifierPropertyNameIn(), packageIdentifier);
    
    // Add submissionReference
    String submissionReference = getSubmissionReference(packageIdentifier.toString(), barcode.toString());
    addSubmissionReference(submissionReference);
    output.put(config.getSubmissionReferenceElementNameOut(), submissionReference);

    // Add default status
    Object status = getDefaultStatus();
    if ( config.getPaidFormList().contains(meta.getString(config.getFormTypePropertyNameIn())) ) {
      status = FormServiceConstants.PACKAGE_STATUS_NEEDS_PAYMENT;
    }
    
    output.put(config.getFormStatusPropertyNameOut(), status);

    // Transform package and form json into base64 xml
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
   * Adds submission reference to json object.
   *
   * @param submissionReference as string.
   * @return Form as JSONObject.
   */
  protected JSONObject addSubmissionReference(String submissionReference) {
    try {
      
      JSONObject filingDetails = form.getJSONObject(config.getFilingDetailsPropertyNameIn());
      
      filingDetails.put(config.getSubmissionReferencePropertyNameIn(), submissionReference);

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
