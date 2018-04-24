package com.mssdevlab.baselib.factory

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.util.ArrayMap
import android.util.Log

import com.mssdevlab.baselib.BaseActivity

/**
 * Holds all configurations for application
 */
object CommonViewProviders {
    private val LOG_TAG = "CommonViewProviders"
    private val sConfigurationMap = ArrayMap<String, CommonViewProvider>(4)
    private val sInitCompleted = MutableLiveData<Boolean>()

    val ARG_PROVIDER_TAG = "CommonViewProviders.parProviderTag"
    val ARG_INSTANCE_TAG = "CommonViewProviders.parInstanceTag"
    val ARG_VIEWSTUB_TAG = "CommonViewProviders.parViewStubTag"

    fun addProvider(providerTag: String, provider: CommonViewProvider) {
        sConfigurationMap[providerTag] = provider
        Log.v(LOG_TAG, "CommonViewProvider added: $providerTag")
    }

    fun setInitCompleted() {
        Log.v(LOG_TAG, "setInitCompleted")
        sInitCompleted.postValue(true)
    }

    fun createCommonView(activity: BaseActivity, args: Bundle) {
        val r = sInitCompleted.value
        if (r != null && r) {
            addFragment(activity, args)
        } else {
            // Create observer to add common view fragment
            sInitCompleted.observe(activity, object : Observer<Boolean> {
                override fun onChanged(initCompleted: Boolean?) {
                    if (initCompleted!!) {
                        addFragment(activity, args)
                        sInitCompleted.removeObserver(this)
                    }
                }
            })
        }
    }

    private fun addFragment(activity: BaseActivity, args: Bundle) {
        val instanceTag = args.getString(ARG_INSTANCE_TAG)
        if (instanceTag != null) {
            val fragmentManager = activity.supportFragmentManager
            var fragment: Fragment? = fragmentManager.findFragmentByTag(instanceTag)
            if (fragment == null) {
                val configTag = args.getString(ARG_PROVIDER_TAG)
                val provider = sConfigurationMap[configTag]
                if (provider != null) {
                    fragment = provider.getFragment(args)
                    fragmentManager.beginTransaction().add(fragment, instanceTag)
                            .commit()
                }
            }
        }
    }
}
