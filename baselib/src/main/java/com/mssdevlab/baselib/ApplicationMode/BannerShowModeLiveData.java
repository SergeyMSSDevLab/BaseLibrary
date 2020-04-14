package com.mssdevlab.baselib.ApplicationMode;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.common.PromoteManager;
import com.mssdevlab.baselib.common.ShowView;

public class BannerShowModeLiveData extends MediatorLiveData<ShowView> {
    private static final String LOG_TAG = "BannerShowModeLiveData";

    public BannerShowModeLiveData() {
        Log.v(LOG_TAG, "Constructor");
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(BaseApplication.getInstance());
        AppViewModel viewModel = factory.create(AppViewModel.class);

        this.addSource(PromoteManager.getShowPromo(),
                aBoolean -> onAppMode(viewModel.getApplicationMode().getValue()) );
        this.addSource(viewModel.getApplicationMode(), this::onAppMode);
    }

    private void onAppMode(AppMode newMode){
        Boolean curValueIsPromo = PromoteManager.getShowPromo().getValue();
        if (curValueIsPromo == null || !curValueIsPromo){
            if (newMode != null){
                if (newMode.ordinal() < AppMode.MODE_NO_ADS.ordinal()){
                    this.setValueInternal(ShowView.ADS);
                } else {
                    this.setValueInternal(ShowView.NOTHING);
                }
            } else {
                this.setValueInternal(ShowView.ADS);
            }
        } else {
            this.setValueInternal(ShowView.PROMO);
        }
    }

    private void setValueInternal(ShowView val){
        if (Looper.myLooper() == Looper.getMainLooper()){
            this.setValue(val);
        } else {
            this.postValue(val);
        }
    }
}
