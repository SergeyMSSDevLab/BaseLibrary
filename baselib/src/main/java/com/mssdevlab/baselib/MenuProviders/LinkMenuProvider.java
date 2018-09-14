package com.mssdevlab.baselib.MenuProviders;

import android.arch.lifecycle.Observer;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuItem;

import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.factory.MenuItemProvider;
import com.mssdevlab.baselib.factory.MenuItemProviders;

public class LinkMenuProvider extends MenuItemProvider {
    private final int resTitle;
    private final int resId;
    private final int resLink;

    public LinkMenuProvider(@StringRes int title, @IdRes int id, @StringRes int link) {
        this.resId = id;
        this.resTitle = title;
        this.resLink = link;
    }

    @Override
    public void attachToActivity(@NonNull final BaseActivity activity, @NonNull final Menu menu) {
        MenuItem item = menu.add(Menu.NONE, resId, Menu.NONE, resTitle);
        item.setIcon(R.drawable.ic_action_our_apps_dark);

        MenuItemProviders.menuItemSelected().observe(activity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer menuId) {
                if (menuId != null && menuId == resId){
                    Helper.openUrl(resLink, activity);
                }
            }
        });
    }
}
