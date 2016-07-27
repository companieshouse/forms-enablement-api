package com.ch.conversion.transformations;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by elliott.jenkins on 30/03/2016.
 */

// TODO: to be refactored
public final class UpperCaseTransform {

  private static UpperCaseTransform instance = new UpperCaseTransform();

  private UpperCaseTransform() {
  }

  public static UpperCaseTransform getInstance() {
    return instance;
  }

  /**
   * Convert to string json properties to upper case.
   *
   * @param parent root json object
   */
  public void parentUpperCase(JSONObject parent, List<String> exceptions) {
    parentUpperCase(parent, exceptions, "");
  }
  
  /**
   * Convert to string json properties to upper case.
   *
   * @param parent root json object
   */
  public void parentUpperCase(JSONObject parent, List<String> exceptions, String path) {      

    Iterator<String> keys = parent.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      Object child = parent.get(key);
      childUpperCase(key, parent, child, exceptions, path + "/" + key);
    }
  }

  private void childUpperCase(String key, JSONObject parent, Object child, List<String> exceptions, String path) {
    if (child instanceof JSONObject) {
      JSONObject childJsonObject = ((JSONObject) child);
      parentUpperCase(childJsonObject, exceptions, path);

    } else if (child instanceof JSONArray) {
      JSONArray array = ((JSONArray) child);
      for (Object item : array) {
        childUpperCase(key, parent, item, exceptions, path);
      }

    } else if (child instanceof String) {
      
      if (exceptions.contains(path)) {
        return;
      }

      String value = ((String) child);
      String upper = value.toUpperCase(Locale.ENGLISH);
      parent.put(key, upper);
    }
  }
}
