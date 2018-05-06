package com.mssdevlab.baselib.ComboBanner;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.factory.CommonViewProviders;

public class ComboBannerObserver implements LifecycleObserver {
    private static final String LOG_TAG = "ComboBannerObserver";

    public static final String ARG_APP_NAME = "ComboBannerFragment.param1";
    public static final String ARG_DEV_EMAIL = "ComboBannerFragment.param2";
    public static final String ARG_AD_UNIT_ID = "ComboBannerFragment.param3";

    private BaseActivity mActivity;
    private Lifecycle mLifecycle;

    private AdView mAdView;
    private View mPromoView;

    private int mViewStubId;
    private String mAdUnitId;

    private String mInstanceTag;

    ComboBannerObserver(@NonNull BaseActivity activity, @NonNull Bundle args) {
        Log.v(LOG_TAG, "Constructor");
        this.mActivity = activity;
        this.mLifecycle = activity.getLifecycle();
        this.mInstanceTag = args.getString(CommonViewProviders.ARG_INSTANCE_TAG);
        this.mAdUnitId = args.getString(ARG_AD_UNIT_ID);
        this.mViewStubId = args.getInt(CommonViewProviders.ARG_VIEWSTUB_TAG);

        ComboBannerViewModel model = ViewModelProviders.of(activity).get(ComboBannerViewModel.class);
        model.updateView().observe(activity, this::updateView);
        model.checkState();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Log.v(LOG_TAG, "onResume");
        if (this.mAdView != null) {
            this.mAdView.resume();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.v(LOG_TAG, "onPause");
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.v(LOG_TAG, "onDestroy");
        if (this.mAdView != null) {
            this.mAdView.destroy();
            this.mAdView = null;
        }
    }

    private void updateView(ComboBannerUpdateLiveData.ShowView data){
        if (this.mLifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            // update UI
            if (data == ComboBannerUpdateLiveData.ShowView.NOTHING){
                Log.v(LOG_TAG, "ComboBannerViewModel: nothing");
                if (this.mPromoView != null){
                    this.mPromoView.setVisibility(View.GONE);
                }
                if (this.mAdView != null){
                    this.mAdView.pause();
                    this.mAdView.setVisibility(View.GONE);
                }
            } else if (data == ComboBannerUpdateLiveData.ShowView.ADS){
                Log.v(LOG_TAG, "ComboBannerViewModel: ads");
                this.ensureAdView();
                this.mAdView.setVisibility(View.VISIBLE);
            } else if (data == ComboBannerUpdateLiveData.ShowView.PROMO){
                Log.v(LOG_TAG, "ComboBannerViewModel: promo");
                this.ensurePromoView();
                this.mPromoView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void ensureAdView(){
        Log.v(LOG_TAG, "ensureAdView");
        if (this.mAdView == null) {
            this.mAdView = new AdView(this.mActivity);
            this.mAdView.setAdSize(AdSize.SMART_BANNER);
            this.mAdView.setAdUnitId(this.mAdUnitId);
            this.mAdView.setVisibility(View.GONE);
            Log.v(LOG_TAG, "ensureAdView: mAdView created");
        }

        if (this.mPromoView != null){
            this.mPromoView.setVisibility(View.GONE);
        }

        this.mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.v(LOG_TAG, "onAdLoaded");
                boolean viewAdded = false;
                if (mPromoView != null){
                    // Replace promotional view
                    Helper.replaceView(mPromoView, mAdView);
                    mPromoView = null;
                    viewAdded = true;
                    Log.v(LOG_TAG, "onAdLoaded: replace promo");
                } else if (mViewStubId > 0){
                    // Replace stub
                    ViewStub vStub = mActivity.findViewById(mViewStubId);
                    mViewStubId = -1;
                    if(vStub != null){
                        vStub.setLayoutResource(R.layout.stub_view);
                        View vTemp = vStub.inflate();
                        Helper.replaceView(vTemp, mAdView);
                        viewAdded = true;
                        Log.v(LOG_TAG, "onAdLoaded: replace stub");
                    }
                }

                if (viewAdded){
                    mActivity.onCommonViewCreated(mAdView, mInstanceTag);
                    mAdView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
            }
        });

        AdRequest adRequest = new AdRequest
                .Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        this.mAdView.loadAd(adRequest);
    }

    private void ensurePromoView(){

    }
}
