package com.mssdevlab.baselib.upgrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.mssdevlab.baselib.BaseApplication;
import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.ads.ComboBannerFragment;
import com.mssdevlab.baselib.ads.InterstitialManager;
import com.mssdevlab.baselib.databinding.ActivityUpgradeBinding;

public class BaseUpgradeActivity extends AppCompatActivity {

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUpgradeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_upgrade);
        binding.setLifecycleOwner(this);
        ViewModelProvider provider = new ViewModelProvider(this);
        BaseUpgradeActivityViewModel viewModel = provider.get(BaseUpgradeActivityViewModel.class);
        binding.setViewModelMain(viewModel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Setup the banner
        String adMobUnitId = getIntent().getStringExtra(BaseApplication.EXTRA_ADMOB_UNIT_ID);
        if (adMobUnitId != null){
            ComboBannerFragment adsFragment = new ComboBannerFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ComboBannerFragment.EXTRA_UNIT_ID, adMobUnitId);
            bundle.putString(ComboBannerFragment.EXTRA_APP_NAME, getIntent().getStringExtra(BaseApplication.EXTRA_APP_NAME));
            bundle.putString(ComboBannerFragment.EXTRA_DEV_EMAIL, getIntent().getStringExtra(BaseApplication.EXTRA_DEV_EMAIL));
            bundle.putBoolean(ComboBannerFragment.EXTRA_MANAGE_PARENT, true);
            adsFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.blUpgaCvBanner, adsFragment, null);
            fragmentTransaction.commit();
        }

        // Rewarded video
        String rewardedUnitId = getIntent().getStringExtra(BaseApplication.EXTRA_REWARDED_UNIT_ID);
        if (rewardedUnitId != null){
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener(viewModel);

            viewModel.getShowRewardedVideo().observe(this, event -> {
                RewardedVideoEvent res = event.getValueIfNotHandled();
                if (res != null){
                    if (res == RewardedVideoEvent.SHOW){
                        if (mRewardedVideoAd.isLoaded()) {
                            mRewardedVideoAd.show();
                        }
                    } else if (res == RewardedVideoEvent.RELOAD){
                        mRewardedVideoAd.loadAd(rewardedUnitId, new AdRequest.Builder().build());
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent != null){
                    if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                        // This activity is NOT part of this app's task, so create a new task
                        // when navigating up, with a synthesized back stack.
                        TaskStackBuilder.create(this)
                                // Add all of this activity's parents to the back stack
                                .addNextIntentWithParentStack(upIntent)
                                // Navigate up to the closest parent
                                .startActivities();
                    } else {
                        // This activity is part of this app's task, so simply
                        // navigate up to the logical parent activity.
                        NavUtils.navigateUpTo(this, upIntent);
                    }
                    InterstitialManager.showInterstitialAd(this,false);
                    return true;
                }
                InterstitialManager.showInterstitialAd(this,false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InterstitialManager.showInterstitialAd(this,false);
    }
}
