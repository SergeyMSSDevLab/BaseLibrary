package com.mssdevlab.baselib.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.os.Looper;
import android.util.Log;

public class ApplicationModeLiveData extends LiveData<AppMode> {
    private static final String LOG_TAG = "ApplicationModeLiveData";

    private static final MutableLiveData<AppMode> sAppMode = new MutableLiveData<>();

    private ApplicationModeLiveData() {
    }

    public static void setMode(AppMode val){
        Log.v(LOG_TAG, "setValueInternal: " + val);
        if (Looper.myLooper() == Looper.getMainLooper()){
            sAppMode.setValue(val);
        } else {
            sAppMode.postValue(val);
        }
    }

    public static LiveData<AppMode> applicationMode(){
        Log.v(LOG_TAG, "applicationMode hasActiveObservers: " + sAppMode.hasActiveObservers());
        return sAppMode;
    }
}
