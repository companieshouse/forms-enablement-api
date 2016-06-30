package com.ch.model;

import io.dropwizard.auth.PrincipalImpl;

/**
 * Created by Eliot Stock on 08/03/2016.
 */
public class FormsApiUser extends PrincipalImpl {

    // Nothing much here. This is a marker class implementation of Principal. We don't care much about FormsApiUser
    // instances, we just need to support HTTP basic auth.
    public FormsApiUser(String name) {
        super(name);
    }

}
