package com.mssdevlab.baselib;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mssdevlab.baselib.ApplicationMode.AppViewModel;
import com.mssdevlab.baselib.ads.InterstitialManager;
import com.mssdevlab.baselib.factory.MenuItemProviders;

/**
 * Class connects child activity with baselib stuff
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = "BaseActivity";

    private AppViewModel mAppViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "OnCreate started");
        BaseApplication.getInstance().handleLastError(this);
        BaseApplication.handleGooglePlayState(this);
        super.onCreate(savedInstanceState);
        InterstitialManager.showInterstitialAd(this, true);
    }

    public AppViewModel getAppViewModel(){
        if (this.mAppViewModel == null){
            ViewModelProvider provider = new ViewModelProvider(this);
            this.mAppViewModel = provider.get(AppViewModel.class);
        }
        return this.mAppViewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume started");
        this.mAppViewModel.loadPurchases(this);
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
