package com.mssdevlab.baselib.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mssdevlab.baselib.OnFormatListener;
import com.mssdevlab.baselib.R;

/**
 * Shows the promote screen and controls the workflow
 */
@SuppressWarnings("unused")
public class PromoteScreenFragment extends Fragment {
    final static public int RATE_SELECTED = 1;
    final static public int NOT_NOW_SELECTED = 2;
    final static public int NEVER_SELECTED = 3;

    private static final String ARG_APP_NAME = "param1";
    private static final String ARG_DEV_EMAIL = "param2";
    private static final String ARG_TAG_NAME = "param3";

    final static private String LOG_TAG = "PromoteScreenFragment";

    private String appName;
    private String devEmail;
    private String tagName;
    private OnRatePromptListener ratePromptListener = null;
    private OnFormatListener formatListener = null;
    Button btnYes;
    Button btnNot;
    TextView tvPrompt;

    public static PromoteScreenFragment newInstance(String appName, String devEMail, String tagName) {
        Log.v(LOG_TAG, "newInstance appName: " + appName);
        PromoteScreenFragment fragment = new PromoteScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_NAME, appName);
        args.putString(ARG_DEV_EMAIL, devEMail);
        args.putString(ARG_TAG_NAME, tagName);
        fragment.setArguments(args);
        return fragment;
    }

    public PromoteScreenFragment() {
        Log.v(LOG_TAG, "Empty constructor");
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.appName = args.getString(ARG_APP_NAME);
            this.devEmail = args.getString(ARG_DEV_EMAIL);
            this.tagName = args.getString(ARG_TAG_NAME);
        }
        Activity activity = getActivity();
        if (activity instanceof OnFormatListener) {
            this.formatListener = (OnFormatListener) activity;
        }
    }

    public void setRatePromptListener(OnRatePromptListener listener){
        this.ratePromptListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        View root = inflater.inflate(R.layout.steps_prompt_fragment, container, false);
        btnYes = (Button) root.findViewById(R.id.btnYes);
        btnNot = (Button) root.findViewById(R.id.btnNot);
        tvPrompt = (TextView) root.findViewById(R.id.tvPromptQuestion);
        String text = this.getActivity().getResources().getString(R.string.common_enjoy_prompt, appName);
        tvPrompt.setText(text);
        btnYes.setText(R.string.common_enjoy_yes);
        btnNot.setText(R.string.common_enjoy_not);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEnjoy();
            }
        });

        btnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNotEnjoy();
            }
        });

        if (this.formatListener != null) {
            this.formatListener.onFormat(root, this.tagName);
        }
        Log.v(LOG_TAG, "onCreateView: initialized");
        return root;
    }

    void userEnjoy() {
        tvPrompt.setText(R.string.common_enjoy_rate_prompt);
        btnYes.setText(R.string.common_enjoy_rate_yes);
        btnNot.setText(R.string.common_enjoy_rate_not);

        final Context activity = this.getActivity();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteStuff.GoToRateScreen(activity);
                onActionSelected(RATE_SELECTED);
            }
        });

        btnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteStuff.MarkRateNotNow(activity);
                onActionSelected(NOT_NOW_SELECTED);
            }
        });
    }

    void userNotEnjoy() {
        tvPrompt.setText(R.string.common_enjoy_feedback_prompt);
        btnYes.setText(R.string.common_enjoy_feedback_yes);
        btnNot.setText(R.string.common_enjoy_feedback_not);

        final Context activity = this.getActivity();
        final Resources res = activity.getResources();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uriText = "mailto:" + devEmail + "?subject="
                        + Uri.encode(appName) + "&body=" + " ... ";
                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                activity.startActivity(Intent.createChooser(sendIntent,
                        res.getString(R.string.common_error_report_choose_title)));

                PromoteStuff.MarkRateCancelPermanently(activity);
                onActionSelected(NEVER_SELECTED);
            }
        });

        btnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteStuff.MarkRateCancelPermanently(activity);
                onActionSelected(NEVER_SELECTED);
            }
        });
    }

    // RateFragment action finished
    public void onActionSelected(int actionSelected) {
        if (this.ratePromptListener != null) {
            ratePromptListener.onActionSelected(actionSelected);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ratePromptListener = null;
        formatListener = null;
    }

    public interface OnRatePromptListener {
        void onActionSelected(int actionSelected);
    }
}
