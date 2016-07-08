package com.ch.conversion.helpers;


import com.ch.exception.MissingRequiredDataException;
import com.ch.helpers.TestHelper;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by elliott.jenkins on 05/04/2016.
 */
public class JsonHelperTest extends TestHelper {
    JsonHelper helper;

    @Before
    public void setUp() {
        helper = JsonHelper.getInstance();
    }

    @Test(expected = MissingRequiredDataException.class)
    public void throwMissingRequiredDataExceptionWhenJsonDoesNotContainProperty() throws Exception {
        String json = getStringFromFile(FORM_ALL_JSON_PATH);
        JSONObject object = new JSONObject(json);
        helper.getObjectFromJson(object, "root", "not-valid");
    }

    @Test
    public void getJsonObjectByPropertyName() throws Exception {
        String json = getStringFromFile(FORM_ALL_JSON_PATH);
        JSONObject object = new JSONObject(json);
        JSONObject result = helper.getObjectFromJson(object, "root", "formdata");
        Assert.assertNotNull(result);
    }
}
