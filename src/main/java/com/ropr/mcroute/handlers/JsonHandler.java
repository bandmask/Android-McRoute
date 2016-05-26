package com.ropr.mcroute.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Created by NIJO7810 on 2016-05-26.
 */
public class JsonHandler {
    private static JsonHandler _handler;
    private Gson _jsonParser;

    private JsonHandler() {
        _jsonParser = new GsonBuilder().create();
    }

    protected static JsonHandler getInstance() {
        _handler = new JsonHandler();
        return _handler;
    }

    protected <T> T fromJson(String json, Class<T> type) {
        return _jsonParser.fromJson(json, type);
    }

    protected <T> T fromJson(JsonObject jsonObject, Class<T> type) {
        return _jsonParser.fromJson(jsonObject, type);
    }

    protected <T> String toJson(T object) {
        return _jsonParser.toJson(object);
    }
}
