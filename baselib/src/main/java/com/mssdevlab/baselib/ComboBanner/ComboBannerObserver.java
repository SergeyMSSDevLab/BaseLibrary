package com.mssdevlab.baselib.ComboBanner;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.common.PromoteManager;
import com.mssdevlab.baselib.factory.CommonViewProviders;

class ComboBannerObserver implements LifecycleObserver {
    private static final String LOG_TAG = "ComboBannerObserver";

    public static final String ARG_APP_NAME = "ComboBannerFragment.param1";
    public static final String ARG_DEV_EMAIL = "ComboBannerFragment.param2";
    public static final String ARG_AD_UNIT_ID = "ComboBannerFragment.param3";

    private AdView mAdView;
    private View mPromoView;

    private int mViewStubId;
    private final String mAdUnitId;

    private final String mInstanceTag;

    private Button mBtnYes;
    private Button mBtnNot;
    private TextView mTvPrompt;
    private String mAppName;
    private String mDevEmail;

    ComboBannerObserver(@NonNull Bundle args) {
        Log.v(LOG_TAG, "Constructor");
        this.mInstanceTag = args.getString(CommonViewProviders.ARG_INSTANCE_TAG);
        this.mAdUnitId = args.getString(ARG_AD_UNIT_ID);
        this.mViewStubId = args.getInt(CommonViewProviders.ARG_VIEWSTUB_TAG);
        this.mAppName = args.getString(ARG_APP_NAME);
        this.mDevEmail = args.getString(ARG_DEV_EMAIL);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Log.v(LOG_TAG, "onResume");
        if (this.mAdView != null) {
            if (this.mAdView.getVisibility() == View.VISIBLE){
                this.mAdView.resume();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.v(LOG_TAG, "onPause");
        if (this.mAdView != null) {
            if (this.mAdView.getVisibility() == View.VISIBLE){
                this.mAdView.pause();
            }
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

    public void updateView(@NonNull BaseActivity activity){
        Log.v(LOG_TAG, "updateView");
        ComboBannerViewModel model = ViewModelProviders.of(activity).get(ComboBannerViewModel.class);
        ComboBannerUpdateLiveData.ShowView data = model.updateView().getValue();
        // update UI
        assert data != null;
        switch (data) {
            case NOTHING:
                Log.v(LOG_TAG, "ComboBannerViewModel: nothing");
                this.hidePromoView();
                this.hideAdView();
                break;
            case ADS:
                Log.v(LOG_TAG, "ComboBannerViewModel: ads");
                this.hidePromoView();
                this.ensureAdView(activity);
                break;
            case PROMO:
                Log.v(LOG_TAG, "ComboBannerViewModel: promo");
                this.hideAdView();
                this.ensurePromoView(activity);
                break;
        }
    }

    private void hideAdView(){
        if (this.mAdView != null) {
            this.mAdView.pause();
            this.mAdView.setVisibility(View.GONE);
        }
    }

    private void hidePromoView(){
        if (this.mPromoView != null){
            this.mPromoView.setVisibility(View.GONE);
        }
    }

    private void ensureAdView(@NonNull BaseActivity activity){
        Log.v(LOG_TAG, "ensureAdView");
        if (this.mAdView == null) {
            this.mAdView = new AdView(activity);
            this.mAdView.setAdSize(AdSize.SMART_BANNER);
            this.mAdView.setAdUnitId(this.mAdUnitId);
            this.mAdView.setVisibility(View.GONE);
            Log.v(LOG_TAG, "ensureAdView: mAdView created");
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
                    ViewStub vStub = activity.findViewById(mViewStubId);
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
                    activity.onCommonViewCreated(mAdView, mInstanceTag);
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

    private void ensurePromoView(@NonNull BaseActivity activity){
        Log.v(LOG_TAG, "ensurePromoView");
        if (this.mPromoView != null){
            this.mPromoView.setVisibility(View.VISIBLE);
        } else {
            // get the root view
            View viewToReplace = null;
            if (this.mAdView != null){
                // Replace adview
                viewToReplace = this.mAdView;
                this.mAdView = null;
                Log.v(LOG_TAG, "ensurePromoView: replace adview");
            } else if (this.mViewStubId > 0){
                // Replace stub
                ViewStub vStub = activity.findViewById(this.mViewStubId);
                this.mViewStubId = -1;
                if(vStub != null){
                    vStub.setLayoutResource(R.layout.stub_view);
                    viewToReplace = vStub.inflate();
                    Log.v(LOG_TAG, "ensurePromoView: replace stub");
                }
            }

            if (viewToReplace != null){
                LayoutInflater factory = LayoutInflater.from(activity);
                this.mPromoView = factory.inflate(R.layout.promote_view, (ViewGroup)viewToReplace.getParent(), false);
                Helper.replaceView(viewToReplace, this.mPromoView);
                this.setUpPromoView(activity);
                activity.onCommonViewCreated(this.mPromoView, this.mInstanceTag);
            }
        }
    }

    private void setUpPromoView(final @NonNull BaseActivity activity){
        Log.v(LOG_TAG, "setUpPromoView: view == null: " + (this.mPromoView == null));
        final Resources res = activity.getResources();

        mBtnYes = this.mPromoView.findViewById(R.id.btnYes);
        mBtnNot = this.mPromoView.findViewById(R.id.btnNot);
        mTvPrompt = this.mPromoView.findViewById(R.id.tvPromptQuestion);
        String text = res.getString(R.string.common_enjoy_prompt,
                this.mAppName == null ? "application": this.mAppName);
        mTvPrompt.setText(text);
        mBtnYes.setText(R.string.common_enjoy_yes);
        mBtnNot.setText(R.string.common_enjoy_not);

        mBtnYes.setOnClickListener((View v) -> {
            mTvPrompt.setText(R.string.common_enjoy_rate_prompt);
            mBtnYes.setText(R.string.common_enjoy_rate_yes);
            mBtnNot.setText(R.string.common_enjoy_rate_not);

            mBtnYes.setOnClickListener(v14 -> {
                PromoteManager.goToPromoScreen(activity);
                mPromoView.setVisibility(View.GONE);
            });

            mBtnNot.setOnClickListener(v13 -> {
                PromoteManager.markRateNotNow();
                mPromoView.setVisibility(View.GONE);
            });
        });

        mBtnNot.setOnClickListener(v -> {
            mTvPrompt.setText(R.string.common_enjoy_feedback_prompt);
            mBtnYes.setText(R.string.common_enjoy_feedback_yes);
            mBtnNot.setText(R.string.common_enjoy_feedback_not);

            mBtnYes.setOnClickListener(v12 -> {

                String uriText = "mailto:" + (mDevEmail == null ? "": mDevEmail) + "?subject="
                        + Uri.encode(mAppName == null ? "application": mAppName) + "&body=" + " ... ";
                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                activity.startActivity(Intent.createChooser(sendIntent,
                        res.getString(R.string.common_error_report_choose_title)));

                PromoteManager.cancelPromoScreenPermanently();
                mPromoView.setVisibility(View.GONE);
            });

            mBtnNot.setOnClickListener(v1 -> {
                PromoteManager.cancelPromoScreenPermanently();
                mPromoView.setVisibility(View.GONE);
            });
        });
    }
}
