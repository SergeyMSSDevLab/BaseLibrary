package com.mssdevlab.baselib.ComboBanner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mssdevlab.baselib.factory.CommonViewModel;

public class ComboBannerViewModel extends CommonViewModel {

    private MediatorLiveData<Integer> _updateCommonView = new MediatorLiveData<>();

    private ComboBannerUpdateLiveData shouldUpdate = new ComboBannerUpdateLiveData();

    public ComboBannerViewModel() {
        _updateCommonView.addSource(shouldUpdate, showView -> {
            //_updateCommonView.removeSource(shouldUpdate);
            int val = 0;
            if (_updateCommonView.getValue() != null){
                val = _updateCommonView.getValue();
            }
            _updateCommonView.setValue(++val);
        });
    }

    public ComboBannerUpdateLiveData updateView(){
        return  shouldUpdate;
    }

    public void checkState(){
        this.shouldUpdate.checkState();
    }

    @Override
    public LiveData<Integer> updateCommonView() {
        return _updateCommonView;
    }
}
