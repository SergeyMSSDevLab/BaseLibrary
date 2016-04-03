package com.mssdevlab.baselib;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ErrorActivity extends AppCompatActivity {
    static final String ERROR_REPORT_CONTENT = "ErrorActivity.report.content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String message = intent.getStringExtra(ERROR_REPORT_CONTENT);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final Resources res = this.getResources();
        dialog.setTitle(res.getString(R.string.common_error_report_email_subject));
        dialog.setMessage(res.getString(R.string.common_error_report_dialog_text));

        final ErrorActivity activity = this;
        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                BaseApplication.reportSender.send(activity, message);
                activity.finish();
            }
        });

        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                activity.finish();
            }
        });
        dialog.create().show();
    }
}
