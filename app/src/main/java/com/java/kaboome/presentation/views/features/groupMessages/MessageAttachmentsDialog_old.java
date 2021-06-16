package com.java.kaboome.presentation.views.features.groupMessages;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
import com.java.kaboome.presentation.views.features.CameraActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageAttachmentsDialog_old extends BottomSheetDialogFragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "KMGroupActionsDialog";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_SELECT_CONTACT = 3;



    private static final int READ_EXTERNAL = 101;
    private static final int MULTIPLE_REQUESTS = 111; //includes camera and write external
    private static final int REQUEST_VIDEO_GALLERY = 222;
    private static final int REQUEST_AUDIO_FILES = 333;
    private static final int READ_CONTACTS = 444;

    File photoFile;
    String pathToPic;
    String attachmentType;
    Uri selectedUri;

    String goingBackTo;

    View rootView;



    public MessageAttachmentsDialog_old() {
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

//        LinearLayout documentsLinearLayout = rootView.findViewById(R.id.message_attach_item_documents);
//        documentsLinearLayout.setOnClickListener(this);

        LinearLayout videoLinearLayout = rootView.findViewById(R.id.message_attach_item_video);
        videoLinearLayout.setOnClickListener(this);

        return rootView;
    }



    public void onClick(View v) {

        switch (v.getId()){

            case R.id.message_attach_item_camera:
            {

                attachmentType = "camera";
                startCapturingImage();
                break;

            }
            case R.id.message_attach_item_gallery:
            {
                attachmentType = "gallery";
                choosePhotoFromGallery();
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
//            case R.id.message_attach_item_documents:
//            {
//                attachmentType = "document";
//                break;
//
//            }



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

    @AfterPermissionGranted(READ_EXTERNAL)
    private void chooseAudioFile() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            //Create an Intent with action as ACTION_PICK
            Intent intent=new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Launching the Intent
            startActivityForResult(intent,REQUEST_AUDIO_FILES);
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", READ_EXTERNAL, perms);
        }
    }


    @AfterPermissionGranted(MULTIPLE_REQUESTS)
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
            EasyPermissions.requestPermissions(this, "This permission is needed to open the camera", MULTIPLE_REQUESTS, perms);
        }
    }

    private File createImageFile(String imageIdentifier) throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + imageIdentifier+"_"+timeStamp + "_";
//        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //in the external directory, there should be a kaboome directory created
        //if it does not already exist
//        String folder_main = "KabooMe";


//        File storageDir = new File(Environment.getExternalStorageDirectory(), folder_main);
//        if (!storageDir.exists()) {
//            storageDir.mkdirs();
//        }

        File storageDir = FileUtils.getAppDirForMime("image/*", true);

//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    /**
     * Though Gallery only needs read external storage permission, but
     * this time we need both read and write because this file is written
     * in the KabooMe folder
     */
    @AfterPermissionGranted(MULTIPLE_REQUESTS)
    private void choosePhotoFromGallery() {

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            //Create an Intent with action as ACTION_PICK
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
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", MULTIPLE_REQUESTS, perms);
        }

    }

    @AfterPermissionGranted(MULTIPLE_REQUESTS)
    private void chooseVideoFromGallery() {

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            //Create an Intent with action as ACTION_PICK
            Intent intent=new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("video/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            // Launching the Intent
            startActivityForResult(intent,REQUEST_VIDEO_GALLERY);
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", MULTIPLE_REQUESTS, perms);
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            selectedUri = Uri.parse(pathToPic);

        }
        else if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {

            //data.getData return the content URI for the selected Image
            selectedUri = data.getData();
//            pathToPic = selectedImage.getPath();
            pathToPic = FileUtils.getPath(getContext(), selectedUri);

            //now copy it to local folder
//            Bitmap thumbnail = FileUtils.getThumbnail(getContext(), selectedImage);
//            Log.d(TAG, "Bit count "+thumbnail.getByteCount());
//            FileHelper.copyAttachmentToApp(selectedImage, getContext() );


        }
        else if(requestCode == REQUEST_VIDEO_GALLERY && resultCode == RESULT_OK){

            selectedUri = data.getData();
            pathToPic = FileUtils.getPath(getContext(), selectedUri);


        }
        else if(requestCode == REQUEST_AUDIO_FILES && resultCode == RESULT_OK){

            selectedUri = data.getData();
            pathToPic = FileUtils.getPath(getContext(), selectedUri);


        }
        else if(requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK){

            selectedUri = data.getData();
            List<String> items = selectedUri.getPathSegments();

            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI,items.get(2) );

            AssetFileDescriptor fd;
            try {
                fd = getContext().getContentResolver()
                        .openAssetFileDescriptor(uri, "r");
                FileInputStream fis = fd.createInputStream();
                byte[] b = new byte[(int) fd.getDeclaredLength()];
                fis.read(b);
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

                NavController navController = NavHostFragment.findNavController(MessageAttachmentsDialog_old.this);
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
            } catch (IOException e) {
                e.printStackTrace();
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
        NavController navController = NavHostFragment.findNavController(MessageAttachmentsDialog_old.this);

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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        //TODO: READ_EXTERNAL is needed for lot of other types of attachmnents.
        //TODO: We need to differentiate between which type user clicked then
        if(requestCode == READ_EXTERNAL){
            if("camera".equals(attachmentType)){
                startCapturingImage();
            }
            else if("gallery".equals(attachmentType)){
                choosePhotoFromGallery();
            }
            else if("video".equals(attachmentType)){
                chooseVideoFromGallery();
            }else if("audio".equals(attachmentType)){
                chooseAudioFile();
            }

        }
        if(requestCode == MULTIPLE_REQUESTS){
            startCapturingImage();
        }
        if(requestCode == READ_CONTACTS){
            chooseContact();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
