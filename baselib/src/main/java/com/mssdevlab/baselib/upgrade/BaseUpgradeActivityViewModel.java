package com.mssdevlab.baselib.upgrade;

import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.mssdevlab.baselib.ApplicationMode.AppMode;
import com.mssdevlab.baselib.ApplicationMode.AppModeManager;
import com.mssdevlab.baselib.ApplicationMode.ApplicationData;
import com.mssdevlab.baselib.R;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class BaseUpgradeActivityViewModel extends ViewModel {
    private static final String LOG_TAG = "UpgradeViewModel";

    public BaseUpgradeActivityViewModel() {
        Log.v(LOG_TAG, "Constructor");
    }

    public LiveData<Integer> getAllowTrackingText(){
        return Transformations.map(ApplicationData.getAllowTrackingParticipated(),
            val -> {
                if (val) {
                    return R.string.bl_upgrade_track_value_done;
                }
                return R.string.bl_upgrade_track_value;
            });
    }

    public LiveData<Boolean> getAllowTracking(){
        return ApplicationData.getAllowTracking();
    }

    public void setAllowTracking(View view){
        AppModeManager.setAllowTracking(((Switch) view).isChecked());
    }

    public void inviteFriend(){

    }
}
