package com.mssdevlab.baselib.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mssdevlab.baselib.BaseApplication;

import static com.mssdevlab.baselib.BaseApplication.SHARED_PREF;

/**
 * Manages promote screen operations
 */
@SuppressWarnings("unused")
public class PromoteManager {
    private static final String LOG_TAG = PromoteManager.class.getCanonicalName();
    private static final Long DAY_MS = 1000L * 60 * 60 * 24;
    private static final String PREF_FIRST_LAUNCH = "FirstLaunch";
    private static final String PREF_LAUNCHES = "Launches";
    private static final String PREF_NEVER_SHOW = "NeverShow";
    private static final String PREF_DAYS_BEFORE_SHOW_RATE = "PromoteManager.getDaysBeforeShowRate";
    private static final String PREF_LAUNCHES_BEFORE_SHOW_RATE = "PromoteManager.sLaunchesBeforeShowRate";
    private static final Object lockObj = new Object();

    // There is the default rule to show a rate dialog: on third day and as least ten launches
    private static final int sDaysBeforeShowRate = 3;
    private static final int sLaunchesBeforeShowRate = 10;

    private static final MutableLiveData<Boolean> sShowPromo = new MutableLiveData<>();

    public static LiveData<Boolean> getShowPromo(){
        Boolean val = sShowPromo.getValue();
        if (val == null){
            setShowPromo(false);
        }
        return sShowPromo;
    }

    public static void setShowPromo(boolean value){
        if (Looper.myLooper() == Looper.getMainLooper()){
            sShowPromo.setValue(value);
        } else {
            sShowPromo.postValue(value);
        }
    }

    /*
    * Update the getShowPromo state
     */
    private static void checkShowPromoDialog(){
        boolean showPromo = isTimeToShowPromoScreen();
        Log.v(LOG_TAG, "checkShowPromoDialog showPromo: " + Boolean.toString(showPromo));
        if (Looper.myLooper() == Looper.getMainLooper()){
            sShowPromo.setValue(showPromo);
        } else {
            sShowPromo.postValue(showPromo);
        }
    }

    /*
    * Stores information about launches of application
    */
    public static void markStarting() {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "MarkStarting");
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
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
        checkShowPromoDialog();
    }

    /*
    * Checks if conditions are ok for the promotion screen showing
    */
    public static boolean isTimeToShowPromoScreen() {
        Log.v(LOG_TAG, "IsTimeToShowRate");
        SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        if (isPromoScreenEnabled()) {
            synchronized (lockObj) {
                int reqDays = getDaysBeforeShowRate();
                int reqLaunches = getLaunchesBeforeShowRate();

                Long curMs = System.currentTimeMillis();
                Long firstLaunch = sharedPref.getLong(PREF_FIRST_LAUNCH, curMs);
                int launches = sharedPref.getInt(PREF_LAUNCHES, 0);
                Log.v(LOG_TAG, "IsTimeToShowRate launches: " + Integer.toString(launches));

                boolean ret = (((firstLaunch + reqDays * DAY_MS) < curMs) && (launches > reqLaunches));
                Log.v(LOG_TAG, "IsTimeToShowRate: " + Boolean.toString(ret));
                return ret;
            }
        } else {
            return false;
        }
    }

    public static boolean isPromoScreenEnabled(){
        synchronized (lockObj) {
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            boolean ret = !sharedPref.getBoolean(PREF_NEVER_SHOW, false);
            Log.v(LOG_TAG, "isPromoScreenEnabled: " + Boolean.toString(ret));
            return ret;
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
            cancelPromoScreenPermanently();

        } catch (Exception ex) {
            Log.e(LOG_TAG, "GoToRateScreen fails: " + ex.getMessage());
            markRateNotNow();
        }
    }

    /*
    * Sets conditions that to show the promotion screen later
    */
    public static void markRateNotNow() {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "MarkRateNotNow");
            setLaunchesBeforeShowRate(getLaunchesFromInstall() + 5);
        }
        checkShowPromoDialog();
    }

    /*
    * Sets conditions that to never show the promotion screen
    */
    public static void cancelPromoScreenPermanently() {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "MarkRateCancelPermanently");
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putBoolean(PREF_NEVER_SHOW, true);
            spEditor.apply();
        }
        checkShowPromoDialog();
    }

    /*
    * Sets conditions that to show the promotion screen
    */
    public static void setPromoScreenEnabled(boolean enabled) {
        synchronized (lockObj) {
            Log.v(LOG_TAG, "EnableMarkRate");
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putBoolean(PREF_NEVER_SHOW, !enabled);
            spEditor.apply();
        }
        checkShowPromoDialog();
    }

    public static int getDaysFromInstall(){
        synchronized (lockObj) {
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            Long curMs = System.currentTimeMillis();
            Long firstLaunch = sharedPref.getLong(PREF_FIRST_LAUNCH, curMs);

            return (int) ((curMs - firstLaunch) / DAY_MS);
        }
    }

    public static int getLaunchesFromInstall(){
        synchronized (lockObj) {
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            return sharedPref.getInt(PREF_LAUNCHES, 0);
        }
    }

    public static int getDaysBeforeShowRate() {
        synchronized (lockObj) {
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            int ret = sharedPref.getInt(PREF_DAYS_BEFORE_SHOW_RATE, PromoteManager.sDaysBeforeShowRate);
            Log.v(LOG_TAG, "getDaysBeforeShowRate: " + ret);
            return ret;
        }
    }

    public static void setDaysBeforeShowRate(int daysBeforeShowRate) {
        synchronized (lockObj) {
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putInt(PREF_DAYS_BEFORE_SHOW_RATE, daysBeforeShowRate);
            spEditor.apply();
        }
    }

    public static int getLaunchesBeforeShowRate() {
        synchronized (lockObj) {
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            int ret =  sharedPref.getInt(PREF_LAUNCHES_BEFORE_SHOW_RATE, PromoteManager.sLaunchesBeforeShowRate);
            Log.v(LOG_TAG, "getLaunchesBeforeShowRate: " + ret);
            return ret;
        }
    }

    public static void setLaunchesBeforeShowRate(int launchesBeforeShowRate) {
        Log.v(LOG_TAG, "setLaunchesBeforeShowRate: " + launchesBeforeShowRate);
        synchronized (lockObj) {
            SharedPreferences sharedPref = BaseApplication.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putInt(PREF_LAUNCHES_BEFORE_SHOW_RATE, launchesBeforeShowRate);
            spEditor.apply();
        }
    }
}
