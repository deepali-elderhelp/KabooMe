/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.images;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.java.kaboome.constants.AWSConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.CredentialsHandler;
import com.java.kaboome.helpers.NetworkHelper;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class S3LoadingHelper {

    private static final String TAG = "KMS3LoadingHelper";

    private static final String s3BucketName = AWSConstants.S3_BUCKET_NAME.toString();

    private static AmazonS3Client s3Client = null;

    public static String getBaseUrlOfImage(String imageName){
        return AWSConstants.S3_BASE_URL+imageName;
    }

    public static void getCachedImageLink(final String objectKey, final ImageLinkHandler imageLinkHandler){
        try {
            URL urlOfImage = new URL(getBaseUrlOfImage(objectKey));
            imageLinkHandler.onImageLinkReady(urlOfImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            imageLinkHandler.onImageLinkError(e);
        }
    }

    public static URL getCachedImageLink(final String objectKey){
        try {
            URL urlOfImage = new URL(getBaseUrlOfImage(objectKey));
            return urlOfImage;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getPresignedImageLink(final String objectKey, final ImageLinkHandler imageLinkHandler){

        //if no network, then just return a url version of getBaseUrlOfImage()
        //this could also be the default way of loading the images
        //and the other part, where the image is loaded from a dynamically generated pre-signed url can be done
        //only when a fresh image is needed. The decision whether a fresh image is needed or not
        //could be based on state of the app

        if(!NetworkHelper.isOnline()){
            try {
                URL urlOfImage = new URL(getBaseUrlOfImage(objectKey));
                imageLinkHandler.onImageLinkReady(urlOfImage);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                imageLinkHandler.onImageLinkError(e);
            }
        }
        else{
            /**
            There is this error that shows up when doing the following -
             1. 1 hour later when the token is invalid, turn airplane mode on
             2. Open KabooMe app, just browse around and exit
             3. Turn airplane mode off
             4. Open app, there is an error originating here
             5. In that case, S3Client is not null, so no credentials are being generated
             6. And app tries to getPresignedURL as part of which credentials are tried to be generated
             7. But since all this is happening on main thread, error - NetworkOnMainThread is thrown up
             8. So, to avoid that, I am checking for credentials all the time, not only when S3CLient is null
             9. Other approach (to be explored) could be that when user get a new session login, immediately after
                Auth success, credentials are created/fetched every time.

             **/
//            if (s3Client == null) {
                try {
                    CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                        @Override
                        public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
                            Log.d(TAG, "Successful retrieval of CredentialsProvider");

                            s3Client = new AmazonS3Client(credentialsProviderReturned);
                            s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
                            URL urlOfImage = getPresisgnedURLOfImage(objectKey);
                            imageLinkHandler.onImageLinkReady(urlOfImage);

                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);
                            imageLinkHandler.onImageLinkError(exception);

                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "No callback passed to getCredentials()");
                    e.printStackTrace();
                    imageLinkHandler.onImageLinkError(e);
                }

//            }
//            else{ //s3 is not null, already initialized and ready to use
//                /**
//                 * It fails somewhere around here after long time no use (session alive but not active?)
//                 * Make sure that though S3 is not null, but is it really active??
//                 */
//                URL urlOfImage = getPresisgnedURLOfImage(objectKey);
//                imageLinkHandler.onImageLinkReady(urlOfImage);
//            }
        }



    }

    public static void uploadFile(final String key, final File fileToUpload, final Context context, final int profileOrTN) {
        if (s3Client == null) {
            try {
                CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                    @Override
                    public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
                        Log.d(TAG, "Successful retrieval of CredentialsProvider");

                        s3Client = new AmazonS3Client(credentialsProviderReturned);
                        s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

                        startUploadFile(key, fileToUpload, context, profileOrTN);

                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);

                    }
                });
            } catch (Exception e) {
                Log.d(TAG, "No callback passed to getCredentials()");
                e.printStackTrace();
            }

        }
        else{ //s3 is not null, already initialized and ready to use
            startUploadFile(key, fileToUpload, context,profileOrTN );

        }


    }

    private static void startUploadFile(final String key, final File fileToUpload, final Context context, final int profileOrTN){

        if(fileToUpload == null){
            //return immediately
            Log.d("Glide", "File is null, returning immediately");
            Intent intent = new Intent();
            intent.setAction(PictureEnums.GROUP_IMAGE_UPLOADED);
            intent.putExtra("pictureType", profileOrTN);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

//        TransferUtility transferUtility = new TransferUtility(s3Client, AppConfigHelper.getContext());
        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(AppConfigHelper.getContext()).build();
//        TransferObserver uploadObserver = transferUtility.upload(s3BucketName, key, fileToUpload);
        TransferObserver uploadObserver = transferUtility.upload(AWSConstants.S3_BUCKET_NAME.toString(), key, fileToUpload);

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {

                    if(PictureEnums.GROUP_PROFILE_PIC == profileOrTN || PictureEnums.PROFILE_PIC == profileOrTN){
                        //only send_background out broadcast when the profile pic is done, so only one broadcast is sent

                        Log.d("Glide", "state completed for "+profileOrTN);
                        //send_background broadcast
                        Intent intent = new Intent();
                        intent.setAction(PictureEnums.GROUP_IMAGE_UPLOADED);
                        intent.putExtra("pictureType", profileOrTN);
                        intent.putExtra("status", "completed");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }

                    //fileToUpload.delete();
                } else if (TransferState.FAILED == state) {
                    //file.delete();
                    Log.d("Glide", "state failed for "+profileOrTN);
//                    Toast.makeText(AppConfigHelper.getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();

                    //send_background broadcast
                    Intent intent = new Intent();
                    intent.setAction(PictureEnums.GROUP_IMAGE_UPLOADED); //change to error
                    intent.putExtra("pictureType", profileOrTN);
                    intent.putExtra("status", "failed");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
            }

        });
    }

    private static URL getPresisgnedURLOfImage(String objectKey){

            s3Client.setEndpoint(AWSConstants.S3_CLIENT_ENDPOINT.toString());

            Date expires = new Date (new Date().getTime() + 1000 * 160); // 3 minute to expire
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(s3BucketName, objectKey);  //generating the signatured url

            generatePresignedUrlRequest.setExpiration(expires);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

            return url;



    }
}
