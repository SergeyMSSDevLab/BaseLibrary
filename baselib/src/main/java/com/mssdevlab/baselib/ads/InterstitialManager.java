package com.mssdevlab.baselib.ads;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.AppMode;
import com.mssdevlab.baselib.common.ApplicationData;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import static com.mssdevlab.baselib.BaseApplication.SHARED_PREF;

public class InterstitialManager {
    private static final String LOG_TAG = "InterstitialManager";
    private static final String PREF_LAUNCHES = "InterstitialManager.Launches";
    private static final String PREF_LAST_INDEX = "InterstitialManager.last.Index";
    private static final String PREF_LAST_LAUNCHES = "InterstitialManager.last.Launches";
    private static final String PREF_SHOW_MODE_WARNING = "InterstitialManager.mode.warning";
    private static final long SHOWING_DELAY  = 1000L * 60 * 9;

    private static int[] sScheduleArr = {1, 3, 5, 7};
    private static InterstitialAd sInterstitialAd;
    private static long sLastShownTime = 0L;

    public static boolean isAppModeAtLeast(Activity activity, @NonNull AppMode minMode) {
        AppMode mode = ApplicationData.getCurrentApplicationMode();
        if (mode.ordinal() >= minMode.ordinal() ){
            return true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Notification");
        builder.setMessage("The application works in demo mode. Some features are limited and advertisements are shown."
                + "\nThe command you are trying to perform is not allowed in the demo mode.")
                .setCancelable(false)
                .setPositiveButton("View upgrade options", (dialog, id) -> BaseApplication.startUpgradeScreen(activity))
                .setNegativeButton("Continue in demo mode", (dialog, id) -> {
                    showInterstitialAd(activity, false);
                    dialog.cancel();
                }).show();

        return false;
    }

    public static void showInterstitialAd(Activity activity, boolean showWarning){
        // TODO: wait until enable ads finishes
        Log.v(LOG_TAG, "showInterstitialAd sInterstitialAd != null: " + (sInterstitialAd != null));
        if (sInterstitialAd != null && sInterstitialAd.isLoaded()) {
            AppMode mode = ApplicationData.getCurrentApplicationMode();
            if (mode.ordinal() < AppMode.MODE_NO_ADS.ordinal()) {
                long passedTime = System.currentTimeMillis() - sLastShownTime;
                if (passedTime > SHOWING_DELAY){
                    SharedPreferences sharedPref = activity.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                    if (showWarning && sharedPref.getBoolean(PREF_SHOW_MODE_WARNING, true)){
                        View checkBoxView = View.inflate(activity, R.layout.checkbox, null);
                        CheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
                        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            SharedPreferences.Editor spEditor = sharedPref.edit();
                            spEditor.putBoolean(PREF_SHOW_MODE_WARNING, !isChecked);
                            spEditor.apply();
                        });
                        checkBox.setText("Don't show this message anymore.");

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Notification");
                        builder.setMessage("The application works in demo mode. Some features are limited and advertisements are shown.")
                                .setView(checkBoxView)
                                .setCancelable(false)
                                .setPositiveButton("View upgrade options", (dialog, id) -> BaseApplication.startUpgradeScreen(activity))
                                .setNegativeButton("Continue in demo mode", (dialog, id) -> {
                                    showInterstitialAd(activity, false);
                                    dialog.cancel();
                                }).show();
                    } else {
                        sInterstitialAd.show();
                        sLastShownTime = System.currentTimeMillis();
                        int launches = sharedPref.getInt(PREF_LAUNCHES, 0);
                        int lastIndex = sharedPref.getInt(PREF_LAST_INDEX, 0) + 1;
                        if (lastIndex >= sScheduleArr.length){
                            lastIndex = 0;
                        }
                        SharedPreferences.Editor spEditor = sharedPref.edit();
                        spEditor.putInt(PREF_LAST_LAUNCHES, launches);
                        spEditor.putInt(PREF_LAST_INDEX, lastIndex);
                        spEditor.apply();
                    }
                }
            }
        }
    }

    public static void enableAds(@StringRes int adUnitId){
        Log.v(LOG_TAG, "enableAds");

        // TODO: enable add only for corresponding application mode
        new Thread(() -> enableAdsInBackground(adUnitId)).start();
    }

    private static void enableAdsInBackground(@StringRes int adUnitId){
        Log.v(LOG_TAG, "enableAdsInBackground");
        Context ctx = BaseApplication.getInstance();

        SharedPreferences sharedPref = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        int launches = sharedPref.getInt(PREF_LAUNCHES, 0);
        if (launches < Integer.MAX_VALUE) {
            launches += 1;
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putInt(PREF_LAUNCHES, launches);
            spEditor.apply();
        }
        int lastIndex = sharedPref.getInt(PREF_LAST_INDEX, 0);
        int difference = launches - sharedPref.getInt(PREF_LAST_LAUNCHES, 0);
        Log.v(LOG_TAG, "enableAdsInBackground: launches=" + launches +
                " lastIndex=" + lastIndex +
                " difference=" + difference +
                " schedule=" + sScheduleArr[lastIndex]);
        if (difference >= sScheduleArr[lastIndex]){
            setUpAd(ctx, adUnitId);
        }
    }

    private static void setUpAd(Context ctx, @StringRes int adUnitId){
        Log.v(LOG_TAG, "setUpAd");
        sInterstitialAd = new InterstitialAd(ctx);
        sInterstitialAd.setAdUnitId(ctx.getResources().getString(adUnitId));
        // Load ads in the main thread
        Handler mainHandler = new Handler(ctx.getMainLooper());
        mainHandler.post(() -> {
            sInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    sInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
            sInterstitialAd.loadAd(new AdRequest.Builder().build());
            Log.v(LOG_TAG, "loadAd() executed");
        });
    }
}
