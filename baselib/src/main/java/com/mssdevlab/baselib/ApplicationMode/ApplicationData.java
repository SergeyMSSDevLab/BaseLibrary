package com.mssdevlab.baselib.ApplicationMode;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mssdevlab.baselib.common.Helper;

class ApplicationData {
    private static final String LOG_TAG = "ApplicationData";

    private static final MutableLiveData<AppMode> sAppMode = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> sAllowTrackingParticipated = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> sAllowTracking = new MutableLiveData<>();
    private static final MutableLiveData<Long> sExpireTime = new MutableLiveData<>();

    private ApplicationData() {
    }

    @NonNull
    static LiveData<Long> getExpireTime(){
        return sExpireTime;
    }
    static void setExpireTime(Long val){
        Helper.setValue(sExpireTime, val);
    }

    @NonNull
    static LiveData<Boolean> getAllowTracking(){
        return sAllowTracking;
    }
    static void setAllowTracking(Boolean val){
        Helper.setValue(sAllowTracking, val);
    }

    @NonNull
    static LiveData<Boolean> getAllowTrackingParticipated(){
        return sAllowTrackingParticipated;
    }
    static void setAllowTrackingParticipated(Boolean val){
        Helper.setValue(sAllowTrackingParticipated, val);
    }

    static void setApplicationMode(AppMode val){
        Helper.setValue(sAppMode, val);
    }

    @NonNull
    static LiveData<AppMode> getApplicationMode(){
        Log.v(LOG_TAG, "getApplicationMode hasActiveObservers: " + sAppMode.hasActiveObservers());
        return sAppMode;
    }
}
