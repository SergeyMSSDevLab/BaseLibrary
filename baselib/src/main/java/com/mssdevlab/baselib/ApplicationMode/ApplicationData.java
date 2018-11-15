package com.mssdevlab.baselib.ApplicationMode;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.os.Looper;
import android.util.Log;

import com.mssdevlab.baselib.common.BannerShowModeLiveData;
import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.common.ShowView;

public class ApplicationData {
    private static final String LOG_TAG = "ApplicationData";

    private static final MutableLiveData<AppMode> sAppMode = new MutableLiveData<>();
    private static final BannerShowModeLiveData sBannerShowMode = new BannerShowModeLiveData();
    private static final MutableLiveData<Boolean> sAllowTrackingParticipated = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> sAllowTracking = new MutableLiveData<>();

    private ApplicationData() {
    }

    public static LiveData<Boolean> getAllowTracking(){
        return sAllowTracking;
    }

    public static void setAllowTracking(Boolean val){
        Helper.setValue(sAllowTracking, val);
    }

    public static LiveData<Boolean> getAllowTrackingParticipated(){
        return sAllowTrackingParticipated;
    }

    public static void setAllowTrackingParticipated(Boolean val){
        Helper.setValue(sAllowTrackingParticipated, val);
    }

    public static void setApplicationMode(AppMode val){
        Helper.setValue(sAppMode, val);
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
