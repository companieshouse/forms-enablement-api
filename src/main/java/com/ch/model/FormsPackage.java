package com.ch.model;

import java.util.List;

/**
 * Created by elliott.jenkins on 05/04/2016.
 */
public class FormsPackage {
    private final String packageMetaData;
    private final List<String> forms;

    public FormsPackage(String packageMetaData, List<String> forms) {
        this.packageMetaData = packageMetaData;
        this.forms = forms;
    }

    public String getPackageMetaData() {
        return packageMetaData;
    }

    public List<String> getForms() {
        return forms;
    }
}
