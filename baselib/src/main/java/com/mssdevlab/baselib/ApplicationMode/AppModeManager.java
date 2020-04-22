package com.mssdevlab.baselib.ApplicationMode;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.Purchase;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mssdevlab.baselib.BaseApplication;

import java.util.List;

import static com.mssdevlab.baselib.BaseApplication.SHARED_PREF;

class AppModeManager {
    private static final String LOG_TAG = "AppModeManager";
    private static final String PREF_ALLOW_TRACKING_PARTICIPATED = "appModeManager.allowTrackingFirst";
    private static final String PREF_ALLOW_TRACKING_EXPIRE = "appModeManager.allowTrackingExpire";
    private static final String PREF_ALLOW_TRACKING = "appModeManager.allowTracking";
    private static final String PREF_AWARD_EXPIRE = "appModeManager.awardExpire";
    private static final String PREF_APP_MODE = "appModeManager.curAppMode";

    private static final long TRACKING_FREE_DAYS = 7L;
    private static final long EARNED_HOURS = 21L;
    private static final long HOUR_MS = 1000L * 60 * 60;
    private static final long DAY_MS = HOUR_MS * 24;
    private static final Object lockObj = new Object();

    private static boolean sAllowTrackingPartisipated = false;
    private static boolean sAllowTracking = false;
    private static long sAllowTrackingExpireAt = 0L;
    private static long sAwardExpireAt = 0L;
    private static AppMode sCurrentMode = AppMode.MODE_DEMO;
    private static List<Purchase> sActivePurchases;

    static void initAppMode(){
        BaseApplication baseApp = BaseApplication.getInstance();
        SharedPreferences sharedPref = baseApp.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        restoreSavedValues(sharedPref);
        checkAppMode(baseApp);
    }

    private static void restoreSavedValues(SharedPreferences sharedPref){
        sAllowTrackingPartisipated = sharedPref.getBoolean(PREF_ALLOW_TRACKING_PARTICIPATED, false);
        ApplicationData.setAllowTrackingParticipated(sAllowTrackingPartisipated);

        sAllowTracking = sharedPref.getBoolean(PREF_ALLOW_TRACKING, false);
        ApplicationData.setAllowTracking(sAllowTracking);

        sAllowTrackingExpireAt = sharedPref.getLong(PREF_ALLOW_TRACKING_EXPIRE, 0L);
        sAwardExpireAt = sharedPref.getLong(PREF_AWARD_EXPIRE, 0L);

        int curOrdinal = sharedPref.getInt(PREF_APP_MODE, AppMode.MODE_DEMO.ordinal());
        sCurrentMode = AppMode.values()[curOrdinal];

        ApplicationData.setApplicationMode(sCurrentMode);
    }

    private static void checkAppMode(Application ctx){
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
        firebaseAnalytics.setAnalyticsCollectionEnabled(sAllowTracking);

        long finalExpired = 0L;
        long nowMs = System.currentTimeMillis();
        if (sAllowTrackingPartisipated
                && nowMs <= sAllowTrackingExpireAt
                && sAllowTracking){
            sCurrentMode = enhanceAppMode(sCurrentMode, AppMode.MODE_EVALUATION);
            finalExpired = sAllowTrackingExpireAt;
        }

        if (nowMs < sAwardExpireAt){
            sCurrentMode = enhanceAppMode(sCurrentMode, AppMode.MODE_NO_ADS);
            finalExpired = sAwardExpireAt;
        }

        if (sActivePurchases != null && sActivePurchases.size() > 0){
            // assume the purchases already checked and only active purchases in the list
            sCurrentMode = enhanceAppMode(sCurrentMode, AppMode.MODE_PRO);
        }

        storeMode(ctx);
        ApplicationData.setApplicationMode(sCurrentMode);
        ApplicationData.setExpireTime(finalExpired);
    }

    private static void storeMode(Application ctx) {
        Log.v(LOG_TAG, "storeMode");
        synchronized (lockObj) {
            SharedPreferences sharedPref = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putInt(PREF_APP_MODE, sCurrentMode.ordinal());
            spEditor.apply();
        }
    }

    @NonNull
    private static AppMode enhanceAppMode(AppMode curMode, AppMode newMode){
        if (curMode.ordinal() < newMode.ordinal()){
            return newMode;
        }
        return curMode;
    }

    static void setAllowTracking(boolean allowTracking) {
        Log.v(LOG_TAG, "setAllowTracking");
        BaseApplication baseApp = BaseApplication.getInstance();
        synchronized (lockObj) {
            SharedPreferences sharedPref = baseApp.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            long nowMs = System.currentTimeMillis();
            if (sAllowTrackingPartisipated){
                if (!allowTracking){
                    // user is checking again, set expiration
                    sAllowTrackingExpireAt = nowMs;
                    spEditor.putLong(PREF_ALLOW_TRACKING_EXPIRE, nowMs);
                }

            } else {
                if (allowTracking){
                    // user is checking first time, set free time
                    spEditor.putBoolean(PREF_ALLOW_TRACKING_PARTICIPATED, true);
                    sAllowTrackingPartisipated = true;
                    long expireAt = nowMs + DAY_MS * TRACKING_FREE_DAYS;
                    sAllowTrackingExpireAt = expireAt;
                    spEditor.putLong(PREF_ALLOW_TRACKING_EXPIRE, expireAt);
                }
            }
            spEditor.putBoolean(PREF_ALLOW_TRACKING, allowTracking);
            sAllowTracking = allowTracking;
            spEditor.apply();
        }

        ApplicationData.setAllowTrackingParticipated(sAllowTrackingPartisipated);
        ApplicationData.setAllowTracking(sAllowTracking);

        checkAppMode(baseApp);
    }

    static void rewardUser(@NonNull RewardItem rewardItem) {
        Log.v(LOG_TAG, "rewardUser itemType:" + rewardItem.getType() + " amount:" + rewardItem.getAmount());
        BaseApplication baseApp = BaseApplication.getInstance();
        long nowMs = System.currentTimeMillis();
        long value;
        if ("hours".equals(rewardItem.getType())){
            value = Math.max(nowMs, sAwardExpireAt) + HOUR_MS * rewardItem.getAmount();
        } else {
            value = Math.max(nowMs, sAwardExpireAt) + HOUR_MS * EARNED_HOURS;
        }
        sAwardExpireAt = value;
        synchronized (lockObj) {
            SharedPreferences sharedPref = baseApp.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putLong(PREF_AWARD_EXPIRE, sAwardExpireAt);
            spEditor.apply();
        }
        checkAppMode(baseApp);
    }

    static void setPurchases(@NonNull List<Purchase> purchases) {
        Log.v(LOG_TAG, "setPurchases");
        BaseApplication baseApp = BaseApplication.getInstance();
        sActivePurchases = purchases;
        sCurrentMode = AppMode.MODE_DEMO;
        checkAppMode(baseApp);
    }

    static void addPurchases(@NonNull List<Purchase> purchases) {
        Log.v(LOG_TAG, "setPurchases");
        if (sActivePurchases == null){
            setPurchases(purchases);
        } else {
            BaseApplication baseApp = BaseApplication.getInstance();
            // todo: check if purchase already exists
            sActivePurchases.addAll(purchases);
            sCurrentMode = AppMode.MODE_DEMO;
            checkAppMode(baseApp);
        }
    }
}
