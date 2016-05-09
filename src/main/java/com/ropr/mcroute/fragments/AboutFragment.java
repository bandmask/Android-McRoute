package com.ropr.mcroute.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcroute.R;

/**
 * Created by NIJO7810 on 2016-05-06.
 */
public class AboutFragment extends Fragment {
    private CloseAboutFragmentInterface _fragmentCloser;

    public interface CloseAboutFragmentInterface {
        void closeAboutFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.view_about, container, false);
        setHasOptionsMenu(false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CloseAboutFragmentInterface) {
            _fragmentCloser = (CloseAboutFragmentInterface) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_fragmentCloser != null)
            _fragmentCloser.closeAboutFragment();
    }
}
