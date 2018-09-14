package com.mssdevlab.baselib;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.mssdevlab.baselib.factory.CommonViewProvider;
import com.mssdevlab.baselib.factory.CommonViewProviders;
import com.mssdevlab.baselib.factory.MenuItemProviders;

/**
 * Class connects child activity with baselib stuff
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = "BaseActivity";

    protected void addCommonMenuItems(Menu menu, @Nullable String[] tags){
        final BaseActivity self = this;
        MenuItemProviders.getInitCompleted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean initCompleted) {
                if (initCompleted != null && initCompleted){
                    MenuItemProviders.attachToActivity(self, menu, tags);
                    MenuItemProviders.getInitCompleted().removeObserver(this);
                }
            }
        });
    }

    protected void onMenuItemSelected(int menuItemId){
        MenuItemProviders.setMenuItemSelected(menuItemId);
    }

    public abstract void onCommonViewCreated(@NonNull View view, @NonNull String instanceTag);

    @SuppressWarnings("unused")
    protected void addCommonView(String providerTag, String instanceTag, int idViewStub){
        Log.v(LOG_TAG, "addCommonView: provider=" + providerTag + "; instance=" + instanceTag);

        CommonViewProviders.getInitCompleted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable  final Boolean initCompleted) {
                if (initCompleted != null && initCompleted){
                    addCommonViewInternal(providerTag, instanceTag, idViewStub);
                    CommonViewProviders.getInitCompleted().removeObserver(this);
                }
            }
        });
    }

    private void addCommonViewInternal(String providerTag, String instanceTag, int idViewStub) {
        Log.v(LOG_TAG, "addCommonViewInternal: provider=" + providerTag + "; instance=" + instanceTag);
        CommonViewProvider provider = CommonViewProviders.getProvider(providerTag);
        if (provider != null){
            Bundle args = new Bundle();
            args.putString(CommonViewProviders.ARG_INSTANCE_TAG, instanceTag);
            args.putInt(CommonViewProviders.ARG_VIEWSTUB_TAG, idViewStub);

            provider.attachToActivity(this, args);
        }
    }
}
