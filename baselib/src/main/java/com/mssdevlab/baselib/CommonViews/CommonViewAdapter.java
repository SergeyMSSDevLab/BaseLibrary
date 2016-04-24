package com.mssdevlab.baselib.CommonViews;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mssdevlab.baselib.CommonViewManagerFragment;

import java.lang.ref.WeakReference;

/**
 * Shows common views inside of the item list.
 */
public class CommonViewAdapter extends BaseAdapter {

    public static class CommonViewAdapterConfig{
        public String mConfigurationTag = "";
        public String mFragmentTag = "";
        public boolean mShowInShortList = true;
        public int mCommonViewsLimit = 3;
        public int mRepeatStartPosition = 2;
        public int mRepeatInterval = 10;
    }

    private static final String LOG_TAG = CommonViewAdapter.class.getCanonicalName();
    private WeakReference<CommonViewManagerFragment> mFragment = new WeakReference<>(null);

    private final BaseAdapter mAdapter;
    private final CommonViewAdapterConfig mConfig;

    public CommonViewAdapter(@NonNull FragmentActivity activity, @NonNull BaseAdapter adapter, @NonNull CommonViewAdapterConfig config) {
        this.mAdapter = adapter;
        this.mConfig = config;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        CommonViewManagerFragment fragment = (CommonViewManagerFragment)fragmentManager.findFragmentByTag(this.mConfig.mFragmentTag);
        this.mFragment = new WeakReference<>(fragment);

        this.mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                notifyDataSetInvalidated();
            }
        });
    }

    @Override
    public int getCount() {
        int originalItemsCount = mAdapter.getCount();
        int addedItemsCount = 0;
        if (originalItemsCount < this.mConfig.mRepeatStartPosition){

        } else {

        }

        if (this.mConfig.mShowInShortList) {

        } else {

        }

        return originalItemsCount  + addedItemsCount;
    }


    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mAdapter.getView(position, convertView, parent);
    }

}
