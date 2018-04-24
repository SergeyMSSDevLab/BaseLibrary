package com.mssdevlab.baselib.factory

import android.view.View

interface CommonViewListener {
    fun onCommonViewCreated(view: View, instanceTag: String)
}