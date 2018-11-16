package com.mssdevlab.baselib.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.PromoteManager;
import com.mssdevlab.baselib.common.ShowView;
import com.mssdevlab.baselib.databinding.ComboBannerFragmentBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;

public class ComboBannerFragment extends Fragment {
    private static final String LOG_TAG = "ComboBannerFragment";

    public static final String EXTRA_UNIT_ID = "ComboBannerFragment.unitid";
    public static final String EXTRA_APP_NAME = "ComboBannerFragment.appname";
    public static final String EXTRA_DEV_EMAIL = "ComboBannerFragment.devemail";
    public static final String EXTRA_MANAGE_PARENT = "ComboBannerFragment.manageParent";

    private ComboBannerViewModel mViewModel;
    private View mRoot;
    private AdView mAdView;

    /* This method is called first in lifecycle*/
    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onInflate");
        this.mViewModel = ViewModelProviders.of(this).get(ComboBannerViewModel.class);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ComboBannerFragment);
        if (arr != null) {
            // extract attributes from the markup
            this.mViewModel.setAdUnitId(arr.getString(R.styleable.ComboBannerFragment_ad_unit_id));
            this.mViewModel.setAppName(arr.getString(R.styleable.ComboBannerFragment_app_name));
            this.mViewModel.setDevEmail(arr.getString(R.styleable.ComboBannerFragment_developer_email));
            this.mViewModel.setManageParent(arr.getBoolean(R.styleable.ComboBannerFragment_manage_parent, false));
            this.mViewModel.setAdSize(AdSize.BANNER);
            arr.recycle();
        }
        Log.v(LOG_TAG, "ensureViewModel data set:" +
                " adUnitId: " + this.mViewModel.getAdUnitId() +
                " appName: " + this.mViewModel.getAppName().getValue() +
                " manageParent: " + this.mViewModel.getManageParent() +
                " devEmail: " + this.mViewModel.getDevEmail().getValue());

        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        if (this.mViewModel == null){
            // Fragment created programmatically, onInflate is not called
            Bundle args = getArguments();
            if (args != null){
                this.mViewModel = ViewModelProviders.of(this).get(ComboBannerViewModel.class);
                this.mViewModel.setAdUnitId(args.getString(EXTRA_UNIT_ID));
                this.mViewModel.setAppName(args.getString(EXTRA_APP_NAME));
                this.mViewModel.setDevEmail(args.getString(EXTRA_DEV_EMAIL));
                this.mViewModel.setManageParent(args.getBoolean(EXTRA_MANAGE_PARENT, false));
                this.mViewModel.setAdSize(AdSize.BANNER);
            }
        }
        ComboBannerFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.combo_banner_fragment, container, false);

        binding.setLifecycleOwner(this);
        binding.setViewModel(this.mViewModel);
        this.mRoot = binding.getRoot();
        return this.mRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(LOG_TAG, "onActivityCreated");
        Activity activity = this.getActivity();
        if (activity != null){
            this.mAdView = new AdView(activity);
            this.mAdView.setAdSize(this.mViewModel.getAdSize());
            this.mAdView.setAdUnitId(this.mViewModel.getAdUnitId());
        }

        FrameLayout amLayout = this.mRoot.findViewById(R.id.flBanner);
        if (this.mAdView != null && amLayout != null) {
            ViewGroup.LayoutParams params = amLayout.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            amLayout.addView(this.mAdView, params);
            Log.v(LOG_TAG, "onCreateView: mAdView done");
        }

        mViewModel.getBannerShowMode().observe(this, this::updateView);
    }

    private void updateView(ShowView showWhat){
        Log.v(LOG_TAG, "getBannerShowMode");

        assert showWhat != null;
        // update UI
        switch (showWhat) {
            case NOTHING:
                Log.v(LOG_TAG, "getBannerShowMode: nothing");
                this.mViewModel.setIsShowPromo(false);
                this.hideAdView();
                this.ensureParentView(false);
                break;
            case ADS:
                Log.v(LOG_TAG, "getBannerShowMode: ads");
                this.mViewModel.setIsShowPromo(false);
                this.ensureAdView();
                this.ensureParentView(true);
                break;
            case PROMO:
                Log.v(LOG_TAG, "getBannerShowMode: promo");
                this.hideAdView();
                this.ensurePromoView();
                this.ensureParentView(true);
                break;
        }
    }

    private void ensureParentView(boolean show){
        if (this.mViewModel.getManageParent()){
            if (this.mRoot != null){
                ViewParent vp = this.mRoot.getParent();
                if (vp instanceof ViewGroup){
                    ViewGroup v = (ViewGroup) vp;
                    if (show){
                        v.setVisibility(View.VISIBLE);
                    } else {
                        v.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void hideAdView(){
        this.mViewModel.setIsShowAd(false);
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
    }

    private void ensureAdView(){
        Log.v(LOG_TAG, "ensureAdView");

        if (this.mAdView != null){

            ensureParentView(false);
            this.mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.v(LOG_TAG, "onAdLoaded");
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
                        if (mViewModel.getBannerShowMode().getValue() == ShowView.ADS){
                            mViewModel.setIsShowAd(true);
                            ensureParentView(true);
                        }
                    }
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Log.v(LOG_TAG, "onAdFailedToLoad: error " + errorCode);
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
                        mViewModel.setIsShowAd(false);
                        ensureParentView(false);
                    }
                }
            });

            AdRequest adRequest = new AdRequest
                    .Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            this.mAdView.loadAd(adRequest);
            Log.v(LOG_TAG, "ensureAdView: loadAd started");
        }
    }

    private void ensurePromoView(){
        Log.v(LOG_TAG, "ensurePromoView");
        this.setUpPromoView();
        this.mViewModel.setIsShowPromo(true);
    }

    private void setUpPromoView(){
        Log.v(LOG_TAG, "setUpPromoView");

        Resources res = this.getResources();
        final Button btnYes = this.mRoot.findViewById(R.id.btnYes);
        final Button btnNot = this.mRoot.findViewById(R.id.btnNot);
        final TextView tvPrompt = this.mRoot.findViewById(R.id.tvPromptQuestion);

        final String reportTitle = res.getString(R.string.bl_common_error_report_choose_title);
        String temp = this.mViewModel.getAppName().getValue();
        final String appName = temp == null ? "application": temp;
        temp = this.mViewModel.getDevEmail().getValue();
        final String devEmail = temp == null ? "" : temp;

        String promptText = res.getString(R.string.bl_common_enjoy_prompt, appName );

        tvPrompt.setText(promptText);
        btnYes.setText(R.string.bl_common_enjoy_yes);
        btnNot.setText(R.string.bl_common_enjoy_not);

        btnYes.setOnClickListener((View v) -> {
            tvPrompt.setText(R.string.bl_common_enjoy_rate_prompt);
            btnYes.setText(R.string.bl_common_enjoy_rate_yes);
            btnNot.setText(R.string.bl_common_enjoy_rate_not);

            btnYes.setOnClickListener(v14 -> PromoteManager.goToPromoScreen(getActivity()));

            btnNot.setOnClickListener(v13 -> PromoteManager.markRateNotNow());
        });

        btnNot.setOnClickListener(v -> {
            tvPrompt.setText(R.string.bl_common_enjoy_feedback_prompt);
            btnYes.setText(R.string.bl_common_enjoy_feedback_yes);
            btnNot.setText(R.string.bl_common_enjoy_feedback_not);

            btnYes.setOnClickListener(v12 -> {
                String uriText = "mailto:" + devEmail + "?subject="
                        + Uri.encode(appName) + "&body=" + " ... ";

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse(uriText));
                startActivity(Intent.createChooser(sendIntent, reportTitle));

                PromoteManager.cancelPromoScreenPermanently();
            });

            btnNot.setOnClickListener(v1 -> PromoteManager.cancelPromoScreenPermanently());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
        if (this.mAdView != null) {
            this.mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause");
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
    }

    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, "onDestroy");
        if (this.mAdView != null) {
            this.mAdView.destroy();
            this.mAdView = null;
        }
        this.mRoot = null;
        this.mViewModel = null;
        super.onDestroy();
    }
}
