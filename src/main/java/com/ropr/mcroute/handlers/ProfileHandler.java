package com.ropr.mcroute.handlers;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ropr.mcroute.models.McRoute;
import com.ropr.mcroute.models.McRouteProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NIJO7810 on 2016-05-25.
 */
public class ProfileHandler extends BaseHandler {
    private McRouteProfile _profile;

    private ProfileHandler() {
        apiResource = "profile/";
        jsonParser = JsonHandler.getInstance();
    }

    public static ProfileHandler getInstance() {
        if (handler == null)
            handler = new ProfileHandler();

        return (ProfileHandler) handler;
    }

    public McRouteProfile getProfile(int profileId, boolean includeRoutes) {
        if (_profile != null && profileId == _profile.getProfileId())
            return _profile;

        HashMap<String, String> dataValues = new HashMap();
        dataValues.put("id", ((Integer) profileId).toString());

        String action = apiResource + "GetProfileById";
        return getProfile(action, dataValues, includeRoutes);
    }

    public McRouteProfile getProfile(String userId, boolean includeRoutes) {
        if (_profile != null && userId == _profile.getUserId())
            return _profile;

        HashMap<String, String> dataValues = new HashMap();
        dataValues.put("userId", userId);

        String action = apiResource + "GetProfileByUserId";

        return getProfile(action, dataValues, includeRoutes);
    }

    private McRouteProfile getProfile(String action, HashMap<String, String> dataValues, boolean includeRoutes) {
        try {
            String json = ApiHttpHandler.getInstance().handleGet(action, dataValues);
            _profile = jsonParser.fromJson(json, McRouteProfile.class);

            if (includeRoutes) {
                ArrayList<McRoute> routes = (ArrayList)RouteHandler.getInstance().getProfileRoutes(_profile.getProfileId());
                _profile.setRoutes(routes);
            }

            return _profile;
        } catch (IllegalStateException ex) {
            Log.e("ProfileHandler", "Error getting profile by id: " + ex.getMessage());
            return null;
        }
    }
}
