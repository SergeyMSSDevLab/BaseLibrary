package com.mssdevlab.baselib.combobanner;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.ShowView;
import com.mssdevlab.baselib.databinding.ComboBannerFragmentBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ComboBannerFragment extends Fragment {
    private static final String LOG_TAG = "ComboBannerFragment";

    private ComboBannerViewModel mViewModel;
    private View mRoot;
    private AdView mAdView;

    public static ComboBannerFragment newInstance() {
        return new ComboBannerFragment();
    }

    /* This method is called first in lifecycle*/
    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onInflate");
        this.mViewModel = ViewModelProviders.of(this).get(ComboBannerViewModel.class);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ComboBannerFragment);
        if (arr != null) {
//            for(int i = 0; i < arr.length(); i++){
//                TypedValue val = arr.peekValue(i);
//                Log.v(LOG_TAG, "onInflate attribute: " + val.toString());
//            }
            this.mViewModel.setAdUnitId(arr.getString(R.styleable.ComboBannerFragment_ad_unit_id));
            this.mViewModel.setAppName(arr.getString(R.styleable.ComboBannerFragment_app_name));
            this.mViewModel.setDevEmail(arr.getString(R.styleable.ComboBannerFragment_developer_email));
            this.mViewModel.setmManageParent(arr.getBoolean(R.styleable.ComboBannerFragment_manage_parent, false));
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
            if (show){
                // TODO: Show parent view
            } else {
                // TODO: Hide parent view
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

//        this.mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                Log.v(LOG_TAG, "onAdLoaded");
//                if (((FragmentActivity) activity).getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
//                    boolean viewAdded = false;
//                    if (mPromoView != null){
//                        // Replace promotional view
//                        Helper.replaceView(mPromoView, mAdView);
//                        mPromoView = null;
//                        viewAdded = true;
//                        Log.v(LOG_TAG, "onAdLoaded: replace promo");
//                    } else if (model.viewStubId > 0){
//                        // Replace stub
//                        ViewStub vStub = activity.findViewById(model.viewStubId);
//                        model.viewStubId = -1;
//                        if(vStub != null){
//                            vStub.setLayoutResource(R.layout.stub_view);
//                            View vTemp = vStub.inflate();
//                            Helper.replaceView(vTemp, mAdView);
//                            viewAdded = true;
//                            Log.v(LOG_TAG, "onAdLoaded: replace stub");
//                        }
//                    }
//
//                    if (viewAdded){
//                        mActivity.onCommonViewCreated(mAdView, model.instanceTag);
//                        mAdView.setVisibility(View.VISIBLE);
        this.mViewModel.setIsShowAd(true);
//                    }
//                }
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)){
//                    mAdView.setVisibility(View.GONE);
//                }
//            }
//        });
//
            AdRequest adRequest = new AdRequest
                    .Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            this.mAdView.loadAd(adRequest);

        }
    }

    private void ensurePromoView(){
        Log.v(LOG_TAG, "ensurePromoView");

//        if (this.mPromoView != null){
//            this.mPromoView.setVisibility(View.VISIBLE);
//        } else {
//            final ComboBannerViewModel model = ViewModelProviders.of(this.mActivity).get(ComboBannerViewModel.class);
//            // get the root view
//            View viewToReplace = null;
//            if (this.mAdView != null){
//                // Replace adview
//                viewToReplace = this.mAdView;
//                this.mAdView = null;
//                Log.v(LOG_TAG, "ensurePromoView: replace adview");
//            } else if (model.viewStubId > 0){
//                // Replace stub
//                ViewStub vStub = this.mActivity.findViewById(model.viewStubId);
//                model.viewStubId = -1;
//                if(vStub != null){
//                    vStub.setLayoutResource(R.layout.stub_view);
//                    viewToReplace = vStub.inflate();
//                    Log.v(LOG_TAG, "ensurePromoView: replace stub");
//                }
//            }
//
//            if (viewToReplace != null){
//                LayoutInflater factory = LayoutInflater.from(this.mActivity);
//                this.mPromoView = factory.inflate(R.layout.promote_view, (ViewGroup)viewToReplace.getParent(), false);
//                Helper.replaceView(viewToReplace, this.mPromoView);
//                this.setUpPromoView(model);
//                this.mActivity.onCommonViewCreated(this.mPromoView, model.instanceTag);
//            }
//        }
        this.mViewModel.setIsShowPromo(true);
    }

    private void setUpPromoView(ComboBannerViewModel model){
//        Log.v(LOG_TAG, "setUpPromoView: view == null: " + (this.mPromoView == null));
//
//        final Resources res = this.mActivity.getResources();
//        final Button btnYes = this.mPromoView.findViewById(R.id.btnYes);
//        final Button btnNot = this.mPromoView.findViewById(R.id.btnNot);
//        final TextView tvPrompt = this.mPromoView.findViewById(R.id.tvPromptQuestion);
//
//        String text = res.getString(R.string.common_enjoy_prompt,
//                model.appName == null ? "application": model.appName);
//        tvPrompt.setText(text);
//        btnYes.setText(R.string.common_enjoy_yes);
//        btnNot.setText(R.string.common_enjoy_not);
//
//        btnYes.setOnClickListener((View v) -> {
//            tvPrompt.setText(R.string.common_enjoy_rate_prompt);
//            btnYes.setText(R.string.common_enjoy_rate_yes);
//            btnNot.setText(R.string.common_enjoy_rate_not);
//
//            btnYes.setOnClickListener(v14 -> {
//                PromoteManager.goToPromoScreen(this.mActivity);
//                mPromoView.setVisibility(View.GONE);
//            });
//
//            btnNot.setOnClickListener(v13 -> {
//                PromoteManager.markRateNotNow();
//                mPromoView.setVisibility(View.GONE);
//            });
//        });
//
//        btnNot.setOnClickListener(v -> {
//            tvPrompt.setText(R.string.common_enjoy_feedback_prompt);
//            btnYes.setText(R.string.common_enjoy_feedback_yes);
//            btnNot.setText(R.string.common_enjoy_feedback_not);
//
//            btnYes.setOnClickListener(v12 -> {
//
//                String uriText = "mailto:" + (model.devEmail == null ? "": model.devEmail) + "?subject="
//                        + Uri.encode(model.appName == null ? "application": model.appName) + "&body=" + " ... ";
//                Uri uri = Uri.parse(uriText);
//
//                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
//                sendIntent.setData(uri);
//                this.mActivity.startActivity(Intent.createChooser(sendIntent,
//                        res.getString(R.string.common_error_report_choose_title)));
//
//                PromoteManager.cancelPromoScreenPermanently();
//                mPromoView.setVisibility(View.GONE);
//            });
//
//            btnNot.setOnClickListener(v1 -> {
//                PromoteManager.cancelPromoScreenPermanently();
//                mPromoView.setVisibility(View.GONE);
//            });
//        });
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
//        this.mPromoView = null;
        super.onDestroy();
    }
}
