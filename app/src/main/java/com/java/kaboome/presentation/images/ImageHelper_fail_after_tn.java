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

public class ImageHelper_fail_after_tn {

    private static final String TAG = "KMImageHelper";
    private static ImageHelper_fail_after_tn instance;
//    private URL completeURL;
//    private boolean loadGoingOn = false;
//    private int imageSignature;


    private ImageHelper_fail_after_tn() {


    }

    public static ImageHelper_fail_after_tn getInstance(){
        if(instance == null){
            instance = new ImageHelper_fail_after_tn();
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


    public void loadGroupImage(String groupId, ImageTypeConstants imageTypeConstants, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar) {

//    public static void loadGroupImage(final String groupId, Long timeStampFromLocalDB, final RequestManager requestManager, final Handler handler, final CircleImageView imageView, final ProgressBar progressBar){
//        final RequestManager newRequestManager = getRequestManager(imageView.getContext(), errorBitmap);

        final String thumbnailImageName = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.THUMBNAIL);
        String thumbnailSourceUrl = S3LoadingHelper.getBaseUrlOfImage(thumbnailImageName);
//        final int signatureKey = AppConfigHelper.getGroupImageSignature(groupId);
        final int thumbnailSignatureKey = AppConfigHelper.getImageSignature(thumbnailImageName, timeStampFromLocalDB);
        final URL thumbnailCachedUrl = S3LoadingHelper.getCachedImageLink(thumbnailImageName);

//        Log.d(TAG, "Image loading for group - "+ groupId+" start - "+(new Date()).getTime());
//        final String imageName = ImagesUtilHelper.getGroupImageName(groupId, imageTypeConstants);
        final String imageName = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.MAIN);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
//        final int signatureKey = AppConfigHelper.getGroupImageSignature(groupId);
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
                .thumbnail(Glide.with(imageView).asBitmap().load(placeholder))
                .load(new GlideUrlWithQueryParameter(thumbnailSourceUrl, thumbnailCachedUrl));



//        final RequestManager requestManagerForMN = Glide.with(imageView);

        requestManager.applyDefaultRequestOptions(new RequestOptions()
//                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(new IntegerVersionSignature(signatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e(TAG, "Error loading groupImage from cache, going to server");

//                        synchronized (this){
//                            if(loadGoingOn == true && cachedUrl.equals(completeURL) && signatureKey == imageSignature){
//                                //this means loading for this image is already going on, return
//                                Log.d(TAG, "onLoadFailed: but loading for same file going on, not going to fetch from server");
//                                return true;
//                            }
//                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                //going to load from server, first set the right variables
//                                synchronized (this){
//                                    completeURL = cachedUrl;
//                                    loadGoingOn = true;
//                                    imageSignature = signatureKey;
//                                    Log.d(TAG, "Going to load from server, setting data before");
//                                }
                                loadImageFromServer(imageName, signatureKey, requestManager, thumbRequest, errorBitmap, imageView, progressBar);
                            }
                        });
//                      The into(ImageView) method of Glide requires you to call it only on main thread
//                        hence this diskIo should not be used, even if you use it to load
//                        into should be called on main thread
//                        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadImageFromServer(imageName, signatureKey, requestManager, imageView, progressBar);
//                            }
//                        });

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Loaded groupImage successfully from cache" );
//                        Log.d(TAG, "Image loading for group - "+ groupId+"end - "+(new Date()).getTime());
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
//                        synchronized (this){
//                            loadGoingOn = false;
//                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
                .thumbnail(thumbRequest)
                .into(imageView);

    }


    private void loadImageFromServer(final String imageName, final int signatureVersion, final RequestManager requestManager, final RequestBuilder<Bitmap> thumbnailRequest, final Drawable errorBitmap, final ImageView imageView, final ProgressBar progressBar) {

//        Log.d(TAG, "imageName - "+imageName);
        //if there is no network connection, just do nothing
        if(!NetworkHelper.isOnline()){
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
//            synchronized (this){
//                Log.d(TAG, "setting loadGoingOn no network to false");
//                loadGoingOn = false;
//            }
            return;
        }

        S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {

                Log.d(TAG, "onImageLinkReady: link ready for MN server");


                requestManager.applyDefaultRequestOptions(new RequestOptions()
//                        .placeholder(placeholder)
//                        .skipMemoryCache(true)
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
//                                synchronized (this){
//                                    Log.d(TAG, "setting loadGoingOn no server to false");
//                                    loadGoingOn = false;
//                                }
//                                return true;
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Loaded "+imageName+" successfully from server" );
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }
//                                Log.d(TAG, "imageName - "+imageName);
//                                synchronized (this){
//                                    Log.d(TAG, "setting loadGoingOn to false, got from server");
//                                    loadGoingOn = false;
//                                }
                                return false;
                            }
                        })
                        .load(new GlideUrlWithQueryParameter(S3LoadingHelper.getBaseUrlOfImage(imageName), url))
//                        .thumbnail(thumbnailRequest)
                        .into(imageView);
            }

            @Override
            public void onImageLinkError(Exception e) {
                Log.d(TAG, "Error in getting pre-signed URL");
//                synchronized (this){
//                    loadGoingOn = false;
//                }

            }
        });
    }

    public void loadUserImage(String userId, ImageTypeConstants imageTypeConstants, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){

        final String imageName = ImagesUtilHelper.getUserProfilePicName(userId, imageTypeConstants);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);

//        Log.d(TAG, "sourceUrl - "+sourceUrl);
//        Log.d(TAG, "imageName - "+imageName);
//        Log.d(TAG, "signatureKey - "+signatureKey);
//        Log.d(TAG, "cachedUrl - "+cachedUrl);

        requestManager.applyDefaultRequestOptions(new RequestOptions()
                .placeholder(placeholder)
//                .skipMemoryCache(true)
                .signature(new IntegerVersionSignature(signatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        Log.e(TAG, "Error loading groupImage from cache, going to server, exception - "+ e);


//                        synchronized (this){
//                            Log.d(TAG, "onLoadFailed: but loadGoingOn is "+loadGoingOn);
//                            if(loadGoingOn == true && cachedUrl.equals(completeURL) && signatureKey == imageSignature){
//                                //this means loading for this image is already going on, return
//                                Log.d(TAG, "onLoadFailed: but loading for same file going on, not going to fetch from server");
//                                return true;
//                            }
//                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                //going to load from server, first set the right variables
//                                synchronized (this){
//                                    completeURL = cachedUrl;
//                                    loadGoingOn = true;
//                                    imageSignature = signatureKey;
//                                    Log.d(TAG, "Going to load from server, setting data before");
//                                }
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
//                        synchronized (this){
//                            Log.d(TAG, "setting loadGoingOn to false");
//                            loadGoingOn = false;
//                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
                .into(imageView);

    }

    public void loadGroupUserImage(String groupId, ImageTypeConstants imageTypeConstants, String userId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){

        final String imageName = ImagesUtilHelper.getGroupUserImageName(groupId, userId, imageTypeConstants);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);



        requestManager.applyDefaultRequestOptions(new RequestOptions()
                .placeholder(placeholder)
//                .skipMemoryCache(true)
                .signature(new IntegerVersionSignature(signatureKey)))
                .asBitmap()
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        Log.e(TAG, "Error loading groupImage from cache, going to server, exception - "+ e);

//                        synchronized (this){
//                            if(loadGoingOn == true && cachedUrl.equals(completeURL) && signatureKey == imageSignature){
//                                //this means loading for this image is already going on, return
//                                return true;
//                            }
//                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                synchronized (this){
//                                    completeURL = cachedUrl;
//                                    loadGoingOn = true;
//                                    imageSignature = signatureKey;
//                                }
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
//                        synchronized (this){
//                            loadGoingOn = false;
//                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
                .into(imageView);

    }

    private void loadImageFromServer(final String imageName, final int signatureVersion, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final ImageView imageView, final ProgressBar progressBar) {

//        Log.d(TAG, "imageName - "+imageName);
        //if there is no network connection, just do nothing
        if(!NetworkHelper.isOnline()){
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
//            synchronized (this){
//                Log.d(TAG, "setting loadGoingOn no network to false");
//                loadGoingOn = false;
//            }
            return;
        }

        S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {


                requestManager.applyDefaultRequestOptions(new RequestOptions()
//                        .placeholder(placeholder)
//                        .skipMemoryCache(true)
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
//                                synchronized (this){
//                                    Log.d(TAG, "setting loadGoingOn no server to false");
//                                    loadGoingOn = false;
//                                }
//                                return true;
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Loaded "+imageName+" successfully from server" );
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }
//                                Log.d(TAG, "imageName - "+imageName);
//                                synchronized (this){
//                                    Log.d(TAG, "setting loadGoingOn to false, got from server");
//                                    loadGoingOn = false;
//                                }
                                return false;
                            }
                        })
                        .load(new GlideUrlWithQueryParameter(S3LoadingHelper.getBaseUrlOfImage(imageName), url))
                        .into(imageView);
            }

            @Override
            public void onImageLinkError(Exception e) {
                Log.d(TAG, "Error in getting pre-signed URL");
//                synchronized (this){
//                    loadGoingOn = false;
//                }

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
                                        .submit(608, 608);

                        try {
                            Bitmap bitmap = futureTarget.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

// Do something with the Bitmap and then when you're done with it:
//                        Glide.with(context).clear(futureTarget);


//                        Bitmap myBitmap = Glide.with(AppConfigHelper.getContext())
////                                .asBitmap()
//                                .download(new GlideUrlWithQueryParameter(sourceUrl, url))
//
//                                .centerCrop()
//                                .into(500, 500)
//                                .get();
//                    }

//                        Glide.with(AppConfigHelper.getContext())
//                                .applyDefaultRequestOptions(new RequestOptions()
//                                        .timeout(3 * 60 * 1000)
//                                        .signature(new IntegerVersionSignature(signatureKey)))
//                                .asBitmap()
//                                .load(new GlideUrlWithQueryParameter(sourceUrl, url))
//                                .into(new Target<Bitmap>() {
//
//                                    @Override
//                                    public void onStart() {
//
//                                    }
//
//                                    @Override
//                                    public void onStop() {
//
//                                    }
//
//                                    @Override
//                                    public void onDestroy() {
//
//                                    }
//
//                                    @Override
//                                    public void onLoadStarted(@Nullable Drawable placeholder) {
//
//                                    }
//
//                                    @Override
//                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//
//                                    }
//
//                                    @Override
//                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//
//                                    }
//
//                                    @Override
//                                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                                    }
//
//                                    @Override
//                                    public void getSize(@NonNull SizeReadyCallback cb) {
//
//                                    }
//
//                                    @Override
//                                    public void removeCallback(@NonNull SizeReadyCallback cb) {
//
//                                    }
//
//                                    @Override
//                                    public void setRequest(@Nullable Request request) {
//
//                                    }
//
//                                    @Nullable
//                                    @Override
//                                    public Request getRequest() {
//                                        return null;
//                                    }
//                                });

//                        try {
//
//                        FutureTarget<File> future = Glide.with(AppConfigHelper.getContext())
//                                .applyDefaultRequestOptions(new RequestOptions()
//                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
//                                        .timeout(3 * 60 * 1000)
//                                        .centerCrop()
//                                        .downsample(DownsampleStrategy.CENTER_OUTSIDE)
//                                        .signature(new IntegerVersionSignature(signatureKey)))
//                                .load(new GlideUrlWithQueryParameter(sourceUrl, url))
//                                .downloadOnly(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL);
//                        File cacheFile = future.get();
//                        String mimeType = FileUtils.getMimeType(cacheFile);
//                        Uri uri = FileProvider.getUriForFile(AppConfigHelper.getContext(), AppConfigHelper.getContext().getPackageName(), cacheFile);
//                            Log.d(TAG, "File uri - "+uri);
//
//
//
////                            FutureTarget<File> future = Glide.with(AppConfigHelper.getContext())
////                                    .applyDefaultRequestOptions(new RequestOptions()
////                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
////                                            .timeout(3 * 60 * 1000)
////                                            .signature(new IntegerVersionSignature(signatureKey)))
////                                    .downloadOnly()
////                                    .load(new GlideUrlWithQueryParameter(sourceUrl, url))
////                                    .submit(608,608);
////                            File cacheFile = future.get();
////
////                            getRequestManager(AppConfigHelper.getContext())
////                                    .applyDefaultRequestOptions(new RequestOptions()
////                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
////                                            .timeout(3 * 60 * 1000)
////                                            .signature(new IntegerVersionSignature(signatureKey)))
////                                    .asBitmap()
////                                    .downloadOnly(608,608)
////                                    .load(new GlideUrlWithQueryParameter(sourceUrl, url)
////
////                                    );
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                });


            }

            @Override
            public void onImageLinkError(Exception e) {
                Log.d(TAG, "Error in getting pre-signed URL");
//                synchronized (this){
//                    loadGoingOn = false;
//                }

            }
        });
    }

}
