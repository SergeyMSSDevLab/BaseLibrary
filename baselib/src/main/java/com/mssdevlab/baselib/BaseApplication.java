package com.mssdevlab.baselib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.common.MessageSender;
import com.mssdevlab.baselib.common.PromoteScreenManager;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Base class for MSSDevLab Application object
 */
@SuppressWarnings("unused")
public abstract class BaseApplication  extends Application implements Thread.UncaughtExceptionHandler {
    private static final String LOG_TAG = "BaseApplication";
    private static final String NAME_FILE_DATA = "error.data";
    private static BaseApplication curInstance;
    private static Thread.UncaughtExceptionHandler originalHandler;
    private static MessageSender reportSender = null;

    private final Object syncObject = new Object();

    public static BaseApplication getInstance() {
        return curInstance;
    }

    @CallSuper
    protected void setReportSender(@NonNull MessageSender sender){
        reportSender = sender;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "onCreate");
        if (curInstance == null) {
            curInstance = this;
            originalHandler = Thread.getDefaultUncaughtExceptionHandler();
            PromoteScreenManager.MarkStarting(this);
        }

        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.v(LOG_TAG, "uncaughtException");
        try {
            // Only create the error report if a report sender is specified
            if (reportSender != null){
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
        } catch (Exception e) {
            Log.v(LOG_TAG, e.getMessage());
        } finally {
            if (originalHandler != null) {
                originalHandler.uncaughtException(thread, ex);
            } else {
                System.exit(0);
            }
        }

    }

    @CheckResult
    public boolean hasError(final Activity context) {
        Log.v(LOG_TAG, "ShowErrorReport");
        final String message = this.GetLastExceptionReport();
        if (message != null) {

            if (reportSender != null){
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                final Resources res = context.getResources();
                dialog.setTitle(res.getString(R.string.common_error_report_email_subject));
                dialog.setMessage(res.getString(R.string.common_error_report_dialog_text));
                dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        reportSender.send(context, message);
                        context.finish();
                    }
                });

                dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        context.finish();
                    }
                });
                dialog.create().show();
            }

            return true;
        }

        return false;
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
}
