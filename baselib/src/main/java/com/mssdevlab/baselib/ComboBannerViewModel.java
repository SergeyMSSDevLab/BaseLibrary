package com.mssdevlab.baselib;

import android.os.Looper;

import com.mssdevlab.baselib.combobanner.ComboBannerUpdateLiveData;
import com.mssdevlab.baselib.combobanner.ShowView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class ComboBannerViewModel extends ViewModel {

    private final ComboBannerUpdateLiveData _updateView;

    public int viewStubId;  // TODO: remove
    public String instanceTag;  // TODO: remove
    private final MutableLiveData<String> mAdUnitId = new MutableLiveData<>();
    private final MutableLiveData<String> mAppName = new MutableLiveData<>();
    private final MutableLiveData<String> mDevEmail = new MutableLiveData<>();

    public ComboBannerViewModel() {
        this._updateView = new ComboBannerUpdateLiveData();
    }

    public LiveData<ShowView> updateView(){
        return _updateView;
    }

    public LiveData<String> getAdUnitId() { return this.mAdUnitId; }

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
