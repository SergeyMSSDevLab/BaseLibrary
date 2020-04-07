package com.mssdevlab.baselib.upgrade;

import androidx.databinding.BaseObservable;

public class UpgradeOptionModel extends BaseObservable {
    private String mOptionTitle;

    public void setTitle(String title) {
        this.mOptionTitle = title;
    }
    public String getTitle() {
        return mOptionTitle;
    }
}
