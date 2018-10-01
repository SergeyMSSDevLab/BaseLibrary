package com.mssdevlab.baselib.upgrade;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

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
