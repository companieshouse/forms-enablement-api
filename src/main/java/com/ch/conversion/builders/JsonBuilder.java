package com.ch.conversion.builders;


import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.helpers.MultiPartHelper;
import com.ch.model.FormsPackage;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

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
     * Get the json object for multiple forms.
     *
     * @return json
     */
    public String getJson() {
        // 1. create root JSON object
        JSONObject root = new JSONObject();

        // 2. create JSON array
        JSONArray array = new JSONArray();

        // 3. loop forms and transform
        for (String formJson : formsPackage.getForms()) {
            FormJsonBuilder builder = new FormJsonBuilder(config, formsPackage.getPackageMetaData(), formJson);
            JSONObject object = builder.getJson();
            array.put(object);
        }

        // 4. add array to root
        root.put(config.getFormsPropertyNameOut(), array);
        return root.toString();
    }
}
