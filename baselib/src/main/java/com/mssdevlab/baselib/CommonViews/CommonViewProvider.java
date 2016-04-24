package com.mssdevlab.baselib.CommonViews;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import java.lang.ref.WeakReference;

/**
 * Base class for all common view viewHolders.
 */
public class CommonViewProvider {
    private static final String LOG_TAG = CommonViewProvider.class.getCanonicalName();

    private WeakReference<FragmentActivity> mActivity = new WeakReference<>(null);
    private CommonViewListener mListener = null;

    protected ViewStub mViewStub;
    protected String mInstanceTag;
    protected Bundle mArguments;

    public void onResume() {
        Log.v(LOG_TAG, "onResume entered.");
    }

    public void onPause() {
        Log.v(LOG_TAG, "onPause entered.");
    }

    @CallSuper
    public void onDestroy() {
        Log.v(LOG_TAG, "onDestroy entered.");
        this.mActivity.clear();
    }

    /**
     * Supply the construction arguments for this holder.
     */
    public void setArguments(@Nullable Bundle args, @Nullable CommonViewListener listener) {
        Log.v(LOG_TAG, "setArguments entered.");
        this.mArguments = args;
        this.mListener = listener;
    }

    @CallSuper
    public void setViewStub(@NonNull ViewStub viewStub) {
        Log.v(LOG_TAG, "setViewStub entered.");
        this.mViewStub = viewStub;
    }

    @CallSuper
    public void setActivity(@NonNull FragmentActivity activity, @NonNull String instanceTag) {
        Log.v(LOG_TAG, "setActivity entered.");
        this.mActivity = new WeakReference<>(activity);
        this.mInstanceTag = instanceTag;
    }

    @CallSuper
    protected void setUpView(@NonNull View view){
        Log.v(LOG_TAG, "setUpView entered.");
        if (this.mListener != null) {
            this.mListener.onViewCreated(view);
        }
        Log.v(LOG_TAG, "setUpView: initialized");
    }

    public FragmentActivity getActivity(){
        return this.mActivity.get();
    }

    public boolean isViewAvailable() {
        return false;
    }

    public View getView(){
        return null;
    }
}
