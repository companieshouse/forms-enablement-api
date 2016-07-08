package com.ch.conversion.validation;

import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.XmlException;
import com.ch.exception.XsdValidationException;
import com.ch.helpers.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by elliott.jenkins on 21/04/2016.
 */
public class XmlValidatorTest extends TestHelper {
    ITransformConfig config;

    @Before
    public void setUp() {
        config = new TransformConfig();
    }

    @Test(expected = XmlException.class)
    public void invalidXml() throws Exception {
        String valid = getStringFromFile(INVALID_FORM_XML_PATH);
        XmlValidator validator = new XmlValidator(config, valid);
        validator.validate();
    }

    @Test(expected = XsdValidationException.class)
    public void invalidXsd() throws Exception {
        // resources/schemas/DS01.xsd is an incomplete xsd
        ITransformConfig schemaConfig = Mockito.spy(config);
        Mockito.when(schemaConfig.getSchemasLocation()).thenReturn("testschemas");

        String valid = getStringFromFile(VALID_DS01_XML_PATH);
        XmlValidator validator = new XmlValidator(schemaConfig, valid);
        validator.validate();
    }

    @Test(expected = XmlException.class)
    public void noFormTypeInXml() throws Exception {
        String valid = getStringFromFile(BLANK_FORM_XML_PATH);
        XmlValidator validator = new XmlValidator(config, valid);
        validator.validate();
    }

    @Test(expected = XsdValidationException.class)
    public void noXsdForFormType() throws Exception {
        String valid = getStringFromFile(NO_SCHEMA_XML_PATH);
        XmlValidator validator = new XmlValidator(config, valid);
        validator.validate();
    }

    @Test
    public void validXmlAgainstXsd() throws Exception {
        String valid = getStringFromFile(VALID_DS01_XML_PATH);
        XmlValidator validator = new XmlValidator(config, valid);
        validator.validate();
    }

    @Test(expected = XsdValidationException.class)
    public void invalidXmlAgainstXsd() throws Exception {
        String invalid = getStringFromFile(INVALID_DS01_XML_PATH);
        XmlValidator validator = new XmlValidator(config, invalid);
        validator.validate();
    }
}
