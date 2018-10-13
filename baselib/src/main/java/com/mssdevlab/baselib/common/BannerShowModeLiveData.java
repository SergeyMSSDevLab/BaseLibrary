package com.mssdevlab.baselib.common;

import androidx.lifecycle.MediatorLiveData;
import android.os.Looper;
import android.util.Log;

public class BannerShowModeLiveData extends MediatorLiveData<ShowView> {
    private static final String LOG_TAG = "BannerShowModeLiveData";

    public BannerShowModeLiveData() {
        Log.v(LOG_TAG, "Constructor");

        this.addSource(PromoteManager.getShowPromo(), this::onShowPromoDialog);
        this.addSource(ApplicationData.getApplicationMode(), this::onAppMode);
    }

    private void onShowPromoDialog(Boolean isTimeToShow){
        if (isTimeToShow != null && isTimeToShow){
            this.setValueInternal(ShowView.PROMO);
        } else {
            this.setValueInternal(ShowView.NOTHING);
            this.onAppMode(ApplicationData.getApplicationMode().getValue());
        }
    }

    private void onAppMode(AppMode newMode){
        Boolean curValueIsPromo = PromoteManager.getShowPromo().getValue();
        if (curValueIsPromo == null || !curValueIsPromo){
            if (newMode != null){
                if (newMode == AppMode.MODE_DEMO){
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
