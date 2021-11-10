package com.java.kaboome.presentation.views.features.groupSearch;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.java.kaboome.R;
import com.java.kaboome.presentation.views.features.BaseFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanQRCodeGalleryFragment extends BaseFragment {

    private static final String TAG = "KMScanQRCodeGalleryFrag";

    private View rootView;
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private ConstraintLayout qrCodeNotReadLayout;
    private TextView tryAgainLink;
    private TextView goBackLink;


    public ScanQRCodeGalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_scan_qrcode_gallery, container, false);
        qrCodeNotReadLayout = rootView.findViewById(R.id.gallery_qr_not_found_layout);
        tryAgainLink = rootView.findViewById(R.id.qr_gallery_try_again);
        tryAgainLink.setOnClickListener(tryAgainClickListener);
        goBackLink = rootView.findViewById(R.id.qr_gallery_go_back);
        goBackLink.setOnClickListener(goBackClickListener);
//        requestRequiredGalleryPermissions();
        return rootView;
    }

    private void requestRequiredGalleryPermissions() {
        if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                    .setTitle("Permissions requested")
                    .setMessage("Write image permission is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //request permission now
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
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_IMAGE_GALLERY){
            if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                choosePhotoFromGallery();
            }
            else{
                Toast.makeText(getContext(), "Permission DENIED, Cannot get image", Toast.LENGTH_SHORT);
            }
        }
    }

    public void choosePhotoFromGallery() {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        //data.getData return the content URI for the selected Image
        if(resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

            InputStream imageStream = null;
            try {
                //getting the image
                imageStream = getContext().getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            //decoding bitmap
            Bitmap bMap = BitmapFactory.decodeStream(imageStream);
            scanQRImage(bMap);
        }
        else{
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack(R.id.searchGroupFragment, false);
        }

    }


    private void scanQRImage(Bitmap bMap) {

//        Bitmap bMap = Bitmap.createScaledBitmap(originalBitmap, 80,80,false);

        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Collection<BarcodeFormat> barcodeFormats = new Vector<>();
        barcodeFormats.add(BarcodeFormat.QR_CODE);

        Map<DecodeHintType, Object> hintsMap = new HashMap<>();
        hintsMap.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap, hintsMap);
            contents = result.getText();
            Log.d(TAG, "scanQRImage: result - "+contents);
            if(contents != null){
//            Toast.makeText(ScanQRCodeActivity.this, "Group selected "+result.getText(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Group selected - "+contents);
                NavController navController = NavHostFragment.findNavController(this);
                navController.getBackStackEntry(R.id.searchGroupFragment).getSavedStateHandle().set("groupId", result.getText());
//                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupId", result.getText());
                navController.popBackStack(R.id.searchGroupFragment, false);
            }
        }
        catch (Exception e) {
            Log.e("QrTest", "Error decoding barcode", e);
//            Toast.makeText(getContext(), "Sorry, could not read the QR Code", Toast.LENGTH_SHORT);
            qrCodeNotReadLayout.setVisibility(View.VISIBLE);
        }

    }

    View.OnClickListener tryAgainClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            choosePhotoFromGallery();
        }
    };

    View.OnClickListener goBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavController navController = NavHostFragment.findNavController(ScanQRCodeGalleryFragment.this);
            navController.popBackStack(R.id.searchGroupFragment, false);
        }
    };


    @Override
    public void onLoginSuccess() {
//        requestRequiredGalleryPermissions();
    }

    @Override
    public void whileLoginInProgress() {
        requestRequiredGalleryPermissions();
    }

    @Override
    public void onNetworkOff() { }

    @Override
    public void onNetworkOn() { }
}
