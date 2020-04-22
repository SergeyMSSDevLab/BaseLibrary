package com.example.admonster;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.mssdevlab.baselib.ApplicationMode.AppMode;
import com.mssdevlab.baselib.ApplicationMode.AppViewModel;
import com.mssdevlab.baselib.common.PromoteManager;
import com.mssdevlab.baselib.common.ShowView;

public class MainActivityViewModel extends AppViewModel {
    private static final String LOG_TAG = "MainActivityViewModel";

    public MainActivityViewModel() {
        Log.v(LOG_TAG, "Constructor");
    }

    public void setShowMode(boolean promo){
        PromoteManager.setShowPromo(promo);
    }

    public LiveData<Boolean> getIsModeDemo(){
        return Transformations.map(this.getApplicationMode(),
                val -> val == AppMode.MODE_DEMO);
    }

    public LiveData<Boolean> getIsModeEval(){
        return Transformations.map(this.getApplicationMode(),
                val -> val == AppMode.MODE_EVALUATION);
    }

    public LiveData<Boolean> getIsModeNoAds(){
        return Transformations.map(this.getApplicationMode(),
                val -> val == AppMode.MODE_NO_ADS);
    }

    public LiveData<Boolean> getIsModePro(){
        return Transformations.map(this.getApplicationMode(),
                val -> val == AppMode.MODE_PRO);
    }

    public LiveData<String> getAppModeStr() {
        return Transformations.map(this.getApplicationMode(),
                val -> val == AppMode.MODE_DEMO ? "Demo":
                        val == AppMode.MODE_EVALUATION ? "Eval" :
                                val == AppMode.MODE_NO_ADS ? "NoAds" :
                                        val == AppMode.MODE_PRO ? "Pro" :
                                                "None");
    }

    public LiveData<Boolean> getIsShowPromo(){
        return Transformations.map(this.getBannerShowMode(),
                val -> val == ShowView.PROMO);
    }

    public LiveData<Boolean> getIsShowAds(){
        return Transformations.map(this.getBannerShowMode(),
                val -> val == ShowView.ADS);
    }

    public LiveData<Boolean> getIsShowNothing(){
        return Transformations.map(this.getBannerShowMode(),
                val -> val == ShowView.NOTHING);
    }

    public LiveData<String> getShowModeStr() {
        return Transformations.map(this.getBannerShowMode(),
                val -> val == ShowView.NOTHING ? "Nothing":
                        val == ShowView.PROMO ? "Promo" :
                                val == ShowView.ADS ? "Ads" :
                                            "None");
    }
}
