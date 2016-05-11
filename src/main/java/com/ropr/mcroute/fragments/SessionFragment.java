package com.ropr.mcroute.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcroute.R;
import com.ropr.mcroute.interfaces.SessionUpdater;
import com.ropr.mcroute.models.McRoute;
import com.ropr.mcroute.models.McRouteProfile;
import com.ropr.mcroute.sources.StaticResources;

import java.util.ArrayList;

/**
 * Created by NIJO7810 on 2016-05-03.
 */
public class SessionFragment extends Fragment {
    private ImageView _profileImage;
    private TextView _userName;
    private TextView _currentCity;
    private TextView _gender;
    private TextView _numRoutings;

    private SessionUpdater _sessionUpdater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.view_session, container, false);

        _profileImage = (ImageView) view.findViewById(R.id.session_view_profile_image);
        _userName = (TextView) view.findViewById(R.id.session_view_user_name);
        _currentCity = (TextView) view.findViewById(R.id.session_view_current_city);
        _gender = (TextView) view.findViewById(R.id.session_view_gender);
        _numRoutings = (TextView) view.findViewById(R.id.session_view_num_routings);

        setHasOptionsMenu(true);

        init();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SessionUpdater)
            _sessionUpdater = (SessionUpdater) context;
    }

     @Override
     public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
         super.onCreateOptionsMenu(menu, menuInflater);

         menuInflater.inflate(R.menu.menu_session, menu);
     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_session_action_signout:
                handleSignOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            try {
                McRouteProfile profile = (McRouteProfile) bundle.get(StaticResources.EXTRA_SESSION_PROFILE);

                /*_profileImage.setImageURI(null);
                _profileImage.setImageURI(Uri.parse(profile.getProfilePicture()));*/

                _userName.setText(profile.getUserName());
                _currentCity.setText(profile.getCurrentCity());
                _gender.setText(profile.getGender());

                ArrayList<McRoute> routes = profile.getRoutes();
                if (routes == null || routes.isEmpty())
                    _numRoutings.setText("You have no routings");
                else
                    _numRoutings.setText("You currently have " + routes.size() + " routings");

                Intent intent = new Intent();
                intent.putExtra(StaticResources.EXTRA_PROFILE_ID, profile.getProfileId());
                RouteFragment routeFragment = new RouteFragment();
                routeFragment.setArguments(intent.getExtras());

                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (fragmentManager.findFragmentByTag("route_fragment") != null)
                    transaction.replace(R.id.fragment_route_placeholder, routeFragment, "route_fragment");
                else
                    transaction.add(R.id.fragment_route_placeholder, routeFragment, "route_fragment");

                transaction.commit();
            } catch (Exception ex) {
                Log.e("SessionFragment", "Failed to produce session fragment from arguments: " + ex.getMessage());
            }
        }
    }

    private void handleSignOut() {
        if (_sessionUpdater != null)
            _sessionUpdater.clearSessionData();
    }
}
