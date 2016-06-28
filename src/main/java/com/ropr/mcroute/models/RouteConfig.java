package com.ropr.mcroute.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NIJO7810 on 2016-06-28.
 */
public class RouteConfig {
    @SerializedName("RoutingInterval")
    private int _routingInterval;
    @SerializedName("FastestInterval")
    private int _fastestInterval;
    @SerializedName("SmallestDisplacement")
    private int _smallestDisplacement;

    public int getRoutingInterval() { return _routingInterval; }
    public void setRoutingInterval(int value) { _routingInterval = value; }

    public int getFastestInterval() { return _fastestInterval; }
    public void setFastestInterval(int value) { _fastestInterval = value; }

    public int getSmallestDisplacement() { return _smallestDisplacement; }
    public void setSmallestDisplacement(int value) { _smallestDisplacement = value; }

    public RouteConfig(boolean useDefault){
        if (useDefault)
        {
            _routingInterval = 1000;
            _fastestInterval = 3600;
            _smallestDisplacement = 100;
        }
    }
}