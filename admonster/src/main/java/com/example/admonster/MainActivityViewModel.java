package com.example.admonster;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mssdevlab.baselib.ApplicationMode.AppMode;
import com.mssdevlab.baselib.ApplicationMode.AppViewModel;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.common.PromoteManager;
import com.mssdevlab.baselib.common.ShowView;

public class MainActivityViewModel extends ViewModel {
    private static final String LOG_TAG = "MainActivityViewModel";

    private AppViewModel mAppViewModel;

    public MainActivityViewModel() {
        Log.v(LOG_TAG, "Constructor");
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(BaseApplication.getInstance());
        this.mAppViewModel = factory.create(AppViewModel.class);
    }

    public void setShowMode(boolean promo){
        PromoteManager.setShowPromo(promo);
    }

    public void setAppMode(AppMode mode){
        AppViewModel.setApplicationMode(mode);
    }

    public LiveData<Boolean> getIsModeDemo(){
        return Transformations.map(this.mAppViewModel.getApplicationMode(),
                val -> val == AppMode.MODE_DEMO);
    }

    public LiveData<Boolean> getIsModeEval(){
        return Transformations.map(this.mAppViewModel.getApplicationMode(),
                val -> val == AppMode.MODE_EVALUATION);
    }

    public LiveData<Boolean> getIsModeNoAds(){
        return Transformations.map(this.mAppViewModel.getApplicationMode(),
                val -> val == AppMode.MODE_NO_ADS);
    }

    public LiveData<Boolean> getIsModePro(){
        return Transformations.map(this.mAppViewModel.getApplicationMode(),
                val -> val == AppMode.MODE_PRO);
    }

    public LiveData<String> getAppModeStr() {
        return Transformations.map(this.mAppViewModel.getApplicationMode(),
                val -> val == AppMode.MODE_DEMO ? "Demo":
                        val == AppMode.MODE_EVALUATION ? "Eval" :
                                val == AppMode.MODE_NO_ADS ? "NoAds" :
                                        val == AppMode.MODE_PRO ? "Pro" :
                                                "None");
    }

    public LiveData<Boolean> getIsShowPromo(){
        return Transformations.map(AppViewModel.getBannerShowMode(),
                val -> val == ShowView.PROMO);
    }

    public LiveData<Boolean> getIsShowAds(){
        return Transformations.map(AppViewModel.getBannerShowMode(),
                val -> val == ShowView.ADS);
    }

    public LiveData<Boolean> getIsShowNothing(){
        return Transformations.map(AppViewModel.getBannerShowMode(),
                val -> val == ShowView.NOTHING);
    }

    public LiveData<String> getShowModeStr() {
        return Transformations.map(AppViewModel.getBannerShowMode(),
                val -> val == ShowView.NOTHING ? "Nothing":
                        val == ShowView.PROMO ? "Promo" :
                                val == ShowView.ADS ? "Ads" :
                                            "None");
    }
}
