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

    private static final MutableLiveData<List<Purchase>> sAppPurchases = new MutableLiveData<>();
    private static final MutableLiveData<List<SkuDetails>> sSkuDetails = new MutableLiveData<>();
    private  static BillingManager sBillingManager;

    @UiThread
    public static void loadSku(Activity activity,
                            int requestCode,
                            @NonNull List<String> subscriptionsSkus,
                            @NonNull List<String> productsSkus){

        if (BaseApplication.checkPlayServices(activity, requestCode)){
            Log.v(LOG_TAG, "Play Services ok");
            setSkuDetails(new ArrayList<>());
            SkuDetailsListener listener = new SkuDetailsListener(productsSkus, subscriptionsSkus);

            if (sBillingManager == null){
                sBillingManager = new BillingManager(activity, listener);
            } else {
                listener.onBillingClientSetupFinished();
            }
        }
        else {
            Log.v(LOG_TAG, "Play Services not available");
        }
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
    private static void addSkuDetails(List<SkuDetails> val){
        ArrayList<SkuDetails> list = new ArrayList<>(
                Helper.getValueOrDefault(sSkuDetails.getValue(), ArrayList::new));
        list.addAll(val);
        setSkuDetails(list);
    }

    private static class SkuDetailsListener implements BillingManager.BillingUpdatesListener {
        private List<String> mProdSkus;
        private List<String> mSubSkus;

        SkuDetailsListener(List<String> prods, List<String> subs){
            this.mProdSkus = prods;
            this.mSubSkus = subs;
        }

        @Override
        public void onBillingClientSetupFinished() {
            if(sBillingManager.isServiceConnected()){
                Log.d(LOG_TAG, "Setup successful. Querying inventory.");
                // todo: move this to separate listener sBillingManager.queryPurchasesAsync();

                Log.d(LOG_TAG, "Querying Sku details.");
                sBillingManager.querySkuDetails(mProdSkus, mSubSkus);
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
            addSkuDetails(details);
        }
    }
}
