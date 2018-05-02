package com.mssdevlab.baselib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mssdevlab.baselib.factory.CommonViewListener;
import com.mssdevlab.baselib.factory.CommonViewProviders;

/**
 * Class connects child activity with baselib stuff
 */
public abstract class BaseActivity extends AppCompatActivity implements CommonViewListener {
    private static final String LOG_TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");

        //AdsObserver observer = new AdsObserver(getLifecycle());
        //final AdsObserver viewModel = ViewModelProviders.of(this).get(AdsObserver.class);
    }

    protected void addCommonView(String providerTag, String instanceTag, int idViewStub){
        Log.v(LOG_TAG, "addCommonView: provider=" + providerTag + "; instance=" + instanceTag);

        Bundle args = new Bundle();
        args.putString(CommonViewProviders.ARG_PROVIDER_TAG, providerTag);
        args.putString(CommonViewProviders.ARG_INSTANCE_TAG, instanceTag);
        args.putInt(CommonViewProviders.ARG_VIEWSTUB_TAG, idViewStub);

        CommonViewProviders.createCommonView(this, args);
    }
}
