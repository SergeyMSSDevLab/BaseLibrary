package com.mssdevlab.baselib.CommonViews;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import com.mssdevlab.baselib.OnFormatListener;

import java.lang.ref.WeakReference;

/**
 * Base class for all common view viewHolders.
 */
public class ViewHolderBase {
    private static final String LOG_TAG = ViewHolderBase.class.getCanonicalName();

    private WeakReference<Activity> mActivity = new WeakReference<>(null);
    private OnFormatListener mFormatListener = null;

    protected ViewStub mViewStub;
    protected String mInstanceTag;
    protected String mConfigTag;
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
    public void setArguments(@NonNull String configTag, @Nullable Bundle args, @Nullable OnFormatListener formatListener) {
        Log.v(LOG_TAG, "setArguments entered.");
        this.mArguments = args;
        this.mFormatListener = formatListener;
        this.mConfigTag = configTag;
    }

    @CallSuper
    public void setPlaceHolder(@NonNull ViewStub viewStub, @NonNull Activity activity, @NonNull String instanceTag) {
        Log.v(LOG_TAG, "setPlaceHolder entered.");
        this.mActivity = new WeakReference<>(activity);
        this.mViewStub = viewStub;
        this.mInstanceTag = instanceTag;
    }

    @CallSuper
    protected void setUpView(@NonNull View view){
        Log.v(LOG_TAG, "setUpView entered.");
        if (this.mFormatListener != null) {
            this.mFormatListener.onFormat(view, this.mConfigTag);
        }
        Log.v(LOG_TAG, "setUpView: initialized");
    }

    public Activity getActivity(){
        return this.mActivity.get();
    }
}
