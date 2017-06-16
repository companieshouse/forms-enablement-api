package com.ch.helpers;

import com.ch.conversion.config.ITransformConfig;
import com.ch.model.FormStatus;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 18/07/2016.
 */
public class QueueHelper {

  private final ITransformConfig configuration;
  private final MongoHelper helper = MongoHelper.getInstance();

  public QueueHelper(ITransformConfig configuration) {
    this.configuration = configuration;
  }

  /**
   * retrieves and builds a list of complete packages of forms and metadata by status, limited by count.
   *
   * @param requestStatus the status of the packages requested.
   * @param count         the size of the list returned, select 0 for all.
   * @return list of complete packages as json objects.
   */
  public List<JSONObject> getCompletePackagesByStatus(String requestStatus, int count) {

    List<JSONObject> completeForms = new ArrayList<>();

    //get all packages matching criteria
    ArrayList<Document> packages = helper.getPackagesCollectionByStatus(requestStatus.toUpperCase(Locale.ENGLISH), count)
      .into(new ArrayList<Document>());

    for (Document pack : packages) {

      //for each package search the database for forms matching the identifier and status
      ArrayList<Document> forms = helper.getFormsCollectionByPackageIdAndStatus((String) pack
        .get(configuration.getPackageIdentifierElementNameOut()), pack.getString(configuration.getFormStatusPropertyNameOut()))
        .into(new ArrayList<Document>());

      //create a complete package from the package metadata and it's accompanying forms.
      completeForms.add(createPackage(new JSONObject(pack.toJson()), forms));
    }
    return completeForms;
  }

  /**
   * retrieves and builds a complete packages of forms and metadata by packageId.
   *
   * @param packageId the id of the package requested.
   * @return a complete package as a json object.
   */
  public JSONObject getCompletePackageById(String packageId) {

    //get package matching criteria
    Document pack = helper.getPackageByPackageId(packageId);

    //search the database for forms matching the identifier
    ArrayList<Document> forms = helper.getFormsCollectionByPackageId((String)pack
      .get(configuration.getPackageIdentifierElementNameOut())).into(new ArrayList<Document>());

    //create a complete package from the package metadata and it's accompanying forms.
    return createPackage(new JSONObject(pack.toJson()), forms);
  }

  /**
   * updates the status of a complete package by package Id.
   *
   * @param packageId     the id of the package requested.
   * @param requestStatus the status of the packages requested.
   * @return true if both the package and forms are updated successfully.
   */
  public boolean updateCompletePackageById(String packageId, String requestStatus) {

    boolean updatePackage = updatePackageStatusById(packageId, requestStatus);

    boolean updateForms = updateFormsStatusById(packageId, requestStatus);

    if (updatePackage && updateForms) {
      return true;
    }
    return false;
  }


  /**
   * updates the status of a package by package Id.
   *
   * @param packageId     the id of the package requested.
   * @param requestStatus the status of the packages requested.
   * @return true if the package is updated successfully.
   */
  public boolean updatePackageStatusById(String packageId, String requestStatus) {
    return helper.updatePackageStatusByPackageId(packageId, requestStatus);
  }

  /**
   * updates the status of a form by package Id.
   *
   * @param packageId     the id of the package requested.
   * @param requestStatus the status of the packages requested.
   * @return true if the form is updated successfully.
   */
  public boolean updateFormsStatusById(String packageId, String requestStatus) {
    return helper.updateFormsStatusByPackageId(packageId, requestStatus);
  }

  private JSONObject createPackage(JSONObject pack, ArrayList<Document> forms) {

    JSONArray formArray = new JSONArray();

    for (Document form : forms) {
      formArray.put(new JSONObject(form.toJson()));
    }
    return pack.put(configuration.getFormsPropertyNameOut(), formArray);
  }

  /**
   * processes the http response from CHIPS, updating the package with the correct status.
   *
   * @param response  response from CHIPS.
   * @param packageId the id of the package which triggered the response.
   */
  public void processResponse(Response response, String packageId) {

    int responseStatus = response.getStatus();
    response.bufferEntity();

    if (responseStatus == 202 || responseStatus == 200) {
      updateCompletePackageById(packageId, FormStatus.SUCCESS.toString().toUpperCase());
    } else {
      updateCompletePackageById(packageId, FormStatus.FAILED.toString().toUpperCase());
    }
  }
}
