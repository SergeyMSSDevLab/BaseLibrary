package com.mssdevlab.baselib;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewStub;

import com.mssdevlab.baselib.CommonViews.Configurator;
import com.mssdevlab.baselib.CommonViews.ConfiguratorListener;
import com.mssdevlab.baselib.CommonViews.ViewHolderBase;

/**
 * Manages common view lifecycle
 */
@SuppressWarnings("unused")
public class CommonViewManagerFragment extends Fragment implements ConfiguratorListener {
    private static final String LOG_TAG = CommonViewManagerFragment.class.getCanonicalName();
    private static final String ARG_CONFIG_TAG = "commonViewManagerFragment.param1";
    private static final String ARG_INSTANCE_TAG = "commonViewManagerFragment.param2";

    private String mConfigurationTag;
    private String mInstanceTag;
    private ViewStub mPlaceHolder;
    private ViewHolderBase mViewHolder;


    public static CommonViewManagerFragment newInstance(final String configTag, final String instanceTag) {
        Log.v(LOG_TAG, "newInstance entered.");
        CommonViewManagerFragment fragment = new CommonViewManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONFIG_TAG, configTag);
        args.putString(ARG_INSTANCE_TAG, instanceTag);
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
            this.mConfigurationTag = args.getString(ARG_CONFIG_TAG);
            this.mInstanceTag = args.getString(ARG_INSTANCE_TAG);
        }

        if (mViewHolder == null)
            Configurator.setListener(this.mConfigurationTag, this.mInstanceTag, this);
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "onResume entered.");
        super.onResume();
        if (this.mViewHolder != null)
            this.mViewHolder.onResume();
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "onPause entered.");
        if (this.mViewHolder != null)
            this.mViewHolder.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, "onDestroy entered.");
        if (this.mViewHolder != null)
            this.mViewHolder.onDestroy();
        super.onDestroy();
    }

    public void setPlaceHolder(@Nullable ViewStub placeHolder){
        Log.v(LOG_TAG, "setPlaceHolder entered.");
        this.mPlaceHolder = placeHolder;
        if (mViewHolder == null)
            Configurator.setListener(this.mConfigurationTag, this.mInstanceTag, this);
    }

    @Override
    public void onConfigureCompleted(@NonNull ViewHolderBase viewHolder) {
        Log.v(LOG_TAG, "onConfigureCompleted entered.");
        this.mViewHolder = viewHolder;
        viewHolder.setPlaceHolder(this.mPlaceHolder, getActivity(), this.mInstanceTag);
    }
}
