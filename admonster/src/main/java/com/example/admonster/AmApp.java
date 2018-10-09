package com.example.admonster;

import android.content.res.Resources;

import com.google.android.gms.ads.MobileAds;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.combobanner.ComboBannerProvider;
import com.mssdevlab.baselib.common.ErrorEMailSender;
import com.mssdevlab.baselib.factory.CommonViewProviders;
import com.mssdevlab.baselib.factory.MenuItemProviders;
import com.mssdevlab.baselib.menuproviders.LinkMenuProvider;
import com.mssdevlab.baselib.menuproviders.UpgradeMenuProvider;


public class AmApp extends BaseApplication {
    public static final String COMBO_CONFIG_TAG = "MainActivity.ComboBanner";
    public static final String OUR_APPS_CONFIG_TAG = "JapApp.OurApps";
    public static final String UPGRADE_CONFIG_TAG = "JapApp.Upgrade";

    public AmApp() {
        this.setReportSender(new ErrorEMailSender(R.string.developers_email_address));
    }

    @Override
    protected void initApplicationInBackground() {
        super.initApplicationInBackground();
        Resources res = AmApp.getInstance().getResources();

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        this.setUpgradeActivity(UpgradeActivity.class);

        CommonViewProviders.addProvider(COMBO_CONFIG_TAG, new ComboBannerProvider(
                res.getString(R.string.developers_email_address),
                "ca-app-pub-3940256099942544/6300978111",
                res.getString(R.string.app_name)));

        MenuItemProviders.addProvider(OUR_APPS_CONFIG_TAG, new LinkMenuProvider(
                com.mssdevlab.baselib.R.string.common_menu_play_apps,
                com.mssdevlab.baselib.R.id.menu_action_play_app,
                R.string.company_url,
                com.mssdevlab.baselib.R.drawable.ic_our_apps_black_24dp
        ));

        MenuItemProviders.addProvider(UPGRADE_CONFIG_TAG, new UpgradeMenuProvider(
                com.mssdevlab.baselib.R.string.common_menu_upgrade,
                com.mssdevlab.baselib.R.id.menu_action_upgrade,
                R.string.company_url,
                com.mssdevlab.baselib.R.drawable.ic_app_update_black_24dp
        ));
    }

}
