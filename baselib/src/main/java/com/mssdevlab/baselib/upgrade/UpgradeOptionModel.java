package com.mssdevlab.baselib.upgrade;

import androidx.databinding.BaseObservable;

import com.android.billingclient.api.SkuDetails;

public class UpgradeOptionModel extends BaseObservable {
    private SkuDetails mSkuDetails;

    public UpgradeOptionModel(SkuDetails skuDetails){
        this.mSkuDetails = skuDetails;
    }

    public String getTitle() {
        String temp = this.mSkuDetails.getTitle();
        return temp;
    }

    public String getDescription() {
        String temp = this.mSkuDetails.getDescription();
        return temp;
    }
}
