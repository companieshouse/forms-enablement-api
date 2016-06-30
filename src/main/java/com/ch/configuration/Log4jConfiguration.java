package com.ch.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Aaron.Witter on 04/04/2016.
 */
public class Log4jConfiguration {
    @JsonProperty
    @NotEmpty
    private long frequency;

    @JsonProperty
    @NotEmpty
    private String timeUnit;

    public long getFrequency() {
        return frequency;
    }


    public String getTimeUnit() {
        return timeUnit;
    }
}
