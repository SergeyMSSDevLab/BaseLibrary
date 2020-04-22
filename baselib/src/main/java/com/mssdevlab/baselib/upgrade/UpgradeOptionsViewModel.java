package com.mssdevlab.baselib.upgrade;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.SkuDetails;
import com.mssdevlab.baselib.ApplicationMode.AppViewModel;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.Helper;

import java.util.ArrayList;

public class UpgradeOptionsViewModel extends AppViewModel {
    private static final String LOG_TAG = "U_OptionsViewModel";

    private MutableLiveData<SkuDetails> mSelectedDetails = new MutableLiveData<>();

    private UpgradeOptionsAdapter mUpgradeOptionsAdapter;

    public UpgradeOptionsViewModel(){
        mUpgradeOptionsAdapter = new UpgradeOptionsAdapter(R.layout.view_option_upgrade, this);
    }

    void loadSkuDetails(LifecycleOwner lifecycleOwner, Resources res){
        this.getSkuDetails().observe(lifecycleOwner, skuDetails -> {
            ArrayList<UpgradeOptionModel> options = new ArrayList<>();
            for (SkuDetails d : skuDetails){
                options.add(new UpgradeOptionModel(d, res));
            }
            this.mUpgradeOptionsAdapter.setOptions(options);
            this.mUpgradeOptionsAdapter.notifyDataSetChanged();
        });
    }

    public UpgradeOptionsAdapter getAdapter() {
        return mUpgradeOptionsAdapter;
    }

    MutableLiveData<SkuDetails> getSelectedDetails(){
        return this.mSelectedDetails;
    }

    public void onItemClick(@NonNull Integer index) {
        Log.v(LOG_TAG, "onItemClick: " + index.toString());
        Helper.setValue(this.mSelectedDetails, getOptionAt(index).getSkuDetails());
    }

    public UpgradeOptionModel getOptionAt(Integer index) {
        return mUpgradeOptionsAdapter.getItemAt(index);
    }

}
