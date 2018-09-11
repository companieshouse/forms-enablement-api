package com.ch.conversion.transformations;

import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.helpers.XmlHelper;
import org.json.JSONObject;
import org.w3c.dom.Document;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class FilingDetailsTransform {

  private final ITransformConfig config;
  private final XmlHelper helper;

  private final Document xml;
  private final String xmlLocation;

  private final JSONObject pack;

  /**
   * Apply transforms to filing details.
   *
   * @param config json and xml
   * @param xml    xml to add to
   * @param pack   package data
   * @throws Exception error
   */
  public FilingDetailsTransform(ITransformConfig config, String xml, JSONObject pack) {
    this.config = config;
    this.pack = pack;
    xmlLocation = config.getFilingDetailsPropertyNameIn();

    helper = XmlHelper.getInstance();
    this.xml = helper.createDocumentFromString(xml);
  }

  /**
   * Add required elements to filing details.
   *
   * @return xml
   * @throws Exception error
   */
  public String getXml() {

    // 1. package identifier
    helper.addJsonValueAsElementToXml(xml, pack, xmlLocation,
      config.getPackageMultiPartName(),
      config.getPackageIdentifierPropertyNameIn(),
      config.getPackageIdentifierElementNameOut());

    // 2. package count
    helper.addJsonValueAsElementToXml(xml, pack, xmlLocation,
      config.getPackageMultiPartName(),
      config.getPackageCountPropertyNameIn(),
      config.getPackageCountElementNameOut());

    return helper.getStringFromDocument(xml);
  }
}
