package com.mssdevlab.baselib.Billing;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class BillingHistoryRecord implements Parcelable
{

    private final String productId;
    private final String purchaseToken;
    private final long purchaseTime;
    private final String developerPayload;
    private final String signature;

    BillingHistoryRecord(String dataAsJson, String signature) throws JSONException
    {
        this(new JSONObject(dataAsJson), signature);
    }

    private BillingHistoryRecord(JSONObject json, String signature) throws JSONException
    {
        productId = json.getString("productId");
        purchaseToken = json.getString("purchaseToken");
        purchaseTime = json.getLong("purchaseTime");
        developerPayload = json.getString("developerPayload");
        this.signature = signature;
    }
/*

    public BillingHistoryRecord(String productId, String purchaseToken, long purchaseTime,
                                String developerPayload, String signature)
    {
        this.productId = productId;
        this.purchaseToken = purchaseToken;
        this.purchaseTime = purchaseTime;
        this.developerPayload = developerPayload;
        this.signature = signature;
    }
*/

    private BillingHistoryRecord(Parcel in)
    {
        productId = in.readString();
        purchaseToken = in.readString();
        purchaseTime = in.readLong();
        developerPayload = in.readString();
        signature = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(productId);
        dest.writeString(purchaseToken);
        dest.writeLong(purchaseTime);
        dest.writeString(developerPayload);
        dest.writeString(signature);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<BillingHistoryRecord> CREATOR = new Creator<BillingHistoryRecord>()
    {
        @Override
        public BillingHistoryRecord createFromParcel(Parcel in)
        {
            return new BillingHistoryRecord(in);
        }

        @Override
        public BillingHistoryRecord[] newArray(int size)
        {
            return new BillingHistoryRecord[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return "BillingHistoryRecord{" +
                "productId='" + productId + '\'' +
                ", purchaseToken='" + purchaseToken + '\'' +
                ", purchaseTime=" + purchaseTime +
                ", developerPayload='" + developerPayload + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
