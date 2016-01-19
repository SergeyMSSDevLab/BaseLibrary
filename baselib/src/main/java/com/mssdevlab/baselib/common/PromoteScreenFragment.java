package com.mssdevlab.baselib.common;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
@SuppressWarnings("unused")
public class PromoteScreenFragment extends Fragment {
    final static public int RATE_SELECTED = 1;
    final static public int NOT_NOW_SELECTED = 2;
    final static public int NEVER_SELECTED = 3;

    final static private String LOG_TAG = "PromoteScreenFragment";
    private static final String ARG_APP_NAME = "param_app_name";
    private static final String ARG_DEV_EMAIL = "param_dev_email";

    private String appName;
    private String devEMail;
    private OnRatePromptListener ratePromptListener;
    private OnFormatListener formatListener;
    Button btnYes;
    Button btnNot;
    TextView tvPrompt;

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

    private void setActivity(FragmentActivity activity) {
        if (activity instanceof OnFormatListener) {
            this.formatListener = (OnFormatListener) activity;
        }
        this.ratePromptListener = (OnRatePromptListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivity(getActivity());

        Log.v(LOG_TAG, "onCreate");
        if (getArguments() != null) {
            this.appName = getArguments().getString(ARG_APP_NAME);
            this.devEMail = getArguments().getString(ARG_DEV_EMAIL);
            Log.v(LOG_TAG, "onCreate appName: " + this.appName + "; email: " + this.devEMail);
        }
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
            this.formatListener.onFormat(root);
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

                String uriText = "mailto:" + devEMail + "?subject="
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
