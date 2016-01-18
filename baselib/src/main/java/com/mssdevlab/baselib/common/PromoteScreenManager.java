package com.mssdevlab.baselib.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

/**
 * Manages promote screen operations
 */
public class PromoteScreenManager {
    private static final String LOG_TAG = "PromoteScreenManager";
    private static final String SHARED_PREF = "PromoteApp";
    private static final String PREF_FIRST_LAUNCH = "FirstLaunch";
    private static final String PREF_LAUNCHES = "Launches";
    private static final String PREF_NEVER_SHOW = "NeverShow";
    private static final Long DAY_MS = 1000l * 60 * 60 * 24;

    /*
    * Stores information about launches of application
    */
    public static void MarkStarting(Context context) {
        Log.v(LOG_TAG, "MarkStarting");
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();

        Long firstLaunch = sharedPref.getLong(PREF_FIRST_LAUNCH, 0);
        if (firstLaunch == 0) {
            firstLaunch = System.currentTimeMillis();
            spEditor.putLong(PREF_FIRST_LAUNCH, firstLaunch);
        }

        long launches = sharedPref.getLong(PREF_LAUNCHES, 0);
        if (launches < 1000) {
            spEditor.putLong(PREF_LAUNCHES, launches + 1);
        }

        spEditor.apply();
    }

    /*
    * Checks if conditions are ok for the promotion screen showing
    */
    public static boolean IsTimeToShowRate(int reqDays, int reqLaunches, final Context context) {
        Log.v(LOG_TAG, "IsTimeToShowRate");
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(PREF_NEVER_SHOW, false)) {
            return false;
        }

        Long firstLaunch = sharedPref.getLong(PREF_FIRST_LAUNCH, 0);
        Long launches = sharedPref.getLong(PREF_LAUNCHES, 0);
        Long curMs = System.currentTimeMillis();

        return (((firstLaunch + reqDays * DAY_MS) < curMs) && (launches > reqLaunches));
    }

    /*
    * Redirects user to google play application screen
    * */
    public static void GoToRateScreen(final Context context) {
        Log.v(LOG_TAG, "GoToRateScreen");
        try {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(goToMarket);
            MarkRateCancelPermanently(context);

        } catch (Exception ex) {
            Log.e(LOG_TAG, "GoToRateScreen fails: " + ex.getMessage());
            MarkRateNotNow(context);
        }
    }

    /*
    * Sets conditions that to show the promotion screen later
    */
    public static void MarkRateNotNow(final Context context) {
        Log.v(LOG_TAG, "MarkRateNotNow");
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putLong(PREF_LAUNCHES, 1);
        spEditor.apply();
    }

    /*
    * Sets conditions that to never show the promotion screen
    */
    public static void MarkRateCancelPermanently(final Context context) {
        Log.v(LOG_TAG, "MarkRateCancelPermanently");
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean(PREF_NEVER_SHOW, true);
        spEditor.apply();
    }

}
