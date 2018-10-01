package com.mssdevlab.baselib.factory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import android.util.Log;

import java.util.Map;

/**
 * Holds all common view configurations for an application
 */
public class CommonViewProviders {
    private static final String LOG_TAG = "CommonViewProviders";
    private static final Map<String, CommonViewProvider> sConfigurationMap = new ArrayMap<>(4);
    private static final MutableLiveData<Boolean> sInitCompleted = new MutableLiveData<>();

    public static final String ARG_INSTANCE_TAG = "CommonViewProviders.parInstanceTag";
    public static final String ARG_VIEWSTUB_TAG = "CommonViewProviders.parViewStubTag";

    public static void addProvider(@NonNull String providerTag, @NonNull CommonViewProvider provider){
        sConfigurationMap.put(providerTag, provider);
        Log.v(LOG_TAG, "CommonViewProvider added: " + providerTag);
    }

    public static CommonViewProvider getProvider(@NonNull String providerTag){
        return sConfigurationMap.get(providerTag);
    }

    public static void setInitCompleted() {
        Log.v(LOG_TAG, "setInitCompleted");
        sInitCompleted.postValue(true);
    }

    public static LiveData<Boolean> getInitCompleted()
    {
        return sInitCompleted;
    }
}
