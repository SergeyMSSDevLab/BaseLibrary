package com.mssdevlab.baselib.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.R;

public class GPErrorActivity extends AppCompatActivity {
    private static final int PLAY_REQUEST_CODE = 1593;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gperror);
        if (!BaseApplication.checkPlayServices(this, PLAY_REQUEST_CODE)) {
            Toast.makeText(this, R.string.bl_gp_error_text,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                if (i != null){
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
