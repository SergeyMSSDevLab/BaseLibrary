package com.mssdevlab.baselib.menuproviders;

import android.view.Menu;
import android.view.MenuItem;

import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.ApplicationMode.AppMode;
import com.mssdevlab.baselib.ApplicationMode.ApplicationData;
import com.mssdevlab.baselib.common.Event;
import com.mssdevlab.baselib.factory.MenuItemProvider;
import com.mssdevlab.baselib.factory.MenuItemProviders;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.Observer;

public class UpgradeMenuProvider extends MenuItemProvider {
    @StringRes private final int resTitle;
    @IdRes private final int resId;
    @DrawableRes private final int resIcon;

    public UpgradeMenuProvider(@StringRes int title, @IdRes int id, @DrawableRes int icon) {
        this.resId = id;
        this.resTitle = title;
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
            MenuItem item = menu.findItem(this.resId);
            if (appMode == AppMode.MODE_PRO){
                if (item != null){
                    menu.removeItem(this.resId);
                    MenuItemProviders.menuItemSelected().removeObserver(menuObserver);
                }
            } else {
                if (item == null){
                    item = menu.add(groupId, resId, Menu.NONE, resTitle);
                    item.setIcon(this.resIcon);
                    MenuItemProviders.menuItemSelected().observe(activity, menuObserver);
                }
            }
        });
    }
}
