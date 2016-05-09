package com.ropr.mcroute.fragments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.mcroute.R;
import com.ropr.mcroute.Route_Activity;
import com.ropr.mcroute.models.McRoute;
import com.ropr.mcroute.models.McRouteProfile;
import com.ropr.mcroute.sources.McRouteJsonParser;
import com.ropr.mcroute.adapters.RoutingAdapter;
import com.ropr.mcroute.sources.StaticResources;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RouteFragment extends Fragment {
	private int _profileId = -1;
	private Button _startRouteButton;
	private List<McRoute> _routes;
	private RoutingAdapter _routeAdapter;

	private List<McRoute> getRoutes() {
		if (_routes == null)
			_routes = new ArrayList<McRoute>();
		return _routes;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.view_route, container, false);
        _startRouteButton = (Button)view.findViewById(R.id.start_route_button);

		_startRouteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				openRouteActivity();
			}
		});

        _routeAdapter = new RoutingAdapter(getActivity(), getRoutes());
		ListView listView = (ListView) view.findViewById(R.id.route_list_view);
		listView.setAdapter(_routeAdapter);
		listView.setItemsCanFocus(true);

		updateFileList();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					McRoute newroute = new ExtractFromFileThread().execute((McRoute) parent.getItemAtPosition(position)).get();
					if (newroute != null)
						openRouteActivity(newroute);
				} catch (Exception ex) {
					Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		});

		setHasOptionsMenu(false);

		init();

		return view;
    }

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null && !bundle.isEmpty()) {
			try {
				_profileId = bundle.getInt(StaticResources.EXTRA_PROFILE_ID);
			} catch (Exception ex) {
				Log.e("Init RouteFrafment", "Error getting profile id from arguments: " + ex.getMessage());
			}
		}
	}

	private void updateFileList() {
		getRoutes().clear();
		File file = getActivity().getApplicationContext().getFilesDir();
		File files[] = file.listFiles();

		if (files.length == 0) return;

		for (File f : files) {
			String name = f.getName();
			if (name.contains("_")) {
				String nameParts[] = f.getName().split("_");
				getRoutes().add(new McRoute(nameParts[0], nameParts[1]));
			}
		}
		_routeAdapter.notifyDataSetChanged();
	}

    private void openRouteActivity() {
    	Intent intent = new Intent(getActivity(), Route_Activity.class);
		McRoute route = new McRoute();
		route.setProfileId(_profileId);
		intent.putExtra(StaticResources.EXTRA_ROUTE, route);
    	startActivityForResult(intent, StaticResources.START_ROUTE_REQUEST_CODE);
    }

    private void openRouteActivity(McRoute route) {
		if (_profileId != -1)
			route.setProfileId(_profileId);

    	Intent intent = new Intent(getActivity(), Route_Activity.class);
    	intent.putExtra(StaticResources.EXTRA_ROUTE, route);
    	startActivityForResult(intent, StaticResources.START_ROUTE_REQUEST_CODE);
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == StaticResources.START_ROUTE_REQUEST_CODE) {
			try {
				if (data.hasExtra(StaticResources.EXTRA_ROUTE)) {
					Bundle bundle = data.getExtras();
					McRoute route = (McRoute) bundle.get(StaticResources.EXTRA_ROUTE);
					if (resultCode == getActivity().RESULT_OK) {
						saveRouteToFile(route);
					} else if (resultCode == StaticResources.ROUTE_SYNCED || resultCode == StaticResources.ROUTE_DELETED_LOCAL) {
						removeLocalRoute(route);
					}
				}
			} catch (Exception ex) {
				Log.e("RouteFragment", "Error extracting result from Route Activity: " + ex.getMessage());
			}
		}
	}

	private class ExtractFromFileThread extends AsyncTask<McRoute, Void, McRoute> {

		@Override
		protected McRoute doInBackground(McRoute... params) {
			try {
				if (params.length != 1)
					throw new IllegalStateException("ExtractFromFileThread params invalid");

				McRoute route = params[0];
				File file = getActivity().getApplicationContext().getFilesDir();
				for (File f : file.listFiles()) {
					if (f.getName().contains(route.getTitle())){
						InputStream inputStream = getActivity().getApplicationContext().openFileInput(f.getName());

						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
						StringBuilder builder = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}

						if (builder.length() <= 0) {
							throw new IOException("Error reading file");
						}

						McRoute newRoute = McRouteJsonParser.fromJson(builder.toString());
						return newRoute;
					}
				}
			}catch (final Exception ex) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}
			return null;
		}
	}

	private void saveRouteToFile(final McRoute route) {
		try {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String data = McRouteJsonParser.toJson(route);
						OutputStream outputStream = getActivity().openFileOutput(route.getTitle() + "_" + route.getDate(), getActivity().MODE_PRIVATE);
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StaticResources.CHARSET_UTF8));
						writer.write(data);
						writer.flush();
						writer.close();
						outputStream.close();
					} catch (Exception ex) {
						Log.e("SaveRouteThread", "Error handling save thread: " + ex.getMessage());
					}
				}
			});
			thread.start();
			thread.join();

			updateFileList();
		} catch (Exception ex) {
			Log.e("SaveRouteToFile", "Error saving route to file: " + ex.getMessage());
		}
	}

	private void removeLocalRoute(final McRoute route) {
		try {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					File file = getActivity().getApplicationContext().getFilesDir();
					for (File f : file.listFiles()) {
						if (f.getName().contains(route.getTitle())) {
							getActivity().getApplicationContext().deleteFile(f.getName());
						}
					}
				}
			});
			thread.start();
			thread.join();

			updateFileList();
		} catch (Exception ex) {
			Log.e("RemoveLocalRoute", "Error removing local file: " + ex.getMessage());
		}
	}
}
