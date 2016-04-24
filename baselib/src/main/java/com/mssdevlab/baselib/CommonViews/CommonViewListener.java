package com.mssdevlab.baselib.CommonViews;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Used to format views added outside of an activity.
 */
@SuppressWarnings("WeakerAccess")
public interface CommonViewListener {
    void onViewCreated(@NonNull View view);
}
