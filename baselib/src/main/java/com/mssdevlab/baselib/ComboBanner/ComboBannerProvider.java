package com.mssdevlab.baselib.ComboBanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.factory.CommonViewProvider;

/**
 * Provider for both ads banner and promotion dialog in the same view
 */
public class ComboBannerProvider extends CommonViewProvider {
    private static final String LOG_TAG = "ComboBannerProvider";

    private String developerEmail;
    private String adUnitId;
    private String appName;

    public ComboBannerProvider(String developerEmail, String adUnitId, String appName) {
        Log.v(LOG_TAG, "Constructor");
        this.developerEmail = developerEmail;
        this.adUnitId = adUnitId;
        this.appName = appName;
    }

    @Override
    public void createView(@NonNull BaseActivity activity, @NonNull Bundle args) {
        Log.v(LOG_TAG, "createView");
        args.putString(ComboBannerObserver.ARG_DEV_EMAIL, this.developerEmail);
        args.putString(ComboBannerObserver.ARG_AD_UNIT_ID, this.adUnitId);
        args.putString(ComboBannerObserver.ARG_APP_NAME, this.appName);

        activity.getLifecycle().addObserver(new ComboBannerObserver(activity, args));
    }
}
