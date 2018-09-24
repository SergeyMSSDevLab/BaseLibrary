package com.mssdevlab.baselib.upgrade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.R;

public abstract class BaseUpgradeActivity extends BaseActivity {
    private static final String LOG_TAG = "BaseUpgradeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseupgrade);
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
