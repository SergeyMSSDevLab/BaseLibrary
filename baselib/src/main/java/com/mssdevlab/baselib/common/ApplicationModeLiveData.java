package com.mssdevlab.baselib.common;

import android.arch.lifecycle.LiveData;

public class ApplicationModeLiveData extends LiveData<AppMode> {

    private static final ApplicationModeLiveData sAppMode = new ApplicationModeLiveData();

    private ApplicationModeLiveData() {
        this.setValue(AppMode.MODE_DEMO);
        // TODO: complete
    }

    public static LiveData<AppMode> applicationMode(){
        return sAppMode;
    }
}
