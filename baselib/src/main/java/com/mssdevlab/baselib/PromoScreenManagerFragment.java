package com.mssdevlab.baselib;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

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
    private static final String ARG_ROOT_ID = "param1";
    private static final String ARG_APP_NAME = "param2";
    private static final String ARG_TAG_NAME = "param3";
    private static final String ARG_DEV_EMAIL = "param4";
    private int parentLayoutId;
    private String appName;
    private String tagName;
    private String devEmail;
    private boolean showPromote = false;

    public static PromoScreenManagerFragment newInstance(final Context context,
                                                         String tag,
                                                         @IdRes int parentLayoutId,
                                                         @StringRes int appNameRes,
                                                         @StringRes int devEmailRes) {
        PromoScreenManagerFragment fragment = new PromoScreenManagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROOT_ID, parentLayoutId);
        args.putString(ARG_APP_NAME, context.getResources().getString(appNameRes));
        args.putString(ARG_TAG_NAME, tag);
        args.putString(ARG_DEV_EMAIL, context.getResources().getString(devEmailRes));
        fragment.setArguments(args);
        return fragment;
    }

    public PromoScreenManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.parentLayoutId = args.getInt(ARG_ROOT_ID);
            this.tagName = args.getString(ARG_TAG_NAME);
            this.appName = args.getString(ARG_APP_NAME);
            this.devEmail = args.getString(ARG_DEV_EMAIL);
        }
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

    @Override
    public void onActionSelected(int actionSelected) {
        Log.v(LOG_TAG, "onActionSelected entered.");
        this.showPromote = false;
        this.managePromoteFragment();
    }

    private void managePromoteFragment(){
        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        if (fragmentManager != null) {
            PromoteScreenFragment fragment = (PromoteScreenFragment)fragmentManager.findFragmentByTag(this.tagName);
            if (this.showPromote) {
                Log.v(LOG_TAG, "managePromoteFragment: promptFragment adding");
                if (fragment == null) {
                    fragment = PromoteScreenFragment.newInstance(this.appName, this.devEmail);
                    fragmentManager.beginTransaction()
                            .replace(this.parentLayoutId, fragment, this.tagName)
                            .commitAllowingStateLoss();
                }
                fragment.setRatePromptListener(this);
            } else {
                if (fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }

            }
        }
    }
}
