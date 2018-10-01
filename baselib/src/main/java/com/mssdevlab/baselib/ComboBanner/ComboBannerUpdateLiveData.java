package com.mssdevlab.baselib.ComboBanner;

import androidx.lifecycle.MediatorLiveData;
import android.os.Looper;
import android.util.Log;

import com.mssdevlab.baselib.common.AppMode;
import com.mssdevlab.baselib.common.ApplicationModeLiveData;
import com.mssdevlab.baselib.common.PromoteManager;

class ComboBannerUpdateLiveData extends MediatorLiveData<ShowView> {
    private static final String LOG_TAG = "ComboBannerU_LiveData";

    ComboBannerUpdateLiveData() {
        Log.v(LOG_TAG, "Constructor");
        this.setValueInternal(ShowView.NOTHING);

        this.addSource(PromoteManager.showPromoDialog(), this::onShowPromoDialog);
        this.addSource(ApplicationModeLiveData.applicationMode(), this::onAppMode);
    }

    private void onShowPromoDialog(Boolean isTimeToShow){
        if (isTimeToShow != null && isTimeToShow){
            this.setValueInternal(ShowView.PROMO);
        } else {
            this.setValueInternal(ShowView.NOTHING);
            this.onAppMode(ApplicationModeLiveData.applicationMode().getValue());
        }
    }

    private void onAppMode(AppMode newMode){
        Boolean curValueIsPromo = PromoteManager.showPromoDialog().getValue();
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
