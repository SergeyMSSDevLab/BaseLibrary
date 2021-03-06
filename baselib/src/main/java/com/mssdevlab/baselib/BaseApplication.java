package com.mssdevlab.baselib;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mssdevlab.baselib.ApplicationMode.AppViewModel;
import com.mssdevlab.baselib.common.ErrorActivity;
import com.mssdevlab.baselib.common.GPErrorActivity;
import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.common.MessageSender;
import com.mssdevlab.baselib.common.PromoteManager;
import com.mssdevlab.baselib.factory.CommonViewProviders;
import com.mssdevlab.baselib.factory.MenuItemProviders;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Base class for MSSDevLab Application object
 */
@SuppressWarnings("unused")
public abstract class BaseApplication  extends Application implements Thread.UncaughtExceptionHandler {
    private static final String LOG_TAG = "BaseApplication";
    private static final String NAME_FILE_DATA = "error.data";
    private static BaseApplication sCurInstance;
    private static Thread.UncaughtExceptionHandler sOriginalHandler;
    private static Class<?> sUpgradeActivityClass;
    private static String sUpgradeActivityAdMobUnitId;
    private static String sUpgradeActivityRewardedUnitId;
    private static String sUpgradeActivityAppName;
    private static String sUpgradeActivityDevEmail;

    public static final String SHARED_PREF = "PromoteApp";
    public static final String EXTRA_ADMOB_UNIT_ID = "BaseApplication.adMobUnitId";
    public static final String EXTRA_REWARDED_UNIT_ID = "BaseApplication.adRewardedUnitId";
    public static final String EXTRA_APP_NAME = "BaseApplication.appname";
    public static final String EXTRA_DEV_EMAIL = "BaseApplication.devemail";
    public static final String EXTRA_MANAGE_PARENT = "BaseApplication.manageParent";

    public static MessageSender reportSender = null;

    private final Object syncObject = new Object();

    public static BaseApplication getInstance() {
        return sCurInstance;
    }

    @CallSuper
    protected void setReportSender(@NonNull MessageSender sender){
        reportSender = sender;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "onCreate");

        if (sCurInstance == null) {
            sCurInstance = this;
            sOriginalHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        Thread.setDefaultUncaughtExceptionHandler(this);

        new Thread(() -> {
            try {
                AppViewModel.initAppMode();
                initApplicationInBackground();

                // Inform observers that configuration finished
                CommonViewProviders.setInitCompleted();
                MenuItemProviders.setInitCompleted();
            } catch (Throwable ex) {
                try {
                    createReportFile(ex);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "onCreate", e);
                }
            }
        }).start();
    }

    public static boolean checkPlayServices(Activity activity, int requestCode){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getInstance());
        if (resultCode != ConnectionResult.SUCCESS && activity != null) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {

                Dialog updateDialog = googleApiAvailability.getErrorDialog(activity, resultCode, requestCode);
                if (updateDialog != null){
                    updateDialog.show();
                }
            }
        }

        return resultCode == ConnectionResult.SUCCESS;
    }

    public static void handleGooglePlayState(Activity activity) {
        Log.v(LOG_TAG, "handleGooglePlayState");
        if (activity != null){
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
            if (resultCode != ConnectionResult.SUCCESS) {
                Intent intent = new Intent(activity, GPErrorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }

    @NonNull
    public abstract String getPublicKey();

    @NonNull
    public abstract List<String> getProductSkus();

    @NonNull
    public abstract List<String> getSubscriptionSkus();

    @CallSuper
    protected void initApplicationInBackground(){
        PromoteManager.markStarting();
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        Log.v(LOG_TAG, "uncaughtException");
        try {
            createReportFile(ex);
        } catch (Exception e) {
            Log.e(LOG_TAG, "uncaughtException", e);
        } finally {
            if (sOriginalHandler != null) {
                sOriginalHandler.uncaughtException(thread, ex);
            } else {
                System.exit(0);
            }
        }
    }

    private void createReportFile(Throwable ex) throws IOException {
        // Only create the error report if a report sender is specified
        if (reportSender != null) {
            synchronized (syncObject) {
                String report = this.CreateReport4Throwable(ex);
                RandomAccessFile file = null;
                try {
                    file = new RandomAccessFile(this.getFileStreamPath(NAME_FILE_DATA), "rw");
                    file.seek(file.length());
                    file.write(report.getBytes(StandardCharsets.UTF_8));
                } finally {
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e) {
                            Log.e(LOG_TAG,"createReportFile", e);
                        }
                    }
                }
            }
        }
    }

    public void handleLastError(Activity activity) {
        Log.v(LOG_TAG, "handleLastError");
        final String message = this.GetLastExceptionReport();
        if (message != null) {

            if (reportSender != null) {
                Intent intent = new Intent(this, ErrorActivity.class);
                intent.putExtra(ErrorActivity.ERROR_REPORT_CONTENT, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }

    private String GetLastExceptionReport() {
        Log.v(LOG_TAG, "GetLastExceptionReport");
        String ret = null;
        synchronized (this.syncObject) {
            RandomAccessFile file = null;
            try {
                file = new RandomAccessFile(this.getFileStreamPath(NAME_FILE_DATA), "rw");
                if (file.length() > 0) {
                    byte[] fContent = new byte[(int) file.length()];
                    file.readFully(fContent);
                    ret = new String(fContent, StandardCharsets.UTF_8);
                    file.setLength(0); // Always truncate file
                }
            } catch (IOException e) {
                Log.v(LOG_TAG, "GetLastExceptionReport", e);
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e) {
                        Log.v(LOG_TAG, "GetLastExceptionReport", e);
                    }
                }
            }
        }

        return ret;
    }

    protected String CreateReport4Throwable(final Throwable e) {
        Log.v(LOG_TAG, "CreateReport4Throwable");
        return Helper.CreateReport4Throwable(e, this);
    }

    protected void setUpgradeActivity(@SuppressWarnings("SameParameterValue") Class<?> cls,
                                      String adMobUnitId,
                                      String appName,
                                      String devEmail,
                                      String rewardedUnitId) {
        sUpgradeActivityClass = cls;
        sUpgradeActivityAdMobUnitId = adMobUnitId;
        sUpgradeActivityAppName = appName;
        sUpgradeActivityDevEmail = devEmail;
        sUpgradeActivityRewardedUnitId = rewardedUnitId;
    }

    public static void startUpgradeScreen(Activity ctx){
        Intent intent = new Intent(ctx, sUpgradeActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(EXTRA_ADMOB_UNIT_ID, sUpgradeActivityAdMobUnitId);
        intent.putExtra(EXTRA_REWARDED_UNIT_ID, sUpgradeActivityRewardedUnitId);
        intent.putExtra(EXTRA_APP_NAME, sUpgradeActivityAppName);
        intent.putExtra(EXTRA_DEV_EMAIL, sUpgradeActivityDevEmail);
        ctx.startActivity(intent);
    }
}
