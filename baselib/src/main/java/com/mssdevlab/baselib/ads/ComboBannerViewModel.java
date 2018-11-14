package com.mssdevlab.baselib.ads;

import android.os.Looper;

import com.google.android.gms.ads.AdSize;
import com.mssdevlab.baselib.ApplicationMode.ApplicationData;
import com.mssdevlab.baselib.common.ShowView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class ComboBannerViewModel extends ViewModel {

    private final MutableLiveData<String> mAppName = new MutableLiveData<>();
    private final MutableLiveData<String> mDevEmail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsShowPromo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsShowAd = new MutableLiveData<>();
    private AdSize mAdSize = AdSize.BANNER; // TODO: get from xml attributes
    private String mAdUnitId;
    private boolean mManageParent = false;

    public ComboBannerViewModel() {

    }

    public LiveData<ShowView> getBannerShowMode(){
        return ApplicationData.getBannerShowMode();
    }

    public String getAdUnitId() { return this.mAdUnitId; }

    public void setAdUnitId(String value){ this.mAdUnitId = value; }

    public boolean getManageParent() { return this.mManageParent; }

    public void setManageParent(boolean val) { this.mManageParent = val; }

    public LiveData<String> getAppName() { return this.mAppName; }

    public void setAppName(String value){ this.setValue(this.mAppName, value); }

    public LiveData<String> getDevEmail() { return this.mDevEmail; }

    public void setDevEmail(String value){ this.setValue(this.mDevEmail, value); }

    public LiveData<Boolean> getIsShowPromo() { return this.mIsShowPromo; }

    public void setIsShowPromo(Boolean val) { this.setValue(this.mIsShowPromo, val);}

    public LiveData<Boolean> getIsShowAd() { return this.mIsShowAd; }

    public void setIsShowAd(Boolean val) { this.setValue(this.mIsShowAd, val);}

    public AdSize getAdSize() { return this.mAdSize; }

    public void setAdSize(AdSize val) { this.mAdSize = val;}

    private <T> void setValue(MutableLiveData<T> field, T value){
        if (Looper.myLooper() == Looper.getMainLooper()){
            field.setValue(value);
        } else {
            field.postValue(value);
        }
    }
}
