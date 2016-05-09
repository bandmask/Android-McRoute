package com.ropr.mcroute.interfaces;

import android.location.Location;

public interface LocationUpdater {
	void notifyPlayServicesAvailable(String status);
	void notifyPlayServicesNotAvailable(int sourceCode, String status);
	void handleLocationUpdate(Location loc);
}
