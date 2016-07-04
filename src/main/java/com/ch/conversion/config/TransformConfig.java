package com.ch.conversion.config;

/**
 * Created by elliott.jenkins on 31/03/2016.
 */
public class TransformConfig implements ITransformConfig {

    public String getPackageMultiPartName() {
        return "packagemetadata";
    }

    public String getMetaPropertyNameIn() {
        return "metadata";
    }

    public String getFormPropertyNameIn() {
        return "formdata";
    }

    public String getAttachmentsPropertyNameIn() {
        return "attachments";
    }

    public String getAttachmentsPropertyNameOut() {
        return "attachments";
    }

    public String getBarcodePropertyNameIn() {
        return "barcode";
    }

    public String getBarcodePropertyNameOut() {
        return "barcode";
    }

    public String getXmlPropertyNameOut() {
        return "xml";
    }

    public String getFormsPropertyNameOut() {
        return "forms";
    }

    public String getFormStatusPropertyNameOut() {
        return "status";
    }

    public String getPackageDatePropertyNameOut() {
        return "date";
    }

    public String getFilingDetailsPropertyNameIn() {
        return "filingDetails";
    }

    public String getSubmissionReferencePropertyNameIn() {
        return "submissionReference";
    }

    public String getSubmissionReferenceElementNameOut() {
        return "submissionReference";
    }

    public String getPackageCountPropertyNameIn() {
        return "count";
    }

    public String getPackageCountElementNameOut() {
        return "packageCount";
    }

    public String getPackageIdentifierPropertyNameIn() {
        return "packageIdentifier";
    }

    public String getPackageIdentifierElementNameOut() {
        return "packageIdentifier";
    }

    public String getRootElementNameOut() {
        return "form";
    }

    public String getFormTypePropertyNameIn() {
        return "type";
    }

    public String getFormTypeAttributeNameOut() {
        return "type";
    }

    public String getFormVersionPropertyNameIn() {
        return "version";
    }

    public String getFormVersionAttributeNameOut() {
        return "version";
    }

    public String getSchemasLocation() {
        return "schemas";
    }
}
