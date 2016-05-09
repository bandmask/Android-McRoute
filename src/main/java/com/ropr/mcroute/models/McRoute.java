package com.ropr.mcroute.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class McRoute implements Parcelable {
	@SerializedName("Id")
	private int _id;
	@SerializedName("Title")
	private String _title;
	@SerializedName("Text")
    private String _text;
	@SerializedName("Date")
	private String _date;
	@SerializedName("Routings")
    private List<McRouting> _routings;
	@SerializedName("IsSynced")
	private boolean _isSynced;
	@SerializedName("ProfileId")
	private int _profileId;
    
	public int getId() {
		return _id;
	}

	public void setId(int value) {
		_id = value;
	}
	
    public String getTitle() {
    	return _title;
    }
    
    public void setTitle(String value) {
    	_title = value;
    }
    
    public String getText() {
    	return _text;
    }
    
	public void setText(String value) {
		_text = value;
	}
    
    public String getDate() {
		return _date;
	}

	public void setDate(String value) {
		_date = value;
	}

	public List<McRouting> getRoutings() {
		if (_routings == null)
			_routings = new ArrayList<McRouting>();
		return _routings;
	}

	public void setRoutings(List<McRouting> value) {
		_routings = value;
	}

	public boolean getIsSynced() { return _isSynced;}

	public void setIsSynced(boolean value) { _isSynced = value; }

	public int getProfileId() { return _profileId; }

	public void setProfileId(int value) { _profileId = value; }

	public McRoute() {}
	
	public McRoute(String title, String date) {
		_title = title;
		_date = date;
	}
	
	public static final Parcelable.Creator<McRoute> CREATOR = new Parcelable.Creator<McRoute>() {
		public McRoute createFromParcel(Parcel in) {
		    return new McRoute(in);
		}
		
		public McRoute[] newArray(int size) {
		    return new McRoute[size];
		}
	};

	private McRoute(Parcel in) {
		_id = in.readInt();
		_title = in.readString();
		_text = in.readString();
		_date = in.readString();
		_routings = new ArrayList<McRouting>();
		in.readTypedList(_routings, McRouting.CREATOR);

		_isSynced = (in.readByte() == 1);
		_profileId = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(_id);
		parcel.writeString(_title);
		parcel.writeString(_text);
		parcel.writeString(_date);
		parcel.writeTypedList(_routings);
		parcel.writeByte((byte)(_isSynced ? 1 : 0));
		parcel.writeInt(_profileId);
	}
}
