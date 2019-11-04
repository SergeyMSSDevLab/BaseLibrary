package com.mssdevlab.baselib.Billing;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.Purchase;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.common.Helper;

import java.util.ArrayList;
import java.util.List;

public class BillingData {
    private static final String LOG_TAG = "BillingData";

    private static final MutableLiveData<List<Purchase>> sAppPurchases = new MutableLiveData<>();

    @UiThread
    public static void checkPurchases(String publicKey){
        Context ctx = BaseApplication.getInstance();
    }

    private static void setsAppPurchases(List<Purchase> val){
        Helper.setValue(sAppPurchases, val);
    }

    public static LiveData<List<Purchase>> getAppPurchases(){
        Log.v(LOG_TAG, "getAppPurchases hasActiveObservers: " + sAppPurchases.hasActiveObservers());
        return sAppPurchases;
    }

    @NonNull
    public static List<Purchase> getCurrentAppPurchases(){
        List<Purchase> purchases = sAppPurchases.getValue();
        if (purchases == null){
            purchases = new ArrayList<Purchase>();
        }
        return purchases;
    }
}
