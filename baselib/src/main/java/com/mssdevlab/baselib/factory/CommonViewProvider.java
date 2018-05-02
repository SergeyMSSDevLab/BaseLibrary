package com.mssdevlab.baselib.factory;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Base class for all providers
 */
public abstract class CommonViewProvider {
    public abstract Fragment getFragment(Bundle args);
}
