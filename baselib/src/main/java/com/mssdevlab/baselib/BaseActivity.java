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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BaseApplication.getInstance().handleLastError(this);
        super.onCreate(savedInstanceState);
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
