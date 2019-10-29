package com.mssdevlab.baselib.ApplicationMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mssdevlab.baselib.BaseApplication;

import androidx.annotation.NonNull;

import static com.mssdevlab.baselib.BaseApplication.SHARED_PREF;

public class AppModeManager {
    private static final String LOG_TAG = "AppModeManager";
    private static final String PREF_ALLOW_TRACKING_PARTICIPATED = "appModeManager.allowTrackingFirst";
    private static final String PREF_ALLOW_TRACKING_EXPIRE = "appModeManager.allowTrackingExpire";
    private static final String PREF_ALLOW_TRACKING = "appModeManager.allowTracking";
    private static final String PREF_AWARD_EARNED = "appModeManager.awardEarned";
    private static final String PREF_PRO_EARNED = "appModeManager.proEarned";
    private static final String PREF_PRO_EXPIRED = "appModeManager.proExpired";

    private static final long TRACKING_FREE_DAYS = 7L;
    private static final long EARNED_HOURS = 12L;
    private static final long HOUR_MS = 1000L * 60 * 60;
    private static final long DAY_MS = HOUR_MS * 24;
    private static final Object lockObj = new Object();

    public static void checkAppMode(){
        AppMode curMode = AppMode.MODE_DEMO;

        BaseApplication baseApp = BaseApplication.getInstance();
        SharedPreferences sharedPref = baseApp.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        boolean allowTrackingParticipated = sharedPref.getBoolean(PREF_ALLOW_TRACKING_PARTICIPATED, false);
        ApplicationData.setAllowTrackingParticipated(allowTrackingParticipated);

        boolean allowTracking = sharedPref.getBoolean(PREF_ALLOW_TRACKING, false);
        ApplicationData.setAllowTracking(allowTracking);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(baseApp);
        firebaseAnalytics.setAnalyticsCollectionEnabled(allowTracking);

        long finalExpired = 0L;
        long nowMs = System.currentTimeMillis();
        long allowtrackingExpired = sharedPref.getLong(PREF_ALLOW_TRACKING_EXPIRE, 0L);
        if (allowTrackingParticipated
                && nowMs <= allowtrackingExpired
                && allowTracking){
            curMode = enhanceAppMode(curMode, AppMode.MODE_EVALUATION);
            finalExpired = allowtrackingExpired;
        }

        long awardExpired = sharedPref.getLong(PREF_AWARD_EARNED, 0L);
        if (nowMs < awardExpired){
            curMode = enhanceAppMode(curMode, AppMode.MODE_NO_ADS);
            finalExpired = awardExpired;
        }

        boolean proEarned = sharedPref.getBoolean(PREF_PRO_EARNED, false);
        if (proEarned){
            finalExpired = sharedPref.getLong(PREF_PRO_EXPIRED, 0L);
            curMode = enhanceAppMode(curMode, AppMode.MODE_PRO);
        }

        ApplicationData.setApplicationMode(curMode);
        ApplicationData.setExpireTime(finalExpired);
    }

    private static AppMode enhanceAppMode(AppMode curMode, AppMode newMode){
        if (curMode.ordinal() < newMode.ordinal()){
            return newMode;
        }
        return curMode;
    }

    public static void setAllowTracking(boolean allowTracking) {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "setAllowTracking");
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            boolean alreadyParticipated = sharedPref.getBoolean(PREF_ALLOW_TRACKING_PARTICIPATED, false);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            long nowMs = System.currentTimeMillis();
            if (alreadyParticipated){
                if (!allowTracking){
                    // user is checking again, set expiration
                    spEditor.putLong(PREF_ALLOW_TRACKING_EXPIRE, nowMs);
                }

            } else {
                if (allowTracking){
                    // user is checking first time, set free time
                    spEditor.putBoolean(PREF_ALLOW_TRACKING_PARTICIPATED, true);
                    spEditor.putLong(PREF_ALLOW_TRACKING_EXPIRE, nowMs + DAY_MS * TRACKING_FREE_DAYS);
                }
            }
            spEditor.putBoolean(PREF_ALLOW_TRACKING, allowTracking);
            spEditor.apply();
        }
        checkAppMode();
    }

    public static void rewardUser(@NonNull RewardItem rewardItem) {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "rewardUser itemType:" + rewardItem.getType() + " amount:" + rewardItem.getAmount());
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            long alreadyEaned = sharedPref.getLong(PREF_AWARD_EARNED, 0L);
            long nowMs = System.currentTimeMillis();

            long value;
            if ("coins".equals(rewardItem.getType())){
                value = (nowMs > alreadyEaned ? nowMs : alreadyEaned) + HOUR_MS * rewardItem.getAmount();
            } else {
                value = (nowMs > alreadyEaned ? nowMs : alreadyEaned) + HOUR_MS * EARNED_HOURS;
            }
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putLong(PREF_AWARD_EARNED, value);
            spEditor.apply();
        }
        checkAppMode();
    }

    public static void setProMode(boolean isProMode) {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "setProMode");
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putBoolean(PREF_PRO_EARNED, isProMode);
            spEditor.apply();
        }
        checkAppMode();
    }
}
