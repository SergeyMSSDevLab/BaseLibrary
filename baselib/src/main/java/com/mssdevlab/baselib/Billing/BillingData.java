package com.mssdevlab.baselib.Billing;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private static String sPublicKey;
    private static List<String> sProdSkus;
    private static List<String> sSubSkus;
    private static BillingProcessor sBillingProcessor;

    private static final MutableLiveData<List<Purchase>> sAppPurchases = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> sCanPurchaseProducts = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> sCanPurchaseSubsctiptions = new MutableLiveData<>();

    public static void setUpPurchases(@NonNull String publicKey,
                                      @NonNull List<String> subscriptionsSkus,
                                      @NonNull List<String> productsSkus){
        sPublicKey = publicKey;
        sProdSkus = productsSkus;
        sSubSkus = subscriptionsSkus;
    }

    @UiThread
    public static void checkPurchases(){
        Context ctx = BaseApplication.getInstance();
        sBillingProcessor = new BillingProcessor(ctx, sPublicKey, new BillingHandler());
        sBillingProcessor.initialize();

        Helper.setValue(sCanPurchaseProducts, sBillingProcessor.isOneTimePurchaseSupported());
        Helper.setValue(sCanPurchaseSubsctiptions, sBillingProcessor.isSubscriptionUpdateSupported());

        sBillingProcessor.loadOwnedPurchasesFromGoogle();
        sBillingProcessor.getPurchaseListingDetails(new ArrayList<>(sProdSkus));
        sBillingProcessor.getSubscriptionListingDetails(new ArrayList<>(sSubSkus));    }

    @NonNull
    public static LiveData<Boolean> getCanPurchaseProducts(){
        Log.v(LOG_TAG, "getCanPurchaseProducts hasActiveObservers: " + sCanPurchaseProducts.hasActiveObservers());
        return sCanPurchaseProducts;
    }
    @NonNull
    public static LiveData<Boolean> getCanPurchaseSubsctiptions(){
        Log.v(LOG_TAG, "getCanPurchaseSubsctiptions hasActiveObservers: " + sCanPurchaseSubsctiptions.hasActiveObservers());
        return sCanPurchaseSubsctiptions;
    }

    static BillingProcessor getsBillingProcessor(){
        return sBillingProcessor;
    }

    private static void setsAppPurchases(List<Purchase> val){
        Helper.setValue(sAppPurchases, val);
    }

    @NonNull
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

    private static class BillingHandler implements BillingProcessor.IBillingHandler{

        @Override
        public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
            Log.v(LOG_TAG, "onProductPurchased");
            /*
             * Called when requested PRODUCT ID was successfully purchased
             */
        }

        @Override
        public void onPurchaseHistoryRestored() {
            Log.v(LOG_TAG, "onPurchaseHistoryRestored");
            /*
             * Called when purchase history was restored and the list of all owned PRODUCT ID's
             * was loaded from Google Play
             */
        }

        @Override
        public void onBillingError(int errorCode, @Nullable Throwable error) {
            Log.v(LOG_TAG, "onBillingError");
            /*
             * Called when some error occurred. See Constants class for more details
             *
             * Note - this includes handling the case where the user canceled the buy dialog:
             * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
             */
        }

        @Override
        public void onBillingInitialized() {
            Log.v(LOG_TAG, "onBillingInitialized");
            /*
             * Called when BillingProcessor was initialized and it's ready to purchase
             */
        }
    }
}
