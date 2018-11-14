package com.mssdevlab.baselib.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.os.Looper;
import android.util.Log;

public class ApplicationData {
    private static final String LOG_TAG = "ApplicationData";

    private static final MutableLiveData<AppMode> sAppMode = new MutableLiveData<>();
    private static final BannerShowModeLiveData sBannerShowMode = new BannerShowModeLiveData();

    private ApplicationData() {
    }

    public static void setApplicationMode(AppMode val){
        Log.v(LOG_TAG, "setValueInternal: " + val);
        if (Looper.myLooper() == Looper.getMainLooper()){
            sAppMode.setValue(val);
        } else {
            sAppMode.postValue(val);
        }
    }

    @NonNull
    public static AppMode getCurrentApplicationMode(){
        AppMode mode = sAppMode.getValue();
        if (mode == null){
            mode = AppMode.MODE_DEMO;
        }
        return mode;
    }

    public static LiveData<AppMode> getApplicationMode(){
        Log.v(LOG_TAG, "getApplicationMode hasActiveObservers: " + sAppMode.hasActiveObservers());
        return sAppMode;
    }

    public static LiveData<ShowView> getBannerShowMode(){
        Log.v(LOG_TAG, "getBannerShowMode hasActiveObservers: " + sBannerShowMode.hasActiveObservers());
        return sBannerShowMode;
    }
}
