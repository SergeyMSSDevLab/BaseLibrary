package com.mssdevlab.baselib.common;

import android.app.Activity;

/**
 * Sending messages
 */
public abstract class MessageSender {
    /**
     * Send message
     */
    public abstract void send(final Activity activity, String message);
}
