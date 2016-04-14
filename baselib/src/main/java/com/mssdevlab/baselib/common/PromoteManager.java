package com.mssdevlab.baselib.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

/**
 * Manages promote screen operations
 */
@SuppressWarnings("unused")
public class PromoteManager {
    private static final String LOG_TAG = "PromoteManager";
    private static final Long DAY_MS = 1000l * 60 * 60 * 24;
    private static final String SHARED_PREF = "PromoteApp";
    private static final String PREF_FIRST_LAUNCH = "FirstLaunch";
    private static final String PREF_LAUNCHES = "Launches";
    private static final String PREF_NEVER_SHOW = "NeverShow";
    private static final String PREF_DAYS_BEFORE_SHOW_RATE = "PromoteManager.getDaysBeforeShowRate";
    private static final String PREF_LAUNCHES_BEFORE_SHOW_RATE = "PromoteManager.launchesBeforeShowRate";
    private static final Object lockObj = new Object();

    // There is the default rule to show a rate dialog: on third day and as least ten launches
    private static final int daysBeforeShowRate = 3;
    private static final int launchesBeforeShowRate = 10;

    /*
    * Stores information about launches of application
    */
    public static void markStarting(Context context) {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "MarkStarting");
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();

            Long firstLaunch = sharedPref.getLong(PREF_FIRST_LAUNCH, 0);
            if (firstLaunch == 0) {
                firstLaunch = System.currentTimeMillis();
                spEditor.putLong(PREF_FIRST_LAUNCH, firstLaunch);
            }

            int launches = sharedPref.getInt(PREF_LAUNCHES, 0);
            if (launches < Integer.MAX_VALUE) {
                spEditor.putInt(PREF_LAUNCHES, launches + 1);
            }

            spEditor.apply();
        }
    }

    /*
    * Checks if conditions are ok for the promotion screen showing
    */
    public static boolean isTimeToShowPromoScreen(final Context context) {
        Log.v(LOG_TAG, "IsTimeToShowRate");
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        if (isPromoScreenEnabled(context)) {
            synchronized (lockObj) {
                int reqDays = getDaysBeforeShowRate(context);
                int reqLaunches = getLaunchesBeforeShowRate(context);

                Long curMs = System.currentTimeMillis();
                Long firstLaunch = sharedPref.getLong(PREF_FIRST_LAUNCH, curMs);
                int launches = sharedPref.getInt(PREF_LAUNCHES, 0);

                return (((firstLaunch + reqDays * DAY_MS) < curMs) && (launches > reqLaunches));
            }
        } else {
            return false;
        }
    }

    public static boolean isPromoScreenEnabled(final Context context){
        synchronized (lockObj) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            return !sharedPref.getBoolean(PREF_NEVER_SHOW, false);
        }
    }

    /*
    * Redirects user to google play application screen
    * */
    public static void goToPromoScreen(final Context context) {
        Log.v(LOG_TAG, "GoToRateScreen");
        try {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(goToMarket);
            cancelPromoScreenPermanently(context);

        } catch (Exception ex) {
            Log.e(LOG_TAG, "GoToRateScreen fails: " + ex.getMessage());
            markRateNotNow(context);
        }
    }

    /*
    * Sets conditions that to show the promotion screen later
    */
    public static void markRateNotNow(final Context context) {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "MarkRateNotNow");
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

            int launches = sharedPref.getInt(PREF_LAUNCHES, 0);
            int reqLaunches = getLaunchesBeforeShowRate(context);
            setLaunchesBeforeShowRate(context, getLaunchesBeforeShowRate(context) + sharedPref.getInt(PREF_LAUNCHES, 0));
        }
    }

    /*
    * Sets conditions that to never show the promotion screen
    */
    public static void cancelPromoScreenPermanently(final Context context) {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "MarkRateCancelPermanently");
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putBoolean(PREF_NEVER_SHOW, true);
            spEditor.apply();
        }
    }

    /*
    * Sets conditions that to never show the promotion screen
    */
    public static void setPromoScreenEnabled(final Context context, boolean enabled) {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "EnableMarkRate");
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putBoolean(PREF_NEVER_SHOW, !enabled);
            spEditor.apply();
        }
    }

    public static int getDaysFromInstall(final Context context){
        synchronized (lockObj) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            Long curMs = System.currentTimeMillis();
            Long firstLaunch = sharedPref.getLong(PREF_FIRST_LAUNCH, curMs);

            return (int) ((curMs - firstLaunch) / DAY_MS);
        }
    }

    public static int getLaunchesFromInstall(final Context context){
        synchronized (lockObj) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            return sharedPref.getInt(PREF_LAUNCHES, 0);
        }
    }

    public static int getDaysBeforeShowRate(final Context context) {
        synchronized (lockObj) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            return sharedPref.getInt(PREF_DAYS_BEFORE_SHOW_RATE, PromoteManager.daysBeforeShowRate);
        }
    }

    public static void setDaysBeforeShowRate(final Context context, int daysBeforeShowRate) {
        synchronized (lockObj) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putInt(PREF_DAYS_BEFORE_SHOW_RATE, daysBeforeShowRate);
            spEditor.apply();
        }
    }

    public static int getLaunchesBeforeShowRate(final Context context) {
        synchronized (lockObj) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            return sharedPref.getInt(PREF_LAUNCHES_BEFORE_SHOW_RATE, PromoteManager.launchesBeforeShowRate);
        }
    }

    public static void setLaunchesBeforeShowRate(final Context context, int launchesBeforeShowRate) {
        synchronized (lockObj) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putInt(PREF_LAUNCHES_BEFORE_SHOW_RATE, launchesBeforeShowRate);
            spEditor.apply();
        }
    }
}
