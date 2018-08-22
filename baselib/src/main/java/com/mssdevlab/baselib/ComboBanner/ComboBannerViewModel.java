package com.mssdevlab.baselib.ComboBanner;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.mssdevlab.baselib.factory.CommonViewModel;

public class ComboBannerViewModel extends CommonViewModel {

    private final MediatorLiveData<Integer> _updateCommonView = new MediatorLiveData<>();

    private final ComboBannerUpdateLiveData shouldUpdate = new ComboBannerUpdateLiveData();

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
