package com.mssdevlab.baselib.ComboBanner;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;

import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.factory.CommonViewProvider;
import com.mssdevlab.baselib.factory.CommonViewProviders;

/**
 * Provider for both ads banner and promotion dialog in the same view
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ComboBannerProvider extends CommonViewProvider {
    private static final String LOG_TAG = "ComboBannerProvider";

    private final String developerEmail;
    private final String adUnitId;
    private final String appName;

    public ComboBannerProvider(String developerEmail, String adUnitId, String appName) {
        Log.v(LOG_TAG, "Constructor");
        this.developerEmail = developerEmail;
        this.adUnitId = adUnitId;
        this.appName = appName;
    }

    @Override
    public void attachToActivity(final @NonNull BaseActivity activity, final @NonNull Bundle args) {
        Log.v(LOG_TAG, "attachToActivity");

        ComboBannerViewModel model = ViewModelProviders.of(activity).get(ComboBannerViewModel.class);
        model.viewStubId = args.getInt(CommonViewProviders.ARG_VIEWSTUB_TAG);
        model.instanceTag = args.getString(CommonViewProviders.ARG_INSTANCE_TAG);
        model.adUnitId = this.adUnitId;
        model.appName = this.appName;
        model.devEmail = this.developerEmail;

        final ComboBannerObserver observer = new ComboBannerObserver(activity);
        activity.getLifecycle().addObserver(observer);
    }
}
