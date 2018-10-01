package com.mssdevlab.baselib;

import androidx.lifecycle.Observer;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
