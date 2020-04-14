package com.mssdevlab.baselib.ApplicationMode;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.google.android.gms.ads.reward.RewardItem;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.common.Helper;
import com.mssdevlab.baselib.common.ShowView;

import java.util.List;

public class AppViewModel extends AndroidViewModel {
    private final MediatorLiveData<AppMode> mAppMode = new MediatorLiveData<>();

    public AppViewModel(@NonNull Application application) {
        super(application);
        this.initAppMode();
    }

    public LiveData<AppMode> getApplicationMode(){
        return mAppMode;
    }

    private void initAppMode(){
        mAppMode.addSource(ApplicationData.getApplicationMode(),
                appMode -> composeMode(appMode, null));
        mAppMode.addSource(BillingData.getAppPurchases(),
                purchases -> composeMode(ApplicationData.getCurrentApplicationMode(), purchases));
    }

    private void composeMode(@Nullable AppMode appMode, @Nullable List<Purchase> purchases){
        AppMode curMode = appMode;
        if (purchases != null && purchases.size() > 0){
            curMode = AppMode.MODE_PRO;
        }
        Helper.setValue(this.mAppMode, curMode);
    }

    @UiThread
    public static void loadPurchases(Activity activity){
        BillingData.loadPurchases(activity, BaseApplication.getInstance().getPublicKey());
    }

    @UiThread
    public static void loadSku(Activity activity, int requestCode){
        BaseApplication app = BaseApplication.getInstance();
        BillingData.loadSku(activity, requestCode, app.getSubscriptionSkus(), app.getProductSkus());
    }

    @UiThread
    public static void startPurchase(Activity activity, SkuDetails skuDetails){
        BillingData.startPurchase(activity, skuDetails, BaseApplication.getInstance().getPublicKey());
    }

    @NonNull
    public static LiveData<List<SkuDetails>> getSkuDetails(){
        return BillingData.getSkuDetails();
    }

    public static void checkAppMode(){
        AppModeManager.checkAppMode();
    }
    public static void rewardUser(@NonNull RewardItem rewardItem) {
        AppModeManager.rewardUser(rewardItem);
    }

    public static void setAllowTracking(boolean allowTracking){
        AppModeManager.setAllowTracking(allowTracking);
    }
    public static LiveData<ShowView> getBannerShowMode(){
        return ApplicationData.getBannerShowMode();
    }

    public static LiveData<Long> getExpireTime(){
        return ApplicationData.getExpireTime();
    }

    public static LiveData<Boolean> getAllowTracking(){
        return ApplicationData.getAllowTracking();
    }

    public static LiveData<Boolean> getAllowTrackingParticipated(){
        return ApplicationData.getAllowTrackingParticipated();
    }

    // TODO: Remove this debug method or add debug class into configuration
    public static void setApplicationMode(AppMode mode){
        ApplicationData.setApplicationMode(mode);
    }

}
