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
    private String mAdUnitId;
    private String mDevEmail;
    private String mAppName;

    public static ComboBannerFragment newInstance() {
        return new ComboBannerFragment();
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onInflate");
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ComboBannerFragment);
        if (arr != null) {
//            for(int i = 0; i < arr.length(); i++){
//                TypedValue val = arr.peekValue(i);
//                Log.v(LOG_TAG, "onInflate attribute: " + val.toString());
//            }
            this.mAdUnitId = arr.getString(R.styleable.ComboBannerFragment_ad_unit_id);
            this.mAppName = arr.getString(R.styleable.ComboBannerFragment_app_name);
            this.mDevEmail = arr.getString(R.styleable.ComboBannerFragment_developer_email);
            arr.recycle();
        }
        ensureViewModel();
        super.onInflate(context, attrs, savedInstanceState);
    }

    // We should call this method as early as possible (onInflate or onAttach)
    private void ensureViewModel(){
        this.mViewModel = ViewModelProviders.of(this).get(ComboBannerViewModel.class);
        this.mViewModel.setAdUnitId(this.mAdUnitId);
        this.mViewModel.setAppName(this.mAppName);
        this.mViewModel.setDevEmail(this.mDevEmail);
        Log.v(LOG_TAG, "ensureViewModel data set:" +
                " adUnitId: " + this.mViewModel.getAdUnitId().getValue() +
                " appName: " + this.mViewModel.getAppName().getValue() +
                " devEmail: " + this.mViewModel.getDevEmail().getValue());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        ComboBannerFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.combo_banner_fragment, container, false);

        binding.setLifecycleOwner(this);
        binding.setViewModel(this.mViewModel);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
//        if (this.mAdView != null) {
//            if (this.mAdView.getVisibility() == View.VISIBLE){
//                this.mAdView.resume();
//            }
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause");
//        if (this.mAdView != null) {
//            if (this.mAdView.getVisibility() == View.VISIBLE){
//                this.mAdView.pause();
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
//        if (this.mAdView != null) {
//            this.mAdView.destroy();
//            this.mAdView = null;
//        }
//        this.mPromoView = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(LOG_TAG, "onActivityCreated");
        mViewModel.getBannerShowMode().observe(getActivity(), this::updateView);

    }

    private void updateView(ShowView showWhat){
        Log.v(LOG_TAG, "getBannerShowMode");

        assert showWhat != null;
        // update UI
        switch (showWhat) {
            case NOTHING:
                Log.v(LOG_TAG, "getBannerShowMode: nothing");
                this.hidePromoView();
                this.hideAdView();
                break;
            case ADS:
                Log.v(LOG_TAG, "getBannerShowMode: ads");
                this.hidePromoView();
                this.ensureAdView();
                break;
            case PROMO:
                Log.v(LOG_TAG, "getBannerShowMode: promo");
                this.hideAdView();
                this.ensurePromoView();
                break;
        }
    }

    private void hideAdView(){
//        if (this.mAdView != null) {
//            this.mAdView.pause();
//            this.mAdView.setVisibility(View.GONE);
//        }
    }

    private void hidePromoView(){
//        if (this.mPromoView != null){
//            this.mPromoView.setVisibility(View.GONE);
//        }
    }

    private void ensureAdView(){
        Log.v(LOG_TAG, "ensureAdView");
        final Activity activity = getActivity();
        //final ComboBannerViewModel model = ViewModelProviders.of(this).get(ComboBannerViewModel.class);

//        if (this.mAdView == null) {
//            this.mAdView = new AdView(this.getActivity());
//            this.mAdView.setAdSize(AdSize.SMART_BANNER);
//            this.mAdView.setAdUnitId(model.mAdUnitId);
//            this.mAdView.setVisibility(View.GONE);
//            Log.v(LOG_TAG, "ensureAdView: mAdView created");
//        }
//
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
//        AdRequest adRequest = new AdRequest
//                .Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build();
//        this.mAdView.loadAd(adRequest);
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
}
