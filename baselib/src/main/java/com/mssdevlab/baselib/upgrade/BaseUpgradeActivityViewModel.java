package com.mssdevlab.baselib.upgrade;

import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mssdevlab.baselib.ApplicationMode.AppModeManager;
import com.mssdevlab.baselib.ApplicationMode.ApplicationData;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.Event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class BaseUpgradeActivityViewModel extends ViewModel implements RewardedVideoAdListener {
    private static final String LOG_TAG = "UpgradeViewModel";

    private final MutableLiveData<Event<RewardedVideoEvent>> mShowRewardedvideo = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mRewardedVideoLoaded = new MutableLiveData<>();

    public BaseUpgradeActivityViewModel() {
        Log.v(LOG_TAG, "Constructor");
        this.mRewardedVideoLoaded.setValue(false);
        this.mShowRewardedvideo.setValue(new Event<>(RewardedVideoEvent.RELOAD));
    }

    public LiveData<Integer> getAllowTrackingText(){
        return Transformations.map(ApplicationData.getAllowTrackingParticipated(),
            val -> {
                if (val) {
                    return R.string.bl_upgrade_track_value_done;
                }
                return R.string.bl_upgrade_track_value;
            });
    }

    public LiveData<Boolean> getAllowTracking(){
        return ApplicationData.getAllowTracking();
    }

    public void setAllowTracking(View view){
        AppModeManager.setAllowTracking(((Switch) view).isChecked());
    }

    public void inviteFriend(){

    }

    public LiveData<Boolean> getIsVideLoaded(){
        return this.mRewardedVideoLoaded;
    }

    public LiveData<Integer> getSeeAdsText(){
        return Transformations.map(this.mRewardedVideoLoaded,
                val -> {
                    if (val) {
                        return R.string.bl_upgrade_see_ads_value;
                    }
                    return R.string.bl_upgrade_see_ads_value_not_loaded;
                });
    }

    public LiveData<Event<RewardedVideoEvent>> getShowRewardedVideo(){
        return mShowRewardedvideo;
    }

    public void onSeeRewardedAds(){
        this.mShowRewardedvideo.setValue(new Event<>(RewardedVideoEvent.SHOW));
    }

    /************************************************************************************
    * RewardedVideoAdListener implementation
    *************************************************************************************/

    @Override
    public void onRewardedVideoAdLoaded() {
        this.mRewardedVideoLoaded.setValue(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
        this.mRewardedVideoLoaded.setValue(false);
    }

    @Override
    public void onRewardedVideoAdClosed() {
        this.mRewardedVideoLoaded.setValue(false);
        this.mShowRewardedvideo.setValue(new Event<>(RewardedVideoEvent.RELOAD));
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        // TODO: reward user
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        this.mRewardedVideoLoaded.setValue(false);
        this.mShowRewardedvideo.setValue(new Event<>(RewardedVideoEvent.RELOAD));
    }

    @Override
    public void onRewardedVideoCompleted() {
        this.mRewardedVideoLoaded.setValue(false);
    }
}
