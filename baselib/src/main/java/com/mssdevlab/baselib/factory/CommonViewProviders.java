package com.mssdevlab.baselib.factory;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.mssdevlab.baselib.BaseActivity;

import java.util.Map;

/**
 * Holds all configurations for application
 */
public class CommonViewProviders {
    private static final String LOG_TAG = "CommonViewProviders";
    private static final Map<String, CommonViewProvider> sConfigurationMap = new ArrayMap<>(4);
    private static MutableLiveData<Boolean> sInitCompleted = new MutableLiveData<Boolean>();

    public static final String ARG_PROVIDER_TAG = "CommonViewProviders.parProviderTag";
    public static final String ARG_INSTANCE_TAG = "CommonViewProviders.parInstanceTag";
    public static final String ARG_VIEWSTUB_TAG = "CommonViewProviders.parViewStubTag";

    public static void addProvider(@NonNull String providerTag, @NonNull CommonViewProvider provider){
        sConfigurationMap.put(providerTag, provider);
        Log.v(LOG_TAG, "CommonViewProvider added: " + providerTag);
    }
    public static void setInitCompleted() {
        Log.v(LOG_TAG, "setInitCompleted");
        sInitCompleted.postValue(true);
    }

    public static void createCommonView(@NonNull final BaseActivity activity, @NonNull final Bundle args) {
        Boolean r = sInitCompleted.getValue();
        if (r != null && r){
            addFragment(activity, args);
        } else {
            // Create observer to add common view fragment
            sInitCompleted.observe(activity, new Observer<Boolean>() {
                @Override
                public void onChanged( final Boolean initCompleted) {
                    if (initCompleted){
                        addFragment(activity, args);
                        sInitCompleted.removeObserver(this);
                    }
                }
            });
        }
    }

    private static void addFragment(@NonNull final BaseActivity activity, @NonNull final Bundle args){
        String instanceTag = args.getString(ARG_INSTANCE_TAG);
        if (instanceTag != null){
            final FragmentManager fragmentManager = activity.getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(instanceTag);
            if (fragment == null) {
                String configTag = args.getString(ARG_PROVIDER_TAG);
                CommonViewProvider provider = sConfigurationMap.get(configTag);
                if (provider != null){
                    fragment = provider.getFragment(args);
                    fragmentManager.beginTransaction().add(fragment, instanceTag)
                            .commit();
                }
            }
        }
    }
}
