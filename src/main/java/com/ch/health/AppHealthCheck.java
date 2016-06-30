package com.ch.health;

import com.codahale.metrics.health.HealthCheck;

/**
 * Created by Aaron.Witter on 07/03/2016.
 */
public class AppHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}