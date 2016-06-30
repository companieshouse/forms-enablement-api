package com.ch.conversion.helpers;

import com.ch.conversion.config.ITransformConfig;
import com.ch.exception.ContentTypeException;
import com.ch.exception.MissingRequiredDataException;
import com.ch.model.FormsPackage;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * Created by elliott.jenkins on 05/04/2016.
 */
public final class MultiPartHelper {
    private static MultiPartHelper instance = new MultiPartHelper();

    private MultiPartHelper() {
    }

    public static MultiPartHelper getInstance() {
        return instance;
    }

    /**
     * Create JsonBuilder input from a FormMultiPart.
     *
     * @param config transformation config
     * @param parts  multi parts
     * @return FormsPackage
     */
    public FormsPackage getPackageFromMultiPart(ITransformConfig config, FormDataMultiPart parts) {
        // parts
        FormDataBodyPart packagePart = null;
        List<FormDataBodyPart> formParts = new ArrayList<>();

        // loop parts
        Map<String, List<FormDataBodyPart>> all = parts.getFields();
        for (Map.Entry<String, List<FormDataBodyPart>> entry : all.entrySet()) {
            // should only be one body part per entry
            FormDataBodyPart body = entry.getValue().get(0);
            // get the part with the package name, every other part is a form
            if (body.getName().equals(config.getPackageMultiPartName())) {
                packagePart = body;
            } else {
                formParts.add(body);
            }
        }

        if (packagePart == null) {
            throw new MissingRequiredDataException(config.getPackageMultiPartName(), "multipart");
        } else if (formParts.isEmpty()) {
            throw new MissingRequiredDataException("forms", "multipart");
        }

        String packageMetaData = handleFormDataBodyPart(packagePart);
        List<String> forms = handleFormParts(formParts);
        return new FormsPackage(packageMetaData, forms);
    }

    private List<String> handleFormParts(List<FormDataBodyPart> parts) {
        List<String> forms = new ArrayList<>();
        for (FormDataBodyPart part : parts) {
            forms.add(handleFormDataBodyPart(part));
        }
        return forms;
    }

    private String handleFormDataBodyPart(FormDataBodyPart part) {
        MediaType type = part.getMediaType();
        if (type.equals(MediaType.TEXT_PLAIN_TYPE)) {
            return part.getValue();

        } else if (type.equals(MediaType.APPLICATION_JSON_TYPE)) {
            return part.getEntity().toString();

        } else {
            throw new ContentTypeException(type.toString());
        }
    }
}





