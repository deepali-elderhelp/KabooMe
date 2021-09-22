package com.java.kaboome.presentation.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.java.kaboome.helpers.AppConfigHelper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Peli
 * @author paulburke (ipaulpro)
 * @version 2013-12-11
 */
public class FileUtils {
    private FileUtils() {
    } //private constructor to enforce Singleton pattern

    /**
     * TAG for log messages.
     */
    static final String TAG = "FileUtils";
    private static final boolean DEBUG = true; // Set to true to enable logging

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";

    public static final String HIDDEN_PREFIX = ".";

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    public static String getExtension(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }

        String uriPath = getPath(context, uri);

        int dot = uriPath.lastIndexOf(".");
        if (dot >= 0) {
            return uriPath.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }



    /**
     * @return Whether the URI is a local one.
     */
    public static boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
    public static String getMimeType(File file) {

        String extension = getExtension(file.getName());

        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    public static String getMimeType(String extension){
        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    /**
     * @return The MIME type for the give Uri.
     */
    public static String getMimeType(Context context, Uri uri) {
        File file = new File(getPath(context, uri));
        return getMimeType(file);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is {@link LocalStorageProvider}.
     * @author paulburke
     */
//    public static boolean isLocalStorageDocument(Uri uri) {
//        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
//    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static int getIdColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_id";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getInt(column_index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null)
                cursor.close();
        }
        return 0;
    }

    /**
     * Get a file path from a Uri. This will quickGet the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     */
    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
//            if (isLocalStorageDocument(uri)) {
//                // The path is the id
//                return DocumentsContract.getDocumentId(uri);
//            }
//            // ExternalStorageProvider
//            else
                if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
                return Environment.getExternalStorageDirectory() + "/" + split[1];

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    Log.d(TAG, "getPath: id= " + id);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }catch (Exception e){
                    e.printStackTrace();
                    List<String> segments = uri.getPathSegments();
                    if(segments.size() > 1) {
                        String rawPath = segments.get(1);
                        if(!rawPath.startsWith("/")){
                            return rawPath.substring(rawPath.indexOf("/"));
                        }else {
                            return rawPath;
                        }
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return uri.getPath();
    }


    /**
     * Get a file path from a Uri. This will quickGet the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     */
    public static String getPathNew(final Context context, final Uri uri) {

        if (DEBUG)
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
//            if (isLocalStorageDocument(uri)) {
//                // The path is the id
//                return DocumentsContract.getDocumentId(uri);
//            }
//            // ExternalStorageProvider
//            else
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
                return Environment.getExternalStorageDirectory() + "/" + split[1];

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    Log.d(TAG, "getPath: id= " + id);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }catch (Exception e){
                    e.printStackTrace();
                    List<String> segments = uri.getPathSegments();
                    if(segments.size() > 1) {
                        String rawPath = segments.get(1);
                        if(!rawPath.startsWith("/")){
                            return rawPath.substring(rawPath.indexOf("/"));
                        }else {
                            return rawPath;
                        }
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getRelativePathColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getRelativePathColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return uri.getPath();
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getRelativePathColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "relative_path";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see #getPath(Context, Uri)
     */
    public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (path != null && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    public static boolean uriFileExists(Uri uriPassed){
        File file = new File(uriPassed.toString());
        if (file.exists()) {
            return true;
        }
        return false;
    }



    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    public static String getReadableFileSize(int size) {
        final int BYTES_IN_KILOBYTES = 1024;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        float fileSize = 0;
        String suffix = KILOBYTES;

        if (size > BYTES_IN_KILOBYTES) {
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }
        return String.valueOf(dec.format(fileSize) + suffix);
    }

    /**
     * Attempt to retrieve the thumbnail of given File from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param file
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, File file) {
        return getThumbnail(context, getUri(file), getMimeType(file));
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, Uri uri) {
        return getThumbnail(context, uri, getMimeType(context, uri));
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @param mimeType
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, Uri uri, String mimeType) {
        if (DEBUG)
            Log.d(TAG, "Attempting to quickGet thumbnail");

//        if (!isMediaUri(uri)) {
//            Log.e(TAG, "You can only retrieve thumbnails for images and videos.");
//            return null;
//        }

        Bitmap bm = null;
        final ContentResolver resolver = context.getContentResolver();
        if (uri != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id_one = wholeID.split(":")[1];
                Long origId = Long.parseLong(id_one);

                if (mimeType.contains("video")) {
                    bm = MediaStore.Video.Thumbnails.getThumbnail(
                            resolver,
                            origId,
                            MediaStore.Video.Thumbnails.MINI_KIND,
                            null);
//                    } else if (mimeType.contains(FileUtils.MIME_TYPE_IMAGE)) {

                } else if (mimeType.contains("image")) {
                    bm = MediaStore.Images.Thumbnails.getThumbnail(
                            resolver,
                            origId,
                            MediaStore.Images.Thumbnails.MICRO_KIND,
                            null);
                }
            }
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
            try {
                cursor = resolver.query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    final int id = cursor.getInt(0);
                    if (DEBUG)
                        Log.d(TAG, "Got thumb ID: " + id);

                    if (mimeType.contains("video")) {
                        bm = MediaStore.Video.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Video.Thumbnails.MINI_KIND,
                                null);

                    } else if (mimeType.contains("image")) {
                        bm = MediaStore.Images.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Images.Thumbnails.MICRO_KIND,
                                null);
                    }
                }
            } catch (Exception e) {
                if (DEBUG)
                    Log.e(TAG, "getThumbnail", e);
            } finally {
                if (cursor != null)
                    cursor.close();
            }

            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                //don;t know what to do
            }

        }
        return bm;
    }

    /**
     * File and folder comparator. TODO Expose sorting option method
     *
     * @author paulburke
     */
    public static Comparator<File> sComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };

    /**
     * File (not directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return files only (not directories) and skip hidden files
            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Folder (directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return directories only and skip hidden directories
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     * @author paulburke
     */
    public static Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    public static File copyAttachmentToApp(Uri attachmentUri, String sentTo, String messageId, String groupId, Context context){

//        String fileType = contentResolver.getType(selectedFile);

//        String extension =
//        Cursor cursor = contentResolver.query(selectedFile, null, null, null, null);
//        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//        String fileName = cursor.getString(nameIndex);




        String fileExt = getExtension(attachmentUri, context);

        File sourceFile = getFile(context, attachmentUri);

        String fileType = FileUtils.getMimeType(sourceFile);

        String newName = groupId+"_"+sentTo+"_"+messageId;

        File destFile = null;


        if(fileType.contains("image")) {

            //copy it to the images folder in the app
            File imageDir = getAppDirForMime(fileType, true);

            //if filename starts with cameraImage, then we know user selected
            //camera to get that image and image is already in the right directory
            //we just need to rename it

            if(sourceFile.getName().contains("cameraImage")){
                File newNameFile = new File(imageDir, newName + fileExt);
                boolean renamed = sourceFile.renameTo(newNameFile);
                return newNameFile;
            }

            destFile = new File(imageDir, newName + fileExt);
        }
        if(fileType.contains("video")) {

            //copy it to the images folder in the app
            File imageDir = getAppDirForMime(fileType, true);
            destFile = new File(imageDir, newName + fileExt);
        }
        if(fileType.contains("audio")) {

            //copy it to the images folder in the app
            File imageDir = getAppDirForMime(fileType, true);
            destFile = new File(imageDir, newName + fileExt);
        }

        try {
            destFile.createNewFile();
            FileChannel source = null;
            FileChannel destination = null;

            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return destFile;


    }

    /**
     *
     * @param attachmentUri
     * @param context
     * @return Absolute path of the copied attachment
     */
    public static String copyAttachmentToApp(Uri attachmentUri, String extension, Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, extension, context.getCacheDir());

        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(attachmentUri, "r");
        InputStream fileStream = new FileInputStream(pfd.getFileDescriptor());
        OutputStream newCacheFile = new FileOutputStream(image);

        byte[] buffer = new byte[1024];
        int length;

        while((length = fileStream.read(buffer)) > 0)
        {
            newCacheFile.write(buffer, 0, length);
        }

        newCacheFile.flush();
        fileStream.close();
        newCacheFile.close();

        return image.getAbsolutePath();
    }

    //this is for below Q
    //you should have another method for Q and above
    public static File getAttachmentFileForMessage(String messageId, String sentTo, String groupName, String extension, String attachmentMime) {
        File appDir = getAppDirForMime(attachmentMime, groupName, sentTo, false);
        if(appDir == null){
            return null;
        }
        File attachmentFile = new File(appDir, messageId + extension);
        return attachmentFile;

//        if(attachmentMime.contains("image")){
//            File appDir = getAppDirForMime(attachmentMime, false);
//
//            if(appDir != null) {
////                File attachmentFile = new File(appDir, groupId+"_"+messageId + extension);
//                File attachmentFile = new File(appDir, groupId+"_"+sentTo+"_"+messageId + extension);
//                return attachmentFile;
//            }
//        }
//        if(attachmentMime.contains("video")){
//            File appDir = getAppDirForMime(attachmentMime, false);
//            if(appDir != null) {
//                File attachmentFile = new File(appDir, groupId+"_"+sentTo+"_"+messageId + extension);
//                return attachmentFile;
//            }
//        }
//        if(attachmentMime.contains("audio")){
//            File appDir = getAppDirForMime(attachmentMime, false);
//            if(appDir != null) {
//                File attachmentFile = new File(appDir, groupId+"_"+sentTo+"_"+messageId + extension);
//                return attachmentFile;
//            }
//        }

//        return null;
    }

    public static File getAppDirForMime(String attachmentMime, boolean makeOneIfDoesNotExist) {

        if(attachmentMime == null){
            return null;
        }

        String folder_main = "KabooMe";

//        File appDir = AppConfigHelper.getContext().getExternalFilesDir(null);
//        File appDir = new File(Environment.getExternalStorageDirectory(), folder_main);
        File appDir = new File(AppConfigHelper.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),folder_main);
        if (!appDir.exists() && makeOneIfDoesNotExist) {
            appDir.mkdirs();
        }

        if(attachmentMime.contains("image")){
            File imageDir = new File(appDir, "images");
            if(!imageDir.exists() && makeOneIfDoesNotExist){
                imageDir.mkdirs();
            }
            return imageDir;
        }
        if(attachmentMime.contains("video")){
            File imageDir = new File(appDir, "videos");
            if(!imageDir.exists() && makeOneIfDoesNotExist){
                imageDir.mkdirs();
            }
            return imageDir;
        }
        if(attachmentMime.contains("audio")){
            File imageDir = new File(appDir, "audios");
            if(!imageDir.exists() && makeOneIfDoesNotExist){
                imageDir.mkdirs();
            }
            return imageDir;
        }


        return null;
    }

    public static File getAppDirForMime(String attachmentMime, String groupName, String sentTo, boolean makeOneIfDoesNotExist) {

        if(attachmentMime == null){
            return null;
        }

//        String folder_main = "KabooMe";
        String folder_main = "KabooMe"+ File.separator + MediaHelper.getGroupNameTrimmed(groupName)+File.separator+sentTo;

//        File appDir = AppConfigHelper.getContext().getExternalFilesDir(null);
//        File appDir = new File(Environment.getExternalStorageDirectory(), folder_main);
//        File appDir = new File(AppConfigHelper.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),folder_main);
        File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),folder_main);

        if (!appDir.exists() && makeOneIfDoesNotExist) {
            appDir.mkdirs();
        }

        if(attachmentMime.contains("image")){
            File imageDir = new File(appDir, "images");
            if(!imageDir.exists() && makeOneIfDoesNotExist){
                imageDir.mkdirs();
            }
            return imageDir;
        }
        if(attachmentMime.contains("video")){
            File imageDir = new File(appDir, "videos");
            if(!imageDir.exists() && makeOneIfDoesNotExist){
                imageDir.mkdirs();
            }
            return imageDir;
        }
        if(attachmentMime.contains("audio")){
            File imageDir = new File(appDir, "audios");
            if(!imageDir.exists() && makeOneIfDoesNotExist){
                imageDir.mkdirs();
            }
            return imageDir;
        }


        return null;
    }

    public static File createAttachmentFileForMessageAttachment(Context context, String groupId, String sentTo, String messageId, String extension, String attachmentMime) throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "attachment_" + timeStamp + "_";
//        String imageFileName = groupId+"_"+messageId;
        String imageFileName = groupId+"_"+sentTo+"_"+messageId;
        File storageDir = null;
        if(attachmentMime.contains("image")){
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if(attachmentMime.contains("video")){
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }
        if(attachmentMime.contains("audio")){
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                extension,         /* suffix */
                storageDir      /* directory */
        );

        return image;


    }

//    public static File createAttachmentFileForMessage(String messageId, String groupId, String sentTo, String extension, String attachmentMime) throws IOException{
//        File imageDir = getAppDirForMime(attachmentMime, true);
//
//        if(imageDir == null){
//            return null;
//        }
//        File attachmentFile = new File(imageDir, groupId+"_"+sentTo+"_"+messageId + extension);
//        attachmentFile.createNewFile();
//        return attachmentFile;
//    }

    public static File createAttachmentFileForMessage(String messageId, String groupName, String sentTo, String extension, String attachmentMime) throws IOException{
        File imageDir = getAppDirForMime(attachmentMime, groupName, sentTo, true);

        if(imageDir == null){
            return null;
        }
        File attachmentFile = new File(imageDir, messageId + extension);
        attachmentFile.createNewFile();
        return attachmentFile;
    }

    public static File getCacheFilePathForImages(Context context){
        File cachePath = new File(context.getCacheDir(), "images");
        if(cachePath.exists()){
            for (File f : cachePath.listFiles()) {
                Log.d(TAG, "File: "+f.lastModified());
                f.delete();
            }
            cachePath.delete();
        }
        cachePath.mkdirs();
        for (File f : cachePath.listFiles()) {
            Log.d(TAG, "File: "+f.lastModified());
        }
        return cachePath;
    }

    public static File getCacheImageFile(Context context){
        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "qr_code_image.jpg");
        return newFile;
    }

    public static File getCacheFileCreated(Context context, String folder, String cacheFileName){
        File cachePath = new File(context.getCacheDir(), folder);
        if(cachePath.exists()){
            for (File f : cachePath.listFiles()) {
                Log.d(TAG, "File: "+f.lastModified());
                f.delete();
            }
            cachePath.delete();
        }
        cachePath.mkdirs();
        for (File f : cachePath.listFiles()) {
            Log.d(TAG, "File: "+f.lastModified());
        }

        File newFile = new File(cachePath, cacheFileName);
        return newFile;
    }

    public static void deleteFile(String path){
        File myFile = new File(path);
        boolean returnedBool = myFile.delete();
        Log.d(TAG, "deleteFile: "+returnedBool);
        return;
    }
}


