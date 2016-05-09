package com.ropr.mcroute.interfaces;

import com.ropr.mcroute.models.SessionData;

/**
 * Created by NIJO7810 on 2016-05-03.
 */
public interface SessionUpdater {
    //TODO: Parse sessiondata from Android.Callback and store in separate fields
    void setSessionData(SessionData sessionData);
    void clearSessionData();
}
