package com.ch.conversion.validation;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;

public class SchemaResolver implements LSResourceResolver {

  private String prefix;

  /**
   * Resolve a resource.
   */
  public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

    String prefixedSystemId = prefix + "/" + systemId;
    InputStream stream = getClass().getClassLoader().getResourceAsStream(prefixedSystemId);

    LSInput input = new CustomLSInput(publicId, prefixedSystemId);

    input.setPublicId(publicId);
    input.setBaseURI(baseURI);
    input.setByteStream(stream);

    return input;
  }

  /**
   * Get the prefix.
   *
   * @return the prefix
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * Set the prefix.
   *
   * @param prefix the prefix to set
   */
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

}