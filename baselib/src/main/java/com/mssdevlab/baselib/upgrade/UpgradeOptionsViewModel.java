package com.mssdevlab.baselib.upgrade;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.android.billingclient.api.SkuDetails;
import com.mssdevlab.baselib.Billing.BillingData;
import com.mssdevlab.baselib.R;

import java.util.ArrayList;

public class UpgradeOptionsViewModel extends ViewModel {
    private static final String LOG_TAG = "U_OptionsViewModel";

    private UpgradeOptionsAdapter mUpgradeOptionsAdapter;

    public UpgradeOptionsViewModel(){
        mUpgradeOptionsAdapter = new UpgradeOptionsAdapter(R.layout.view_option_upgrade, this);
    }

    void attachActivity(LifecycleOwner lifecycleOwner, Resources res){
        BillingData.getSkuDetails().observe(lifecycleOwner, skuDetails -> {
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

    public void onItemClick(@NonNull Integer index) {
        Log.v(LOG_TAG, "onItemClick: " + index.toString());
        //UpgradeOptionModel db = getOptionAt(index);
    }

    public UpgradeOptionModel getOptionAt(Integer index) {
        return mUpgradeOptionsAdapter.getItemAt(index);
    }

}
