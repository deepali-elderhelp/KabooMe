package com.java.kaboome.presentation.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.java.kaboome.presentation.views.features.CameraActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MediaHelper {

    private static final String TAG = "KMMediaHelper";

    public static String saveMediaToGallery(Context context, ContentResolver contentResolver, String pathToMedia, String fileName, String mimeType, String originalGroupName ) {

        String groupName = getGroupNameTrimmed(originalGroupName);
        Uri imageUri = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            if(mimeType.contains("image")){
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "KabooMe" + File.separator + groupName);
                contentValues.put(MediaStore.Images.Media.IS_PENDING, true);
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
            if(mimeType.contains("video")){
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + File.separator + "KabooMe" + File.separator + groupName);
                contentValues.put(MediaStore.Video.Media.IS_PENDING, true);
                imageUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
            if(mimeType.contains("audio")){
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + File.separator + "KabooMe" + File.separator + groupName);
                contentValues.put(MediaStore.Audio.Media.IS_PENDING, true);
                imageUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            }


            try {
                FileInputStream fileInputStream = new FileInputStream(new File(pathToMedia));
                OutputStream outputStream = contentResolver.openOutputStream(imageUri);

                byte[] buf = new byte[8192];
                int length;
                while ((length = fileInputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, length);
                }
                contentValues.clear();
                if(mimeType.contains("image")){
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
                }
                if(mimeType.contains("video")){
                    contentValues.put(MediaStore.Video.Media.IS_PENDING, false);
                }
                if(mimeType.contains("audio")){
                    contentValues.put(MediaStore.Audio.Media.IS_PENDING, false);
                }

                contentResolver.update(imageUri, contentValues, null, null);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return imageUri.toString();
        }
        else{

            if (pathToMedia != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, pathToMedia);
                // .DATA is deprecated in API 29
                imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if(imageUri == null){
                    return null;
                }
                return imageUri.toString();
            }
//                File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        }

        return null;
    }

    public static Uri getMediaImageUri(ContentResolver contentResolver, String displayName){
        String[] projection = new String[] {
                MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME
        };
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[] { displayName };
//        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

//        Cursor cursor = contentResolver.query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                projection, null, null, null
//        );


        while (cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            String displayNameOfCursor = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            if(displayName.equals(displayNameOfCursor)){
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            }

        }

        return null;
    }




    public static List<Uri> getImagesFromGallery(ContentResolver contentResolver){

        List<Uri> uriList = new ArrayList<Uri>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

//        String[] projection = new String[] {
//                MediaStore.Video.Media._ID,
//                MediaStore.Video.Media.DISPLAY_NAME,
//                MediaStore.Video.Media.DURATION,
//                MediaStore.Video.Media.SIZE
//        };
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE
        };
//        String selection = MediaStore.Video.Media.DURATION +
//                " >= ?";
//        String[] selectionArgs = new String[] {
//                String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
//};
        String selection = null;
        String[] selectionArgs = null;

        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
//            int durationColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
//                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);

//                Uri contentUri = ContentUris.withAppendedId(
//                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                // Stores column values and the contentUri in a local object
                // that represents the media file.
//                videoList.add(new Video(contentUri, name, duration, size));
                uriList.add(contentUri);
            }
        }
        return uriList;
    }

    public static boolean doesUriFileExists(ContentResolver contentResolver, Uri uri){

        Log.d(TAG, "doesUriFileExists: "+uri);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            inputStream.close();
            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "Exception - "+e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "Exception - "+e.getMessage());
            e.printStackTrace();
            return false;
        }
        catch(Exception e){
            Log.d(TAG, "Exception - "+e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public static String getGroupNameTrimmed(String originalGroupName){

        if(originalGroupName != null){
            return originalGroupName.replaceAll("[^a-zA-Z]", "").replaceAll("\\s+","");
        }
        return null;
    }



}
