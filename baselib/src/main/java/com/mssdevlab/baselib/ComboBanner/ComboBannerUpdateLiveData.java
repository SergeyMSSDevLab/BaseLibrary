package com.mssdevlab.baselib.ComboBanner;

import android.arch.lifecycle.LiveData;
import android.util.Log;

class ComboBannerUpdateLiveData extends LiveData<ShowView> {
    private static final String LOG_TAG = "ComboBannerU_LiveData";

    ComboBannerUpdateLiveData() {
        this.setValue(ShowView.NOTHING);
        Log.v(LOG_TAG, "Constructor");
    }

    @Override
    protected void onActive() {
        Log.v(LOG_TAG, "onActive");
        this.setValue(ShowView.PROMO); // TODO: replace with real checking
    }

}
