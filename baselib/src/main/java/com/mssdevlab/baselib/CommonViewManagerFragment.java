package com.mssdevlab.baselib;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewStub;

import com.mssdevlab.baselib.CommonViews.Configurator;
import com.mssdevlab.baselib.CommonViews.ConfiguratorListener;
import com.mssdevlab.baselib.CommonViews.CommonViewProvider;

/**
 * Manages common view lifecycle
 */
@SuppressWarnings("unused")
public class CommonViewManagerFragment extends Fragment implements ConfiguratorListener {
    private static final String LOG_TAG = CommonViewManagerFragment.class.getCanonicalName();
    private static final String ARG_CONFIG_TAG = "commonViewManagerFragment.param1";
    private static final String ARG_INSTANCE_TAG = "commonViewManagerFragment.param2";
    private static final String ARG_VIEWSTUB_TAG = "commonViewManagerFragment.param3";

    private String mConfigurationTag;
    private String mInstanceTag;
    private ViewStub mViewStub = null;
    private CommonViewProvider mViewProvider;

    public static CommonViewManagerFragment newInstance(@NonNull final String configTag, @NonNull final String instanceTag, @IdRes int idViewStub) {
        Log.v(LOG_TAG, "newInstance entered.");
        CommonViewManagerFragment fragment = new CommonViewManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONFIG_TAG, configTag);
        args.putString(ARG_INSTANCE_TAG, instanceTag);
        args.putInt(ARG_VIEWSTUB_TAG, idViewStub);
        fragment.setArguments(args);
        return fragment;
    }

    public CommonViewManagerFragment() {
        Log.v(LOG_TAG, "Constructor entered.");
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onActivityCreated entered.");
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            this.mConfigurationTag = args.getString(ARG_CONFIG_TAG);
            this.mInstanceTag = args.getString(ARG_INSTANCE_TAG);
            if (args.containsKey(ARG_VIEWSTUB_TAG)){
                int id = args.getInt(ARG_VIEWSTUB_TAG);
                this.mViewStub = (ViewStub) this.getActivity().findViewById(id);
            }
        }
        Log.v(LOG_TAG, "onActivityCreated mViewStub != null: " + (this.mViewStub != null));

        // Configurator initializes asynchronously and we have to wait callback.
        Configurator.setListener(this.mConfigurationTag, this.mInstanceTag, this);
    }

    @Override
    public void onConfigureCompleted(@NonNull CommonViewProvider viewProvider) {
        Log.v(LOG_TAG, "onConfigureCompleted entered.");
        this.mViewProvider = viewProvider;
        this.mViewProvider.setActivity(getActivity(), this.mInstanceTag);
        if (this.mViewStub != null)
            this.mViewProvider.setViewStub(this.mViewStub);
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "onResume entered.");
        super.onResume();
        if (this.mViewProvider != null)
            this.mViewProvider.onResume();
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "onPause entered.");
        if (this.mViewProvider != null)
            this.mViewProvider.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, "onDestroy entered.");
        Configurator.removeListener(this.mConfigurationTag, this.mInstanceTag);
        if (this.mViewProvider != null)
            this.mViewProvider.onDestroy();
        super.onDestroy();
    }

    public boolean isViewAvailable() {
        if (this.mViewProvider != null) {
            return this.mViewProvider.isViewAvailable();
        } else {
            return false;
        }
    }
}
