package com.mssdevlab.baselib.Billing;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BillingManager implements PurchasesUpdatedListener {
    private static final String LOG_TAG = "BillingManager";
    private static String mBase64EncodedPublicKey;

    private BillingClient mBillingClient;

    private final BillingUpdatesListener mBillingUpdatesListener;

    private final List<Purchase> mPurchases = new ArrayList<>();

    private Set<String> mTokensToBeConsumed;

    private int mBillingClientResponseCode = BillingClient.BillingResponseCode.SERVICE_DISCONNECTED;

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();
        void onConsumeFinished(String token, @BillingClient.BillingResponseCode int result);
        void onPurchasesUpdated(List<Purchase> purchases);
    }

    public BillingManager(Context context, String publicKey, final BillingUpdatesListener updatesListener) {
        Log.d(LOG_TAG, "Creating Billing client.");
        mBase64EncodedPublicKey = publicKey;
        mBillingUpdatesListener = updatesListener;
        mBillingClient = BillingClient.newBuilder(context).setListener(this).build();


        Log.d(LOG_TAG, "Starting setup.");

        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startServiceConnection(() -> {
            // Notifying the listener that billing client is ready
            mBillingUpdatesListener.onBillingClientSetupFinished();
            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(LOG_TAG, "Setup successful. Querying inventory.");
            queryPurchases();
        });
    }

    public int getBillingClientResponseCode() {
        return mBillingClientResponseCode;
    }

    private void queryPurchases() {
        Runnable queryToExecute = () -> {
            long time = System.currentTimeMillis();
            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
            Log.i(LOG_TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                    + "ms");
            // If there are subscriptions supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                Purchase.PurchasesResult subscriptionResult
                        = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
                Log.i(LOG_TAG, "Querying purchases and subscriptions elapsed time: "
                        + (System.currentTimeMillis() - time) + "ms");
                Log.i(LOG_TAG, "Querying subscriptions result code: "
                        + subscriptionResult.getResponseCode()
                        + " res: " + subscriptionResult.getPurchasesList().size());

                if (subscriptionResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    purchasesResult.getPurchasesList().addAll(
                            subscriptionResult.getPurchasesList());
                } else {
                    Log.e(LOG_TAG, "Got an error response trying to query subscription purchases");
                }
            } else if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.i(LOG_TAG, "Skipped subscription purchases query since they are not supported");
            } else {
                Log.w(LOG_TAG, "queryPurchases() got an error response code: "
                        + purchasesResult.getResponseCode());
            }
            onQueryPurchasesFinished(purchasesResult);
        };

        executeServiceRequest(queryToExecute);
    }

    private boolean isServiceConnected(){
        return mBillingClientResponseCode == BillingClient.BillingResponseCode.OK;
    }

    private void executeServiceRequest(Runnable runnable) {
        if (isServiceConnected()) {
            runnable.run();
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // TODO: (feel free to introduce your retry policy here).
            startServiceConnection(runnable);
        }
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private void onQueryPurchasesFinished(Purchase.PurchasesResult result) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.w(LOG_TAG, "Billing client was null or result code (" + result.getResponseCode()
                    + ") was bad - quitting");
            return;
        }

        Log.d(LOG_TAG, "Query inventory was successful.");

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear();
        onPurchasesUpdated(result.getBillingResult(), result.getPurchasesList());
    }

    private boolean areSubscriptionsSupported() {
        int responseCode = mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).getResponseCode();
        if (responseCode != BillingClient.BillingResponseCode.OK) {
            Log.w(LOG_TAG, "areSubscriptionsSupported() got an error response: " + responseCode);
        }
        return responseCode == BillingClient.BillingResponseCode.OK;
    }

    private void startServiceConnection(final Runnable executeOnSuccess) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                mBillingClientResponseCode = billingResult.getResponseCode();
                Log.d(LOG_TAG, "Setup finished. Response code: " + mBillingClientResponseCode);

                if (mBillingClientResponseCode == BillingClient.BillingResponseCode.OK) {
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                mBillingClientResponseCode = BillingClient.BillingResponseCode.SERVICE_DISCONNECTED;
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        int resultCode = billingResult.getResponseCode();
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            if (purchases != null){
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            }
            mBillingUpdatesListener.onPurchasesUpdated(mPurchases);
        } else if (resultCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(LOG_TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
        } else {
            Log.w(LOG_TAG, "onPurchasesUpdated() got unknown resultCode: " + resultCode);
        }

    }

    private void handlePurchase(Purchase purchase) {
        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
            Log.i(LOG_TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
            return;
        }

        Log.d(LOG_TAG, "Got a verified purchase: " + purchase);
        mPurchases.add(purchase);
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            return Security.verifyPurchase(mBase64EncodedPublicKey, signedData, signature);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }
}