package com.example.admonster;

import android.util.Log;

import com.mssdevlab.baselib.common.AppMode;
import com.mssdevlab.baselib.common.ApplicationModeLiveData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private static final String LOG_TAG = "MainActivityViewModel";

    public MainActivityViewModel() {
        Log.v(LOG_TAG, "Constructor");
    }

    public void setAppMode(AppMode mode){
        ApplicationModeLiveData.setMode(mode);
    }

    public LiveData<Boolean> getIsModeDemo(){
        return Transformations.map(ApplicationModeLiveData.applicationMode(),
                val -> val == AppMode.MODE_DEMO);
    }

    public LiveData<Boolean> getIsModeEval(){
        return Transformations.map(ApplicationModeLiveData.applicationMode(),
                val -> val == AppMode.MODE_EVALUATION);
    }

    public LiveData<Boolean> getIsModeNoAds(){
        return Transformations.map(ApplicationModeLiveData.applicationMode(),
                val -> val == AppMode.MODE_NO_ADS);
    }

    public LiveData<Boolean> getIsModePro(){
        return Transformations.map(ApplicationModeLiveData.applicationMode(),
                val -> val == AppMode.MODE_PRO);
    }

    public LiveData<String> getAppModeStr() {
        return Transformations.map(ApplicationModeLiveData.applicationMode(),
                val -> "Current mode is: " + (val == AppMode.MODE_DEMO ? "Demo":
                        val == AppMode.MODE_EVALUATION ? "Eval" :
                                val == AppMode.MODE_NO_ADS ? "NoAds" :
                                        val == AppMode.MODE_PRO ? "Pro" :
                                                "Unknown"));
    }
}
