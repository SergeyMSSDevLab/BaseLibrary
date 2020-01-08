package com.mssdevlab.baselib;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.mssdevlab.baselib.ads.InterstitialManager;
import com.mssdevlab.baselib.factory.MenuItemProviders;

/**
 * Class connects child activity with baselib stuff
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "OnCreate started");
        BaseApplication.getInstance().handleLastError(this);
        BaseApplication.handleGooglePlayState(this);
        super.onCreate(savedInstanceState);
        InterstitialManager.showInterstitialAd(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume started");
    }

    protected void addCommonMenuItems(final Menu menu,
                                      @IdRes final int groupId,
                                      @Nullable final String[] tags){
        final BaseActivity self = this;
        MenuItemProviders.getInitCompleted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean initCompleted) {
                if (initCompleted != null && initCompleted){
                    MenuItemProviders.getInitCompleted().removeObserver(this);
                    MenuItemProviders.attachToActivity(self, menu, groupId, tags);
                }
            }
        });
    }

    protected void onMenuItemSelected(int menuItemId){
        MenuItemProviders.setMenuItemSelected(menuItemId);
    }
}
