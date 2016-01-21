package com.mssdevlab.baselib;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Used to format views added outside of an activity.
 */
@SuppressWarnings("WeakerAccess")
public interface OnFormatListener {
    void onFormat(@NonNull View view, @NonNull String tagName);
}
