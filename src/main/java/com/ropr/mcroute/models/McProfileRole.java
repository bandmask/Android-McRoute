package com.ropr.mcroute.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NIJO7810 on 2016-05-04.
 */
public class McProfileRole {
    @SerializedName("id")
    private int _roleId;
    @SerializedName("roleName")
    private String _roleName;
    @SerializedName("mcProfileId")
    private int _profileId;
}
