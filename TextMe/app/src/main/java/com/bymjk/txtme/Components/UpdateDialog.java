package com.bymjk.txtme.Components;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bymjk.txtme.Activities.MainActivity;
import com.bymjk.txtme.Activities.SplashScreen;
import com.bymjk.txtme.BuildConfig;
import com.bymjk.txtme.Models.AppVersion;
import com.bymjk.txtme.R;

public class UpdateDialog {

    HelperFunctions helperFunctions = new HelperFunctions();

    public void ShowUpdateDialog(Context context, AppVersion appVersion, int daysLeft, Activity activity){
        TextView versionToVersion, messageBox, noThanksBtn, updateNowBtn;
        TextView firstFeature,secondFeature,thirdFeature,forthFeature,fifthFeature,sixthFeature;
        String firstStr,secondStr, thirdStr, fourthStr, fifthStr, sixthStr;

        String url = appVersion.getAppUrl();

        String currentVersion = BuildConfig.VERSION_NAME;
        String newVersion = appVersion.getAppVersion();

        String kMessage = context.getResources().getString(R.string.update_version_bottom_text);
        String kNewMessage = String.format(kMessage, daysLeft);

        String kVersions = context.getResources().getString(R.string.version_to_version);
        String kNewVersions = String.format(kVersions, currentVersion, newVersion);




        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.app_update_available_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        versionToVersion = dialog.findViewById(R.id.versionToVersion);
        messageBox = dialog.findViewById(R.id.updateMassageBox);
        firstFeature = dialog.findViewById(R.id.point1);
        secondFeature = dialog.findViewById(R.id.point2);
        thirdFeature = dialog.findViewById(R.id.point3);
        forthFeature = dialog.findViewById(R.id.point4);
        fifthFeature = dialog.findViewById(R.id.point5);
        sixthFeature = dialog.findViewById(R.id.point6);
        noThanksBtn = dialog.findViewById(R.id.noThanksBtn);
        updateNowBtn = dialog.findViewById(R.id.updateVersionNowBtn);


        if (newVersion != null ) {
            versionToVersion.setText(kNewVersions);
        }

        messageBox.setText(kNewMessage);

        if (appVersion != null && appVersion.getFeature()!= null) {
            firstStr = appVersion.getFeature().getFirst();
            secondStr = appVersion.getFeature().getSecond();
            thirdStr = appVersion.getFeature().getThird();
            fourthStr = appVersion.getFeature().getForth();
            fifthStr = appVersion.getFeature().getFifth();
            sixthStr = appVersion.getFeature().getSixth();


        if (firstStr == null || firstStr.isEmpty() ){
            firstFeature.setVisibility(View.INVISIBLE);
        }
        else {
            firstFeature.setVisibility(View.VISIBLE);
            firstFeature.setText(firstStr);
        }

        if (secondStr == null || secondStr.isEmpty() ){
            secondFeature.setVisibility(View.INVISIBLE);
        }
        else {
            secondFeature.setVisibility(View.VISIBLE);
            secondFeature.setText(secondStr);
        }

        if (thirdStr == null || thirdStr.isEmpty() ){
            thirdFeature.setVisibility(View.INVISIBLE);
        }
        else {
            thirdFeature.setVisibility(View.VISIBLE);
            thirdFeature.setText(thirdStr);
        }

        if (fourthStr == null || fourthStr.isEmpty() ){
            forthFeature.setVisibility(View.INVISIBLE);
        }
        else {
            forthFeature.setVisibility(View.VISIBLE);
            forthFeature.setText(fourthStr);
        }

        if (fifthStr == null || fifthStr.isEmpty() ){
            fifthFeature.setVisibility(View.INVISIBLE);
        }
        else {
            fifthFeature.setVisibility(View.VISIBLE);
            fifthFeature.setText(fifthStr);
        }

        if (sixthStr == null || sixthStr.isEmpty() ){
            sixthFeature.setVisibility(View.INVISIBLE);
        }
        else {
            sixthFeature.setVisibility(View.VISIBLE);
            sixthFeature.setText(sixthStr);
        }
        }

        dialog.setCancelable(false);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        noThanksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (daysLeft > 0){
                    dialog.dismiss();
                }
                else {
                    activity.finish();
                }
            }
        });

        updateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNowBtn.setEnabled(false);
                dialog.dismiss();
                helperFunctions.downloadFile(url,context,newVersion,activity);

            }
        });

    }

}
