package com.mssdevlab.baselib.common;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mssdevlab.baselib.R;

/**
 * Shows the promote screen and controls the workflow
 */
public class PromoteScreenFragment extends Fragment {
    final static public int RATE_SELECTED = 1;
    final static public int NOT_NOW_SELECTED = 2;
    final static public int NEVER_SELECTED = 3;

    final static private String LOG_TAG = "PromoteScreenFragment";
    private static final String ARG_APP_NAME = "param_app_name";
    private static final String ARG_DEV_EMAIL = "param_dev_email";

    private String mAppName;
    private String mDevEMail;
    private OnRatePromptListener mListener;
    private OnFormatListener mFormatCallback;
    Button mBtnYes;
    Button mBtnNot;
    TextView mTvPrompt;

    public static PromoteScreenFragment newInstance(String appName, String devEMail) {
        Log.v(LOG_TAG, "newInstance appName: " + appName);
        PromoteScreenFragment fragment = new PromoteScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_NAME, appName);
        args.putString(ARG_DEV_EMAIL, devEMail);
        fragment.setArguments(args);
        return fragment;
    }

    public PromoteScreenFragment() {
        Log.v(LOG_TAG, "Empty constructor");
        // Required empty public constructor
    }

    private void setActivity(Activity activity) {
        if (activity instanceof OnFormatListener) {
            this.mFormatCallback = (OnFormatListener) activity;
        }
        this.mListener = (OnRatePromptListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivity(getActivity());

        Log.v(LOG_TAG, "onCreate");
        if (getArguments() != null) {
            this.mAppName = getArguments().getString(ARG_APP_NAME);
            this.mDevEMail = getArguments().getString(ARG_DEV_EMAIL);
            Log.v(LOG_TAG, "onCreate mAppName: " + this.mAppName + "; email: " + this.mDevEMail);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        View root = inflater.inflate(R.layout.steps_prompt_fragment, container, false);
        mBtnYes = (Button) root.findViewById(R.id.btnYes);
        mBtnNot = (Button) root.findViewById(R.id.btnNot);
        mTvPrompt = (TextView) root.findViewById(R.id.tvPromptQuestion);
        String text = this.getActivity().getResources().getString(R.string.common_enjoy_prompt, mAppName);
        mTvPrompt.setText(text);
        mBtnYes.setText(R.string.common_enjoy_yes);
        mBtnNot.setText(R.string.common_enjoy_not);

        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEnjoy();
            }
        });

        mBtnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNotEnjoy();
            }
        });

        if (this.mFormatCallback != null) {
            this.mFormatCallback.onFormat(root);
        }
        Log.v(LOG_TAG, "onCreateView: initialized");
        return root;
    }

    void userEnjoy() {
        mTvPrompt.setText(R.string.common_enjoy_rate_prompt);
        mBtnYes.setText(R.string.common_enjoy_rate_yes);
        mBtnNot.setText(R.string.common_enjoy_rate_not);

        final Context activity = this.getActivity();
        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteScreenManager.GoToRateScreen(activity);
                onActionSelected(RATE_SELECTED);
            }
        });

        mBtnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteScreenManager.MarkRateNotNow(activity);
                onActionSelected(NOT_NOW_SELECTED);
            }
        });
    }

    void userNotEnjoy() {
        mTvPrompt.setText(R.string.common_enjoy_feedback_prompt);
        mBtnYes.setText(R.string.common_enjoy_feedback_yes);
        mBtnNot.setText(R.string.common_enjoy_feedback_not);

        final Context activity = this.getActivity();
        final Resources res = activity.getResources();
        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uriText = "mailto:" + mDevEMail + "?subject="
                        + Uri.encode(mAppName) + "&body=" + " ... ";
                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                activity.startActivity(Intent.createChooser(sendIntent,
                        res.getString(R.string.common_error_report_choose_title)));

                PromoteScreenManager.MarkRateCancelPermanently(activity);
                onActionSelected(NEVER_SELECTED);
            }
        });

        mBtnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteScreenManager.MarkRateCancelPermanently(activity);
                onActionSelected(NEVER_SELECTED);
            }
        });
    }

    // RateFragment action finished
    public void onActionSelected(int actionSelected) {
        if (this.mListener != null) {
            mListener.onActionSelected(actionSelected);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mFormatCallback = null;
    }

    public interface OnRatePromptListener {
        void onActionSelected(int actionSelected);
    }
}
