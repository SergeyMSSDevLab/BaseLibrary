package com.mssdevlab.baselib.ApplicationMode;

import android.app.Activity;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.billingclient.api.SkuDetails;
import com.google.android.gms.ads.reward.RewardItem;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.ads.InterstitialManager;
import com.mssdevlab.baselib.common.ShowView;

import java.util.List;

/* Base application view model
Exposes application mode.
Implements facade for billing functionality.
*/
public class AppViewModel extends ViewModel {

    public static void initAppMode(){
        AppModeManager.initAppMode();
    }

    public AppViewModel() {
        super();
    }

    @NonNull
    public LiveData<AppMode> getApplicationMode(){
        return ApplicationData.getApplicationMode();
    }

    @NonNull
    public LiveData<List<SkuDetails>> getSkuDetails(){
        return BillingData.getSkuDetails();
    }

    @UiThread
    public void loadPurchases(Activity activity){
        BillingData.loadPurchases(activity, BaseApplication.getInstance().getPublicKey());
    }

    @UiThread
    public void loadSku(Activity activity, int requestCode){
        BaseApplication app = BaseApplication.getInstance();
        BillingData.loadSku(activity, requestCode, app.getSubscriptionSkus(), app.getProductSkus());
    }

    @UiThread
    public void startPurchase(Activity activity, SkuDetails skuDetails){
        BillingData.startPurchase(activity, skuDetails, BaseApplication.getInstance().getPublicKey());
    }

    public boolean isAppModeAtLeast(AppCompatActivity activity, @NonNull AppMode minMode) {

        AppMode mode = getApplicationMode().getValue();
        if (mode == null){
            mode = AppMode.MODE_DEMO;
        }

        if (mode.ordinal() >= minMode.ordinal()){
            return true;
        }

        final Resources res = activity.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.bl_ads_notification_title);
        builder.setMessage(res.getStringArray(R.array.bl_ads_restriction_message_array)[mode.ordinal()])
                .setCancelable(false)
                .setPositiveButton(R.string.bl_ads_upgrade_button_title,
                        (dialog, id) -> BaseApplication.startUpgradeScreen(activity))
                .setNegativeButton(res.getString(R.string.bl_ads_continue_button_title,
                        res.getStringArray(R.array.bl_common_app_mode_array)[mode.ordinal()]),
                        (dialog, id) -> {
                            InterstitialManager.showInterstitialAd(activity, false);
                            dialog.cancel();
                        }).show();

        return false;
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
