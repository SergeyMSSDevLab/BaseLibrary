package com.mssdevlab.baselib.menuproviders;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.Observer;

import com.mssdevlab.baselib.ApplicationMode.AppMode;
import com.mssdevlab.baselib.ApplicationMode.ApplicationData;
import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.common.Event;
import com.mssdevlab.baselib.factory.MenuItemProvider;
import com.mssdevlab.baselib.factory.MenuItemProviders;

public class UpgradeMenuProvider extends MenuItemProvider {
    @StringRes private final int resTitle;
    @StringRes private final int resNoAdsTitle;
    @IdRes private final int resId;
    @DrawableRes private final int resIcon;

    public UpgradeMenuProvider(@StringRes int title, @StringRes int noAdsTitle, @IdRes int id, @DrawableRes int icon) {
        this.resId = id;
        this.resTitle = title;
        this.resNoAdsTitle = noAdsTitle;
        this.resIcon = icon;
    }

    @Override
    public void attachToActivity(@NonNull final BaseActivity activity, @NonNull final Menu menu, @IdRes int groupId) {
        final Observer<Event<Integer>> menuObserver = menuEvent -> {
            if (menuEvent == null){
                return;
            }
            Integer menuId = menuEvent.peekValue();
            if (menuId != null && menuId == resId){
                menuId = menuEvent.getValueIfNotHandled();  // Mark the event as handled
                if (menuId != null){
                    BaseApplication.startUpgradeScreen(activity);
                }
            }
        };

        ApplicationData.getApplicationMode().observe(activity, appMode -> {
            int menuTitle = resTitle;
            if (appMode == AppMode.MODE_PRO
                || appMode == AppMode.MODE_NO_ADS) {
                menuTitle = resNoAdsTitle;
            }
            MenuItem item = menu.findItem(this.resId);
            if (item == null) {
                item = menu.add(groupId, resId, Menu.NONE, menuTitle);
                item.setIcon(this.resIcon);
                MenuItemProviders.menuItemSelected().observe(activity, menuObserver);
            } else {
                item.setTitle(menuTitle);
            }
        });
    }
}
