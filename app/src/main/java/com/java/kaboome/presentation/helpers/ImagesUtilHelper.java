package com.java.kaboome.presentation.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;

import java.io.IOException;

public class ImagesUtilHelper {


    public static final String TAG = "KMImagesUtilHlpr";

    public static String getUserProfilePicName(String userId, ImageTypeConstants imageTypeConstants) {
        if (ImageTypeConstants.MAIN.equals(imageTypeConstants))
            return "userProfile"+userId + "_MN";
        else
            return "userProfile"+userId + "_TN";
    }

    public static String getGroupImageName(String groupId, ImageTypeConstants imageTypeConstants) {
        if (ImageTypeConstants.MAIN.equals(imageTypeConstants))
            return "group" + groupId + "_MN";
        else
            return "group" + groupId + "_TN";
    }

    public static String getGroupUserImageName(String groupId, String userId, ImageTypeConstants imageTypeConstants) {
        if (ImageTypeConstants.MAIN.equals(imageTypeConstants))
            return "groupUser" + userId + "_" + groupId + "_MN";
        else
            return "groupUser" + userId + "_" + groupId + "_TN";
    }

    public static String getMessageAttachmentKeyName(String groupId, String messageId) {
        return "message" + groupId + "_" + messageId;
    }

//    public static String getGroupUserTNName(String groupId, String userId) {
//        return userId+"_"+groupId+"_Profile_TN";
//    }
//
//    public static String getGroupTNName(String groupId) {
//        return groupId+"_Profile_TN";
//    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getThumbnailBitmap(Context context, String attachmentURI, String attachmentPath, String fileMime) {
        Bitmap thumbnail = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                thumbnail = context.getContentResolver().loadThumbnail(Uri.parse(attachmentURI), new Size(20, 20), null);
            } catch (IOException e) {
                Log.d(TAG, "exception on thumbnail; generation - " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            thumbnail = FileUtils.getThumbnail(context, Uri.parse(attachmentURI), fileMime);
            Bitmap exifedBitmap = thumbnail;
            if(thumbnail == null){
                //somehow bitmap is null, put default
                Drawable d = context.getResources().getDrawable(R.drawable.attachment_default);
                thumbnail = ImagesUtilHelper.drawableToBitmap(d);
            }


                try {
                    //sometimes for the older versions, the exif is coming off by 90 degrees
                    //hence rotating it
                    int angle = 0;
                    ExifInterface oldExif = new ExifInterface(attachmentPath);
                    int orientation = oldExif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            angle = 90;
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            angle = 180;
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            angle = 270;
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            angle = 0;
                    }

                    Matrix matrix = new Matrix();
                    matrix.postRotate(angle);
                    exifedBitmap = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(),
                            matrix, true);
                    thumbnail = exifedBitmap;

                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
        return thumbnail;
    }
}
