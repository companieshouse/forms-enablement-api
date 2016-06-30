package com.ch.conversion.transformations;

import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.helpers.TestHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by elliott.jenkins on 04/04/2016.
 */
public class ManualElementsTransformTest extends TestHelper {

    ITransformConfig config;

    @Before
    public void setUp() {
        config = new TransformConfig();
    }

    @Test
    public void addManualElementsToXml() throws Exception {
        // xml
        String xml = getStringFromFile(CONVERTED_FORM_XML_PATH);

        // transform
        ManualElementsTransform transform = new ManualElementsTransform(config, xml);
        String output = transform.getXml();

        Assert.assertThat(output, CoreMatchers.containsString("<method>enablement</method>"));
    }

}
