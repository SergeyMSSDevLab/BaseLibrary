package com.mssdevlab.baselib.factory

import android.os.Bundle
import android.support.v4.app.Fragment

/**
 * Base class for all providers
 */
abstract class CommonViewProvider {
    abstract fun getFragment(args: Bundle): Fragment
}