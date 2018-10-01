package com.mssdevlab.baselib.factory;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import android.view.Menu;

import com.mssdevlab.baselib.BaseActivity;

/*
Base class for all menu item providers
 */
public abstract class MenuItemProvider {

    public abstract void attachToActivity(@NonNull final BaseActivity activity,
                                          @NonNull final Menu menu,
                                          @IdRes int groupId);
}
