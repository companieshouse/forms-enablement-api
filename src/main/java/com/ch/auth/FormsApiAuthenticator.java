package com.ch.auth;

import com.ch.configuration.FormsServiceConfiguration;
import com.ch.model.FormsApiUser;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * Created by Eliot Stock on 08/03/2016.
 */
public class FormsApiAuthenticator implements Authenticator<BasicCredentials, FormsApiUser> {

    private final FormsServiceConfiguration configuration;

    public FormsApiAuthenticator(FormsServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Note this warning from the Dropwizard docs: You should only throw an AuthenticationException if the authenticator
     * is unable to check the credentials (e.g., your database is down).
     */
    @Override
    public Optional<FormsApiUser> authenticate(BasicCredentials credentials) throws AuthenticationException {
        String username = credentials.getUsername();
        String secret = credentials.getPassword();

        if (configuration.getSalesforceConfiguration().getSecret().equals(secret)
            && configuration.getSalesforceConfiguration().getName().equals(username)) {
            return Optional.of(new FormsApiUser(credentials.getUsername()));
        }
        if (configuration.getCompaniesHouseConfiguration().getSecret().equals(secret)
            && configuration.getCompaniesHouseConfiguration().getName().equals(username)) {
            return Optional.of(new FormsApiUser(credentials.getUsername()));
        }
        return Optional.absent();
    }

}
