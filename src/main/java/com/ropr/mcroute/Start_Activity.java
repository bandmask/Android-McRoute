package com.ropr.mcroute;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.mcroute.R;
import com.ropr.mcroute.fragments.LoginFragment;
import com.ropr.mcroute.fragments.SessionFragment;
import com.ropr.mcroute.handlers.ProfileHandler;
import com.ropr.mcroute.interfaces.SessionUpdater;
import com.ropr.mcroute.models.McRouteProfile;
import com.ropr.mcroute.models.SessionData;
import com.ropr.mcroute.sources.StaticResources;

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
                McRouteProfile profile = ProfileHandler.getInstance().getProfile(StaticResources.SessionManager.getUserId(), true);

                if (profile == null) throw new IllegalStateException();

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