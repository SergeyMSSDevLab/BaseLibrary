package com.mssdevlab.baselib.factory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.mssdevlab.baselib.BaseActivity;

/**
 * Base class for all providers
 */
public abstract class CommonViewProvider {

    public abstract void createView(@NonNull final BaseActivity activity, @NonNull final Bundle args);
}
