package com.ch.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by elliott.jenkins on 19/05/2016.
 */
public class FluentLoggingConfiguration {

    @JsonProperty
    private boolean fluentLoggingOn;

    @JsonProperty
    private String fluentHost;

    @JsonProperty
    private int fluentPort;

    @JsonProperty
    private String tag;

    @JsonProperty
    private int maxQueueSize;

    public boolean isFluentLoggingOn() {
        return fluentLoggingOn;
    }

    public String getFluentHost() {
        return fluentHost;
    }

    public int getFluentPort() {
        return fluentPort;
    }

    public String getTag() {
        return tag;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }
}
