package com.ropr.mcroute;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mcroute.R;
import com.ropr.mcroute.fragments.LoginFragment;
import com.ropr.mcroute.fragments.SessionFragment;
import com.ropr.mcroute.handlers.ApiHttpHandler;
import com.ropr.mcroute.interfaces.SessionUpdater;
import com.ropr.mcroute.models.McRouteProfile;
import com.ropr.mcroute.models.SessionData;
import com.ropr.mcroute.sources.StaticResources;

import java.util.HashMap;

/**
 * Created by NIJO7810 on 2016-05-03.
 */
public class Start_Activity extends Activity implements SessionUpdater {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticResources.SessionManager.InitSessionManager(getApplicationContext());

        setContentView(R.layout.activity_start);
        handleFragmentTransition();
    }

    private void handleFragmentTransition() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (!StaticResources.SessionManager.hasSessionValues()) {
            LoginFragment loginFragment = new LoginFragment();
            if (fragmentManager.findFragmentByTag("login_fragment") != null || fragmentManager.findFragmentByTag("session_fragment") != null)
                transaction.replace(R.id.fragment_session_placeholder, loginFragment, "login_fragment");
            else
                transaction.add(R.id.fragment_session_placeholder, loginFragment, "login_fragment");

            transaction.commit();
        } else {
            try {
                String action = "profile/GetProfileByUserId";
                HashMap<String, String> dataValues = new HashMap<String, String>();
                dataValues.put("userId", StaticResources.SessionManager.getUserId());
                JsonObject apiResult = ApiHttpHandler.getInstance().handleGet(action, dataValues);

                Gson gson = new GsonBuilder().create();
                McRouteProfile profile = gson.fromJson(apiResult, McRouteProfile.class);

                Intent sessionIntent = new Intent();
                sessionIntent.putExtra(StaticResources.EXTRA_SESSION_PROFILE, profile);

                SessionFragment sessionFragment = new SessionFragment();
                sessionFragment.setArguments(sessionIntent.getExtras());

                if (fragmentManager.findFragmentByTag("login_fragment") != null || fragmentManager.findFragmentByTag("session_fragment") != null)
                    transaction.replace(R.id.fragment_session_placeholder, sessionFragment, "session_fragment");
                else
                    transaction.add(R.id.fragment_session_placeholder, sessionFragment, "session_fragment");

                transaction.commit();

            } catch (IllegalStateException exception) {
                clearSessionData();
            }
        }
    }

    @Override
    public void setSessionData(SessionData sessionData) {
        StaticResources.SessionManager.addSession(sessionData);
        handleFragmentTransition();
    }

    @Override
    public void clearSessionData() {
        StaticResources.SessionManager.clearSession();
        handleFragmentTransition();
    }
}