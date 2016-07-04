package com.ch.conversion.builders;


import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.helpers.JsonHelper;
import com.ch.model.FormStatus;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class FormJsonBuilder {

    private final JsonHelper helper;

    private final ITransformConfig config;
    private final JSONObject pack;
    private final JSONObject meta;
    private final JSONObject form;
    private final JSONArray attachments;

    /**
     * Builder to convert one form into the required json object.
     *
     * @param config      json and xml
     * @param packageJson package data json
     * @param formJson    form data json
     */
    public FormJsonBuilder(ITransformConfig config, String packageJson, String formJson) {
        this.config = config;
        this.helper = JsonHelper.getInstance();

        this.pack = new JSONObject(packageJson);
        JSONObject form = new JSONObject(formJson);

        this.meta = helper.getObjectFromJson(form, "parent json object (form body part)", config.getMetaPropertyNameIn());
        this.form = helper.getObjectFromJson(form, "parent json object (form body part)", config.getFormPropertyNameIn());
        this.attachments = helper.getArrayFromJson(form, "parent json object (form body part)",
            config.getAttachmentsPropertyNameIn());
    }

    /**
     * Get the json object for a single form.
     *
     * @return transformed json
     */
    public JSONObject getJson() {
        // 1. create empty json object
        JSONObject output = new JSONObject();

        // 2. add attachments
        output.put(config.getAttachmentsPropertyNameOut(), attachments);

        // 3. add barcode
        Object barcode = getFormBarcode();
        output.put(config.getBarcodePropertyNameOut(), barcode);

        // 4. add package identifier
        Object packageIdentifier = getPackageIdentifier();
        output.put(config.getPackageIdentifierPropertyNameIn(), packageIdentifier);

        // 5. add pending status
        Object status = FormStatus.PENDING.toString();
        output.put(config.getFormStatusPropertyNameOut(), status);

        // 6. transform package and form json into base64 xml
        String xml = getFormXML();
        output.put(config.getXmlPropertyNameOut(), xml);
        return output;
    }

    private Object getFormBarcode() {
        JSONObject details = helper.getObjectFromJson(form, config.getFormPropertyNameIn(),
            config.getFilingDetailsPropertyNameIn());
        return helper.getValueFromJson(details, config.getFilingDetailsPropertyNameIn(), config.getBarcodePropertyNameIn());
    }

    private Object getPackageIdentifier() {
        return helper.getValueFromJson(pack, "", config.getPackageIdentifierPropertyNameIn());
    }

    private String getFormXML() {
        FormXmlBuilder builder = new FormXmlBuilder(config, pack, meta, form);
        return builder.getXML();
    }
}
