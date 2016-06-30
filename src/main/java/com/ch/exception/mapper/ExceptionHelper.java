package com.ch.exception.mapper;

import org.json.JSONObject;

/**
 * Created by elliott.jenkins on 10/06/2016.
 */
public final class ExceptionHelper {
    private static ExceptionHelper instance = new ExceptionHelper();

    private ExceptionHelper() {
    }

    public static ExceptionHelper getInstance() {
        return instance;
    }

    /**
     * Helper method to return an exception message as a json string.
     *
     * @param exception exception
     * @return error json string
     */
    public String getJsonError(Exception exception) {
        JSONObject error = new JSONObject();
        error.put("message", exception.getMessage());
        return error.toString();
    }
}