package com.ch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Aaron.Witter on 10/04/2016.
 */
public class DateReceived {
    private String datereceived;

    public DateReceived() {
        // empty constructor required for jackson
    }

    @JsonProperty
    public String getDatereceived() {
        return datereceived;
    }
}