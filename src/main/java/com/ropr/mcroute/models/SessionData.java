package com.ropr.mcroute.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NIJO7810 on 2016-05-04.
 */
public class SessionData implements Parcelable {
    @SerializedName("internal_userid")
    private String _userId;
    @SerializedName("access_token")
    private String _accessToken;
    @SerializedName("refresh_token")
    private String _refreshToken;

    public String getUserId() { return _userId; }
    public String getAccessToken() { return _accessToken; }
    public boolean getUseRefreshToken() {return _refreshToken != null && _refreshToken != "" ; }
    public String getRefreshToken() { return _refreshToken; }

    public SessionData() {}

    public SessionData(String userId, String accessToken, String refreshToken) {
        _userId = userId;
        _accessToken = accessToken;
        _refreshToken = refreshToken;
    }

    public SessionData(Parcel in) {
        _userId = in.readString();
        _accessToken = in.readString();
        _refreshToken = in.readString();
    }

    public static final Parcelable.Creator<SessionData> CREATOR = new Parcelable.Creator<SessionData>() {
        public SessionData createFromParcel(Parcel in) {return new SessionData(in); }
        public SessionData[] newArray(int size) { return new SessionData[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(_userId);
        parcel.writeString(_accessToken);
        parcel.writeString(_refreshToken);
    }
}
