package com.mssdevlab.baselib;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import android.util.Log;

import com.mssdevlab.baselib.ApplicationMode.AppModeManager;
import com.mssdevlab.baselib.common.ErrorActivity;
import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.common.MessageSender;
import com.mssdevlab.baselib.common.PromoteManager;
import com.mssdevlab.baselib.factory.CommonViewProviders;
import com.mssdevlab.baselib.factory.MenuItemProviders;

import java.io.IOException;
import java.io.RandomAccessFile;

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
// TODO: check google play compatibility
        if (sCurInstance == null) {
            sCurInstance = this;
            sOriginalHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        Thread.setDefaultUncaughtExceptionHandler(this);

        new Thread(() -> {
            try {
                AppModeManager.checkAppMode();
                initApplicationInBackground();

                // Inform observers that configuration finished
                CommonViewProviders.setInitCompleted();
                MenuItemProviders.setInitCompleted();
            } catch (Throwable ex) {
                try {
                    createReportFile(ex);
                } catch (Exception e) {
                    Log.v(LOG_TAG, e.getMessage());
                }
            }
        }).start();
    }

    @CallSuper
    protected void initApplicationInBackground(){
        PromoteManager.markStarting();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.v(LOG_TAG, "uncaughtException");
        try {
            createReportFile(ex);
        } catch (Exception e) {
            Log.v(LOG_TAG, e.getMessage());
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
                    file.write(report.getBytes("UTF-8"));
                } finally {
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e) {
                            Log.v(LOG_TAG, e.getMessage());
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
                    ret = new String(fContent, "UTF-8");
                    file.setLength(0); // Always truncate file
                }
            } catch (IOException e) {
                Log.v(LOG_TAG, e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e) {
                        Log.v(LOG_TAG, e.getMessage());
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

    protected void setUpgradeActivity(Class<?> cls,
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
