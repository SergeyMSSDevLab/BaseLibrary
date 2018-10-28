package com.mssdevlab.baselib.upgrade;

import android.os.Bundle;

import androidx.core.app.NavUtils;

import android.util.Log;
import android.view.MenuItem;

import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.R;

public class UpgradeActivity1 extends BaseActivity {
    private static final String LOG_TAG = "UpgradeActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            // TODO: consider to show ads
            case android.R.id.home:
                Log.v(LOG_TAG, "onOptionsItemSelected home");
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
