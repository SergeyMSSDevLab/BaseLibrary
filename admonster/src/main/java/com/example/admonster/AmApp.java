package com.example.admonster;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.ads.InterstitialManager;
import com.mssdevlab.baselib.common.ErrorEMailSender;
import com.mssdevlab.baselib.factory.MenuItemProviders;
import com.mssdevlab.baselib.menuproviders.LinkMenuProvider;
import com.mssdevlab.baselib.menuproviders.UpgradeMenuProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class AmApp extends BaseApplication {
    public static final String OUR_APPS_CONFIG_TAG = "JapApp.OurApps";
    public static final String UPGRADE_CONFIG_TAG = "JapApp.Upgrade";

    public AmApp() {
        this.setReportSender(new ErrorEMailSender(R.string.developers_email_address));
    }

    @NonNull
    @Override
    public String getPublicKey() {
        return "samplePublicKey";
    }

    @NonNull
    @Override
    public List<String> getProductSkus() {
        return  Arrays.asList("android.test.purchased", "android.test.canceled", "android.test.item_unavailable");
    }

    @NonNull
    @Override
    public List<String> getSubscriptionSkus() {
        return Collections.emptyList();
    }

    @Override
    protected void initApplicationInBackground() {
        super.initApplicationInBackground();
        Resources res = AmApp.getInstance().getResources();

        MobileAds.initialize(this, res.getString(R.string.admob_app_id));
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
        RequestConfiguration requestConfiguration
                = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        InterstitialManager.enableAds(R.string.ad_interstitial_unit_id);

        this.setUpgradeActivity(UpgradeActivity.class,
                res.getString(R.string.ad_banner_unit_id),
                res.getString(R.string.app_name),
                res.getString(R.string.developers_email_address),
                res.getString(R.string.ad_reward_unit_id));

        MenuItemProviders.addProvider(OUR_APPS_CONFIG_TAG, new LinkMenuProvider(
                com.mssdevlab.baselib.R.string.bl_common_menu_play_apps,
                R.id.menu_action_play_app,
                R.string.company_url,
                com.mssdevlab.baselib.R.drawable.ic_our_apps_black_24dp
        ));

        MenuItemProviders.addProvider(UPGRADE_CONFIG_TAG, new UpgradeMenuProvider(
                com.mssdevlab.baselib.R.string.bl_common_menu_upgrade,
                com.mssdevlab.baselib.R.string.bl_common_menu_upgrade_noads,
                R.id.menu_action_upgrade,
                com.mssdevlab.baselib.R.drawable.ic_app_update_black_24dp
        ));
    }

}
