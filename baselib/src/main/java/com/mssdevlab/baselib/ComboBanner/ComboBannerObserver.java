package com.mssdevlab.baselib.ComboBanner;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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

class ComboBannerObserver implements LifecycleObserver {
    private static final String LOG_TAG = "ComboBannerObserver";

    private BaseActivity mActivity;
    private AdView mAdView;
    private View mPromoView;

    ComboBannerObserver(@NonNull BaseActivity activity) {
        this.mActivity = activity;
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
        this.mPromoView = null;
        this.mActivity = null;
    }

    public void updateView(ShowView showWhat){
        Log.v(LOG_TAG, "updateView");

        if (this.mActivity != null &&
            this.mActivity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
            // update UI
            assert showWhat != null;
            switch (showWhat) {
                case NOTHING:
                    Log.v(LOG_TAG, "ComboBannerViewModel: nothing");
                    this.hidePromoView();
                    this.hideAdView();
                    break;
                case ADS:
                    Log.v(LOG_TAG, "ComboBannerViewModel: ads");
                    this.hidePromoView();
                    this.ensureAdView();
                    break;
                case PROMO:
                    Log.v(LOG_TAG, "ComboBannerViewModel: promo");
                    this.hideAdView();
                    this.ensurePromoView();
                    break;
            }
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

    private void ensureAdView(){
        Log.v(LOG_TAG, "ensureAdView");
        final ComboBannerViewModel model = ViewModelProviders.of(this.mActivity).get(ComboBannerViewModel.class);

        if (this.mAdView == null) {
            this.mAdView = new AdView(this.mActivity);
            this.mAdView.setAdSize(AdSize.SMART_BANNER);
            this.mAdView.setAdUnitId(model.adUnitId);
            this.mAdView.setVisibility(View.GONE);
            Log.v(LOG_TAG, "ensureAdView: mAdView created");
        }
        final Lifecycle lifecycle = this.mActivity.getLifecycle();

        this.mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.v(LOG_TAG, "onAdLoaded");
                if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
                    boolean viewAdded = false;
                    if (mPromoView != null){
                        // Replace promotional view
                        Helper.replaceView(mPromoView, mAdView);
                        mPromoView = null;
                        viewAdded = true;
                        Log.v(LOG_TAG, "onAdLoaded: replace promo");
                    } else if (model.viewStubId > 0){
                        // Replace stub
                        ViewStub vStub = mActivity.findViewById(model.viewStubId);
                        model.viewStubId = -1;
                        if(vStub != null){
                            vStub.setLayoutResource(R.layout.stub_view);
                            View vTemp = vStub.inflate();
                            Helper.replaceView(vTemp, mAdView);
                            viewAdded = true;
                            Log.v(LOG_TAG, "onAdLoaded: replace stub");
                        }
                    }

                    if (viewAdded){
                        mActivity.onCommonViewCreated(mAdView, model.instanceTag);
                        mAdView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
                    mAdView.setVisibility(View.GONE);
                }
            }
        });

        AdRequest adRequest = new AdRequest
                .Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        this.mAdView.loadAd(adRequest);
    }

    private void ensurePromoView(){
        Log.v(LOG_TAG, "ensurePromoView");

        if (this.mPromoView != null){
            this.mPromoView.setVisibility(View.VISIBLE);
        } else {
            final ComboBannerViewModel model = ViewModelProviders.of(this.mActivity).get(ComboBannerViewModel.class);
            // get the root view
            View viewToReplace = null;
            if (this.mAdView != null){
                // Replace adview
                viewToReplace = this.mAdView;
                this.mAdView = null;
                Log.v(LOG_TAG, "ensurePromoView: replace adview");
            } else if (model.viewStubId > 0){
                // Replace stub
                ViewStub vStub = this.mActivity.findViewById(model.viewStubId);
                model.viewStubId = -1;
                if(vStub != null){
                    vStub.setLayoutResource(R.layout.stub_view);
                    viewToReplace = vStub.inflate();
                    Log.v(LOG_TAG, "ensurePromoView: replace stub");
                }
            }

            if (viewToReplace != null){
                LayoutInflater factory = LayoutInflater.from(this.mActivity);
                this.mPromoView = factory.inflate(R.layout.promote_view, (ViewGroup)viewToReplace.getParent(), false);
                Helper.replaceView(viewToReplace, this.mPromoView);
                this.setUpPromoView(model);
                this.mActivity.onCommonViewCreated(this.mPromoView, model.instanceTag);
            }
        }
    }

    private void setUpPromoView(ComboBannerViewModel model){
        Log.v(LOG_TAG, "setUpPromoView: view == null: " + (this.mPromoView == null));

        final Resources res = this.mActivity.getResources();
        final Button btnYes = this.mPromoView.findViewById(R.id.btnYes);
        final Button btnNot = this.mPromoView.findViewById(R.id.btnNot);
        final TextView tvPrompt = this.mPromoView.findViewById(R.id.tvPromptQuestion);

        String text = res.getString(R.string.common_enjoy_prompt,
                model.appName == null ? "application": model.appName);
        tvPrompt.setText(text);
        btnYes.setText(R.string.common_enjoy_yes);
        btnNot.setText(R.string.common_enjoy_not);

        btnYes.setOnClickListener((View v) -> {
            tvPrompt.setText(R.string.common_enjoy_rate_prompt);
            btnYes.setText(R.string.common_enjoy_rate_yes);
            btnNot.setText(R.string.common_enjoy_rate_not);

            btnYes.setOnClickListener(v14 -> {
                PromoteManager.goToPromoScreen(this.mActivity);
                mPromoView.setVisibility(View.GONE);
            });

            btnNot.setOnClickListener(v13 -> {
                PromoteManager.markRateNotNow();
                mPromoView.setVisibility(View.GONE);
            });
        });

        btnNot.setOnClickListener(v -> {
            tvPrompt.setText(R.string.common_enjoy_feedback_prompt);
            btnYes.setText(R.string.common_enjoy_feedback_yes);
            btnNot.setText(R.string.common_enjoy_feedback_not);

            btnYes.setOnClickListener(v12 -> {

                String uriText = "mailto:" + (model.devEmail == null ? "": model.devEmail) + "?subject="
                        + Uri.encode(model.appName == null ? "application": model.appName) + "&body=" + " ... ";
                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                this.mActivity.startActivity(Intent.createChooser(sendIntent,
                        res.getString(R.string.common_error_report_choose_title)));

                PromoteManager.cancelPromoScreenPermanently();
                mPromoView.setVisibility(View.GONE);
            });

            btnNot.setOnClickListener(v1 -> {
                PromoteManager.cancelPromoScreenPermanently();
                mPromoView.setVisibility(View.GONE);
            });
        });
    }
}
