package com.mssdevlab.baselib.upgrade;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.mssdevlab.baselib.R;

import java.util.List;

public class UpgradeOptionsViewModel extends ViewModel {
    private static final String LOG_TAG = "U_OptionsViewModel";

    private UpgradeOptionsAdapter mUpgradeOptionsAdapter;

    public UpgradeOptionsViewModel(){
        mUpgradeOptionsAdapter = new UpgradeOptionsAdapter(R.layout.view_option_upgrade, this);
    }

    public UpgradeOptionsAdapter getAdapter() {
        return mUpgradeOptionsAdapter;
    }

    public void setUpgradeOptionsInAdapter(List<UpgradeOptionModel> options) {
        this.mUpgradeOptionsAdapter.setOptions(options);
        this.mUpgradeOptionsAdapter.notifyDataSetChanged();
    }

    public void onItemClick(@NonNull Integer index) {
        Log.v(LOG_TAG, "onItemClick: " + index.toString());
        //UpgradeOptionModel db = getOptionAt(index);
    }

    public UpgradeOptionModel getOptionAt(Integer index) {
        return mUpgradeOptionsAdapter.getItemAt(index);
    }

}
