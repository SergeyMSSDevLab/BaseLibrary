package com.mssdevlab.baselib;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mssdevlab.baselib.factory.CommonViewProvider;
import com.mssdevlab.baselib.factory.CommonViewProviders;

/**
 * Class connects child activity with baselib stuff
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");

        //AdsObserver observer = new AdsObserver(getLifecycle());
        //final AdsObserver viewModel = ViewModelProviders.of(this).get(AdsObserver.class);
    }

    public abstract void onCommonViewCreated(@NonNull View view, @NonNull String instanceTag);

    @SuppressWarnings("unused")
    protected void addCommonView(String providerTag, String instanceTag, int idViewStub){
        Log.v(LOG_TAG, "addCommonView: provider=" + providerTag + "; instance=" + instanceTag);

        CommonViewProviders.getInitCompleted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged( final Boolean initCompleted) {
                if (initCompleted){
                    addCommonViewInternal(providerTag, instanceTag, idViewStub);
                    CommonViewProviders.getInitCompleted().removeObserver(this);
                }
            }
        });
    }

    private void addCommonViewInternal(String providerTag, String instanceTag, int idViewStub) {

        final Bundle args = new Bundle();
        args.putString(CommonViewProviders.ARG_PROVIDER_TAG, providerTag);
        args.putString(CommonViewProviders.ARG_INSTANCE_TAG, instanceTag);
        args.putInt(CommonViewProviders.ARG_VIEWSTUB_TAG, idViewStub);

        final BaseActivity instance = this;
        final CommonViewProvider provider = CommonViewProviders.getProvider(providerTag);
        if (provider != null){
            provider.getViewModel(this).updateCommonView().observe(this, counter -> provider.createView(instance, args));
        }
    }
}
