package com.ropr.mcroute.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationResult;

public class LocationService extends IntentService {
	public static final int MY_PERMISSION_ACCESS_LOCATION = 3;

	public LocationService(){
		super("locationService");
	}
	
	public LocationService(String name) {
		super(name);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (LocationResult.hasResult(intent)) {
			LocationResult locationResult = LocationResult.extractResult(intent);
			Location location = locationResult.getLastLocation();
			if (location != null) {
				Intent toBroadcast = new Intent("location_event");
				toBroadcast.putExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED, location);
				LocalBroadcastManager.getInstance(this).sendBroadcast(toBroadcast);
			}
		}
	}
}
