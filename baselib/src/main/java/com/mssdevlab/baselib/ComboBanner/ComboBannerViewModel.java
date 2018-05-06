package com.mssdevlab.baselib.ComboBanner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

public class ComboBannerViewModel extends AndroidViewModel {

    private ComboBannerUpdateLiveData shouldUpdate;

    public ComboBannerViewModel(Application ctx) {
        super(ctx);

        this.shouldUpdate = new ComboBannerUpdateLiveData();
    }

    public ComboBannerUpdateLiveData updateView(){
        return  shouldUpdate;
    }

    public void checkState(){
        this.shouldUpdate.checkState();
    }
}
