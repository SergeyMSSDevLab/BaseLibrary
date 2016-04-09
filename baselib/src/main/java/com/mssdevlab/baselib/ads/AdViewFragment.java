package com.mssdevlab.baselib.ads;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mssdevlab.baselib.OnFormatListener;

/**
 * Manages ad view in application
 */
public class AdViewFragment extends Fragment {
    private static final String LOG_TAG = "AdViewFragment";
    private static final String ARG_AD_UNIT_ID = "param1";
    private static final String ARG_TAG_NAME = "param2";

    private String mAdUnitId;
    private String tagName;
    private AdView mAdView;
    private OnFormatListener formatListener = null;

    public static AdViewFragment newInstance(String adUnitId, String tagName) {
        Log.v(LOG_TAG, "newInstance adUnitId: " + adUnitId);
        AdViewFragment fragment = new AdViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_AD_UNIT_ID, adUnitId);
        args.putString(ARG_TAG_NAME, tagName);
        fragment.setArguments(args);
        return fragment;
    }

    public AdViewFragment() {
        Log.v(LOG_TAG, "Empty constructor");
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "onCreate");
        Bundle args = getArguments();
        if (args != null) {
            this.mAdUnitId = args.getString(ARG_AD_UNIT_ID);
            Log.v(LOG_TAG, "onCreate adUnitId: " + mAdUnitId);
            this.tagName = args.getString(ARG_TAG_NAME);
        }
        Activity activity = getActivity();
        if (activity instanceof OnFormatListener) {
            this.formatListener = (OnFormatListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");

        this.mAdView = new AdView(this.getActivity());
        this.mAdView.setAdSize(AdSize.SMART_BANNER);
        this.mAdView.setAdUnitId(this.mAdUnitId);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        this.mAdView.loadAd(adRequest);

        if (this.formatListener != null) {
            this.formatListener.onFormat(this.mAdView, this.tagName);
        }
        return this.mAdView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "resume (this.mAdView != null): " + (this.mAdView != null));
        if (this.mAdView != null) {
            this.mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "pause (this.mAdView != null): " + (this.mAdView != null));
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "destroy (this.mAdView != null): " + (this.mAdView != null));
        if (this.mAdView != null) {
            this.mAdView.destroy();
            this.mAdView = null;
        }
    }
}