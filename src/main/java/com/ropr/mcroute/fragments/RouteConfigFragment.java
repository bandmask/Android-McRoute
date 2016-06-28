package com.ropr.mcroute.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mcroute.R;
import com.ropr.mcroute.handlers.RouteConfigHandler;
import com.ropr.mcroute.models.RouteConfig;

/**
 * Created by NIJO7810 on 2016-06-28.
 */
public class RouteConfigFragment extends Fragment {
    private EditText _routingInterval;
    private EditText _fastestInterval;
    private EditText _smallestDisplacement;
    private Button _saveConfigButton;
    private Button _discardConfigButton;
    private RouteConfigHandler _configHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_route_config, container, false);
        setHasOptionsMenu(false);

        _configHandler = new RouteConfigHandler(this.getActivity());

        _routingInterval = (EditText) view.findViewById(R.id.editRoutingInterval);
        _fastestInterval = (EditText) view.findViewById(R.id.editFastestInterval);
        _smallestDisplacement = (EditText) view.findViewById(R.id.editSmallestDisplacement);
        _saveConfigButton = (Button) view.findViewById(R.id.btnSaveConfig);
        _discardConfigButton = (Button) view.findViewById(R.id.btnDiscardConfig);

        init();

        return view;
    }

    private void init() {
        final RouteConfig config = _configHandler.getCurrentConfig();
        _routingInterval.setText(Integer.toString(config.getRoutingInterval()));
        _fastestInterval.setText(Integer.toString(config.getFastestInterval()));
        _smallestDisplacement.setText(Integer.toString(config.getSmallestDisplacement()));

        _saveConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.setRoutingInterval(Integer.parseInt(_routingInterval.getText().toString()));
                config.setFastestInterval(Integer.parseInt(_fastestInterval.getText().toString()));
                config.setSmallestDisplacement(Integer.parseInt(_smallestDisplacement.getText().toString()));

                _configHandler.updateRouteConfig();
                getFragmentManager().popBackStack();
            }
        });

        _discardConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }
}
