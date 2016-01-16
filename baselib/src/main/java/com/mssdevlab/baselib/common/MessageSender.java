package com.mssdevlab.baselib.common;

import android.app.Activity;

/**
 * Sending messages
 */
@SuppressWarnings("ALL")
public interface MessageSender {
    /**
     * Send message
     */
    void send(final Activity activity, String message);
}
