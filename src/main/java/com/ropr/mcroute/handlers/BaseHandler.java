package com.ropr.mcroute.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by NIJO7810 on 2016-05-25.
 */
public abstract class BaseHandler {
    protected static BaseHandler handler;
    protected String apiResource;
    protected JsonHandler jsonParser;
}
