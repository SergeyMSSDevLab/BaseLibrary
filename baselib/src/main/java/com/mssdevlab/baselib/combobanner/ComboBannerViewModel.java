package com.mssdevlab.baselib.combobanner;

import android.os.Looper;
import android.util.Log;

import com.mssdevlab.baselib.common.ApplicationData;
import com.mssdevlab.baselib.common.BannerShowModeLiveData;
import com.mssdevlab.baselib.common.ShowView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class ComboBannerViewModel extends ViewModel {
    private static final String LOG_TAG = "ComboBannerViewModel";

    public int viewStubId;  // TODO: remove
    public String instanceTag;  // TODO: remove
    private final MutableLiveData<String> mAdUnitId = new MutableLiveData<>();
    private final MutableLiveData<String> mAppName = new MutableLiveData<>();
    private final MutableLiveData<String> mDevEmail = new MutableLiveData<>();

    public ComboBannerViewModel() {

    }

    public LiveData<ShowView> getBannerShowMode(){
        return ApplicationData.getBannerShowMode();
    }

    public LiveData<String> getAdUnitId() {
        Log.v(LOG_TAG, "getAdUnitId: " + this.mAdUnitId.getValue());
        return this.mAdUnitId;
    }

    public LiveData<String> getAppName() { return this.mAppName; }

    public LiveData<String> getDevEmail() { return this.mDevEmail; }

    public void setAdUnitId(String value){ this.setValue(this.mAdUnitId, value); }

    public void setAppName(String value){ this.setValue(this.mAppName, value); }

    public void setDevEmail(String value){ this.setValue(this.mDevEmail, value); }

    private <T> void setValue(MutableLiveData<T> field, T value){
        if (Looper.myLooper() == Looper.getMainLooper()){
            field.setValue(value);
        } else {
            field.postValue(value);
        }
    }
}
