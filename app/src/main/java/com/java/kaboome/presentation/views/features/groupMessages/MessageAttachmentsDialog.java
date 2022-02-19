package com.java.kaboome.presentation.views.features.groupMessages;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.java.kaboome.R;
import com.java.kaboome.presentation.entities.ContactModel;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.views.features.CameraActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Email;
import ezvcard.property.Telephone;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageAttachmentsDialog extends BottomSheetDialogFragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "KMGroupActionsDialog";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_SELECT_CONTACT = 3;
    private static final int REQUEST_VIDEO_CAPTURE = 4;
    private static final int REQUEST_AUDIO_CAPTURE = 5;
    private static final int REQUEST_VIDEO_GALLERY = 222;
    private static final int REQUEST_AUDIO_FILES = 333;
    private static final int READ_CONTACTS = 444;




//    private static final int CAMERA = 99;
//    private static final int RECORD_AUDIO = 555;
//    private static final int CAMERA_RECORD_AUDIO = 556;
//    private static final int CAMERA_WRITE_RECORD_AUDIO = 557;
//    private static final int READ_EXTERNAL = 101;
//    private static final int MULTIPLE_REQUESTS = 111; //includes camera and write external


    private static final int IMAGE_CAPTURE_UNDER_10_REQUESTS = 701;
    private static final int IMAGE_CAPTURE_OVER_10_REQUESTS = 702;
    private static final int IMAGE_GALLERY_REQUESTS = 703;
//    private static final int IMAGE_GALLERY_OVER_10_REQUESTS = 704;

    private static final int AUDIO_CAPTURE_REQUESTS = 801;
//    private static final int AUDIO_CAPTURE_OVER_10_REQUESTS = 802;
    private static final int AUDIO_GALLERY_REQUESTS = 803;
//    private static final int AUDIO_GALLERY_OVER_10_REQUESTS = 804;

    private static final int VIDEO_CAPTURE_UNDER_10_REQUESTS = 901;
    private static final int VIDEO_CAPTURE_OVER_10_REQUESTS = 902;
    private static final int VIDEO_GALLERY_REQUESTS = 903;
//    private static final int VIDEO_GALLERY_OVER_10_REQUESTS = 904;



    File photoFile;
    String pathToPic;
    Uri picUri;
    String attachmentType;
    Uri selectedUri;

    String goingBackTo;
    String groupId;
    String groupName;

    ImageView audio_mic_image;

    View rootView;
    private MediaRecorder recorder;
    private boolean startedRecording = false;
    TextView audio_label;
    Chronometer audio_chronometer;


    public MessageAttachmentsDialog() {
        // Required empty public constructor
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        goingBackTo = getArguments().getString("goingBackTo");
        groupId = getArguments().getString("groupId");
        groupName = getArguments().getString("groupName");
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_message_attachments, container, false);

        LinearLayout cameraLinearLayout = rootView.findViewById(R.id.message_attach_item_camera);
        cameraLinearLayout.setOnClickListener(this);

        LinearLayout galleryLinearLayout = rootView.findViewById(R.id.message_attach_item_gallery);
        galleryLinearLayout.setOnClickListener(this);

        LinearLayout contactsLinearLayout = rootView.findViewById(R.id.message_attach_item_contacts);
        contactsLinearLayout.setOnClickListener(this);

        LinearLayout audioLinearLayout = rootView.findViewById(R.id.message_attach_item_audio);
        audioLinearLayout.setOnClickListener(this);

        LinearLayout recordVideoLinearLayout = rootView.findViewById(R.id.message_attach_item_video_record);
        recordVideoLinearLayout.setOnClickListener(this);
        
        LinearLayout recordAudioLinearLayout = rootView.findViewById(R.id.message_attach_item_audio_record);
        audio_label = rootView.findViewById(R.id.message_attach_text_audio_record);
        audio_chronometer = rootView.findViewById(R.id.audio_chronometer);
        audio_mic_image = rootView.findViewById(R.id.message_attach_img_audio_record);
        audio_mic_image.setOnClickListener(this);
        recordAudioLinearLayout.setOnClickListener(this);

        LinearLayout videoLinearLayout = rootView.findViewById(R.id.message_attach_item_video);
        videoLinearLayout.setOnClickListener(this);

        return rootView;
    }



    public void onClick(View v) {

        switch (v.getId()){

            case R.id.message_attach_item_camera:
            {

                //the reason for this is that for Q onwards there is no permission needed for this
                attachmentType = "camera";
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    startCapturingImageForQAndR();
                }
                else {
                    startCapturingImage();
                }
                break;

            }
            case R.id.message_attach_item_gallery:
            {
                attachmentType = "gallery";
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    choosePhotoFromGallery();
//                }
//                else {
//                    choosePhotoFromGallery();
//                }

                break;

            }
            case R.id.message_attach_item_video_record:
            {
                attachmentType = "video_record";
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    recordVideoForQAndUp();
                }
                else {
                    recordVideo();
                }
                break;
            }

            case R.id.message_attach_item_audio_record:
            {
                attachmentType = "audio_record";
                recordAudio();

                break;
            }
            
            case R.id.message_attach_item_video:
            {
                attachmentType = "video";
                chooseVideoFromGallery();
                break;

            }
            case R.id.message_attach_item_contacts:
            {
                attachmentType = "contact";
                chooseContact();
                break;

            }
            case R.id.message_attach_item_audio:
            {
                attachmentType = "audio";
                chooseAudioFile();
                break;

            }
            case R.id.message_attach_img_audio_record:
            {
                attachmentType = "audio_record";
                if(!startedRecording){
                    recordAudio();
//                    startRecording();
                }
                else{
                    stopRecording();
                }
            }
//            case R.id.message_attach_item_documents:
//            {
//                attachmentType = "document";
//                break;
//
//            }



        }

    }



    @AfterPermissionGranted(AUDIO_CAPTURE_REQUESTS)
    private void recordAudio() {

        String[] perms = {Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            //let's just keep app recording stuff right now
//            Intent intent = new Intent(
//                    MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                startActivityForResult(intent, REQUEST_AUDIO_CAPTURE);
//            } else {
//                Toast.makeText(getContext(), "No sound record application found", Toast.LENGTH_SHORT).show();
                startRecording();
//            }
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to record audio", AUDIO_CAPTURE_REQUESTS, perms);
        }

    }

    private void stopRecording() {
        startedRecording = false;
        audio_mic_image.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_microphone));
        try{
            recorder.stop();
            recorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        recorder = null;

        audio_chronometer.stop();
        audio_chronometer.setVisibility(View.GONE);
        audio_label.setVisibility(View.VISIBLE);
        //copy the file to media and generate the link
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String newName = groupId + timeStamp + ".mp3";
        final String audioUri = MediaHelper.saveMediaToGallery(getContext(), getActivity().getContentResolver(), pathToPic, newName, "audio/*", groupName);
        selectedUri = Uri.parse(audioUri);
//        getContext().getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //message has been selected - send it to FinishAttachment page, user might want to add a caption
        NavController navController = NavHostFragment.findNavController(MessageAttachmentsDialog.this);

        //if user selected something so pathToPic will have a value, else it will not have anything
        //if it has a value, go to finishAttachment
        //if user did not select anything, just dismiss this dialog and go to messages again
        if(pathToPic != null && !pathToPic.isEmpty()){
            Bundle args = new Bundle();
//            String[] attachmentPaths = new String[]{pathToPic};
//            args.putSerializable("attachmentPaths", attachmentPaths);
//            args.putString("uri", String.valueOf(selectedUri));
//            args.putString("attachmentType", attachmentType);
            String[] uris = new String[]{String.valueOf(selectedUri)};
            args.putSerializable("attachmentURIs", uris);
            args.putSerializable("attachmentPath", pathToPic);
            args.putString("attachmentType", attachmentType);
            args.putString("goingBackTo", goingBackTo);

            if(navController.getCurrentDestination().getId() == R.id.messageAttachmentsDialog){
                navController.navigate(R.id.action_messageAttachmentsDialog_to_finishAttachmentFragment, args);
            }
            dismiss();
        }
        else{
            navController.popBackStack();
            dismiss();
        }
    }

    private void startRecording() {
        try {
            startedRecording = true;
            audio_mic_image.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_stop));
            audio_label.setVisibility(View.GONE);
            audio_chronometer.setVisibility(View.VISIBLE);

            File audioFile = createAudioFile();
            pathToPic = audioFile.getAbsolutePath();
            selectedUri = FileUtils.getUri(audioFile);
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();


        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
        audio_chronometer.start();
    }

    @AfterPermissionGranted(VIDEO_CAPTURE_UNDER_10_REQUESTS)
    private void recordVideo() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                    try {
                        photoFile = createVideoFile();
                        pathToPic = photoFile.getAbsolutePath();

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
                        Log.d(TAG, "Image File Path- " + pathToPic);
                        this.picUri = photoURI;
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                photoURI);
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                File outputFile = createVideoFile();
//                pathToPic = outputFile.getAbsolutePath();
//                Uri videoUri = Uri.fromFile(outputFile);
//                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
//                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//                }
                    }
                }


        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to open the camera", VIDEO_CAPTURE_UNDER_10_REQUESTS, perms);
        }
    }

    @AfterPermissionGranted(VIDEO_CAPTURE_OVER_10_REQUESTS)
    private void recordVideoForQAndUp() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {

            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                try {
                    photoFile = createVideoFile();
                    pathToPic = photoFile.getAbsolutePath();

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
                    Log.d(TAG, "Image File Path- " + pathToPic);
                    this.picUri = photoURI;
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                File outputFile = createVideoFile();
//                pathToPic = outputFile.getAbsolutePath();
//                Uri videoUri = Uri.fromFile(outputFile);
//                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
//                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//                }
                }
            }


        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to open the camera", VIDEO_CAPTURE_OVER_10_REQUESTS, perms);
        }
    }



    @AfterPermissionGranted(READ_CONTACTS)
    public void chooseContact() {
        String[] perms = {Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//            intent.setType("text/x-vcard");
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your phonebook", READ_CONTACTS, perms);
        }


    }

    @AfterPermissionGranted(AUDIO_GALLERY_REQUESTS)
    private void chooseAudioFile() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {

            Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);

            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("audio/*");
//            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            //Create an Intent with action as ACTION_PICK
//            Intent intent=new Intent(Intent.ACTION_PICK);
//            // Sets the type as image/*. This ensures only components of type image are selected
//            intent.setType("audio/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Launching the Intent
            startActivityForResult(intent,REQUEST_AUDIO_FILES);
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", AUDIO_GALLERY_REQUESTS, perms);
        }
    }

    @AfterPermissionGranted(IMAGE_CAPTURE_OVER_10_REQUESTS)
    private void startCapturingImageForQAndR() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            CameraActivity activity = (CameraActivity) getActivity();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                try {
                    photoFile = createImageFile("cameraImage");
                    pathToPic = photoFile.getAbsolutePath();

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
                    Log.d(TAG, "Image File Path- " + pathToPic);
                    this.picUri = photoURI;
                    activity.setCapturedImageURI(photoURI);
                    activity.setCurrentPhotoPath(photoURI.getPath());
                    activity.setCapturedImageFilePath(pathToPic);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            activity.getCapturedImageURI());
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to open the camera", IMAGE_CAPTURE_OVER_10_REQUESTS, perms);
        }


    }


    @AfterPermissionGranted(IMAGE_CAPTURE_UNDER_10_REQUESTS)
    private void startCapturingImage() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            CameraActivity activity = (CameraActivity) getActivity();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){

                try {
                    photoFile = createImageFile("cameraImage");
                    pathToPic = photoFile.getAbsolutePath();

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
                    Log.d(TAG, "Image URI- "+photoURI);
                    Log.d(TAG, "Photo Path - "+photoURI.getPath());
                    Log.d(TAG, "Image File Path- "+ pathToPic);
                    activity.setCapturedImageURI(photoURI);
                    activity.setCurrentPhotoPath(photoURI.getPath());
                    activity.setCapturedImageFilePath(pathToPic);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            activity.getCapturedImageURI());
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
        }

        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to open the camera", IMAGE_CAPTURE_UNDER_10_REQUESTS, perms);
        }
    }

    private File createAudioFile() throws IOException {
        return createAttachmentFile("audio");
    }

    private File createVideoFile() throws IOException {
        return createAttachmentFile("video");
    }

    private File createImageFile(String imageIdentifier) throws IOException {
        return createAttachmentFile("image");
    }

    private File createAttachmentFile(String type) throws IOException {
        String typeOfFile = "JPEG_";
        String mimeType = "image/*";
        String suffix = ".jpg";

        if("audio".equals(type)){
            typeOfFile = "AUDIO_";
            mimeType = "audio/*";
            suffix = ".mp3";
        }
        else if("video".equals(type)){
            typeOfFile = "VIDEO_";
            mimeType = "video/*";
            suffix = ".mp4";
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = typeOfFile + timeStamp + "_";
            File image = File.createTempFile(imageFileName, suffix, getContext().getCacheDir());
            return image;
        }
        else{
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = typeOfFile + "_"+timeStamp + "_";
//            File storageDir = FileUtils.getAppDirForMime("image/*", true);
            File storageDir = FileUtils.getAppDirForMime(mimeType, groupName, "Group",true);

            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    suffix,         /* suffix */
                    storageDir      /* directory */
            );

            return image;
        }
    }

    /**
     * Though Gallery only needs read external storage permission, but
     * this time we need both read and write because this file is written
     * in the KabooMe folder
     */
//    @AfterPermissionGranted(MULTIPLE_REQUESTS)
//    private void choosePhotoFromGallery() {
//
//        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if (EasyPermissions.hasPermissions(getContext(), perms)) {
//            //Create an Intent with action as ACTION_PICK
//            Intent intent=new Intent(Intent.ACTION_PICK);
//            // Sets the type as image/*. This ensures only components of type image are selected
//            intent.setType("image/*");
//            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
//            String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
//            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
//            // Launching the Intent
//            startActivityForResult(intent,REQUEST_IMAGE_GALLERY);
//        }
//        else{
//            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", MULTIPLE_REQUESTS, perms);
//        }
//
//    }


    @AfterPermissionGranted(IMAGE_GALLERY_REQUESTS)
    private void choosePhotoFromGallery() {

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {

//            List<Uri> uris = MediaHelper.getImagesFromGallery(getActivity().getContentResolver());
//            Log.d(TAG, "choosePhotoFromGalleryForQAndUp: number of images - "+uris.size());

            //Create an Intent with action as ACTION_PICK
//            Intent intent=new Intent(Intent.ACTION_PICK);
            Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);

            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
//            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            // Launching the Intent
            startActivityForResult(intent,REQUEST_IMAGE_GALLERY);


        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", IMAGE_GALLERY_REQUESTS, perms);
        }

    }

    @AfterPermissionGranted(VIDEO_GALLERY_REQUESTS)
    private void chooseVideoFromGallery() {

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            //Create an Intent with action as ACTION_PICK
//            Intent intent=new Intent(Intent.ACTION_PICK);
            Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("video/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
//            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            // Launching the Intent
            startActivityForResult(intent,REQUEST_VIDEO_GALLERY);
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", VIDEO_GALLERY_REQUESTS, perms);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called");
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Log.d(TAG, "onActivityResult: came back from settings page");
            return;
        }
        if(resultCode == RESULT_CANCELED){
//            Toast.makeText(getContext(), "Action is cancelled. Please try again by clicking on the attachment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
//            selectedUri = data.getData();
//            getContext().getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String newName = groupId + timeStamp + ".mp4";
//
            final String galleryImageUri = MediaHelper.saveMediaToGallery(getContext(), getActivity().getContentResolver(), pathToPic, newName, "video/*", groupName);
            selectedUri = Uri.parse(galleryImageUri);

//            getContext().getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            //now copy this to the internal cache directory
//            try {
//                pathToPic = FileUtils.copyAttachmentToApp(selectedUri, ".mp4", getContext());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        if (requestCode == REQUEST_AUDIO_CAPTURE && resultCode == RESULT_OK) {
            startedRecording = false;
            selectedUri = data.getData();
            //following line not sure - needs to be tested otherwise could fail
            //this comes here when the audio capture is done by some other app
            getContext().getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String newName = groupId + timeStamp + ".mp4";
//
//            final String galleryImageUri = MediaHelper.saveMediaToGallery(getActivity().getContentResolver(), pathToPic, newName, "video/*", groupName);
//            selectedUri = Uri.parse(galleryImageUri);


            //now copy this to the internal cache directory
            try {
                pathToPic = FileUtils.copyAttachmentToApp(selectedUri, ".mp3", getContext());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            selectedUri = Uri.parse(pathToPic);
            selectedUri = picUri;
//            //following lines only for version Q and above
//            //because for the rest of the versions, the file is copied to a
//            //different name
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String newName = groupId + timeStamp + ".jpg";
                final String galleryImageUri = MediaHelper.saveMediaToGallery(getContext(), getActivity().getContentResolver(), pathToPic, newName, "image/*", groupName);
                if(galleryImageUri != null) {
                    selectedUri = Uri.parse(galleryImageUri);
                }
//            }



            //let's try to insert the image in the gallery
            //doing that after getting the messageID
//            saveImageToGallery();

        }
        else if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {

            //data.getData return the content URI for the selected Image
            selectedUri = data.getData();
            getContext().getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            //now copy this to the internal cache directory
            try {

                pathToPic = FileUtils.copyAttachmentToApp(selectedUri, ".jpg", getContext());

//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                String imageFileName = "JPEG_" + timeStamp + "_";
//                File image = File.createTempFile(imageFileName, ".jpg", getContext().getCacheDir());
//
//                ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(selectedUri, "r");
//                InputStream fileStream = new FileInputStream(pfd.getFileDescriptor());
//                OutputStream newCacheFile = new FileOutputStream(image);
//
//                byte[] buffer = new byte[1024];
//                int length;
//
//                while((length = fileStream.read(buffer)) > 0)
//                {
//                    newCacheFile.write(buffer, 0, length);
//                }
//
//                newCacheFile.flush();
//                fileStream.close();
//                newCacheFile.close();
//
//               pathToPic = image.getAbsolutePath();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


//            pathToPic = selectedImage.getPath();
//            pathToPic = FileUtils.getPath(getContext(), selectedUri);
//            pathToPic = FileUtils.getPathNew(getContext(), selectedUri);


            //now copy it to local folder
//            Bitmap thumbnail = FileUtils.getThumbnail(getContext(), selectedImage);
//            Log.d(TAG, "Bit count "+thumbnail.getByteCount());
//            FileHelper.copyAttachmentToApp(selectedImage, getContext() );


        }
        else if(requestCode == REQUEST_VIDEO_GALLERY && resultCode == RESULT_OK){

            selectedUri = data.getData();
            getContext().getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                pathToPic = FileUtils.copyAttachmentToApp(selectedUri, ".mp4", getContext());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            pathToPic = FileUtils.getPath(getContext(), selectedUri);


        }
        else if(requestCode == REQUEST_AUDIO_FILES && resultCode == RESULT_OK){

            selectedUri = data.getData();
            getContext().getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            pathToPic = FileUtils.getPath(getContext(), selectedUri);
            try {
                pathToPic = FileUtils.copyAttachmentToApp(selectedUri, ".mp3", getContext());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else if(requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK){

            selectedUri = data.getData();
            String lookupKey =null;

            List<String> items = selectedUri.getPathSegments();
            Cursor cursor = getContext().getContentResolver().query(selectedUri, new String[] {
                    ContactsContract.Contacts.LOOKUP_KEY
            }, null, null, null);

            if (cursor.moveToFirst()) {
                lookupKey = cursor.getString(0);
            }
            cursor.close();

            if(lookupKey == null){
                lookupKey = items.get(2);
            }

//            commenting this since I think that getting the lookup key from the cursor is a better move
//            also, the difference is that only items.get(2) is needed, so it should look like -
//            Uri uri1 = Uri.withAppendedPath(
//                    ContactsContract.Contacts.CONTENT_VCARD_URI,items.get(2));
//
//            Uri uri1 = Uri.withAppendedPath(
//                    ContactsContract.Contacts.CONTENT_VCARD_URI,items.get(2)+File.separator+items.get(3) );

            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI,lookupKey);


            AssetFileDescriptor fd;
            try {
                byte[] b;
                fd = getContext().getContentResolver()
                        .openAssetFileDescriptor(uri, "r");
                FileInputStream fis = fd.createInputStream();
                if(fd.getDeclaredLength() != -1) {
                    b = new byte[(int) fd.getDeclaredLength()];
                    fis.read(b);
                }
                else{
                    b = readBytes(fis);
                }

                String str = new String(b);
                fis.close();

                VCard vcard = Ezvcard.parse(str).first();
                String fullName = vcard.getFormattedName().getValue();
//                String lastName = vcard.getStructuredName().getFamily();
                List<Telephone> telephones = vcard.getTelephoneNumbers();
                String phoneNumber="";
                for(Telephone telephone:telephones){
                    if(telephone.getTypes() != null && telephone.getTypes().contains(TelephoneType.PREF)){
                        phoneNumber = telephone.getText();
                    }
                }
                List<Email> emails = vcard.getEmails();
                String emailAddress = "";
                for(Email email:emails){
                    if(email.getTypes() != null && email.getTypes().contains(EmailType.PREF)){
                        emailAddress = email.getValue();
                    }
                }

                ContactModel contactModel = new ContactModel();
                contactModel.setName(fullName);
                contactModel.setEmail(emailAddress);
                contactModel.setPhone(phoneNumber);

                NavController navController = NavHostFragment.findNavController(MessageAttachmentsDialog.this);
                if(goingBackTo.equals("GroupMessages")) {
                    navController.getBackStackEntry(R.id.groupMessagesFragment).getSavedStateHandle().set("contact", contactModel);
                }
                else if(goingBackTo.equals("UserAdminMessages")){
                    navController.getBackStackEntry(R.id.groupUserAdminMessagesFragment).getSavedStateHandle().set("contact", contactModel);
                }
                else if(goingBackTo.equals("AdminUserMessages")){
                    navController.getBackStackEntry(R.id.groupAdminUserMessagesFragment).getSavedStateHandle().set("contact", contactModel);
                }
                //don't need to popback, just need to dismiss this, it will remove the dialog
                //we are already in the GMF, so popBack takes us to root page
                dismiss();

//                List<Photo> photos = vcard.getPhotos();
//                byte[] image = null;
//                if(photos.get(0) != null){
//                    image = photos.get(0).getData();
//                }
//
//                Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);
//                Glide.with(FinishAttachmentFragment.this)
//                        .asBitmap()
//                        .load(bitmap)
//                        .into(contactImage);
////                selectedImage.setImageBitmap(bitmap);

//                messageInput.getInputEditText().setText(fullName+" \n "+phoneNumber+" \n "+emailAddress);

//                Log.i(TAG, str);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Sorry, this contact does not allow to be shared", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Sorry, this contact does not allow to be shared", Toast.LENGTH_SHORT).show();
            }

//            pathToPic = FileUtils.getPath(getContext(), selectedUri);

            //Data.CONTACT_ID + "=?",
            //            new String[] {String.valueOf(contactId)}


//            Uri contactDetailsUri = Uri.withAppendedPath(selectedUri, ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
//
//            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
//            Cursor cursor = AppConfigHelper.getContext().getContentResolver().query(contactDetailsUri, projection,
//                    null, null, null);
//            // If the cursor returned is valid, get the phone number
//            if (cursor != null && cursor.moveToFirst()) {
//
//                for(int i=0; i<100; i++){
//                    Log.d(TAG, cursor.getString(i));
//                }
//                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                int emailColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
//                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME);
//                int photoUriColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
//
//                String number = cursor.getString(numberIndex);
//                String email = cursor.getString(emailColumnIndex);
//                String name = cursor.getString(nameColumnIndex);
//                String photoUri = cursor.getString(photoUriColumnIndex);
//
//            }
//
//            if (cursor != null) {
//                cursor.close();
//            }
//

        }

        //message has been selected - send it to FinishAttachment page, user might want to add a caption
        NavController navController = NavHostFragment.findNavController(MessageAttachmentsDialog.this);

        //if user selected something so pathToPic will have a value, else it will not have anything
        //if it has a value, go to finishAttachment
        //if user did not select anything, just dismiss this dialog and go to messages again
        if(pathToPic != null && !pathToPic.isEmpty()){
            Bundle args = new Bundle();
//            String[] attachmentPaths = new String[]{pathToPic};
//            args.putSerializable("attachmentPaths", attachmentPaths);
//            args.putString("uri", String.valueOf(selectedUri));
//            args.putString("attachmentType", attachmentType);
            String[] uris = new String[]{String.valueOf(selectedUri)};
            args.putSerializable("attachmentURIs", uris);
            args.putSerializable("attachmentPath", pathToPic);
            args.putString("attachmentType", attachmentType);
            args.putString("goingBackTo", goingBackTo);

            if(navController.getCurrentDestination().getId() == R.id.messageAttachmentsDialog){
                navController.navigate(R.id.action_messageAttachmentsDialog_to_finishAttachmentFragment, args);
            }
            dismiss();
        }
        else{
            navController.popBackStack();
            dismiss();
        }





    }

//    private void saveImageToGallery() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            ContentResolver contentResolver = getActivity().getContentResolver();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_1.jpg");
//            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
//            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+File.separator+"KabooMe");
//            contentValues.put(MediaStore.Images.Media.IS_PENDING, true);
//
//
//
//            Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//
//            try {
//                FileInputStream fileInputStream = new FileInputStream(new File(pathToPic));
//                OutputStream outputStream = contentResolver.openOutputStream(imageUri);
//
//                byte[] buf = new byte[8192];
//                int length;
//                while ((length = fileInputStream.read(buf)) > 0) {
//                    outputStream.write(buf, 0, length);
//                }
//                contentValues.clear();
//                contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
//                contentResolver.update(imageUri, contentValues, null, null);
//
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//
//
//
//
//
//
//
//        }
//    }




    private String getPath(Context context, Uri uri ) {
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

//    @Override
//    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//
//        //TODO: READ_EXTERNAL is needed for lot of other types of attachmnents.
//        //TODO: We need to differentiate between which type user clicked then
////        if(requestCode == CAPTURE_UNDER_10_REQUESTS){
////            startCapturingImage();
////        }
//        if(requestCode == READ_EXTERNAL){
////            if("camera".equals(attachmentType)){
////                startCapturingImage();
////            }
//             if("gallery".equals(attachmentType)){
//                choosePhotoFromGallery();
//            }
//            else if("video".equals(attachmentType)){
//                chooseVideoFromGallery();
//            }else if("audio".equals(attachmentType)){
//                chooseAudioFile();
//            }
//
//        }
////        if(requestCode == MULTIPLE_REQUESTS){
////            startCapturingImage();
////        }
//        if(requestCode == READ_CONTACTS){
//            chooseContact();
//        }
//    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //no need to implement this since we are handling every method through @AfterPermissionGranted
        //above the method declaration
        //if we add it here, then we should remove the @AfterPermissionGranted
        //otherwise the method gets called twice
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
}
