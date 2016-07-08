package com.ch.conversion.transformations;

import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.helpers.TestHelper;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class FilingDetailsTransformTest extends TestHelper {

    ITransformConfig config;

    @Before
    public void setUp() {
        config = new TransformConfig();
    }

    @Test
    public void addFilingDetailsToXml() throws Exception {
        // xml
        String xml = getStringFromFile(FORM_XML_PATH);

        // package json
        String package_string = getStringFromFile(PACKAGE_JSON_PATH);
        JSONObject package_json = new JSONObject(package_string);

        // meta json
        String meta_string = getStringFromFile(META_PATH);
        JSONObject meta_json = new JSONObject(meta_string);

        FilingDetailsTransform transform = new FilingDetailsTransform(config, xml, package_json, meta_json);
        String output = transform.getXml();

        Assert.assertThat(output, CoreMatchers.containsString("<submissionReference>038-496949</submissionReference>"));
        Assert.assertThat(output, CoreMatchers.containsString("<packageIdentifier>some identifier</packageIdentifier>"));
        Assert.assertThat(output, CoreMatchers.containsString("<packageCount>3</packageCount>"));
    }
}
