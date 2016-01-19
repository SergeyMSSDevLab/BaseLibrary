package com.mssdevlab.baselib;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.FrameLayout;

import com.mssdevlab.baselib.common.PromoteScreenFragment;
import com.mssdevlab.baselib.common.PromoteStuff;

/**
 * Manages the rate dialog showing/removing.
 */
@SuppressWarnings("unused")
public class PromoScreenManagerFragment
        extends Fragment
        implements PromoteScreenFragment.OnRatePromptListener {
    private static final String LOG_TAG = "PromoScreenManagerFr-t";
    private FrameLayout parentLayout;
    private String appName;
    private String tagName;
    private String devEmail;
    private boolean showPromote = false;

    public static PromoScreenManagerFragment newInstance(final Context context,
                                                         String tag,
                                                         FrameLayout parentLayout,
                                                         @StringRes int appNameRes,
                                                         @StringRes int devEmailRes) {
        PromoScreenManagerFragment fragment = new PromoScreenManagerFragment();
        fragment.parentLayout = parentLayout;
        fragment.tagName = tag;
        fragment.appName = context.getResources().getString(appNameRes);
        fragment.devEmail = context.getResources().getString(devEmailRes);
        return fragment;
    }

    public PromoScreenManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.showPromote = PromoteStuff.IsTimeToShowRate(getActivity());
        this.showPromote = true; // TODO: Don't forget to comment the line

        this.setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume entered.");
        this.managePromoteFragment();
    }

    private void managePromoteFragment(){
        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        if (fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentByTag(this.tagName);
            if (this.showPromote) {
                Log.v(LOG_TAG, "managePromoteFragment: promptFragment adding");
                if (fragment == null) {
                    fragment = PromoteScreenFragment.newInstance(this.appName, this.devEmail, this);
                    fragmentManager.beginTransaction()
                            .replace(this.parentLayout.getId(), fragment, this.tagName)
                            .commitAllowingStateLoss();
                }
            } else {
                if (fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }

            }
        }
    }

    @Override
    public void onActionSelected(int actionSelected) {
        Log.v(LOG_TAG, "onActionSelected entered.");
        this.showPromote = false;
        if (actionSelected != PromoteScreenFragment.RATE_SELECTED) {
            this.managePromoteFragment();
        }
    }
}
