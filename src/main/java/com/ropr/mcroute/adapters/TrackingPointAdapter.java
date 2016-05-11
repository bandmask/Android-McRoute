package com.ropr.mcroute.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcroute.R;
import com.ropr.mcroute.models.McRouting;

import java.util.List;

public class TrackingPointAdapter extends BaseAdapter{
	private Activity _activity;
    private LayoutInflater _inflater;
	private List<McRouting> _trackings;
	
	public TrackingPointAdapter(Activity activity, List<McRouting> trackings) {
		_activity = activity;
		_trackings = trackings;
	}
	
	@Override
	public int getCount() {
		return _trackings.size();
	}

	@Override
	public Object getItem(int location) {
		return _trackings.get(location);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (_inflater == null)
            _inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = _inflater.inflate(R.layout.list_row_point, parent, false);
        
        TextView latitude = (TextView) convertView.findViewById(R.id.point_latitude);
        TextView longitude = (TextView) convertView.findViewById(R.id.point_longitude);
        
        McRouting m = _trackings.get(position);
 
        latitude.setText(String.valueOf(m.getLatitude()));
        longitude.setText(String.valueOf(m.getLongitude()));
 
        return convertView;
	}

}
