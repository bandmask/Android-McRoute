package com.ropr.mcroute.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class McRouting implements Parcelable {
	@SerializedName("Id")
	private int _id;
	@SerializedName("Latitude")
	private double _latitude;
	@SerializedName("Longitude")
	private double _longitude;
	
	public McRouting(int id, double latitude, double longitude) {
		_id = id;
		_latitude = latitude;
		_longitude = longitude;
	}
	
	public McRouting(double latitude, double longitude) {
		_latitude = latitude;
		_longitude = longitude;
	}
	
	public int getId() {
		return _id;
	}
	public void setId(int value) {
		_id = value;
	}
	
	public double getLatitude() {
		return _latitude;
	}
	public void setId(double value) {
		_latitude = value;
	}
	
	public double getLongitude() {
		return _longitude;
	}
	public void setLongitude(double value) {
		_longitude = value;
	}
	
	public static final Parcelable.Creator<McRouting> CREATOR = new Parcelable.Creator<McRouting>() {
		public McRouting createFromParcel(Parcel in) {
		    return new McRouting(in);
		}
		
		public McRouting[] newArray(int size) {
		    return new McRouting[size];
		}
	};

	private McRouting(Parcel in) {
		_id = in.readInt();
		_latitude = in.readDouble();
		_longitude = in.readDouble();
	}


	@Override
	public int describeContents() {
		return _id;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(_id);
		parcel.writeDouble(_latitude);
		parcel.writeDouble(_longitude);
	}
}
