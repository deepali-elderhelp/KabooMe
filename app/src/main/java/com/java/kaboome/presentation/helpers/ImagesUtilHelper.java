package com.java.kaboome.presentation.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.java.kaboome.constants.ImageTypeConstants;

public class ImagesUtilHelper {

    public static String getUserProfilePicName(String userId, ImageTypeConstants imageTypeConstants){
        if(ImageTypeConstants.MAIN.equals(imageTypeConstants))
            return userId+"Profile_MN";
        else
            return userId+"Profile_TN";
    }

    public static String getUserProfileThumbnailName(String userId){
        return "profileTN"+userId;
    }

    public static String getGroupImageName(String groupId, ImageTypeConstants imageTypeConstants) {
        if(ImageTypeConstants.MAIN.equals(imageTypeConstants))
            return groupId+"_MN";
        else
            return groupId+"_TN";
    }

    public static String getGroupUserImageName(String groupId, String userId, ImageTypeConstants imageTypeConstants) {
        if(ImageTypeConstants.MAIN.equals(imageTypeConstants))
            return userId+"_"+groupId+"_MN";
        else
            return userId+"_"+groupId+"_TN";
    }

//    public static String getGroupUserTNName(String groupId, String userId) {
//        return userId+"_"+groupId+"_Profile_TN";
//    }
//
//    public static String getGroupTNName(String groupId) {
//        return groupId+"_Profile_TN";
//    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
