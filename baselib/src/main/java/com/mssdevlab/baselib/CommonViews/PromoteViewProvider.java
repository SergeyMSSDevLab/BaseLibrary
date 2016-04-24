package com.mssdevlab.baselib.CommonViews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mssdevlab.baselib.R;
import com.mssdevlab.baselib.common.PromoteManager;

/**
 * View holder manages the promote screen.
 */
public class PromoteViewProvider extends CommonViewProvider {
    private static final String LOG_TAG = PromoteViewProvider.class.getCanonicalName();
    public static final String ARG_APP_NAME = "promoteScreenHolder.param1";
    public static final String ARG_DEV_EMAIL = "promoteScreenHolder.param2";

    private View mView;
    private Button btnYes;
    private Button btnNot;
    private TextView tvPrompt;
    private String mAppName;
    private String mDevEmail;

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "mView != null: " + (mView != null));
        if (PromoteManager.isTimeToShowPromoScreen()){
            if (this.mView == null && this.mViewStub != null) {
                this.mViewStub.setLayoutResource(R.layout.promote_view);
                this.mView = this.mViewStub.inflate();
                this.setUpView(this.mView);
            }
            if (this.mView != null) {
                this.mView.setVisibility(View.VISIBLE);
                Log.v(LOG_TAG, "onResume: Promo screen visible.");
            }
        } else {
            if (this.mView != null){
                this.mView.setVisibility(View.GONE);
                Log.v(LOG_TAG, "onResume: Promo screen gone.");
            }
        }
    }

    @Override
    protected void setUpView(@NonNull View view){
        Log.v(LOG_TAG, "setUpView: view == null: " + (view == null));
        if (this.mArguments != null){
            this.mAppName = this.mArguments.getString(ARG_APP_NAME);
            this.mDevEmail = this.mArguments.getString(ARG_DEV_EMAIL);
        }

        btnYes = (Button) mView.findViewById(R.id.btnYes);
        btnNot = (Button) mView.findViewById(R.id.btnNot);
        tvPrompt = (TextView) mView.findViewById(R.id.tvPromptQuestion);
        String text = this.getActivity().getResources().getString(R.string.common_enjoy_prompt, this.mAppName == null ? "application": this.mAppName);
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

        super.setUpView(view);
    }

    void userEnjoy() {
        tvPrompt.setText(R.string.common_enjoy_rate_prompt);
        btnYes.setText(R.string.common_enjoy_rate_yes);
        btnNot.setText(R.string.common_enjoy_rate_not);

        final Context activity = this.getActivity();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteManager.goToPromoScreen(activity);
                mView.setVisibility(View.GONE);
            }
        });

        btnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteManager.markRateNotNow();
                mView.setVisibility(View.GONE);
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

                String uriText = "mailto:" + (mDevEmail == null ? "": mDevEmail) + "?subject="
                        + Uri.encode(mAppName == null ? "application": mAppName) + "&body=" + " ... ";
                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                activity.startActivity(Intent.createChooser(sendIntent,
                        res.getString(R.string.common_error_report_choose_title)));

                PromoteManager.cancelPromoScreenPermanently();
                mView.setVisibility(View.GONE);
            }
        });

        btnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoteManager.cancelPromoScreenPermanently();
                mView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean isViewAvailable() {
        return PromoteManager.isTimeToShowPromoScreen();
    }
}
