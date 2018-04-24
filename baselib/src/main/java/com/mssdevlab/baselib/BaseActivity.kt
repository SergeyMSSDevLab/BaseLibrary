package com.mssdevlab.baselib

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.mssdevlab.baselib.factory.CommonViewListener
import com.mssdevlab.baselib.factory.CommonViewProviders

/**
 * Class connects child activity with baselib stuff
 */
abstract class BaseActivity : AppCompatActivity(), CommonViewListener {
    private val LOG_TAG = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(LOG_TAG, "onCreate")

        //AdsObserver observer = new AdsObserver(getLifecycle());
        //final AdsObserver viewModel = ViewModelProviders.of(this).get(AdsObserver.class);
    }

    protected fun addCommonView(providerTag: String, instanceTag: String, idViewStub: Int) {
        Log.v(LOG_TAG, "addCommonView: provider=$providerTag; instance=$instanceTag")

        val args = Bundle()
        args.putString(CommonViewProviders.ARG_PROVIDER_TAG, providerTag)
        args.putString(CommonViewProviders.ARG_INSTANCE_TAG, instanceTag)
        args.putInt(CommonViewProviders.ARG_VIEWSTUB_TAG, idViewStub)

        CommonViewProviders.createCommonView(this, args)
    }
}
