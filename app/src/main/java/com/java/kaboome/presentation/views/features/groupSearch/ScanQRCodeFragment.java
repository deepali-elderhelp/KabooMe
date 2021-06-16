package com.java.kaboome.presentation.views.features.groupSearch;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.java.kaboome.R;
import com.java.kaboome.presentation.views.features.BaseFragment;

import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanQRCodeFragment extends BaseFragment implements ZXingScannerView.ResultHandler{

    private static final String TAG = "KMScanQRCodeFragment";

    private View rootView;
    private ZXingScannerView qrCodeScanner;
    private static final int REQUEST_CAMERA = 1;
    private TextView scanFromGallery;


    public ScanQRCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_scan_qrcode, container, false);

        qrCodeScanner = rootView.findViewById(R.id.qrCodeScanner);
        scanFromGallery = rootView.findViewById(R.id.scan_qr_from_gallery);
        scanFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.fragment).navigate(R.id.action_scanQRCodeFragment_to_scanQRCodeGalleryFragment);
            }
        });

        setScannerProperties();
        return rootView;
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

//        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//            scanQRCode();
//        }
//        else{
//            requestCameraPermission();
//        }

    }

    private void setScannerProperties() {

        qrCodeScanner.setFormats(Arrays.asList(BarcodeFormat.QR_CODE));
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.colorAccent);
        qrCodeScanner.setMaskColor(R.color.colorAccent);

    }

    private void scanQRCode() {

        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);

    }


    private void requestCameraPermission() {
//        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)){
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                    .setTitle("Permission requested")
                    .setMessage("Camera permission is needed for scanning qr code")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //request permission now
//                            ActivityCompat.requestPermissions(ScanQRCodeActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

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
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CAMERA){
            if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                scanQRCode();
            }
            else{
                Toast.makeText(getContext(), "Permission DENIED, Cannot scan QR code", Toast.LENGTH_SHORT);

            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        qrCodeScanner.stopCamera();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        qrCodeScanner.stopCamera();

    }


    @Override
    public void handleResult(Result result) {
        Log.d(TAG, "The qr code scanned - content is - "+result);
        if(result.getText() != null){
            Log.d(TAG, "Group selected - "+result.getText());
            NavController navController = NavHostFragment.findNavController(this);
            navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupId", result.getText());
            navController.popBackStack();
        }
    }

    @Override
    public void onLoginSuccess() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            scanQRCode();
        }
        else{
            requestCameraPermission();
        }
    }

    @Override
    public void onNetworkOff() { }

    @Override
    public void onNetworkOn() { }
}
