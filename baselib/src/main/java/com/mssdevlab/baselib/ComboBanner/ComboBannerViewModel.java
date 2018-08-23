package com.mssdevlab.baselib.ComboBanner;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class ComboBannerViewModel extends ViewModel {

    private final ComboBannerUpdateLiveData _updateView;

    public int viewStubId;
    public String adUnitId;
    public String instanceTag;
    public String appName;
    public String devEmail;

    public ComboBannerViewModel() {
        this._updateView = new ComboBannerUpdateLiveData();
    }

    public LiveData<ShowView> updateView(){
        return _updateView;
    }
}
