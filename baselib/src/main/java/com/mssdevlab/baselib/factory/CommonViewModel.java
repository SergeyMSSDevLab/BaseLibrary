package com.mssdevlab.baselib.factory;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

public abstract class CommonViewModel extends ViewModel {

    public abstract LiveData<Integer> updateCommonView();
}
