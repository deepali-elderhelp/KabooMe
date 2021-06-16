package com.java.kaboome.presentation.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.images.glide.GlideUrlWithQueryParameter;
import com.java.kaboome.presentation.images.glide.IntegerVersionSignature;

import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ImageHelper_last_working {

    private static final String TAG = "KMImageHelper";
    private static ImageHelper_last_working instance;

    private ImageHelper_last_working() {


    }

    public static ImageHelper_last_working getInstance(){
        if(instance == null){
            instance = new ImageHelper_last_working();
        }
        return instance;
    }

    public RequestManager getRequestManager(Context context){

        RequestOptions options = new RequestOptions();

        return Glide.with(context)
                .setDefaultRequestOptions(options);

    }

    public RequestManager getRequestManager(Context context, int placeholderImage, int errorImage){

        RequestOptions options = new RequestOptions();

        if (placeholderImage != -1) {
            options.placeholder(placeholderImage);
        }
        options.error(errorImage);

        return Glide.with(context)
                .setDefaultRequestOptions(options);

    }

    public RequestManager getRequestManager(Context context, Drawable errorImage){

        RequestOptions options = new RequestOptions();


        options.error(errorImage);

        return Glide.with(context)
                .setDefaultRequestOptions(options);

    }


    public RequestManager getRequestManager(Context context, Drawable placeholderImage, Drawable errorImage){

        RequestOptions options = new RequestOptions();

        if (placeholderImage != null) {
            options.placeholder(placeholderImage);
        }
        options.error(errorImage);

        return Glide.with(context)
                .setDefaultRequestOptions(options);

    }

    private void loadGroupTNImage(String groupId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){

        final String thumbnailImageName = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.THUMBNAIL);
        String thumbnailSourceUrl = S3LoadingHelper.getBaseUrlOfImage(thumbnailImageName);
        final int thumbnailSignatureKey = AppConfigHelper.getImageSignature(thumbnailImageName, timeStampFromLocalDB);
        final URL thumbnailCachedUrl = S3LoadingHelper.getCachedImageLink(thumbnailImageName);

        requestManager.applyDefaultRequestOptions(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholder)
                .signature(new IntegerVersionSignature(thumbnailSignatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e(TAG, "Error loading groupImage from cache, going to server");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(thumbnailImageName, thumbnailSignatureKey, requestManager, placeholder, errorBitmap, imageView, progressBar);
                            }
                        });

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Loaded groupImage successfully from cache" );
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(thumbnailSourceUrl, thumbnailCachedUrl))
                .into(imageView);


    }


    public void loadGroupImage(String groupId, ImageTypeConstants imageTypeConstants, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar) {

        if(ImageTypeConstants.THUMBNAIL == imageTypeConstants){
            loadGroupTNImage(groupId, timeStampFromLocalDB, requestManager, placeholder, errorBitmap, handler, imageView, progressBar);
        }
        else{
            loadGroupMNImage(groupId, timeStampFromLocalDB, requestManager, placeholder, errorBitmap, handler, imageView, progressBar);
        }

//        final String thumbnailImageName = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.THUMBNAIL);
//        String thumbnailSourceUrl = S3LoadingHelper.getBaseUrlOfImage(thumbnailImageName);
//        final int thumbnailSignatureKey = AppConfigHelper.getImageSignature(thumbnailImageName, timeStampFromLocalDB);
//        final URL thumbnailCachedUrl = S3LoadingHelper.getCachedImageLink(thumbnailImageName);
//
//        final String imageName = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.MAIN);
//        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
//        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
//        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);
//
//        final RequestManager requestManagerForTN = Glide.with(imageView);
//
//        final RequestBuilder<Bitmap> thumbRequest = requestManagerForTN
//                .applyDefaultRequestOptions(new RequestOptions()
//                        .placeholder(placeholder)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .signature(new IntegerVersionSignature(thumbnailSignatureKey)))
//                .asBitmap()
//                .addListener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        Log.d(TAG, "Error loading thumbnail Image from cache, going to server");
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadImageFromServer(thumbnailImageName, thumbnailSignatureKey, requestManagerForTN, placeholder, errorBitmap, imageView, progressBar);
//                            }
//                        });
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        Log.d(TAG, "Loaded thumbnail image successfully from cache");
//                        if(progressBar != null){
//                            progressBar.setVisibility(View.GONE);
//                        }
//                        return false;
//                    }
//                })
////                .thumbnail(Glide.with(imageView).asBitmap().load(placeholder))
//                .load(new GlideUrlWithQueryParameter(thumbnailSourceUrl, thumbnailCachedUrl));
//
//        requestManager.applyDefaultRequestOptions(new RequestOptions()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .signature(new IntegerVersionSignature(signatureKey)))
//                .asBitmap()
//                .addListener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        Log.e(TAG, "Error loading groupImage from cache, going to server");
//
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadImageFromServer(imageName, signatureKey, requestManager, thumbRequest, errorBitmap, imageView, progressBar);
//                            }
//                        });
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        Log.d(TAG, "Loaded groupImage successfully from cache" );
//                        if(progressBar != null){
//                            progressBar.setVisibility(View.GONE);
//                        }
//                        return false;
//                    }
//                })
//                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
////                .thumbnail(0.1f)
//                .into(imageView);

    }


    public void loadGroupMNImage(String groupId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar) {

        final String thumbnailImageName = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.THUMBNAIL);
        String thumbnailSourceUrl = S3LoadingHelper.getBaseUrlOfImage(thumbnailImageName);
        final int thumbnailSignatureKey = AppConfigHelper.getImageSignature(thumbnailImageName, timeStampFromLocalDB);
        final URL thumbnailCachedUrl = S3LoadingHelper.getCachedImageLink(thumbnailImageName);

        final String imageName = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.MAIN);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);

        final RequestManager requestManagerForTN = Glide.with(imageView);

        final RequestBuilder<Bitmap> thumbRequest = requestManagerForTN
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new IntegerVersionSignature(thumbnailSignatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.d(TAG, "Error loading thumbnail Image from cache, going to server");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(thumbnailImageName, thumbnailSignatureKey, requestManagerForTN, placeholder, errorBitmap, imageView, progressBar);
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Loaded thumbnail image successfully from cache");
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
//                .thumbnail(Glide.with(imageView).asBitmap().load(placeholder))
                .load(new GlideUrlWithQueryParameter(thumbnailSourceUrl, thumbnailCachedUrl));

        requestManager.applyDefaultRequestOptions(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new IntegerVersionSignature(signatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e(TAG, "Error loading groupImage from cache, going to server");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(imageName, signatureKey, requestManager, thumbRequest, errorBitmap, imageView, progressBar);
                            }
                        });

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Loaded groupImage successfully from cache" );
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .thumbnail(0.1f)
                .into(imageView);

    }


    private void loadImageFromServer(final String imageName, final int signatureVersion, final RequestManager requestManager, final RequestBuilder<Bitmap> thumbRequest, final Drawable errorBitmap, final ImageView imageView, final ProgressBar progressBar) {

        //if there is no network connection, just do nothing
        if(!NetworkHelper.isOnline()){
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
            return;
        }

        S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {

                Log.d(TAG, "onImageLinkReady: link ready for MN server");


                requestManager.applyDefaultRequestOptions(new RequestOptions()
                        .timeout(3 * 60 * 1000)
                        .signature(new IntegerVersionSignature(signatureVersion)))
                        .asBitmap()
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                Log.d(TAG, "Error loading "+imageName+" from server");
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }

                                target.onLoadFailed(errorBitmap);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Loaded "+imageName+" successfully from server" );
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .load(new GlideUrlWithQueryParameter(S3LoadingHelper.getBaseUrlOfImage(imageName), url))
                        .thumbnail(thumbRequest)
//                        .thumbnail(0.1f)
                        .into(imageView);
            }

            @Override
            public void onImageLinkError(Exception e) {
                Log.d(TAG, "Error in getting pre-signed URL");
            }
        });
    }

    public void loadUserImage(String userId, ImageTypeConstants imageTypeConstants, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){

        if(ImageTypeConstants.THUMBNAIL == imageTypeConstants){
            loadUserTNImage(userId, timeStampFromLocalDB, requestManager, placeholder, errorBitmap, handler, imageView, progressBar);
        }
        else{
            loadUserMNImage(userId, timeStampFromLocalDB, requestManager, placeholder, errorBitmap, handler, imageView, progressBar);
        }

//        final String imageName = ImagesUtilHelper.getUserProfilePicName(userId, imageTypeConstants);
//        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
//        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
//        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);
//
//        requestManager.applyDefaultRequestOptions(new RequestOptions()
//                .placeholder(placeholder)
//                .signature(new IntegerVersionSignature(signatureKey)))
//                .asBitmap()
//                .addListener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadImageFromServer(imageName, signatureKey, requestManager, placeholder, errorBitmap, imageView, progressBar);
//                            }
//                        });
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
////                        Log.d(TAG, "Loaded groupImage successfully from cache" );
//                        if(progressBar != null){
//                            progressBar.setVisibility(View.GONE);
//                        }
//                        return false;
//                    }
//                })
//                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .into(imageView);

    }

    private void loadUserTNImage(String userId,  Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){

        final String imageName = ImagesUtilHelper.getUserProfilePicName(userId, ImageTypeConstants.THUMBNAIL);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);

        requestManager.applyDefaultRequestOptions(new RequestOptions()
                .placeholder(placeholder)
                .signature(new IntegerVersionSignature(signatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(imageName, signatureKey, requestManager, placeholder, errorBitmap, imageView, progressBar);
                            }
                        });

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        Log.d(TAG, "Loaded groupImage successfully from cache" );
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
                .into(imageView);

    }


    private void loadUserMNImage(String userId,  Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){

        final String thumbnailImageName = ImagesUtilHelper.getUserProfilePicName(userId, ImageTypeConstants.THUMBNAIL);
        String thumbnailSourceUrl = S3LoadingHelper.getBaseUrlOfImage(thumbnailImageName);
        final int thumbnailSignatureKey = AppConfigHelper.getImageSignature(thumbnailImageName, timeStampFromLocalDB);
        final URL thumbnailCachedUrl = S3LoadingHelper.getCachedImageLink(thumbnailImageName);

        final String imageName = ImagesUtilHelper.getUserProfilePicName(userId, ImageTypeConstants.MAIN);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);

        final RequestManager requestManagerForTN = Glide.with(imageView);

        final RequestBuilder<Bitmap> thumbRequest = requestManagerForTN
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new IntegerVersionSignature(thumbnailSignatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.d(TAG, "Error loading thumbnail Image from cache, going to server");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(thumbnailImageName, thumbnailSignatureKey, requestManagerForTN, placeholder, errorBitmap, imageView, progressBar);
                            }
                        });
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Loaded thumbnail image successfully from cache");
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
//                .thumbnail(Glide.with(imageView).asBitmap().load(placeholder))
                .load(new GlideUrlWithQueryParameter(thumbnailSourceUrl, thumbnailCachedUrl));

        requestManager.applyDefaultRequestOptions(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new IntegerVersionSignature(signatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e(TAG, "Error loading groupImage from cache, going to server");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(imageName, signatureKey, requestManager, thumbRequest, errorBitmap, imageView, progressBar);
                            }
                        });

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Loaded groupImage successfully from cache" );
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .thumbnail(0.1f)
                .into(imageView);

    }


    public void loadGroupUserImage(String groupId, ImageTypeConstants imageTypeConstants, String userId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){

        if(ImageTypeConstants.THUMBNAIL == imageTypeConstants){
            loadGroupUserTNImage(groupId, userId, timeStampFromLocalDB, requestManager, placeholder, errorBitmap, handler, imageView, progressBar);
        }
        else{
            loadGroupUserMNImage(groupId, userId, timeStampFromLocalDB, requestManager, placeholder, errorBitmap, handler, imageView, progressBar);
        }

//        final String imageName = ImagesUtilHelper.getGroupUserImageName(groupId, userId, imageTypeConstants);
//        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
//        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
//        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);
//
//
//
//        requestManager.applyDefaultRequestOptions(new RequestOptions()
//                .placeholder(placeholder)
//                .signature(new IntegerVersionSignature(signatureKey)))
//                .asBitmap()
//                .addListener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadImageFromServer(imageName, signatureKey, requestManager, placeholder, errorBitmap, imageView, progressBar);
//                            }
//                        });
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
////                        Log.d(TAG, "Loaded groupImage successfully from cache" );
//                        if(progressBar != null){
//                            progressBar.setVisibility(View.GONE);
//                        }
//                        return false;
//                    }
//                })
//                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .into(imageView);

    }

    private void loadGroupUserMNImage(String groupId, String userId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){
        //here first TN is loaded and then MN is loaded

        final String thumbnailImageName = ImagesUtilHelper.getGroupUserImageName(groupId, userId, ImageTypeConstants.THUMBNAIL);
        String thumbnailSourceUrl = S3LoadingHelper.getBaseUrlOfImage(thumbnailImageName);
        final int thumbnailSignatureKey = AppConfigHelper.getImageSignature(thumbnailImageName, timeStampFromLocalDB);
        final URL thumbnailCachedUrl = S3LoadingHelper.getCachedImageLink(thumbnailImageName);

        final String imageName = ImagesUtilHelper.getGroupUserImageName(groupId, userId, ImageTypeConstants.MAIN);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);

        final RequestManager requestManagerForTN = Glide.with(imageView);

        final RequestBuilder<Bitmap> thumbRequest = requestManagerForTN
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new IntegerVersionSignature(thumbnailSignatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.d(TAG, "Error loading thumbnail Image from cache, going to server");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(thumbnailImageName, thumbnailSignatureKey, requestManagerForTN, placeholder, errorBitmap, imageView, progressBar);
                            }
                        });
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Loaded thumbnail image successfully from cache");
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
//                .thumbnail(Glide.with(imageView).asBitmap().load(placeholder))
                .load(new GlideUrlWithQueryParameter(thumbnailSourceUrl, thumbnailCachedUrl));

        requestManager.applyDefaultRequestOptions(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new IntegerVersionSignature(signatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e(TAG, "Error loading groupImage from cache, going to server");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(imageName, signatureKey, requestManager, thumbRequest, errorBitmap, imageView, progressBar);
                            }
                        });

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Loaded groupImage successfully from cache" );
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .thumbnail(0.1f)
                .into(imageView);

    }

    private void loadGroupUserTNImage(String groupId, String userId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){
        final String imageName = ImagesUtilHelper.getGroupUserImageName(groupId, userId, ImageTypeConstants.THUMBNAIL);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);



        requestManager.applyDefaultRequestOptions(new RequestOptions()
                .placeholder(placeholder)
                .signature(new IntegerVersionSignature(signatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadImageFromServer(imageName, signatureKey, requestManager, placeholder, errorBitmap, imageView, progressBar);
                            }
                        });

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        Log.d(TAG, "Loaded groupImage successfully from cache" );
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
                .into(imageView);
    }

    private void loadImageFromServer(final String imageName, final int signatureVersion, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final ImageView imageView, final ProgressBar progressBar) {

        //if there is no network connection, just do nothing
        if(!NetworkHelper.isOnline()){
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
            return;
        }

        S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {

                requestManager.applyDefaultRequestOptions(new RequestOptions()
                        .timeout(3 * 60 * 1000)
                        .signature(new IntegerVersionSignature(signatureVersion)))
                        .asBitmap()
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                Log.d(TAG, "Error loading "+imageName+" from server");
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }

                                target.onLoadFailed(errorBitmap);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Loaded "+imageName+" successfully from server" );
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .load(new GlideUrlWithQueryParameter(S3LoadingHelper.getBaseUrlOfImage(imageName), url))
                        .submit(); //this is for thumbnail - if we load into image, then the primary request is cancelled
            }

            @Override
            public void onImageLinkError(Exception e) {
                Log.d(TAG, "Error in getting pre-signed URL");
            }
        });
    }


    public void downloadImage(final String imageName) {


        final String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, (new Date()).getTime());


        S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(final URL url) {
                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        FutureTarget<Bitmap> futureTarget =
                                Glide.with(AppConfigHelper.getContext())
                                        .applyDefaultRequestOptions(new RequestOptions()
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .centerCrop()
                                                .downsample(DownsampleStrategy.CENTER_OUTSIDE)
                                                .timeout(3 * 60 * 1000)
                                                .signature(new IntegerVersionSignature(signatureKey)))
                                        .asBitmap()
                                        .load(new GlideUrlWithQueryParameter(sourceUrl, url))
//                                        .submit(608, 608);
                                        .submit();

                        try {
                            Bitmap bitmap = futureTarget.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });


            }

            @Override
            public void onImageLinkError(Exception e) {
                Log.d(TAG, "Error in getting pre-signed URL");
            }
        });
    }

}
