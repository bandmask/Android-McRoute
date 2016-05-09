package com.ropr.mcroute.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by NIJO7810 on 2016-05-04.
 */
public class McRouteProfile implements Parcelable {
    @SerializedName("id")
    private int _profileId;
    @SerializedName("userId")
    private String _userId;
    @SerializedName("userName")
    private String _userName;
    @SerializedName("currentCity")
    private String _currentCity;
    @SerializedName("gender")
    private String _gender;
    @SerializedName("profilePicture")
    private String _profilePicture;
    @SerializedName("roles")
    private ArrayList<McProfileRole> _roles;
    @SerializedName("routes")
    private ArrayList<McRoute> _routes;

    public int getProfileId() { return _profileId; }
    public String getUserId() { return _userId; }
    public String getUserName() { return _userName; }
    public String getCurrentCity() { return _currentCity; }
    public String getGender() { return _gender; }
    public String getProfilePicture() { return _profilePicture; }
    public ArrayList<McRoute> getRoutes() { return _routes; }

    public static final Parcelable.Creator<McRouteProfile> CREATOR = new Parcelable.Creator<McRouteProfile>() {
        public McRouteProfile createFromParcel(Parcel in) {
            return new McRouteProfile(in);
        }

        public McRouteProfile[] newArray(int size) {
            return new McRouteProfile[size];
        }
    };

    public McRouteProfile(Parcel in) {
        _profileId = in.readInt();
        _userId = in.readString();
        _userName = in.readString();
        _currentCity = in.readString();
        _gender = in.readString();
        _profilePicture = in.readString();
        _routes = new ArrayList<McRoute>();
        in.readTypedList(_routes, McRoute.CREATOR);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(_profileId);
        parcel.writeString(_userId);
        parcel.writeString(_userName);
        parcel.writeString(_currentCity);
        parcel.writeString(_gender);
        parcel.writeString(_profilePicture);
        parcel.writeTypedList(_routes);
    }
}
