package com.ropr.mcroute;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.mcroute.R;
import com.ropr.mcroute.adapters.TrackingPointAdapter;
import com.ropr.mcroute.handlers.ApiHttpHandler;
import com.ropr.mcroute.interfaces.LocationUpdater;
import com.ropr.mcroute.models.McRoute;
import com.ropr.mcroute.models.McRouting;
import com.ropr.mcroute.services.LocationService;
import com.ropr.mcroute.sources.ErrorDialogFragment;
import com.ropr.mcroute.sources.McRouteJsonParser;
import com.ropr.mcroute.sources.StaticResources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Route_Activity extends FragmentActivity implements LocationUpdater, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private boolean _routeIsSynced;
	private boolean _isPlayServicesActive;
	private final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private McRoute _activeRoute;
	private TrackingPointAdapter _pointAdapter;

	private GoogleApiClient _locationClient;
	private LocationRequest _locationRequest;

	private Intent _serviceIntent;
	private PendingIntent _pendingIntent;

	private EditText _routeTitle;
	private Button _routingButton;

	private WakeLock _wakeLock;

	private boolean aquireWakeLock() {
		if (_wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			_wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Reciever");
		}
		if (!_wakeLock.isHeld())
			_wakeLock.acquire();

		return _wakeLock.isHeld();
	}

	private boolean releaseWakeLock() {
		if (_wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			_wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Reciever");
		}
		if (_wakeLock.isHeld())
			_wakeLock.release();

		return _wakeLock.isHeld();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		_routeTitle = (EditText) findViewById(R.id.route_title_value);

		try {
			_activeRoute = (McRoute) b.get(StaticResources.EXTRA_ROUTE);
			_routeTitle.setText(_activeRoute.getTitle());
		} catch (Exception ex) {
			_activeRoute = new McRoute();
		}

		TextView routingCount = (TextView) findViewById(R.id.routing_count);
		routingCount.setText(String.valueOf(_activeRoute.getRoutings().size()));
		_routingButton = (Button) findViewById(R.id.start_route_button);

		_routingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startRouting(v);
			}
		});

		_locationClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initiateRoutings();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_route, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.menu_route_action_save:
				saveAndCloseRoute();
				break;
			case R.id.menu_route_action_sync:
				handleSyncRoute();
				break;
			case R.id.menu_route_action_delete:
				deleteRoute();
				break;
			default:
				break;
		}

		return true;
	}

	private void initiateRoutings() {
		_pointAdapter = new TrackingPointAdapter(this, _activeRoute.getRoutings());
		ListView listView = (ListView) findViewById(R.id.routing_list_view);
		listView.setAdapter(_pointAdapter);

		if (_locationClient == null) return;

		_locationClient.connect();

		_serviceIntent = new Intent(this, LocationService.class);
		_pendingIntent = PendingIntent.getService(getApplicationContext(), 0, _serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public void onConnected(Bundle bundle) {
		int sourceCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
		if (sourceCode == ConnectionResult.SUCCESS) {
			_isPlayServicesActive = true;
			notifyPlayServicesAvailable("Tracking Available");

			_routingButton.setEnabled(true);

			Toast.makeText(this, "Google API Connected", Toast.LENGTH_SHORT).show();
		} else {
			_isPlayServicesActive = true;
			notifyPlayServicesNotAvailable(sourceCode, "Tracking not available");

			_routingButton.setEnabled(false);
		}
	}

	public void startRouting(View sender) {
		Button routeButton = (Button) sender;

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
					LocationService.MY_PERMISSION_ACCESS_LOCATION);
			return;
		}

		if (routeButton.getText() == getString(R.string.start_routing_text) && _isPlayServicesActive) {
			routeButton.setText(getString(R.string.stop_routing_text));
			LocalBroadcastManager.getInstance(this).registerReceiver(_locationReceiver, new IntentFilter("location_event"));

			_locationRequest = LocationRequest.create();
			_locationRequest.setInterval(1000);
			_locationRequest.setFastestInterval(3600);
			_locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			_locationRequest.setSmallestDisplacement(100);

			LocationServices.FusedLocationApi.requestLocationUpdates(_locationClient, _locationRequest, _pendingIntent);
			
			TextView playServicesAvailable = (TextView) findViewById(R.id.play_services_status);
			playServicesAvailable.setText("Routing started");
		} else {
			routeButton.setText(getResources().getString(R.string.start_routing_text));
			TextView playServicesAvailable = (TextView) findViewById(R.id.play_services_status);
			playServicesAvailable.setText("Routing stopped");
			LocationServices.FusedLocationApi.removeLocationUpdates(_locationClient, _pendingIntent);
		}
	}
	
	public void notifyPlayServicesAvailable(final String status) {
		runOnUiThread(new Runnable() {
			public void run() {
				TextView playServicesAvailable = (TextView) findViewById(R.id.play_services_status);
				playServicesAvailable.setText(status);
				playServicesAvailable.setVisibility(View.VISIBLE);
			}
		});
	}
	
	public void notifyPlayServicesNotAvailable(final int sourceCode, final String status) {
		final Activity parentActivity = this;
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(parentActivity, sourceCode, CONNECTION_FAILURE_RESOLUTION_REQUEST);
		        if (errorDialog != null) {
		            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
		            errorFragment.setDialog(errorDialog);
		            errorFragment.show(getSupportFragmentManager(), "Location Updates");
		        } else {
		        	TextView playServicesAvailable = (TextView) findViewById(R.id.play_services_status);
		    		playServicesAvailable.setText(status);
		    		playServicesAvailable.setVisibility(View.VISIBLE);
		        }
			}
		});
	}
	
	private BroadcastReceiver _locationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			Location loc = (Location) b.get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
			handleLocationUpdate(loc);
		}
	};

	@Override
	public void handleLocationUpdate(final Location loc) {
		if (loc != null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					List<McRouting> trackings = _activeRoute.getRoutings();
					trackings.add(new McRouting(trackings.size(), loc.getLatitude(), loc.getLongitude()));
					
					TextView routingCount = (TextView) findViewById(R.id.routing_count);
					routingCount.setText(String.valueOf(_activeRoute.getRoutings().size()));
					
					_pointAdapter.notifyDataSetChanged();
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "location null", Toast.LENGTH_SHORT).show();
		}
	}

	private void disconnectApi() {
		try {
			if (_locationRequest != null)
				LocationServices.FusedLocationApi.removeLocationUpdates(_locationClient, _pendingIntent);

			if (_locationClient != null && _locationClient.isConnected())
				_locationClient.disconnect();
		} catch (Exception ex) {
			Log.e("DisconnectAPI", "Error disconnecting api: " + ex.getMessage());
		}

		_locationRequest = null;
		_locationClient = null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		disconnectApi();
	}

	private void saveAndCloseRoute() {
		_activeRoute.setTitle(_routeTitle.getText().toString());

		String date = formatRouteDate(_activeRoute.getDate());
		_activeRoute.setDate(date);

		Intent resultIntent = new Intent();
		resultIntent.putExtra(StaticResources.EXTRA_ROUTE, _activeRoute);

		int resultCode;
		if (_routeIsSynced)
			resultCode = StaticResources.ROUTE_SYNCED;
		else
			resultCode = RESULT_OK;

		setResult(resultCode, resultIntent);
		finish();
	}

	private void deleteRoute() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(StaticResources.EXTRA_ROUTE, _activeRoute);
		setResult(StaticResources.ROUTE_DELETED_LOCAL, resultIntent);

		finish();
	}

	private String formatRouteDate(String fromDate) {
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.US);

		try {
			if (fromDate != null)
				return dateFormatter.format(dateFormatter.parseObject(fromDate));
			return dateFormatter.format(new Date());
		} catch (Exception ex) {
			Log.e("DateFormatter", "Error parsing date: " + ex.getMessage());
			return fromDate;
		}
	}

	private void handleSyncRoute() {
		JsonObject response = ApiHttpHandler.getInstance().HandlePost("mcroute/", McRouteJsonParser.toJson(_activeRoute));
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), "Route is " + (_routeIsSynced ? "synced" : "unsynced"), Toast.LENGTH_SHORT).show();
			}
		});*/
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		notifyPlayServicesNotAvailable(-1, "Connection Failed");
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == LocationService.MY_PERMISSION_ACCESS_LOCATION && grantResults.length > 0) {
			for (int result : grantResults) {
				if (result == PackageManager.PERMISSION_GRANTED && _routingButton != null) {
					startRouting(_routingButton);
					break;
				}
			}
		}
	}
}
