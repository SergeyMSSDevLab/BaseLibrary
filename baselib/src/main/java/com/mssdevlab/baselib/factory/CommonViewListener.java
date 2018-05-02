package com.mssdevlab.baselib.factory;

import android.support.annotation.NonNull;
import android.view.View;

public interface CommonViewListener {
    void onCommonViewCreated(@NonNull View view, @NonNull String instanceTag);
}
