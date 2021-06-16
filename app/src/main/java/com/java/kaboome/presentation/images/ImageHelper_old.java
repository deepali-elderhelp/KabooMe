//package com.java.kaboome.presentation.images;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import androidx.annotation.Nullable;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.RequestManager;
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.Target;
//import com.java.kaboome.helpers.AppConfigHelper;
//import com.java.kaboome.helpers.NetworkHelper;
//import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
//import com.java.kaboome.presentation.images.glide.GlideUrlWithQueryParameter;
//import com.java.kaboome.presentation.images.glide.IntegerVersionSignature;
//
//import java.net.URL;
//
//public class ImageHelper_old {
//
//    private static final String TAG = "KMImageHelper";
//
//    public static RequestManager getRequestManager(Context context){
//
//        RequestOptions options = new RequestOptions();
//
//        return Glide.with(context)
//                .setDefaultRequestOptions(options);
//
//    }
//
//    public static RequestManager getRequestManager(Context context, int placeholderImage, int errorImage){
//
//        RequestOptions options = new RequestOptions();
//
//        if (placeholderImage != -1) {
//            options.placeholder(placeholderImage);
//        }
//        options.error(errorImage);
//
//        return Glide.with(context)
//                .setDefaultRequestOptions(options);
//
//    }
//
//    public static RequestManager getRequestManager(Context context, Drawable placeholderImage, Drawable errorImage){
//
//        RequestOptions options = new RequestOptions();
//
//        if (placeholderImage != null) {
//            options.placeholder(placeholderImage);
//        }
//        options.error(errorImage);
//
//        return Glide.with(context)
//                .setDefaultRequestOptions(options);
//
//    }
//
////    public static void loadGroupImage(String groupId, Long timeStampFromLocalDB, final RequestManager requestManager, final BitmapDrawable placeholder, final BitmapDrawable errorBitmap, final Handler handler, final CircleImageView imageView, final ProgressBar progressBar) {
////
////        Log.d(TAG, "Trying to load image for image name - "+groupId);
////
////        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(ImagesUtilHelper.getGroupImageName(groupId));
////        final String imageName = ImagesUtilHelper.getGroupImageName(groupId);
//////        final int signatureKey = AppConfigHelper.getGroupImageSignature(groupId);
////        final int signatureKey = AppConfigHelper.getImageSignature(groupId, timeStampFromLocalDB);
////        URL cachedUrl = S3LoadingHelper.getCachedImageLink(ImagesUtilHelper.getGroupImageName(groupId));
////
////
////
////        requestManager.applyDefaultRequestOptions(new RequestOptions()
////                .placeholder(placeholder)
////                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
////                .signature(new IntegerVersionSignature(signatureKey)))
////                .asBitmap()
////                .addListener(new RequestListener<Bitmap>() {
////                    @Override
////                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//////                        Log.e(TAG, "Error loading groupImage from cache, going to server, excpetion - "+ e);
////
////                        handler.post(new Runnable() {
////                            @Override
////                            public void run() {
////                                S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
////                                    @Override
////                                    public void onImageLinkReady(URL url) {
////
////                                        requestManager.applyDefaultRequestOptions(new RequestOptions()
////                                                .placeholder(placeholder)
////                                                .timeout(3 * 60 * 1000)
////                                                .signature(new IntegerVersionSignature(signatureKey)))
////                                                .asBitmap()
////                                                .addListener(new RequestListener<Bitmap>() {
////                                                    @Override
////                                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//////                                Log.d(TAG, "Error loading groupImage from server "+ e.getMessage());
////                                                        if(progressBar != null){
////                                                            progressBar.setVisibility(View.GONE);
////                                                        }
////
////                                                        Log.d(TAG, "Error image for image name - "+imageName);
////                                                        target.onLoadFailed(errorBitmap);
////
////                                                        return true;
////                                                    }
////
////                                                    @Override
////                                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//////                                Log.d(TAG, "Loaded groupImage successfully from server" );
////                                                        if(progressBar != null){
////                                                            progressBar.setVisibility(View.GONE);
////                                                        }
//////                                Log.d(TAG, "imageName - "+imageName);
////                                                        return false;
////                                                    }
////                                                })
////                                                .load(new GlideUrlWithQueryParameter(S3LoadingHelper.getBaseUrlOfImage(imageName), url))
////                                                .into(imageView);
////                                    }
////
////                                    @Override
////                                    public void onImageLinkError(Exception e) {
////                                        Log.d(TAG, "Error in getting pre-signed URL");
////
////                                    }
////                                });
////                            }
////                        });
//////                      The into(ImageView) method of Glide requires you to call it only on main thread
//////                        hence this diskIo should not be used, even if you use it to load
//////                        into should be called on main thread
//////                        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//////                            @Override
//////                            public void run() {
//////                                loadImageFromServer(imageName, signatureKey, requestManager, imageView, progressBar);
//////                            }
//////                        });
////
////                        return true;
////                    }
////
////                    @Override
////                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//////                        Log.d(TAG, "Loaded groupImage successfully from cache" );
//////                        Log.d(TAG, "Image loading for group - "+ groupId+"end - "+(new Date()).getTime());
////                        if(progressBar != null){
////                            progressBar.setVisibility(View.GONE);
////                        }
////                        return false;
////                    }
////                })
////                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
////                .into(imageView);
////
////    }
//
//    public static void loadGroupImage(String groupId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar) {
//
////    public static void loadGroupImage(final String groupId, Long timeStampFromLocalDB, final RequestManager requestManager, final Handler handler, final CircleImageView imageView, final ProgressBar progressBar){
//
////        Log.d(TAG, "Image loading for group - "+ groupId+" start - "+(new Date()).getTime());
//        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(ImagesUtilHelper.getGroupImageName(groupId, imageTypeConstants));
//        final String imageName = ImagesUtilHelper.getGroupImageName(groupId, imageTypeConstants);
////        final int signatureKey = AppConfigHelper.getGroupImageSignature(groupId);
//        final int signatureKey = AppConfigHelper.getImageSignature(groupId, timeStampFromLocalDB);
//        URL cachedUrl = S3LoadingHelper.getCachedImageLink(ImagesUtilHelper.getGroupImageName(groupId, imageTypeConstants));
//
//
//        requestManager.applyDefaultRequestOptions(new RequestOptions()
//                .placeholder(placeholder)
////                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .signature(new IntegerVersionSignature(signatureKey)))
//                .asBitmap()
//                .addListener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
////                        Log.e(TAG, "Error loading groupImage from cache, going to server, exception - "+ e);
//
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadImageFromServer(imageName, signatureKey, requestManager, placeholder, errorBitmap, imageView, progressBar);
//                            }
//                        });
////                      The into(ImageView) method of Glide requires you to call it only on main thread
////                        hence this diskIo should not be used, even if you use it to load
////                        into should be called on main thread
////                        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
////                            @Override
////                            public void run() {
////                                loadImageFromServer(imageName, signatureKey, requestManager, imageView, progressBar);
////                            }
////                        });
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
////                        Log.d(TAG, "Loaded groupImage successfully from cache" );
////                        Log.d(TAG, "Image loading for group - "+ groupId+"end - "+(new Date()).getTime());
//                        if(progressBar != null){
//                            progressBar.setVisibility(View.GONE);
//                        }
//                        return false;
//                    }
//                })
//                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .into(imageView);
//
//    }
//
//    public static void loadUserImage(String userId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){
//
//        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(ImagesUtilHelper.getUserProfilePicName(userId));
//        final String imageName = ImagesUtilHelper.getUserProfilePicName(userId);
//        final int signatureKey = AppConfigHelper.getImageSignature(userId, timeStampFromLocalDB);
//        URL cachedUrl = S3LoadingHelper.getCachedImageLink(ImagesUtilHelper.getUserProfilePicName(userId));
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
////                        Log.e(TAG, "Error loading groupImage from cache, going to server, exception - "+ e);
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
//
//                        return false;
//                    }
//                })
//                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .into(imageView);
//
//    }
//
//    public static void loadGroupUserImage(String groupId, String userId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){
//
//        final String imageName = ImagesUtilHelper.getGroupUserImageName(groupId, userId);
//        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
//        final int signatureKey = AppConfigHelper.getImageSignature(groupId+"_"+userId, timeStampFromLocalDB);
//        URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);
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
////                        Log.e(TAG, "Error loading groupImage from cache, going to server, exception - "+ e);
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
//
//                        return false;
//                    }
//                })
//                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .into(imageView);
//
//    }
//
//    private static void loadImageFromServer(final String imageName, final int signatureVersion, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final ImageView imageView, final ProgressBar progressBar) {
//
//        Log.d(TAG, "imageName - "+imageName);
//        //if there is no network connection, just do nothing
//        if(!NetworkHelper.isOnline()){
//            if(progressBar != null){
//                progressBar.setVisibility(View.GONE);
//            }
//            return;
//        }
//
//        S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
//            @Override
//            public void onImageLinkReady(URL url) {
//
//                requestManager.applyDefaultRequestOptions(new RequestOptions()
//                        .placeholder(placeholder)
//                        .timeout(3 * 60 * 1000)
//                        .signature(new IntegerVersionSignature(signatureVersion)))
//                        .asBitmap()
//                        .addListener(new RequestListener<Bitmap>() {
//                            @Override
//                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
////                                Log.d(TAG, "Error loading groupImage from server "+ e.getMessage());
//                                if(progressBar != null){
//                                    progressBar.setVisibility(View.GONE);
//                                }
//
//                                target.onLoadFailed(errorBitmap);
//                                return true;
////                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
////                                Log.d(TAG, "Loaded groupImage successfully from server" );
//                                if(progressBar != null){
//                                    progressBar.setVisibility(View.GONE);
//                                }
////                                Log.d(TAG, "imageName - "+imageName);
//                                return false;
//                            }
//                        })
//                        .load(new GlideUrlWithQueryParameter(S3LoadingHelper.getBaseUrlOfImage(imageName), url))
//                        .into(imageView);
//            }
//
//            @Override
//            public void onImageLinkError(Exception e) {
//                Log.d(TAG, "Error in getting pre-signed URL");
//
//            }
//        });
//    }
//
//
////    public static void loadUserImage(String userId, Long timeStampFromLocalDB, final RequestManager requestManager, final Handler handler, final CircleImageView imageView, final ProgressBar progressBar){
////
////        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(ImagesUtilHelper.getUserProfilePicName(userId));
////        final String imageName = ImagesUtilHelper.getUserProfilePicName(userId);
////        final int signatureKey = AppConfigHelper.getImageSignature(userId, timeStampFromLocalDB);
////        URL cachedUrl = S3LoadingHelper.getCachedImageLink(ImagesUtilHelper.getUserProfilePicName(userId));
////
////
////
////        requestManager.applyDefaultRequestOptions(new RequestOptions()
////                .signature(new IntegerVersionSignature(signatureKey)))
////                .asBitmap()
////                .addListener(new RequestListener<Bitmap>() {
////                    @Override
////                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//////                        Log.e(TAG, "Error loading groupImage from cache, going to server, exception - "+ e);
////
////                        handler.post(new Runnable() {
////                            @Override
////                            public void run() {
////                                loadImageFromServer(imageName, signatureKey, requestManager, imageView, progressBar);
////                            }
////                        });
////
////                        return true;
////                    }
////
////                    @Override
////                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//////                        Log.d(TAG, "Loaded groupImage successfully from cache" );
////                        if(progressBar != null){
////                            progressBar.setVisibility(View.GONE);
////                        }
////
////                        return false;
////                    }
////                })
////                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
////                .into(imageView);
////
////    }
////
////    private static void loadImageFromServer(final String imageName, final int signatureVersion, final RequestManager requestManager, final CircleImageView imageView, final ProgressBar progressBar) {
////
////        Log.d(TAG, "imageName - "+imageName);
////        //if there is no network connection, just do nothing
////        if(!NetworkHelper.isOnline()){
////            if(progressBar != null){
////                progressBar.setVisibility(View.GONE);
////            }
////            return;
////        }
////
////        S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
////            @Override
////            public void onImageLinkReady(URL url) {
////
////                requestManager.applyDefaultRequestOptions(new RequestOptions()
////                        .timeout(3 * 60 * 1000)
////                        .signature(new IntegerVersionSignature(signatureVersion)))
////                        .asBitmap()
////                        .addListener(new RequestListener<Bitmap>() {
////                            @Override
////                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//////                                Log.d(TAG, "Error loading groupImage from server "+ e.getMessage());
////                                if(progressBar != null){
////                                    progressBar.setVisibility(View.GONE);
////                                }
////                                return false;
////                            }
////
////                            @Override
////                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//////                                Log.d(TAG, "Loaded groupImage successfully from server" );
////                                if(progressBar != null){
////                                    progressBar.setVisibility(View.GONE);
////                                }
//////                                Log.d(TAG, "imageName - "+imageName);
////                                return false;
////                            }
////                        })
////                        .load(new GlideUrlWithQueryParameter(S3LoadingHelper.getBaseUrlOfImage(imageName), url))
////                        .into(imageView);
////            }
////
////            @Override
////            public void onImageLinkError(Exception e) {
////                Log.d(TAG, "Error in getting pre-signed URL");
////
////            }
////        });
////    }
//
//
//
//}
