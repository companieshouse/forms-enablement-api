package com.ch.conversion.validation;

import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.Reader;

public class CustomLSInput implements LSInput {

  private String publicId;

  private String systemId;

  private String baseURI;

  private InputStream byteStream;

  /**
   * Constructor.
   */
  public CustomLSInput(String publicId, String sysId) {
    this.publicId = publicId;
    this.systemId = sysId;

  }

  public String getPublicId() {
    return publicId;
  }

  public void setPublicId(String publicId) {
    this.publicId = publicId;
  }

  public String getBaseURI() {
    return baseURI;
  }

  public void setBaseURI(String baseURI) {
    this.baseURI = baseURI;
  }

  public InputStream getByteStream() {
    return byteStream;
  }

  public void setByteStream(InputStream byteStream) {
    this.byteStream = byteStream;
  }

  public boolean getCertifiedText() {
    return false;
  }

  public void setCertifiedText(boolean certifiedText) {
    //Not implemented
  }

  public Reader getCharacterStream() {
    return null;
  }

  public void setCharacterStream(Reader characterStream) {
    //Not implemented
  }

  public String getEncoding() {
    return "UTF-8";
  }

  public void setEncoding(String encoding) {
    //Not implemented
  }

  public String getStringData() {
    return null;
  }

  public void setStringData(String stringData) {
    //Not implemented
  }

  public String getSystemId() {
    return systemId;
  }

  public void setSystemId(String systemId) {
    this.systemId = systemId;
  }
}