package com.ch.helpers;

import static com.ch.service.LoggingService.LoggingLevel.INFO;
import static com.ch.service.LoggingService.tag;

import com.ch.application.FormServiceConstants;
import com.ch.client.PresenterHelper;
import com.ch.conversion.config.ITransformConfig;
import com.ch.exception.DatabaseException;
import com.ch.exception.NoPackageFoundException;
import com.ch.exception.PresenterAuthenticationException;
import com.ch.model.ConfirmPaymentRequest;
import com.ch.model.Payment;
import com.ch.model.PresenterAuthResponse;
import com.ch.resources.ConfirmPaymentResource;
import com.ch.service.LoggingService;
import org.apache.commons.codec.binary.Base64;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConfirmPaymentHelper {

  private final ITransformConfig config;
  private final MongoHelper helper = MongoHelper.getInstance();
  private final PresenterHelper presenterHelper;
  
  public ConfirmPaymentHelper(ITransformConfig configuration, PresenterHelper presenterHelper) {
    this.config = configuration;
    this.presenterHelper = presenterHelper;
  }
  
  /**
   * Search for a package and update the form xml with the payment data and the form and package status to PEDNING.
   * 
   * @param confirmPaymentRequest the model of the payment confirmation request.
   */
  public void confirm(ConfirmPaymentRequest confirmPaymentRequest) {
    
    // Try to find an existing package with the supplied identifier and status of UNPAID
    ArrayList<Document> packages = helper.getPackagesCollectionByPackageIdAndStatus(
      confirmPaymentRequest.getPackageIdentifier(), FormServiceConstants.PACKAGE_STATUS_NEEDS_PAYMENT)
      .into(new ArrayList<Document>());
    
    if ( packages.size() == 1 ) {
      
      Payment payment = confirmPaymentRequest.getPayment();
      
      boolean isAccountPayment = false;
      if ( payment.getPaymentMethod().equalsIgnoreCase(FormServiceConstants.PAYMENT_TYPE_ACCOUNT) ) {
        isAccountPayment = true;
      }
      
      // If account payment, then obtain the account number from the presenter auth service
      // The accountNumber submitted to here in confirmPaymentRequest is actually the presenter identifier 
      // and not the account number used to make a charge from CHIPS
      String presenterAccountNumber = "";
      if ( isAccountPayment ) {
        presenterAccountNumber = getPresenterAccountNumber(confirmPaymentRequest);
      }
      
      //Find corresponding forms and update the payment information in each and the status
      ArrayList<Document> forms = helper.getFormsCollectionByPackageId(confirmPaymentRequest.getPackageIdentifier())
              .into(new ArrayList<Document>());
      for (Document form : forms) {
        updateFormXmlAndStatus(form, payment, presenterAccountNumber);
      }
      
      //Update package status
      boolean result = MongoHelper.getInstance()
              .updatePackageStatusByPackageId(confirmPaymentRequest.getPackageIdentifier(),
                FormServiceConstants.PACKAGE_STATUS_DEFAULT);
      if ( !result ) {
        throw new DatabaseException("Failed to update database. Updating package status package id " 
         + confirmPaymentRequest.getPackageIdentifier());
      }

    } else {
      LoggingService.log(tag, INFO, "ConfirmPaymentRequest from Salesforce - no packages found: " + confirmPaymentRequest,
        ConfirmPaymentResource.class);
      throw new NoPackageFoundException(confirmPaymentRequest.getPackageIdentifier(), 
        FormServiceConstants.PACKAGE_STATUS_NEEDS_PAYMENT);
    }
  }
  
  private String getPresenterAccountNumber(ConfirmPaymentRequest confirmPaymentRequest) throws PresenterAuthenticationException {
    String presenterId = confirmPaymentRequest.getPayment().getAccountNumber();
    String presenterAccountNumber = null;
    if ( presenterId != null && !presenterId.isEmpty() ) {
      PresenterAuthResponse presenterAuthResponse = presenterHelper.getPresenterResponse(presenterId, 
        confirmPaymentRequest.getPayment().getAccountAuthCode());
      presenterAccountNumber = presenterAuthResponse.getPresenterAccountNumber();
      if ( presenterAccountNumber == null ) {
        throw new PresenterAuthenticationException(presenterId, confirmPaymentRequest.getPayment().getAccountAuthCode());
      }
    }
    return presenterAccountNumber;
  }
  
  private void updateFormXmlAndStatus(Document form, Payment payment, String presenterAccountNumber) {
    JSONObject jsonForm = new JSONObject(form.toJson());
    String base64EncodedXml = jsonForm.getString(config.getXmlPropertyNameOut());
    String unEncodedXml = new String(Base64.decodeBase64(base64EncodedXml));

    unEncodedXml = unEncodedXml.replace(config.getReferenceNumberPlaceholderValueOut(), payment.getReferenceNumber());
    unEncodedXml = unEncodedXml.replace(config.getPaymentMethodPlaceholderValueOut(), payment.getPaymentMethod());
    unEncodedXml = unEncodedXml.replace(config.getAccountNumberPlaceholderValueOut(), presenterAccountNumber);
    
    base64EncodedXml = new String(Base64.encodeBase64(unEncodedXml.getBytes()));
    
    //Update form xml
    boolean result = MongoHelper.getInstance()
            .updateFormXmlByObjectId(form.getObjectId(FormServiceConstants.DATABASE_OBJECTID_KEY), base64EncodedXml);
    if ( !result ) {
      throw new DatabaseException("Failed to update database while updating form xml for form id " 
        + form.getObjectId(FormServiceConstants.DATABASE_OBJECTID_KEY));
    }
    
    //Update form status
    result = MongoHelper.getInstance()
            .updateFormStatusByObjectId(form.getObjectId(FormServiceConstants.DATABASE_OBJECTID_KEY), 
              FormServiceConstants.PACKAGE_STATUS_DEFAULT);
    if ( !result ) {
      throw new DatabaseException("Failed to update database while pdating form status for form id " 
        + form.getObjectId(FormServiceConstants.DATABASE_OBJECTID_KEY));
    }
  }
}
