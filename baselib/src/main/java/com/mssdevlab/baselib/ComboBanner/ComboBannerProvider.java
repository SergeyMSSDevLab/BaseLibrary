package com.mssdevlab.baselib.ComboBanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.mssdevlab.baselib.factory.CommonViewProvider;

/**
 * Provider for both ads banner and promotion dialog in the same view
 */
public class ComboBannerProvider extends CommonViewProvider {
    private String developerEmail;
    private String adUnitId;
    private String appName;

    public ComboBannerProvider(String developerEmail, String adUnitId, String appName) {
        this.developerEmail = developerEmail;
        this.adUnitId = adUnitId;
        this.appName = appName;
    }

    @Override
    public @NonNull Fragment getFragment(Bundle args) {
        ComboBannerFragment fragment = new ComboBannerFragment();

        args.putString(ComboBannerFragment.ARG_DEV_EMAIL, this.developerEmail);
        args.putString(ComboBannerFragment.ARG_AD_UNIT_ID, this.adUnitId);
        args.putString(ComboBannerFragment.ARG_APP_NAME, this.appName);
        fragment.setArguments(args);

        return fragment;
    }
}
