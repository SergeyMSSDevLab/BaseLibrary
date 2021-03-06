package com.mssdevlab.baselib.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import androidx.annotation.StringRes;

import java.util.List;

/**
 * Send e-mail according to specified parameters.
 */
@SuppressWarnings("unused")
public class EMailSender extends MessageSender {
    private final int emailAddressRes;
    private final int emailSubjectRes;
    private final int chooseTitleRes;

    protected EMailSender(@StringRes int emailAddressRes, @StringRes int emailSubjectRes, @StringRes int chooseTitleRes) {
        this.emailAddressRes = emailAddressRes;
        this.emailSubjectRes = emailSubjectRes;
        this.chooseTitleRes = chooseTitleRes;
    }

    @Override
    public void send(Activity activity, String message) {
        if (message != null) {
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            Resources res = activity.getResources();
            String uriText = "mailto:" + res.getString(this.emailAddressRes) + "?subject="
                    + Uri.encode(res.getString(this.emailSubjectRes)) + "&body=" + message;

            sendIntent.setData(Uri.parse(uriText));
            PackageManager packageManager = activity.getPackageManager();
            List activities = packageManager.queryIntentActivities(sendIntent,
                    PackageManager.MATCH_DEFAULT_ONLY);

            if (activities.size() > 0) {
                activity.startActivity(Intent.createChooser(sendIntent,
                        res.getString(this.chooseTitleRes)));
            }
        }

    }
}
