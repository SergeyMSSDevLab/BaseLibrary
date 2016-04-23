package com.mssdevlab.baselib.CommonViews;

import android.support.annotation.NonNull;

/**
 * Listener to obtain configuration
 */
public interface ConfiguratorListener {
    void onConfigureCompleted(@NonNull CommonViewProvider viewHolder);
}
