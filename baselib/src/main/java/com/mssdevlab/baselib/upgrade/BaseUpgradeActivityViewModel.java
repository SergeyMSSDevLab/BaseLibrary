package com.mssdevlab.baselib.upgrade;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mssdevlab.baselib.ApplicationMode.AppMode;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.Event;
import com.mssdevlab.baselib.common.Helper;

import java.text.DateFormat;
import java.util.Date;

public class BaseUpgradeActivityViewModel extends UpgradeOptionsViewModel implements RewardedVideoAdListener {
    private static final String LOG_TAG = "U_ActivityViewModel";

    private final MutableLiveData<Event<RewardedVideoEvent>> mShowRewardedvideo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRewardedVideoLoaded = new MutableLiveData<>();
    private final MediatorLiveData<String> mCurModeText = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> mAdsVisibilityValue = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> mSeeAdsText = new MediatorLiveData<>();

    private final String[] mAppModeNames;
    private final String mExpireName;

    public BaseUpgradeActivityViewModel() {
        Log.v(LOG_TAG, "Constructor");
        this.mRewardedVideoLoaded.setValue(false);
        this.mShowRewardedvideo.setValue(new Event<>(RewardedVideoEvent.RELOAD));

        Resources res = BaseApplication.getInstance().getResources();
        this.mAppModeNames = res.getStringArray(R.array.bl_common_app_mode_array);
        this.mExpireName = res.getString(R.string.bl_upgrade_expire_time);

        mAdsVisibilityValue.addSource(this.getApplicationMode(), appMode -> {
            if (appMode == AppMode.MODE_NO_ADS || appMode == AppMode.MODE_PRO){
                this.mAdsVisibilityValue.setValue(View.GONE);
            } else {
                this.mAdsVisibilityValue.setValue(View.VISIBLE);
            }
        });

        mCurModeText.addSource(this.getApplicationMode(),
                appMode -> composeModeText(appMode, this.getExpireTime().getValue()));
        mCurModeText.addSource(this.getExpireTime(),
                expireTime -> composeModeText(this.getApplicationMode().getValue(), expireTime));

        mSeeAdsText.addSource(this.getApplicationMode(),
                appMode -> composeSeeAdsText(appMode, this.mRewardedVideoLoaded.getValue()));
        mSeeAdsText.addSource(this.mRewardedVideoLoaded,
                adsLoaded -> composeSeeAdsText(this.getApplicationMode().getValue(), adsLoaded));
    }

    private void composeSeeAdsText(@Nullable AppMode mode, @Nullable Boolean adsLoaded){
        if (mode == null){
            mode = AppMode.MODE_DEMO;
        }

        if (adsLoaded == null){
            adsLoaded = false;
        }

        int textResId;
        if (mode == AppMode.MODE_NO_ADS || mode == AppMode.MODE_PRO){
            if (adsLoaded) {
                textResId = R.string.bl_upgrade_see_ads_value_noads;
            } else {
                textResId = R.string.bl_upgrade_see_ads_value_noads_not_loaded;
            }
        } else {
            if (adsLoaded) {
                textResId = R.string.bl_upgrade_see_ads_value;
            } else {
                textResId = R.string.bl_upgrade_see_ads_value_not_loaded;
            }
        }
        Helper.setValue(this.mSeeAdsText, textResId);
    }

    private void composeModeText(@Nullable AppMode mode, @Nullable Long expireTime){
        if (mode == null){
            mode = AppMode.MODE_DEMO;
        }
        String curModeText = this.mAppModeNames[mode.ordinal()];
        Long nowMS = System.currentTimeMillis();
        if ((mode == AppMode.MODE_EVALUATION || mode == AppMode.MODE_NO_ADS) && expireTime != null && nowMS < expireTime){
            curModeText += ", " + this.mExpireName + " " +
            DateFormat
                    .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(new Date(expireTime));
        }
        Helper.setValue(this.mCurModeText, curModeText);
    }

    public LiveData<String> getCurrentModeText(){
        return mCurModeText;
    }

    public LiveData<Integer> getAllowTrackingText(){
        return Transformations.map(this.getAllowTrackingParticipated(),
            val -> {
                if (val) {
                    return R.string.bl_upgrade_track_value_done;
                }
                return R.string.bl_upgrade_track_value;
            });
    }

    public void setAllowTracking(View view){
        this.setAllowTracking(((Switch) view).isChecked());
    }

    public LiveData<Boolean> getIsVideLoaded(){
        return this.mRewardedVideoLoaded;
    }

    public LiveData<Integer> getSeeAdsText(){
        return mSeeAdsText;
    }

    LiveData<Integer> getAdsVisibilityValue(){
        return mAdsVisibilityValue;
    }

    public LiveData<Integer> getSkuVisibilityValue(){
        return Transformations.map(this.getApplicationMode(),
                appMode -> {
                    int result;
                    if (appMode == AppMode.MODE_PRO) {
                        result = View.GONE;
                    } else {
                        result = View.VISIBLE;
                    }
                    return result;
                });
    }

    public void onSeeRewardedAds(){
        this.mShowRewardedvideo.setValue(new Event<>(RewardedVideoEvent.SHOW));
    }

    LiveData<Event<RewardedVideoEvent>> getShowRewardedVideo(){
        return mShowRewardedvideo;
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
        this.rewardUser(rewardItem);
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
