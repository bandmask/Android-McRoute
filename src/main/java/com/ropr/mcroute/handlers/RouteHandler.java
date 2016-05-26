package com.ropr.mcroute.handlers;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ropr.mcroute.models.McRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NIJO7810 on 2016-05-26.
 */
public class RouteHandler extends BaseHandler {

    private RouteHandler() {
        apiResource = "mcroute/";
        jsonParser = JsonHandler.getInstance();
    }

    public static RouteHandler getInstance() {
        handler = new RouteHandler();

        return (RouteHandler) handler;
    }

    public List<McRoute> getProfileRoutes(int profileId) {
        HashMap<String, String> dataValues = new HashMap();
        dataValues.put("profileId", ((Integer) profileId).toString());

        try {
            String json = ApiHttpHandler.getInstance().handleGet(apiResource + "GetByProfileId", dataValues);
            return jsonParser.fromJson(json, new ArrayList<McRoute>().getClass());
        } catch (Exception ex) {
            Log.e("RouteHandler", "Error getting routes for profile, id " + profileId + ": " + ex.getMessage());
            return null;
        }
    }

    public McRoute getRoute(int routeId) {
        HashMap<String, String> dataValues = new HashMap();
        dataValues.put("profileId", ((Integer) routeId).toString());

        try {
            String json = ApiHttpHandler.getInstance().handleGet(apiResource, dataValues);
            return (McRoute)jsonParser.fromJson(json, McRoute.class);
        } catch (Exception ex) {
            Log.e("RouteHandler", "Error getting route, id " + routeId + ": " + ex.getMessage());
            return null;
        }
    }

    public String postRoute(McRoute route) {
        String json = ApiHttpHandler.getInstance().HandlePost(apiResource, jsonParser.toJson(route));
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(json);
        return obj.get("tempId").toString();
    }
}
