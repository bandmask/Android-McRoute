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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mcroute.R;
import com.ropr.mcroute.Login_Activity;
import com.ropr.mcroute.configs.ApiConfig;
import com.ropr.mcroute.interfaces.SessionUpdater;
import com.ropr.mcroute.models.SessionData;
import com.ropr.mcroute.sources.StaticResources;


/**
 * Created by NIJO7810 on 2016-05-03.
 */
public class LoginFragment extends Fragment implements AboutFragment.CloseAboutFragmentInterface {
    private Button _googleProviderActionButton;
    private SessionUpdater _sessionUpdater;
    private AboutFragment _aboutFragment;
    private FrameLayout _mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_login, container, false);
        _mainLayout = (FrameLayout) view.findViewById(R.id.view_login_main_layout);

        final TextView header = (TextView) view.findViewById(R.id.login_view_header);
        _googleProviderActionButton = (Button) view.findViewById(R.id.login_view_action_google);

        _googleProviderActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAPIConnection();
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onAttach(Context owner) {
        super.onAttach(owner);
        if (owner instanceof SessionUpdater)
            _sessionUpdater = (SessionUpdater) owner;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == StaticResources.LOGIN_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                try {
                    Bundle extras = data.getExtras();
                    SessionData sessionData = (SessionData) extras.get("result");
                    _sessionUpdater.setSessionData(sessionData);
                } catch (Exception ex) {
                    Log.e("ActivityResult", ex.getMessage());
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_action_about:
                handleAboutFragmentTransition();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleAboutFragmentTransition() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (_aboutFragment == null)
            _aboutFragment = new AboutFragment();

        if (fragmentManager.findFragmentByTag("about_fragment") == null)
            transaction.add(R.id.fragment_about_placeholder, _aboutFragment, "about_fragment");
        else
            transaction.replace(R.id.fragment_about_placeholder, _aboutFragment, "about_fragment");

        _mainLayout.setVisibility(View.INVISIBLE);

        transaction.commit();
    }

    private void handleAPIConnection() {
        Intent intent = new Intent(this.getActivity(), Login_Activity.class);
        intent.putExtra(StaticResources.EXTRA_URL, getSigninUrl());
        startActivityForResult(intent, StaticResources.LOGIN_REQUEST_CODE);
    }

    private String getSigninUrl() {
        return new StringBuilder().append(ApiConfig.API_ENDPOINT + "Account/ExternalLogin")
                .append("?provider=Google")
                .append("&response_type=token")
                .append("&client_id=" + ApiConfig.API_CLIENT)
                .append("&redirect_uri=" + ApiConfig.API_REDIRECT_URL)
                .append("&client_secret=" + ApiConfig.API_SECRET)
                .toString();
    }

    @Override
    public void closeAboutFragment() {

    }
}
