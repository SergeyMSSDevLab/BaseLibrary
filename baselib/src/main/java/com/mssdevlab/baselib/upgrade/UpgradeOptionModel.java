package com.mssdevlab.baselib.upgrade;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;

import com.android.billingclient.api.SkuDetails;
import com.mssdevlab.baselib.R;

public class UpgradeOptionModel extends BaseObservable {
    private final SkuDetails mSkuDetails;
    private final String mPriceTitle;

    UpgradeOptionModel(@NonNull final SkuDetails skuDetails, @NonNull final Resources res){
        this.mSkuDetails = skuDetails;
        if (this.mSkuDetails.getPriceAmountMicros() == this.mSkuDetails.getOriginalPriceAmountMicros()){
            this.mPriceTitle = res.getString(R.string.bl_upgrade_price_title,
                    this.mSkuDetails.getOriginalPrice());
        } else {
            this.mPriceTitle = res.getString(R.string.bl_upgrade_price_title_discount,
                    this.mSkuDetails.getPrice(),
                    this.mSkuDetails.getOriginalPrice());
        }
    }

    public String getTitle() {
        return this.mSkuDetails.getTitle();
    }

    public String getDescription() {
        return this.mSkuDetails.getDescription();
    }

    public String getPriceTitle() {
        return this.mPriceTitle;
    }

    SkuDetails getSkuDetails() { return this.mSkuDetails; }
}
