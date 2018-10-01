package com.mssdevlab.baselib.common;

import androidx.lifecycle.LiveData;
import android.os.Looper;

public class ApplicationModeLiveData extends LiveData<AppMode> {

    private static final ApplicationModeLiveData sAppMode = new ApplicationModeLiveData();

    private ApplicationModeLiveData() {
        this.setValueInternal(AppMode.MODE_DEMO);
        // TODO: complete
    }

    private void setValueInternal(AppMode val){
        if (Looper.myLooper() == Looper.getMainLooper()){
            this.setValue(val);
        } else {
            this.postValue(val);
        }
    }

    public static LiveData<AppMode> applicationMode(){
        return sAppMode;
    }
}
