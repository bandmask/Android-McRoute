package com.ropr.mcroute.adapters;

import java.util.List;

import com.mcroute.R;
import com.ropr.mcroute.models.McRoute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RoutingAdapter extends BaseAdapter {
	private Context _context;
    private LayoutInflater _inflater;
	private List<McRoute> _routes;
	
	public RoutingAdapter(Context context, List<McRoute> routes) {
		_context = context;
		_routes = routes;
	}
	
	@Override
	public int getCount() {
		return _routes.size();
	}

	@Override
	public Object getItem(int location) {
		return _routes.get(location);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (_inflater == null)
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = _inflater.inflate(R.layout.list_row_route, parent, false);
        
        TextView date = (TextView) convertView.findViewById(R.id.route_date);
        TextView title = (TextView) convertView.findViewById(R.id.route_title);
        
        McRoute m = _routes.get(position);
 
        date.setText(m.getDate());
        title.setText(m.getTitle());
 
        return convertView;
	}
}
