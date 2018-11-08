package com.mssdevlab.baselib.ads;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.AppMode;
import com.mssdevlab.baselib.common.ApplicationData;

import androidx.annotation.CallSuper;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import static com.mssdevlab.baselib.BaseApplication.SHARED_PREF;

public class InterstitialManager {
    private static final String LOG_TAG = "InterstitialManager";
    private static final String PREF_LAUNCHES = "InterstitialManager.Launches";
    private static final String PREF_LAST_INDEX = "InterstitialManager.last.Index";
    private static final String PREF_LAST_LAUNCHES = "InterstitialManager.last.Launches";
    private static final long SHOWING_DELAY  = 1000L * 60 * 9;

    private static int[] sScheduleArr = {0, 1, 3, 7};
    private static InterstitialAd sInterstitialAd;
    private static long sLastShownTime = 0L;

    public static void showInterstitialAd(Activity activity, boolean showWarning){
        if (sInterstitialAd != null && sInterstitialAd.isLoaded()) {
            AppMode mode = ApplicationData.getApplicationMode().getValue();
            if (mode != AppMode.MODE_NO_ADS && mode != AppMode.MODE_PRO) {
                long passedTime = System.currentTimeMillis() - sLastShownTime;
                if (passedTime > SHOWING_DELAY){
                    if (showWarning){
                        showUpgradeWarning(activity);
                    } else {
                        sInterstitialAd.show();
                        sLastShownTime = System.currentTimeMillis();
                        // TODO: save information for the next session
                    }
                }
            }
        }
    }

    private static void showUpgradeWarning(Activity activity){
        // TODO: complete
        View checkBoxView = View.inflate(activity, R.layout.checkbox, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Save to shared preferences
            }
        });
        checkBox.setText("Text to the right of the check box.");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(" MY_TEXT");
        builder.setMessage(" MY_TEXT ")
                .setView(checkBoxView)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: go to upgrade
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: show ads
                        dialog.cancel();
                    }
                }).show();
    }

    public static void enableAds(@StringRes int adUnitId){
        Log.v(LOG_TAG, "enableAds");

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
        setUpAd(ctx, adUnitId); // TODO: complete checking first
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
