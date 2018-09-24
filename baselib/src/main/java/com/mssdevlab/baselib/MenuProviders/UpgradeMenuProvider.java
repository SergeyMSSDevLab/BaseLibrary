package com.mssdevlab.baselib.MenuProviders;

import android.arch.lifecycle.Observer;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuItem;

import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.factory.MenuItemProvider;
import com.mssdevlab.baselib.factory.MenuItemProviders;

public class UpgradeMenuProvider extends MenuItemProvider {
    @StringRes private final int resTitle;
    @IdRes private final int resId;
    @StringRes private final int resLink;
    @DrawableRes private final int resIcon;

    public UpgradeMenuProvider(@StringRes int title, @IdRes int id, @StringRes int link, @DrawableRes int icon) {
        this.resId = id;
        this.resTitle = title;
        this.resLink = link;
        this.resIcon = icon;
    }

    @Override
    public void attachToActivity(@NonNull final BaseActivity activity, @NonNull final Menu menu, @IdRes int groupId) {
        MenuItem item = menu.findItem(this.resId);
        if (item == null){
            item = menu.add(groupId, resId, Menu.NONE, resTitle);
            item.setIcon(this.resIcon);

            MenuItemProviders.menuItemSelected().observe(activity, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable final Integer menuId) {
                    if (menuId != null && menuId == resId){
                        BaseApplication.startUpgradeScreen(activity);
                    }
                }
            });
        }
    }
}
