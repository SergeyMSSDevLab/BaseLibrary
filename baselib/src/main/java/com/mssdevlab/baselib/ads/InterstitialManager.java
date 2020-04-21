package com.mssdevlab.baselib.ads;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mssdevlab.baselib.ApplicationMode.AppMode;
import com.mssdevlab.baselib.ApplicationMode.AppViewModel;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.R;

import java.util.Timer;
import java.util.TimerTask;

import static com.mssdevlab.baselib.BaseApplication.SHARED_PREF;

public class InterstitialManager {
    private static final String LOG_TAG = "InterstitialManager";
    private static final String PREF_LAUNCHES = "InterstitialManager.Launches";
    private static final String PREF_LAST_INDEX = "InterstitialManager.last.Index";
    private static final String PREF_LAST_LAUNCHES = "InterstitialManager.last.Launches";
    private static final String PREF_SHOW_MODE_WARNING = "InterstitialManager.mode.warning";
    private static final long SHOWING_DELAY  = 1000L * 60 * 9;

    private static int[] sScheduleArr = {1, 3, 5, 7};
    private static long sLastShownTime = 0L;
    private static final MutableLiveData<InterstitialAd> sInterstitialAd = new MutableLiveData<>();
    private static Observer<InterstitialAd> sAdObserver;

    public static void showInterstitialAd(final AppCompatActivity activity, boolean showWarning){
        Log.v(LOG_TAG, "showInterstitialAd");
        if (!sInterstitialAd.hasActiveObservers()){
            Log.v(LOG_TAG, "showInterstitialAd adding observer");
            sAdObserver = interstitialAd -> showAd(activity, interstitialAd, showWarning);
            sInterstitialAd.observe(activity, sAdObserver);
        }
    }

    private static void showAd(final AppCompatActivity activity, final InterstitialAd ad, boolean showWarning){
        Log.v(LOG_TAG, "showAd ad != null:" + (ad != null));
        if (sAdObserver != null){
            sInterstitialAd.removeObserver(sAdObserver);
            sAdObserver = null;
            Log.v(LOG_TAG, "showAd observer removed");
        }
        if (ad != null) {
            if (ad.isLoaded()){
                Log.v(LOG_TAG, "showAd isLoaded:true");
                onAdLoadedInternal(activity, ad, showWarning);
            } else {
                Log.v(LOG_TAG, "showAd adding listener");
                ad.setAdListener(new AdListener(){
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        onAdLoadedInternal(activity, ad, showWarning);
                    }
                });

                if (activity != null && !ad.isLoading()) {
                    new Timer().schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            activity.runOnUiThread(() -> {
                                ad.loadAd(new AdRequest.Builder().build());
                                Log.v(LOG_TAG, "showAd: request ads");
                            });
                        }
                    }, 500);
                }
            }
        }
    }

    private static void onAdLoadedInternal(final AppCompatActivity activity, final InterstitialAd ad, boolean showWarning){
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(BaseApplication.getInstance());
        AppViewModel viewModel = factory.create(AppViewModel.class);

        AppMode mode = viewModel.getApplicationMode().getValue();
        if (mode == null){
            mode = AppMode.MODE_DEMO;
        }

        if (mode.ordinal() < AppMode.MODE_NO_ADS.ordinal()) {
            long passedTime = System.currentTimeMillis() - sLastShownTime;
            Log.v(LOG_TAG, "onAdLoadedInternal passedTime > SHOWING_DELAY:" + (passedTime > SHOWING_DELAY));
            if (passedTime > SHOWING_DELAY) {
                final SharedPreferences sharedPref = activity.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                final Resources res = activity.getResources();
                if (showWarning && sharedPref.getBoolean(PREF_SHOW_MODE_WARNING, true)) {
                    View checkBoxView = View.inflate(activity, R.layout.checkbox, null);
                    CheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SharedPreferences.Editor spEditor = sharedPref.edit();
                        spEditor.putBoolean(PREF_SHOW_MODE_WARNING, !isChecked);
                        spEditor.apply();
                    });
                    checkBox.setText(R.string.bl_ads_hide_checkbox_title);

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.bl_ads_notification_title);
                    builder.setMessage(res.getStringArray(R.array.bl_ads_notification_message_array)[mode.ordinal()])
                            .setView(checkBoxView)
                            .setCancelable(false)
                            .setPositiveButton(R.string.bl_ads_upgrade_button_title, (dialog, id) -> {
                                clearAdListener(ad);
                                BaseApplication.startUpgradeScreen(activity);
                            })
                            .setNegativeButton(res.getString(R.string.bl_ads_continue_button_title,
                                    res.getStringArray(R.array.bl_common_app_mode_array)[mode.ordinal()]),
                                    (dialog, id) -> {
                                        showAdScreen(ad, sharedPref);
                                        dialog.cancel();
                                    }).show();
                } else {
                    showAdScreen(ad, sharedPref);
                }
            }
        }
    }

    // Shows ad and remove listeners
    private static void showAdScreen(InterstitialAd ad, SharedPreferences sharedPref){
        ad.show();
        clearAdListener(ad);
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

    private static void clearAdListener(InterstitialAd ad){
        ad.setAdListener(null);
        ad.loadAd(new AdRequest.Builder().build());
    }

    public static void enableAds(@StringRes int adUnitId){
        Log.v(LOG_TAG, "enableAds");
        BaseApplication ctx = BaseApplication.getInstance();
        ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(ctx);
        AppViewModel viewModel = factory.create(AppViewModel.class);

        Handler mainHandler = new Handler(ctx.getMainLooper());
        mainHandler.post(() -> {
            viewModel.getApplicationMode().observeForever(appMode -> enableDisableAds(appMode, adUnitId));
            Log.v(LOG_TAG, "enableAds() appMode observer added");
        });
    }

    private static void enableDisableAds(AppMode mode, int adUnitId){
        Log.v(LOG_TAG, "enableDisableAds");
        if (mode == null){
            mode = AppMode.MODE_DEMO;
        }
        if (mode.ordinal() < AppMode.MODE_NO_ADS.ordinal()){
            if (sInterstitialAd.getValue() == null){
                new Thread(() -> enableAdsInBackground(adUnitId)).start();
            }
        } else {
            setInterstitialAd(null);
            Log.v(LOG_TAG, "enableDisableAds: InterstitialAd disabled");
        }
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
        final InterstitialAd ad = new InterstitialAd(ctx);
        ad.setAdUnitId(ctx.getResources().getString(adUnitId));
        setInterstitialAd(ad);
        // Load ads in the main thread
        Handler mainHandler = new Handler(ctx.getMainLooper());
        mainHandler.post(() -> {
            ad.loadAd(new AdRequest.Builder().build());
            Log.v(LOG_TAG, "loadAd() executed");
        });
    }

    private static void setInterstitialAd(InterstitialAd val){
        Log.v(LOG_TAG, "setValueInternal: " + val);
        if (Looper.myLooper() == Looper.getMainLooper()){
            sInterstitialAd.setValue(val);
        } else {
            sInterstitialAd.postValue(val);
        }
    }

}
