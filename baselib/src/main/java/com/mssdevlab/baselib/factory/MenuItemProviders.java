package com.mssdevlab.baselib.factory;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.Menu;

import com.mssdevlab.baselib.BaseActivity;

import java.util.Map;

/**
 * Holds all menu item configurations for an application
 */
public class MenuItemProviders {
    private static final String LOG_TAG = "MenuItemProviders";
    private static final Map<String, MenuItemProvider> sConfigurationMap = new ArrayMap<>(4);
    private static final MutableLiveData<Integer> sMenuItemSelected = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> sInitCompleted = new MutableLiveData<>();

    public static void addProvider(@NonNull String providerTag, @NonNull MenuItemProvider provider){
        sConfigurationMap.put(providerTag, provider);
        Log.v(LOG_TAG, "MenuItemProvider added: " + providerTag);
    }

    public static void attachToActivity(@NonNull final BaseActivity activity, @NonNull final Menu menu, @Nullable String[] tags){
        if (sConfigurationMap.entrySet().size() > 0){
            if (tags != null && tags.length > 0){
                // add passed set of items
                for(int i=0; i < tags.length; i++) {
                    MenuItemProvider p = sConfigurationMap.get(tags[i]);
                    p.attachToActivity(activity, menu);
                }
            } else {
                // add all items from the map
                for (MenuItemProvider p : sConfigurationMap.values())
                {
                    p.attachToActivity(activity, menu);
                }
            }
        }
    }

    public static void setMenuItemSelected(int menuItemId) {
        Log.v(LOG_TAG, "menuItemSelected");
        sMenuItemSelected.setValue(menuItemId);
    }

    public static LiveData<Integer> menuItemSelected()
    {
        return sMenuItemSelected;
    }

    public static void setInitCompleted() {
        Log.v(LOG_TAG, "setInitCompleted");
        sInitCompleted.postValue(true);
    }

    public static LiveData<Boolean> getInitCompleted()
    {
        return sInitCompleted;
    }
}
