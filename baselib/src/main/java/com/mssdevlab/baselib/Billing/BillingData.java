package com.mssdevlab.baselib.Billing;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.common.Helper;

import java.util.ArrayList;
import java.util.List;

public class BillingData {
    private static final String LOG_TAG = "BillingData";

    private static String sPublicKey;
    private static List<String> sProdSkus;
    private static List<String> sSubSkus;

    private static final MutableLiveData<List<Purchase>> sAppPurchases = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> sCanPurchase = new MutableLiveData<>();
    private static final MutableLiveData<List<SkuDetails>> sSkuDetails = new MutableLiveData<>();
    private  static BillingManager sBillingManager;

    @UiThread
    public static void setUpBilling(@NonNull String publicKey,
                                      @NonNull List<String> subscriptionsSkus,
                                      @NonNull List<String> productsSkus){
        sPublicKey = publicKey;
        sProdSkus = productsSkus;
        sSubSkus = subscriptionsSkus;

        getBillindData(null, 0);
    }

    public static void getBillindData(Activity activity, int requestCode){
        setCanPurchase(false);
        if (BaseApplication.checkPlayServices(activity, requestCode)){
            Log.v(LOG_TAG, "Play Services ok");

            if (sBillingManager == null){
                sBillingManager = new BillingManager(BaseApplication.getInstance(), sPublicKey, new BillingListener());
            }
        }
        else {
            Log.v(LOG_TAG, "Play Services not available");
        }
    }

    @NonNull
    public static LiveData<Boolean> getCanPurchase(){
        Log.v(LOG_TAG, "sCanPurchase hasActiveObservers: " + sCanPurchase.hasActiveObservers());
        return sCanPurchase;
    }
    private static void setCanPurchase(Boolean val){
        Helper.setValue(sCanPurchase, val);
    }

    @NonNull
    public static LiveData<List<Purchase>> getAppPurchases(){
        Log.v(LOG_TAG, "getAppPurchases hasActiveObservers: " + sAppPurchases.hasActiveObservers());
        return sAppPurchases;
    }
    private static void setAppPurchases(List<Purchase> val){
        Helper.setValue(sAppPurchases, val);
    }

    @NonNull
    public static List<Purchase> getCurrentAppPurchases(){
        List<Purchase> purchases = sAppPurchases.getValue();
        if (purchases == null){
            purchases = new ArrayList<Purchase>();
        }
        return purchases;
    }

    @NonNull
    public static LiveData<List<SkuDetails>> getSkuDetails(){
        Log.v(LOG_TAG, "sSkuDetails hasActiveObservers: " + sSkuDetails.hasActiveObservers());
        return sSkuDetails;
    }
    private static void setSkuDetails(List<SkuDetails> val){
        Helper.setValue(sSkuDetails, val);
    }

    private static class BillingListener implements BillingManager.BillingUpdatesListener {

        @Override
        public void onBillingClientSetupFinished() {
            if(sBillingManager.isServiceConnected()){
                setCanPurchase(true);
                Log.d(LOG_TAG, "Setup successful. Querying inventory.");
                sBillingManager.queryPurchasesAsync();
                Log.d(LOG_TAG, "Querying Sku details.");
                sBillingManager.querySkuDetails(sProdSkus, sSubSkus);
            }
        }

        @Override
        public void onConsumeFinished(String token, int result) {

        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
            // TODO: check if pending purchases are confirmed and confirm if needed
            setAppPurchases(purchases);
        }

        @Override
        public void onSkuListUpdated(List<SkuDetails> details) {
            setSkuDetails(details);
        }
    }
}
