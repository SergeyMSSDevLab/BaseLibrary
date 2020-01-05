package com.mssdevlab.baselib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

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
    private static final int PLAY_REQUEST_CODE = 1593;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "OnCreate started");
        BaseApplication.getInstance().handleLastError(this);
        super.onCreate(savedInstanceState);
        InterstitialManager.showInterstitialAd(this, true);
        if (!BaseApplication.checkPlayServices(this, PLAY_REQUEST_CODE)) {
            Toast.makeText(this, "This device is not supported.",
                    Toast.LENGTH_LONG).show(); // TODO: get string from resources
            //finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_REQUEST_CODE){
            if (resultCode == RESULT_CANCELED) {
                finish();   // No sense to display message
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
