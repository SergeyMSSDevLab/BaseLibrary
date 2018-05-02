package com.mssdevlab.baselib.ComboBanner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mssdevlab.baselib.factory.CommonViewListener;
import com.mssdevlab.baselib.factory.CommonViewProviders;

public class ComboBannerFragment extends Fragment {
    private static final String LOG_TAG = ComboBannerFragment.class.getCanonicalName();

    public static final String ARG_APP_NAME = "ComboBannerFragment.param1";
    public static final String ARG_DEV_EMAIL = "ComboBannerFragment.param2";
    public static final String ARG_AD_UNIT_ID = "ComboBannerFragment.param3";

    private String mInstanceTag;
    private ViewStub mViewStub = null;
    private String mAdUnitId;
    private AdSize mAdSize = null;
    private CommonViewListener formatListener = null;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "onCreate");
        Bundle args = getArguments();
        if (args != null) {
            this.mInstanceTag = args.getString(CommonViewProviders.ARG_INSTANCE_TAG);
            this.mAdUnitId = args.getString(ARG_AD_UNIT_ID);
            Log.v(LOG_TAG, "onCreate adUnitId: " + mAdUnitId);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onActivityCreated entered.");
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        Activity activity = this.getActivity();
        if (args != null && activity != null) {
            if (args.containsKey(CommonViewProviders.ARG_VIEWSTUB_TAG)){
                int id = args.getInt(CommonViewProviders.ARG_VIEWSTUB_TAG);
                this.mViewStub = activity.findViewById(id);
            }
            if (activity instanceof CommonViewListener) {
                this.formatListener = (CommonViewListener) activity;
            }
        }
        Log.v(LOG_TAG, "onActivityCreated mViewStub != null: " + (this.mViewStub != null));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
// TODO: we need to create banner view from layout file in onActivityCreated

        Log.v(LOG_TAG, "onCreateView this.getActivity() != null: " + (this.getActivity() != null));

        this.mAdView = new AdView(this.getActivity());
        if (this.mAdSize == null) {
            this.mAdSize = AdSize.SMART_BANNER;
        }
        Log.v(LOG_TAG, "onCreateView: " + this.mAdSize.toString());
        this.mAdView.setAdSize(this.mAdSize);
        this.mAdView.setAdUnitId(this.mAdUnitId);
        AdRequest adRequest = new AdRequest
                .Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        this.mAdView.loadAd(adRequest);

        if (this.formatListener != null) {
            // TODO: remove the class. this.formatListener.onViewCreated(this.mAdView, this.tagName);
        }
        return this.mAdView;
    }

}
