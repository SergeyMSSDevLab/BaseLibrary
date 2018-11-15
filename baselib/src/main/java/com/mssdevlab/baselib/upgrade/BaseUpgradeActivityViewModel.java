package com.mssdevlab.baselib.upgrade;

import android.util.Log;

import com.mssdevlab.baselib.ApplicationMode.AppMode;
import com.mssdevlab.baselib.ApplicationMode.ApplicationData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class BaseUpgradeActivityViewModel extends ViewModel {
    private static final String LOG_TAG = "UpgradeViewModel";

    public BaseUpgradeActivityViewModel() {
        Log.v(LOG_TAG, "Constructor");
    }

    public LiveData<Boolean> getAllowTracking(){
        return Transformations.map(ApplicationData.getApplicationMode(),
                val -> val == AppMode.MODE_EVALUATION);
    }

}
