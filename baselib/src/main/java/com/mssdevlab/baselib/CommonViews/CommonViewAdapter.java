package com.mssdevlab.baselib.CommonViews;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Shows commonviews inside of the item list.
 */
public class CommonViewAdapter extends BaseAdapter {

    public static class CommonViewAdapterConfig{
        public String mConfigurationTag = "";
        public boolean mShowInEmptyList = true;
        public int mCommonViewsLimit = 3;
        public int mRepeatStartPosition = 2;
        public int mRepeatInterval = 10;
    }

    private static final String LOG_TAG = CommonViewAdapter.class.getCanonicalName();

    private final BaseAdapter mAdapter;
    private final CommonViewAdapterConfig mConfig;

    public CommonViewAdapter(@NonNull BaseAdapter adapter, @NonNull CommonViewAdapterConfig config) {
        this.mAdapter = adapter;
        this.mConfig = config;

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
        return mAdapter.getCount();
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
