package com.mssdevlab.baselib;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Manages common view lifecycle
 */
@SuppressWarnings("unused")
public class CommonViewManagerFragment extends Fragment {
    private static final String LOG_TAG = CommonViewManagerFragment.class.getCanonicalName();
    private static final String ARG_CONFIG_TAG = "param1";

    private String ConfigurationTag;

    public static CommonViewManagerFragment newInstance(final String configTag) {
        Log.v(LOG_TAG, "newInstance entered.");
        CommonViewManagerFragment fragment = new CommonViewManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONFIG_TAG, configTag);
        fragment.setArguments(args);
        return fragment;
    }

    public CommonViewManagerFragment() {
        Log.v(LOG_TAG, "Constructor entered.");
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate entered.");
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.ConfigurationTag = args.getString(ARG_CONFIG_TAG);
        }
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "onResume entered.");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "onPause entered.");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, "onDestroy entered.");
        super.onDestroy();
    }

    public void setPlaceHolder(@Nullable ViewGroup placeHolder){
        Log.v(LOG_TAG, "addCommonView entered.");

    }
}
