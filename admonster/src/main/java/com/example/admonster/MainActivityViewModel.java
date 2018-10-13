package com.example.admonster;

import android.util.Log;

import com.mssdevlab.baselib.common.ShowView;
import com.mssdevlab.baselib.common.AppMode;
import com.mssdevlab.baselib.common.ApplicationData;
import com.mssdevlab.baselib.common.PromoteManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private static final String LOG_TAG = "MainActivityViewModel";

    public MainActivityViewModel() {
        Log.v(LOG_TAG, "Constructor");
    }

    public void setShowMode(boolean promo){
        PromoteManager.setShowPromo(promo);
    }

    public void setAppMode(AppMode mode){
        ApplicationData.setMode(mode);
    }

    public LiveData<Boolean> getIsModeDemo(){
        return Transformations.map(ApplicationData.getApplicationMode(),
                val -> val == AppMode.MODE_DEMO);
    }

    public LiveData<Boolean> getIsModeEval(){
        return Transformations.map(ApplicationData.getApplicationMode(),
                val -> val == AppMode.MODE_EVALUATION);
    }

    public LiveData<Boolean> getIsModeNoAds(){
        return Transformations.map(ApplicationData.getApplicationMode(),
                val -> val == AppMode.MODE_NO_ADS);
    }

    public LiveData<Boolean> getIsModePro(){
        return Transformations.map(ApplicationData.getApplicationMode(),
                val -> val == AppMode.MODE_PRO);
    }

    public LiveData<String> getAppModeStr() {
        return Transformations.map(ApplicationData.getApplicationMode(),
                val -> val == AppMode.MODE_DEMO ? "Demo":
                        val == AppMode.MODE_EVALUATION ? "Eval" :
                                val == AppMode.MODE_NO_ADS ? "NoAds" :
                                        val == AppMode.MODE_PRO ? "Pro" :
                                                "None");
    }

    public LiveData<Boolean> getIsShowPromo(){
        return Transformations.map(ApplicationData.getBannerShowMode(),
                val -> val == ShowView.PROMO);
    }

    public LiveData<Boolean> getIsShowAds(){
        return Transformations.map(ApplicationData.getBannerShowMode(),
                val -> val == ShowView.ADS);
    }

    public LiveData<Boolean> getIsShowNothing(){
        return Transformations.map(ApplicationData.getBannerShowMode(),
                val -> val == ShowView.NOTHING);
    }

    public LiveData<String> getShowModeStr() {
        return Transformations.map(ApplicationData.getBannerShowMode(),
                val -> val == ShowView.NOTHING ? "Nothing":
                        val == ShowView.PROMO ? "Promo" :
                                val == ShowView.ADS ? "Ads" :
                                            "None");
    }
}
