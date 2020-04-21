package com.mssdevlab.baselib.ApplicationMode;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.common.Helper;

import java.util.ArrayList;
import java.util.List;

class BillingData {
    private static final String LOG_TAG = "BillingData";

    private static final MutableLiveData<List<SkuDetails>> sSkuDetails = new MutableLiveData<>();

    private  static BillingManager sPurchaseManager;
    private  static BillingManager sSkuManager;
    private  static BillingManager sInventoryManager;

    static void loadPurchases(Activity activity, String publicKey){
        // todo handle request code
        if (BaseApplication.checkPlayServices(activity, 0)){

            Log.v(LOG_TAG, "Play Services ok");
            InventoryListener listener = new InventoryListener(publicKey);
            if (sInventoryManager == null){
                sInventoryManager = new BillingManager(activity, listener);
            } else {
                sInventoryManager.setUpdatesListener(listener);
            }
        }
        else {
            Log.v(LOG_TAG, "Play Services not available");
        }
    }

    @UiThread
    static void startPurchase(Activity activity, SkuDetails skuDetails, String publicKey){
        // todo handle request code
        if (BaseApplication.checkPlayServices(activity, 0)){

            Log.v(LOG_TAG, "Play Services ok");
            PurchaseListener listener = new PurchaseListener(activity, skuDetails, publicKey);
            if (sPurchaseManager == null){
                sPurchaseManager = new BillingManager(activity, listener);
            } else {
                sPurchaseManager.setUpdatesListener(listener);
            }
        }
        else {
            Log.v(LOG_TAG, "Play Services not available");
        }
    }

    @UiThread
    static void loadSku(Activity activity,
                            int requestCode,
                            @NonNull List<String> subscriptionsSkus,
                            @NonNull List<String> productsSkus){

        if (BaseApplication.checkPlayServices(activity, requestCode)){
            Log.v(LOG_TAG, "Play Services ok");
            setSkuDetails(new ArrayList<>());
            SkuDetailsListener listener = new SkuDetailsListener(productsSkus, subscriptionsSkus);

            if (sSkuManager == null){
                sSkuManager = new BillingManager(activity, listener);
            } else {
                sSkuManager.setUpdatesListener(listener);
            }
        }
        else {
            Log.v(LOG_TAG, "Play Services not available");
        }
    }

    private static void setAppPurchases(@NonNull List<Purchase> val){
        // TODO: check purchase acknowledge and save ofline information
        AppModeManager.setPurchases(val);
    }
    private static void addAppPurchases(List<Purchase> val){
        // todo: check if purchase already exists
        AppModeManager.addPurchases(val);
    }

    @NonNull
    static LiveData<List<SkuDetails>> getSkuDetails(){
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
            if(sSkuManager.isServiceConnected()){
                Log.d(LOG_TAG, "Setup successful. Querying Sku details.");
                sSkuManager.querySkuDetails(mProdSkus, mSubSkus);
            }
        }

        @Override
        public void onConsumeFinished(String token, int result) {

        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
        }

        @Override
        public void onSkuListUpdated(List<SkuDetails> details) {
            addSkuDetails(details);
        }
    }

    private static class PurchaseListener implements BillingManager.BillingUpdatesListener {

        private final Activity mActivity;
        private final SkuDetails mSkuDetails;
        private final String mPublicKey;

        PurchaseListener(Activity activity, SkuDetails skuDetails, String publicKey){
            mActivity = activity;
            mSkuDetails = skuDetails;
            mPublicKey = publicKey;
        }

        @Override
        public void onBillingClientSetupFinished() {
            if(sPurchaseManager.isServiceConnected()){
                Log.d(LOG_TAG, "Setup successful. Starting purchase flow.");
                BillingResult result = sPurchaseManager.startPurchase(this.mActivity, this.mSkuDetails, this.mPublicKey);
                Log.d(LOG_TAG, "Purchase flow finished: " + result.getResponseCode());
            }
        }

        @Override
        public void onConsumeFinished(String token, int result) {

        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
            addAppPurchases(purchases);
        }

        @Override
        public void onSkuListUpdated(List<SkuDetails> details) {
        }
    }

    private static class InventoryListener implements BillingManager.BillingUpdatesListener {

        private final String mPublicKey;

        InventoryListener(String publicKey){
            mPublicKey = publicKey;
        }

        @Override
        public void onBillingClientSetupFinished() {
            if(sInventoryManager.isServiceConnected()){
                Log.d(LOG_TAG, "Setup successful. Getting inventory");
                sInventoryManager.queryPurchases(this.mPublicKey);
            }
        }

        @Override
        public void onConsumeFinished(String token, int result) {

        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
            setAppPurchases(purchases);
        }

        @Override
        public void onSkuListUpdated(List<SkuDetails> details) {
        }
    }
}
