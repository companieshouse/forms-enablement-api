package com.ch.conversion.builders;

import com.ch.application.FormServiceConstants;
import com.ch.client.PresenterHelper;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.helpers.MultiPartHelper;
import com.ch.exception.PackageContentsException;
import com.ch.model.FormsPackage;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class JsonBuilder {

  private final ITransformConfig config;
  private final FormsPackage formsPackage;

  /**
   * Convert FormDataMultiPart.
   *
   * @param config json and xml
   * @param parts  the form parts
   */
  public JsonBuilder(ITransformConfig config, FormDataMultiPart parts) {
    this.config = config;
    MultiPartHelper helper = MultiPartHelper.getInstance();
    this.formsPackage = helper.getPackageFromMultiPart(config, parts);
  }

  /**
   * Builder to create the json object for multiple forms.
   *
   * @param config      json and xml
   * @param packageJson package data
   * @param formsJson   list of forms json (untransformed)
   */
  public JsonBuilder(ITransformConfig config, String packageJson, List<String> formsJson) {
    this.config = config;
    this.formsPackage = new FormsPackage(packageJson, formsJson);
  }

  /**
   * Get the transformed forms package for multiple forms.
   *
   * @return forms package
   */
  public FormsPackage getTransformedPackage() {
    // Create list of transformed forms
    List<JSONObject> forms = new ArrayList<>();

    // Get the submission reference for addition to all parts of the entity
    String packageIdentifier = formsPackage.getPackageMetaDataJson().getString(config.getPackageIdentifierElementNameOut());

    // Transform package meta data
    JSONObject packageMetaData = getTransformedPackageMetaData();
 
    //TODO: This old presenter auth check needs to move to the payment confirm endpoint
    /**
    // If there are credentials make call to presenter auth api
    if (presenterAuthRequest != null) {

      //a. Get the presenters account number
      PresenterAuthResponse presenterAuthResponse = presenterHelper.getPresenterResponse(
        presenterAuthRequest.getPresenterId(),
        presenterAuthRequest.getPresenterAuth());

      //b. If no account number is returned from api, end submission with exception
      presenterAccountNumber = presenterAuthResponse.getPresenterAccountNumber();
      if (presenterAccountNumber == null) {
        throw new PresenterAuthenticationException(presenterAuthRequest.getPresenterId(), presenterAuthRequest.getPresenterAuth());
      }
      
      //c. Remove the presenterAuth from the package meta data
      packageMetaData.remove(config.getPresenterAuthPropertyNameIn());
    }
    **/
    
    // Loop forms and transform
    for (String formJson : formsPackage.getForms()) {
      forms.add(getBuilderJson(formJson, packageIdentifier));
    }

    // Check the number of forms matches those prescribed in the package, if not throw an exception
    int packageFormCount = (Integer) formsPackage.getPackageMetaDataJson().get(config.getPackageCountPropertyNameIn());

    if (forms.size() != packageFormCount) {
      throw new PackageContentsException(config.getPackageCountPropertyNameIn());
    }

    // Return transformed package
    return new FormsPackage(packageMetaData.toString(), getFormsAsString(forms));
  }

  private JSONObject getTransformedPackageMetaData() {
    JSONObject packageMetaData = new JSONObject(formsPackage.getPackageMetaData());

    // Add datetime to package meta data
    packageMetaData.put(config.getPackageDatePropertyNameOut(), getDateTime());

    // Add default status
    Object status = getDefaultStatus();
    packageMetaData.put(config.getFormStatusPropertyNameOut(), status);

    return packageMetaData;
  }

  protected JSONObject getBuilderJson(String json, String packageIdentifier) {
    FormJsonBuilder builder = new FormJsonBuilder(config, formsPackage.getPackageMetaData(), json);
    return builder.getJson();
  }

  private List<String> getFormsAsString(List<JSONObject> forms) {
    List<String> formList = new ArrayList<>();
    for (JSONObject form : forms) {
      formList.add(form.toString());
    }
    return formList;
  }

  private String getDefaultStatus() {
    return FormServiceConstants.PACKAGE_STATUS_DEFAULT;
  }

  private String getDateTime() {
    DateFormat dateFormat = new SimpleDateFormat(FormServiceConstants.DATE_TIME_FORMAT_DB, Locale.ENGLISH);
    return dateFormat.format(new Date());
  }
}
