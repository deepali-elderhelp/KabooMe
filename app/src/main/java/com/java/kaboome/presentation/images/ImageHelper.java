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
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
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


/**
 * Some notes -
 * everywhere using placeholder and not error bitmap, because if the placeholder is null, glide shows a flicker
 * also, if the placeholder is there, but the error bitmap is not there, then even on error, the placeholder keeps
 * showing up. That is the strategy that I am using, only placeholder, not error bitmaps.
 *
 */
public class ImageHelper {

    private static final String TAG = "KMImageHelper";
    private static ImageHelper instance;

    private ImageHelper() {


    }

    public static ImageHelper getInstance(){
        if(instance == null){
            instance = new ImageHelper();
        }
        return instance;
    }

    public RequestManager getRequestManager(Context context){

        if(context == null){
            return null;
        }

        RequestOptions options = new RequestOptions();

        return Glide.with(context)
                .setDefaultRequestOptions(options);

    }

    public RequestManager getRequestManager(Context context, int placeholderImage, int errorImage){

        if(context == null){
            return null;
        }

        RequestOptions options = new RequestOptions();

        if (placeholderImage != -1) {
            options.placeholder(placeholderImage);
        }
        options.error(errorImage);

        return Glide.with(context)
                .setDefaultRequestOptions(options);

    }

    public RequestManager getRequestManager(Context context, Drawable errorImage){

        if(context == null){
            return null;
        }
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

        loadThumbnailImage(requestManager, placeholder, errorBitmap, handler, imageView, progressBar, thumbnailImageName, thumbnailSourceUrl, thumbnailSignatureKey, thumbnailCachedUrl);


    }

    private void loadThumbnailImage(final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar, final String thumbnailImageName, String thumbnailSourceUrl, final int thumbnailSignatureKey, URL thumbnailCachedUrl) {
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
                                loadImageFromServerNoTN(thumbnailImageName, thumbnailSignatureKey, requestManager, placeholder, errorBitmap, imageView, progressBar);
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
        Log.d(TAG, "loadGroupMNImage - "+imageName+" signature key "+signatureKey);


        final RequestBuilder<Bitmap> thumbRequest = getThumbnailRequest(placeholder, errorBitmap, handler, imageView, progressBar, thumbnailImageName, thumbnailSourceUrl, thumbnailSignatureKey, thumbnailCachedUrl);

        loadMainImage(requestManager, errorBitmap, handler, imageView, progressBar, imageName, sourceUrl, signatureKey, cachedUrl, thumbRequest);

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
                        .placeholder(errorBitmap)
                        .signature(new IntegerVersionSignature(signatureVersion)))
                        .asBitmap()
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                Log.d(TAG, "Error loading "+imageName+" from server");
//                                Log.d(TAG, "onLoadFailed: "+e);
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }
//                                return false;
//                                target.onLoadFailed(errorBitmap); //commenting this so that the placholder keeps getting displayed
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
                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
                //load error image with glide
                requestManager.load(errorBitmap).into(imageView);
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

    }

    private void loadUserTNImage(String userId,  Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){

        final String imageName = ImagesUtilHelper.getUserProfilePicName(userId, ImageTypeConstants.THUMBNAIL);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);

        loadThumbnailImage(requestManager, placeholder, errorBitmap, handler, imageView, progressBar, imageName, sourceUrl, signatureKey, cachedUrl);
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


        final RequestBuilder<Bitmap> thumbRequest = getThumbnailRequest(placeholder, errorBitmap, handler, imageView, progressBar, thumbnailImageName, thumbnailSourceUrl, thumbnailSignatureKey, thumbnailCachedUrl);
        loadMainImage(requestManager, errorBitmap, handler, imageView, progressBar, imageName, sourceUrl, signatureKey, cachedUrl, thumbRequest);

    }

    private void loadMainImage(final RequestManager requestManager, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar, final String imageName, String sourceUrl, final int signatureKey, URL cachedUrl, final RequestBuilder<Bitmap> thumbRequest) {
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
                        Log.d(TAG, "Loaded groupImage successfully from cache");
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .transition(BitmapTransitionOptions.withCrossFade())
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


        final RequestBuilder<Bitmap> thumbRequest = getThumbnailRequest(placeholder, errorBitmap, handler, imageView, progressBar, thumbnailImageName, thumbnailSourceUrl, thumbnailSignatureKey, thumbnailCachedUrl);
        loadMainImage(requestManager, errorBitmap, handler, imageView, progressBar, imageName, sourceUrl, signatureKey, cachedUrl, thumbRequest);

    }

    private RequestBuilder<Bitmap> getThumbnailRequest(final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar, final String thumbnailImageName, String thumbnailSourceUrl, final int thumbnailSignatureKey, URL thumbnailCachedUrl){
        final RequestManager requestManagerForTN = Glide.with(imageView);

        return requestManagerForTN
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
                                getImageFromServer(thumbnailImageName, thumbnailSignatureKey, requestManagerForTN, placeholder, errorBitmap, imageView, progressBar);
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
    }

    private void loadGroupUserTNImage(String groupId, String userId, Long timeStampFromLocalDB, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final Handler handler, final ImageView imageView, final ProgressBar progressBar){
        final String imageName = ImagesUtilHelper.getGroupUserImageName(groupId, userId, ImageTypeConstants.THUMBNAIL);
        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
        final int signatureKey = AppConfigHelper.getImageSignature(imageName, timeStampFromLocalDB);
        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);

        loadThumbnailImage(requestManager, placeholder, errorBitmap, handler, imageView, progressBar, imageName, sourceUrl, signatureKey, cachedUrl);

    }

    //this is needed when thumbnail and main image are needed, this only gets the thumbnail image, so only submit, not into
    private void getImageFromServer(final String imageName, final int signatureVersion, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final ImageView imageView, final ProgressBar progressBar) {

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
                        .placeholder(errorBitmap)
                        .signature(new IntegerVersionSignature(signatureVersion)))
                        .asBitmap()
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                Log.d(TAG, "Error loading "+imageName+" from server");
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }

//                                target.onLoadFailed(errorBitmap);
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
                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
                //load error image with glide
                requestManager.load(errorBitmap).into(imageView);
            }
        });
    }

    private void loadImageFromServerNoTN(final String imageName, final int signatureVersion, final RequestManager requestManager, final Drawable placeholder, final Drawable errorBitmap, final ImageView imageView, final ProgressBar progressBar) {

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
                        .placeholder(placeholder)
                        .signature(new IntegerVersionSignature(signatureVersion)))
                        .asBitmap()
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                Log.d(TAG, "Error loading "+imageName+" from server");
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }

//                                target.onLoadFailed(errorBitmap);
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
                        .into(imageView); //this is for thumbnail load only, there is no primary request, so into is needed
            }

            @Override
            public void onImageLinkError(Exception e) {
                Log.d(TAG, "Error in getting pre-signed URL");
                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
                //load error image with glide
                requestManager.load(errorBitmap).into(imageView);
            }
        });
    }



    public void downloadImage(final String imageName) {

        Log.d("TestTAG", "downloadImage:  = "+imageName);
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
                            Log.d("TestTAG", "Image has been gotten by Glide = "+imageName);
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
