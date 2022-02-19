/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.images;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.java.kaboome.R;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.views.features.CameraActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureDialog extends DialogFragment {

    private static final String TAG = "GlidePictureDialog";

    private static final int CAMERA = 99;
    private static final int READ_EXTERNAL = 98;

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private static final int MULTIPLE_REQUESTS = 111;

    View rootView;
    Button cameraButton;
    Button galleryButton;
    String pathToProfilePic;
    String pathToThumbnailPic;
    File photoFile;
    Context context;
//    PictureDoneListener pictureDoneListener;
    Boolean pictureToBeSavedOnServer = false;
    Boolean pictureIsGroupPicture = true;
//    String groupId = null;
    String userData = null;
    String imageId = null;
    ProgressBar pageProgressBar;
//    NavController navController;
    private Uri picUri;


    public PictureDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.picture_dialog_layout, container, false);
        cameraButton = rootView.findViewById(R.id.fr_gr_cr_4_1_camera_button);
        cameraButton.setOnClickListener(cameraClicked);
        galleryButton = rootView.findViewById(R.id.fr_gr_cr_4_1_gallery_button);
        galleryButton.setOnClickListener(galleryClicked);
        pageProgressBar = rootView.findViewById(R.id.picture_dialog_pb);

        Bundle argumentsPassed = getArguments();
        pictureToBeSavedOnServer = argumentsPassed.getBoolean("pictureToBeSavedOnServer");
        pictureIsGroupPicture = argumentsPassed.getBoolean("pictureIsGroupPicture");
        userData = argumentsPassed.getString("userData");
        imageId = argumentsPassed.getString("imageId");
//        if(pictureToBeSavedOnServer && pictureIsGroupPicture){
//            groupId = argumentsPassed.getString("groupId");
//        }
//        navController = NavHostFragment.findNavController(PictureDialog.this);
        return rootView;
    }

    @Override
    public void onResume() {

        context = getContext();
        Log.d(TAG, "Dialog onResume");

        Window window = getDialog().getWindow();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthLcl = (int) (displayMetrics.widthPixels*0.6f);
        int heightLcl = (int) (displayMetrics.heightPixels*0.5f);

        window.setLayout(widthLcl, heightLcl);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

//    public void setPictureDoneListener(PictureDoneListener pictureDoneListener) {
//        this.pictureDoneListener = pictureDoneListener;
//    }

    View.OnClickListener cameraClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                startCapturingImageForQAndR();
            }
            else {
                startCapturingImage();
            }



//            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                startCapturingImage();
//            }
//            else{
//                requestRequiredPermissions();
//            }

        }
    };

    private void requestRequiredPermissions() {
//            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                        .setTitle("Permissions requested")
                        .setMessage("Camera and write image permission is needed")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //request permission now
//                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MULTIPLE_REQUESTS);
                                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MULTIPLE_REQUESTS);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
            else{
                //request permission directly
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MULTIPLE_REQUESTS);
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MULTIPLE_REQUESTS);
            }
    }

    private void requestRequiredGalleryPermissions() {
//        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
        if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                    .setTitle("Permissions requested")
                    .setMessage("Write image permission is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //request permission now
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
        else{
            //request permission directly
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
        }
    }

//    View.OnClickListener galleryClicked = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                choosePhotoFromGallery();
//            }
//            else{
//                requestRequiredGalleryPermissions();
//            }
//
//        }
//    };

    View.OnClickListener galleryClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                choosePhotoFromGalleryForQAndUp();
            }
            else {
                choosePhotoFromGallery();
            }
        }
    };

    @AfterPermissionGranted(CAMERA)
    private void startCapturingImageForQAndR() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            CameraActivity activity = (CameraActivity) getActivity();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                try {
                    photoFile = createImageFile("cameraImage");
                    pathToProfilePic = photoFile.getAbsolutePath();

                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.d(TAG, "Exception while creating file: " + ex.toString());
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.d(TAG, "Photofile not null");
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.java.kaboome.fileprovider",
                            photoFile);
                    Log.d(TAG, "Image URI- " + photoURI);
                    Log.d(TAG, "Photo Path - " + photoURI.getPath());
                    Log.d(TAG, "Image File Path- " + pathToProfilePic);
                    this.picUri = photoURI;
                    activity.setCapturedImageURI(photoURI);
                    activity.setCurrentPhotoPath(photoURI.getPath());
                    activity.setCapturedImageFilePath(pathToProfilePic);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            activity.getCapturedImageURI());
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to open the camera", CAMERA, perms);
        }


    }

    @AfterPermissionGranted(MULTIPLE_REQUESTS)
    private void startCapturingImage() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            CameraActivity activity = (CameraActivity) getActivity();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                try {
                    photoFile = createImageFile(imageId);
//                picUri = FileProvider.getUriForFile(getActivity(),
//                        "com.java.kaboome.fileprovider",
//                        photoFile);
//                pathToProfilePic = getPath( getActivity().getApplicationContext(),picUri );
                    pathToProfilePic = photoFile.getAbsolutePath();


                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.d(TAG, "Exception while creating file: " + ex.toString());
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.d(TAG, "Photofile not null");
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.java.kaboome.fileprovider",
                            photoFile);
                    Log.d(TAG, "Image URI- " + photoURI);
                    Log.d(TAG, "Photo Path - " + photoURI.getPath());
                    Log.d(TAG, "Image File Path- " + pathToProfilePic);
                    activity.setCapturedImageURI(photoURI);
                    activity.setCurrentPhotoPath(photoURI.getPath());
                    activity.setCapturedImageFilePath(pathToProfilePic);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            activity.getCapturedImageURI());
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to open the camera", MULTIPLE_REQUESTS, perms);
        }
    }

    private File createImageFile(String imageIdentifier) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", getContext().getCacheDir());
        return image;
    }

//    private File createImageFile(String imageIdentifier) throws IOException {
//
//        // Create an image file name
////        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
////        String imageFileName = "JPEG_" + imageIdentifier+"_"+timeStamp + "_";
//        String imageFileName = "JPEG_"+imageIdentifier+".jpg";
////        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        //in the external directory, there should be a kaboome directory created
//        //if it does not already exist
//        String folder_main = "KabooMe";
//
////        File storageDir = AppConfigHelper.getContext().getExternalFilesDir(null);
////        if (!storageDir.exists()) {
////            storageDir.mkdirs();
////        }
//
//        File storageDir = new File(Environment.getExternalStorageDirectory(), folder_main);
//        if (!storageDir.exists()) {
//            storageDir.mkdirs();
//        }
//
////        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
////        File image = File.createTempFile(
////                imageFileName,  /* prefix */
////                ".jpg",         /* suffix */
////                storageDir      /* directory */
////        );
//
//        File image = new File(storageDir, imageFileName);
//
//        if(image.exists()){
//            boolean result = image.getCanonicalFile().delete();
//            boolean created = image.createNewFile();
//            Log.d(TAG, "createImageFile: created - "+created);
//        }
//
//
//        return image;
//    }

    @AfterPermissionGranted(MULTIPLE_REQUESTS)
    public void choosePhotoFromGallery() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", MULTIPLE_REQUESTS, perms);
        }
    }

    @AfterPermissionGranted(READ_EXTERNAL)
    private void choosePhotoFromGalleryForQAndUp() {

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {

            Intent intent=new Intent(Intent.ACTION_PICK);

            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            // Launching the Intent
            startActivityForResult(intent,REQUEST_IMAGE_GALLERY);


        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", READ_EXTERNAL, perms);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called with request code - "+requestCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

////            //generate the thumbnail picture of the image
//            Bitmap originalBitmap = BitmapFactory.decodeFile(pathToProfilePic);
//            createThumbnailFromBitmap(imageId+"_TN", originalBitmap);

            File f = new File(pathToProfilePic);
            launchImageCrop(FileUtils.getUri(f));

        }
        else if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {

            Uri selectedImage = data.getData();
            launchImageCrop(selectedImage);
//            //data.getData return the content URI for the selected Image
//            Uri selectedImage = data.getData();
//
//            pathToProfilePic = getPath( getActivity( ).getApplicationContext( ), selectedImage );
//            createThumbnailFromUri(getActivity().getApplicationContext(), selectedImage, "profileTN");
//            Log.d("Picture Path", pathToProfilePic);

        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult resultData = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Log.d(TAG, "onActivityResult:CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE ");
//                Bitmap originalBitmap = resultData.getOriginalBitmap();
//                createThumbnailFromBitmap(imageId+"_TN", originalBitmap);
//                pathToProfilePic = FileUtils.getPath(context, resultData.getUri());
                pathToProfilePic = resultData.getUri().getPath();
                createThumbnailFromUri(context, resultData.getUri(), imageId+"_TN");

//                pathToProfilePic = getPath( getActivity( ).getApplicationContext( ), resultData.getUri() );


                NavController navController = NavHostFragment.findNavController(PictureDialog.this);
                navController.getPreviousBackStackEntry().getSavedStateHandle().set("imagePicked", new String[]{pathToProfilePic, userData, pathToThumbnailPic});
                navController.popBackStack();
                dismiss();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d(TAG, "Crop error: "+resultData.getError() );
            }

        }

//        NavController navController = NavHostFragment.findNavController(PictureDialog.this);
//        navController.getPreviousBackStackEntry().getSavedStateHandle().set("imagePicked", new String[]{pathToProfilePic, userData, pathToThumbnailPic});
//        navController.popBackStack();
//        dismiss();



    }

    private void launchImageCrop(Uri uri){

        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1080, 1080)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(getContext(), this);
    }

    private void createThumbnailFromUri(Context context, Uri uri, String imageId){

        Bitmap thumbnail = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                CancellationSignal ca = new CancellationSignal();
              //  thumbnail = context.getContentResolver().loadThumbnail(uri, new Size(20, 20), ca);
                thumbnail = ThumbnailUtils.createImageThumbnail (new File(pathToProfilePic),
                        new Size(20, 20),
                        ca);
            } catch (IOException e) {
                Log.d(TAG, "exception on thumbnail; generation - " + e.getMessage());
                e.printStackTrace();
            }
            if (thumbnail == null) {
                //somehow bitmap is null, put default
                Drawable d = context.getResources().getDrawable(R.drawable.account_group_white);
                thumbnail = ImagesUtilHelper.drawableToBitmap(d);
            }

        } else {
//            thumbnail = FileUtils.getThumbnail(context, uri, FileUtils.getMimeType(context, uri));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                thumbnail = ThumbnailUtils.extractThumbnail(bitmap,20,20);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (thumbnail == null) {
                //somehow bitmap is null, put default
                Drawable d = context.getResources().getDrawable(R.drawable.account_group_white);
                thumbnail = ImagesUtilHelper.drawableToBitmap(d);
            }
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        saveThumbnail(imageId, bytes);



//        Bitmap bitmap = null;
//        try {
//
//            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            saveThumbnail(imageId, bytes);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Cursor cursor = context.getContentResolver( ).query(uri, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            final int id = cursor.getInt(0);
//            Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(
//                    context.getContentResolver(),
//                    id,
//                    MediaStore.Images.Thumbnails.MICRO_KIND,
//                    null);
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//
//            saveThumbnail(imageId, bytes);
//
//            Log.d(TAG, "Bitmap size is - "+bytes.size());
//        }
//
//        cursor.close();

//        int id = FileUtils.getIdColumn(context, uri, null, null);
//        if(id == 0) return;
//
//        Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(
//                    context.getContentResolver(),
//                    id,
//                    MediaStore.Images.Thumbnails.MICRO_KIND,
//                    null);
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//
//            saveThumbnail(imageId, bytes);
//
//            Log.d(TAG, "Bitmap size is - "+bytes.size());

    }

    private void createThumbnailFromBitmap(String imageId, Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ThumbnailUtils.extractThumbnail(bitmap, 96, 96).compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Log.d(TAG, "Bitmap size is - "+bytes.size());
        saveThumbnail(imageId, bytes);
    }

    private void saveThumbnail(String imageId, ByteArrayOutputStream bytes){
        try {
            File photoFileThumbnail = createImageFile(imageId);

            if(photoFileThumbnail != null){
                FileOutputStream fo = new FileOutputStream(photoFileThumbnail);
                fo.write(bytes.toByteArray());

                //trying to set the old exif's orientation on this new file
                try {
                    ExifInterface oldExif = new ExifInterface(pathToProfilePic);
                    String exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);

                    if (exifOrientation != null) {
                        ExifInterface newExif = new ExifInterface(photoFileThumbnail.getAbsolutePath());
                        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                        newExif.saveAttributes();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                MediaScannerConnection.scanFile(context,
//                        new String[]{photoFileThumbnail.getPath()},
//                        new String[]{"image/jpeg"}, null);
                fo.close();
                Log.d(TAG, "Thumbnail File Saved::---&gt;" + photoFileThumbnail.getAbsolutePath());
            }

            pathToThumbnailPic = photoFileThumbnail.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }


    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     *
     * old method - new APIs don;t support this
     */
    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        CameraActivity activity = (CameraActivity)getActivity();
//        File f = new File(activity.getCurrentPhotoPath());
        File f = new File(activity.getCapturedImageFilePath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == MULTIPLE_REQUESTS){
            if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                startCapturingImage();
            }
            else{
                Toast.makeText(getContext(), "Permission DENIED, Cannot capture image", Toast.LENGTH_SHORT);
                dismiss();
            }
        }

        if(requestCode == REQUEST_IMAGE_GALLERY){
            if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                choosePhotoFromGallery();
            }
            else{
                Toast.makeText(getContext(), "Permission DENIED, Cannot get image", Toast.LENGTH_SHORT);
                dismiss();
            }
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }
}
