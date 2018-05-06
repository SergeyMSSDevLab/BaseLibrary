package com.mssdevlab.baselib.ComboBanner;

import android.arch.lifecycle.LiveData;
import android.util.Log;

public class ComboBannerUpdateLiveData extends LiveData<ComboBannerUpdateLiveData.ShowView> {
    public enum ShowView {
        NOTHING, PROMO, ADS
    }

    private static final String LOG_TAG = "ComboBannerU_LiveData";

    ComboBannerUpdateLiveData() {
        this.setValue(ShowView.NOTHING);
        Log.v(LOG_TAG, "Constructor");
    }

    @Override
    protected void onActive() {
        Log.v(LOG_TAG, "onActive");
        this.checkState();
    }

    public void checkState(){
        Log.v(LOG_TAG, "checkState");
        this.setValue(ShowView.ADS); // TODO: replace with real checking
    }
}
