package com.mssdevlab.baselib.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Common Helpers
 */
public class Helper {
    private static final String LOG_TAG = "Helper";

    public static String CreateReport4Throwable(final Throwable e, Application app) {
        Log.v(LOG_TAG, "CreateReport4Throwable");
        final String DOUBLE_LINE_SEP = "\n\n";
        final String SINGLE_LINE_SEP = "\n";
        final String LINE_SEP = "-------------------------------" + DOUBLE_LINE_SEP;

        StackTraceElement[] arr = e.getStackTrace();
        DateFormat df = DateFormat.getDateTimeInstance();
        Calendar cal = Calendar.getInstance();

        final StringBuilder report = new StringBuilder(e.toString());
        report.append(DOUBLE_LINE_SEP);
        report.append(df.format(cal.getTime()));
        report.append(SINGLE_LINE_SEP);
        report.append("--------- Stack trace ---------");
        report.append(DOUBLE_LINE_SEP);
        for(StackTraceElement el : arr){
            report.append("    ");
            report.append(el.toString());
            report.append(SINGLE_LINE_SEP);
        }
        report.append(LINE_SEP);

        report.append("--------- Cause ---------");
        report.append(DOUBLE_LINE_SEP);
        Throwable cause = e.getCause();
        if (cause != null) {
            report.append(cause.toString());
            report.append(DOUBLE_LINE_SEP);
            arr = cause.getStackTrace();
            for(StackTraceElement el : arr){
                report.append("    ");
                report.append(el.toString());
                report.append(SINGLE_LINE_SEP);
            }
        }
        report.append(LINE_SEP);
        report.append("Package: ");
        report.append(app.getPackageName());
        report.append(SINGLE_LINE_SEP);
        report.append(LINE_SEP);
        report.append("Package version code: ");
        report.append(getVersion(app));
        report.append(SINGLE_LINE_SEP);
        report.append(LINE_SEP);
        report.append("Build.BRAND: ");
        report.append(Build.BRAND);
        report.append(SINGLE_LINE_SEP);
        report.append("Build.DEVICE: ");
        report.append(Build.DEVICE);
        report.append(SINGLE_LINE_SEP);
        report.append("Build.MODEL: ");
        report.append(Build.MODEL);
        report.append(SINGLE_LINE_SEP);
        report.append("Build.ID: ");
        report.append(Build.ID);
        report.append(SINGLE_LINE_SEP);
        report.append("Build.PRODUCT: ");
        report.append(Build.PRODUCT);
        report.append(SINGLE_LINE_SEP);
        report.append("Locale: ");
        report.append(Locale.getDefault().toString());
        report.append(SINGLE_LINE_SEP);
        report.append(LINE_SEP);
        report.append("Build.VERSION.SDK_INT: ");
        report.append(Build.VERSION.SDK_INT);
        report.append(SINGLE_LINE_SEP);
        report.append("Build.VERSION.RELEASE: ");
        report.append(Build.VERSION.RELEASE);
        report.append(SINGLE_LINE_SEP);
        report.append("Build.VERSION.INCREMENTAL: ");
        report.append(Build.VERSION.INCREMENTAL);
        report.append(SINGLE_LINE_SEP);
        report.append(LINE_SEP);

        String rep = report.toString();
        Log.v(LOG_TAG, rep);
        return rep;
    }

    private static int getVersion(Application app) {
        int v = 0;
        try {
            v = app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Huh? Really?
        }
        return v;
    }

    /*
    * Open url in a default browser
    */
    public static void openUrl(@StringRes int gotoUrl, @NonNull final Context context) {
        Log.v(LOG_TAG, "openURL");
        try {
            Uri uri = Uri.parse(context.getString(gotoUrl));
            Intent goToIntent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(goToIntent);

        } catch (Exception ex) {
            Log.e(LOG_TAG, "openURL fails: " + ex.getMessage());
        }
    }

    private static ViewGroup getParent(View view) {
        return (ViewGroup)view.getParent();
    }

    private static void removeView(View view) {
        ViewGroup parent = getParent(view);
        if(parent != null) {
            parent.removeView(view);
        }
    }

    public static void replaceView(View currentView, View newView) {
        final ViewGroup parent = getParent(currentView);
        if(parent == null) {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        newView.setId(currentView.getId());
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

}
