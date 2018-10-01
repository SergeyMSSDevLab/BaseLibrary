package com.mssdevlab.baselib.common;

import androidx.annotation.StringRes;

import com.mssdevlab.baselib.R;

/**
 * Error report e-mail sender
 */
@SuppressWarnings("WeakerAccess")
public class ErrorEMailSender extends EMailSender {

    @SuppressWarnings("unused")
    public ErrorEMailSender(@StringRes int emailAddressRes) {
        super(emailAddressRes, R.string.common_error_report_email_subject, R.string.common_error_report_choose_title);
    }

}
