package com.java.kaboome.presentation.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileHelper {

    public static File getTempFileInExternalDir(String prefix, String suffix) throws IOException {
        //in the external directory, there should be a kaboome directory created
        //if it does not already exist
        String folder_main = "KabooMe";

        File storageDir = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                prefix,  /* prefix */
                suffix,         /* suffix */
                storageDir      /* directory */
        );

        return image;

    }

    public static void copyAttachmentToApp(Uri selectedFile, Context context){

//        String fileType = contentResolver.getType(selectedFile);

//        String extension =
//        Cursor cursor = contentResolver.query(selectedFile, null, null, null, null);
//        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//        String fileName = cursor.getString(nameIndex);
        String filePath = FileUtils.getPath(context, selectedFile);
        String fileExt = FileUtils.getExtension(filePath);
        String fileType = FileUtils.getMimeType(context, selectedFile);



        File sourceFile = new File(filePath);
        File destFile = null;


        if(fileType.contains("image")) {
            //copy it to the images folder in the app
            String folder_main = "KabooMe";

            File appDir = new File(Environment.getExternalStorageDirectory(), folder_main);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }

            File imageDir = new File(appDir, "images");
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            destFile = new File(imageDir, "test" + fileExt);
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





    }
}
