package com.mssdevlab.baselib.common;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.R;

public class ErrorActivity extends AppCompatActivity {
    public static final String ERROR_REPORT_CONTENT = "ErrorActivity.report.content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String message = intent.getStringExtra(ERROR_REPORT_CONTENT);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final Resources res = this.getResources();
        dialog.setTitle(res.getString(R.string.bl_common_error_report_email_subject));
        dialog.setMessage(res.getString(R.string.bl_common_error_report_dialog_text));

        final ErrorActivity activity = this;
        dialog.setPositiveButton(android.R.string.yes, (dialog1, id) -> {
            dialog1.dismiss();
            BaseApplication.reportSender.send(activity, message);
            activity.finish();
        });

        dialog.setNegativeButton(android.R.string.no, (dialog12, id) -> {
            dialog12.dismiss();
            activity.finish();
        });
        dialog.create().show();
    }
}
