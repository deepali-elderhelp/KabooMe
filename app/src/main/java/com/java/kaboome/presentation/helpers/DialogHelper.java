/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.java.kaboome.presentation.views.features.joinGroup.JoinGroupDialog;

public class DialogHelper {

    private static ProgressDialog waitDialog;
    private static AlertDialog userDialog;


    public static void showWaitDialog(Context mContext, String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(mContext);

        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setMessage("Loading. Please wait...");
        waitDialog.setIndeterminate(true);
        waitDialog.setCanceledOnTouchOutside(false);

        waitDialog.setTitle(message);
        waitDialog.show();
    }

    public static void updateWaitDialogMessage(String newMessage){
        if(waitDialog != null){
            waitDialog.setMessage(newMessage);
        }
    }

    public static ProgressBar getLoadSpinner(Context mContext){

        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return progressBar;


    }

    public static void showDialogMessage(Context mContext, String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    public static void closeWaitDialog() {
        if(waitDialog == null)
            return;
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    public static void showAlert(Context context, String message, String title, String positiveButton, DialogInterface.OnClickListener onPositiveClick, DialogInterface.OnClickListener onNegativeClick) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButton, onPositiveClick);
        builder.setNegativeButton("Cancel", onNegativeClick);
        builder.show();
    }

    public static void showOnlyYesAlert(Context context,String message, String title, String positiveButton, DialogInterface.OnClickListener onPositiveClick) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButton, onPositiveClick);
        builder.show();
    }




}


