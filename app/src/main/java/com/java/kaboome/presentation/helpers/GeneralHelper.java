package com.java.kaboome.presentation.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.appcompat.app.AlertDialog;

import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.presentation.entities.IoTMessage;
import com.java.kaboome.presentation.views.features.joinGroup.JoinGroupDialog;

import java.io.ByteArrayOutputStream;

public class GeneralHelper {

    /**
     * This method validates if the string is proper alphanumeric
     * with no invalid characters
     * @param text
     * @return
     */
    public static boolean validateString(String text){
        if(text != null){
            return text.matches("[A-Za-z0-9() ]+");
        }
        return false;
    }

    public static void showAlert(Context context, String message, String title, String positiveButton, DialogInterface.OnClickListener onPositiveClick) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButton, onPositiveClick);
        builder.show();
    }

    public static String cleanString(String text){
        if(text != null){
            return text.replaceAll("[^\\dA-Za-z]", "");
        }
        return text;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        if(bitmap == null){
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String b64) {
        if(b64 == null){
            return null;
        }
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }


}
