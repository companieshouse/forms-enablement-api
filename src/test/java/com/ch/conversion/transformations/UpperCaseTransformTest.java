package com.ch.conversion.transformations;

import com.ch.helpers.TestHelper;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
        transform.parentUpperCase(json);
        String result = json.toString();

        Assert.assertThat(result, CoreMatchers.containsString("HELLO"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO NESTED"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY STRING"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY NESTED 1"));
        Assert.assertThat(result, CoreMatchers.containsString("HELLO ARRAY NESTED 2"));
    }
}
