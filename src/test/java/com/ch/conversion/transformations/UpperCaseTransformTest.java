package com.ch.conversion.transformations;

import com.ch.helpers.TestHelper;

import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class UpperCaseTransformTest extends TestHelper {

    UpperCaseTransform transform;

    @Before
    public void setUp() {
        transform = UpperCaseTransform.getInstance();
    }

    @Test
    public void convertToUpperCase() throws IOException {
        String input = getStringFromFile(LOWERCASE_JSON_PATH);

        JSONObject json = new JSONObject(input);
        transform.parentUpperCase(json, new ArrayList<String>());
        String result = json.toString();

        Assert.assertThat(result, CoreMatchers.containsString("HELLO"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO NESTED"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY STRING"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY NESTED 1"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY NESTED 2"));
    }
    
    @Test
    public void convertToUpperCaseWithExceptions() throws IOException {
        String input = getStringFromFile(LOWERCASE_JSON_PATH);
        
        List<String> exceptions = new ArrayList<String>();
        exceptions.add("/nested_exception/level_1/level_2/level_3/nested_string");
        
        JSONObject json = new JSONObject(input);
        transform.parentUpperCase(json, exceptions);
        String result = json.toString();

        Assert.assertThat(result, CoreMatchers.containsString("HELLO"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO NESTED"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY STRING"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY NESTED 1"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY NESTED 2"));
        Assert.assertThat(result, CoreMatchers.containsString("hello nested exception"));
    }    
    
}
