package com.ch.conversion.builders;

import com.ch.conversion.config.ITransformConfig;

import com.ch.conversion.transformations.FilingDetailsTransform;
import com.ch.conversion.transformations.ManualElementsTransform;
import com.ch.conversion.transformations.MetaDataTransform;
import com.ch.conversion.transformations.UpperCaseTransform;
import com.ch.conversion.validation.XmlValidator;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.json.XML;


/**
 * Created by elliott.jenkins on 30/03/2016.
 */
public class FormXmlBuilder {

  private final ITransformConfig config;
  private final XmlValidator xmlValidator;
  private final JSONObject pack;
  private final JSONObject meta;
  private final JSONObject form;

  /**
   * Builder to create the encoded xml of a form.
   *
   * @param config json and xml
   * @param pack   package data
   * @param meta   form meta data
   * @param form   form data
   */
  public FormXmlBuilder(ITransformConfig config, JSONObject pack, JSONObject meta, JSONObject form, XmlValidator xmlValidator) {
    this.config = config;
    this.pack = pack;
    this.meta = meta;
    this.form = form;
    this.xmlValidator = xmlValidator;
  }

  /**
   * Get the encoded xml.
   *
   * @return base64 encoded xml
   */
  public String getXML() {
    // Convert form data strings to upper case in the json
    UpperCaseTransform.getInstance().parentUpperCase(form, config.getCaseTransformExceptions());

    //Overwrite any payment data with placeholder
    JSONObject jsonXmlFilingDetails = form.optJSONObject(config.getFilingDetailsPropertyNameIn());
    if ( jsonXmlFilingDetails != null ) {
      
      JSONObject jsonPayment = new JSONObject();
      jsonPayment.put(config.getReferenceNumberPropertyNameIn(), config.getReferenceNumberPlaceholderValueOut());
      jsonPayment.put(config.getPaymentMethodPropertyNameIn(), config.getPaymentMethodPlaceholderValueOut());
      jsonPayment.put(config.getAccountNumberPropertyNameIn(), config.getAccountNumberPlaceholderValueOut());
      
      jsonXmlFilingDetails.put(config.getPaymentPropertyNameIn(), jsonPayment);
      form.put(config.getFilingDetailsPropertyNameIn(), jsonXmlFilingDetails);
    }
    
    // Convert the form json straight to xml
    String xml = toXml();

    // Add root element and meta data attributes to the xml
    String metaXml = addMetaData(xml);

    // Add extra filing details
    String filingDetailsXml = addFilingDetails(metaXml);

    // Add any manual elements into the xml
    String manualXml = addManualElements(filingDetailsXml);

    // Validate xml against form xsd
    validateXml(manualXml);

    // Base64 encode
    return encode(manualXml);
  }

  private String toXml() {
    return XML.toString(form);
  }

  private String addMetaData(String xml) {
    MetaDataTransform transform = new MetaDataTransform(config, xml, meta);
    return transform.getXml();
  }

  private String addFilingDetails(String xml) {
    FilingDetailsTransform transform = new FilingDetailsTransform(config, xml, pack);
    return transform.getXml();
  }

  private String addManualElements(String xml) {
    ManualElementsTransform transform = new ManualElementsTransform(config, xml);
    return transform.getXml();
  }

  private void validateXml(String xml) {
    xmlValidator.validate(config, xml);
  }

  private String encode(String xml) {
    byte[] encoded = Base64.encodeBase64(xml.getBytes());
    return new String(encoded);
  }
}
