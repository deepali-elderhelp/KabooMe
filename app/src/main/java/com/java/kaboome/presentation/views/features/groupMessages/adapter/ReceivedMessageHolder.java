package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.images.ImageLinkHandler;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.images.S3LoadingHelper;
import com.java.kaboome.presentation.images.glide.GlideUrlWithQueryParameter;
import com.java.kaboome.presentation.images.glide.IntegerVersionSignature;
import com.java.kaboome.presentation.views.widgets.MessagesListStyle;

import java.io.File;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMReceivedMessageHolder";

    TextView messageText, timeText, aliasText, roleText, newMessageHeader;
    TextView messageImage, timeImage, aliasImage,roleImage;
    Context context;
    CircleImageView profileImage;
    View bubble;
    RequestManager requestManager;
    Handler handler; //Glide needs it
    ImageView ugentMessageImage, imageAttached, imageDownload;
    ConstraintLayout textMessageLayout;
    ConstraintLayout attachmentMessageLayout;
//    private boolean showNewMessageHeader;

    ReceivedMessageHolder(View itemView, Context context, RequestManager requestManager, MessagesListStyle style) {
        super(itemView);

        this.context = context;
        messageText = (TextView) itemView.findViewById(R.id.text_message);
        timeText = (TextView) itemView.findViewById(R.id.text_time);
        aliasText = (TextView) itemView.findViewById(R.id.text_user_alias);
        roleText = (TextView) itemView.findViewById(R.id.text_user_role);

        messageImage =  itemView.findViewById(R.id.image_message_rcvd);
        timeImage =  itemView.findViewById(R.id.image_time_rcvd);
        aliasImage = itemView.findViewById(R.id.image_user_alias_rcvd);
        roleImage = itemView.findViewById(R.id.image_user_role_rcvd);

        imageAttached = itemView.findViewById(R.id.image_bubble_image_rcvd);
        imageDownload = itemView.findViewById(R.id.image_bubble_download_rcvd);

        profileImage = (CircleImageView) itemView.findViewById(R.id.image_user_profile);
        ugentMessageImage = itemView.findViewById(R.id.urgent_image_recvd);
        newMessageHeader = itemView.findViewById(R.id.newMessagesLabel);
//        bubble = itemView.findViewById(R.id.bubble_recvd);
        this.requestManager = requestManager;
        textMessageLayout = itemView.findViewById(R.id.text_bubble);
        attachmentMessageLayout = itemView.findViewById(R.id.image_bubble_recvd);
//        this.showNewMessageHeader = showNewMessageHeader;

//        applyStyle(style);
    }


    public void onBind(Message message, Handler handler, View.OnClickListener messageClickListener, View.OnLongClickListener messageLongClickListener, boolean showNewMessageHeader) {

        itemView.setOnClickListener(messageClickListener);
        itemView.setOnLongClickListener(messageLongClickListener);
        this.handler = handler;

        //            //if the message time is after user's last access time, then make the background off white
//            if (userLastAccessedThisGroup < message.getSentAt()) {
//                backgroundLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorOffWhite));
//            } else {
//                backgroundLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
//            }

        if(message.getHasAttachment() != null && message.getHasAttachment()){
            textMessageLayout.setVisibility(View.GONE);
            attachmentMessageLayout.setVisibility(View.VISIBLE);

            messageImage.setText(message.getMessageText());
            aliasImage.setText(message.getAlias());
            if(message.getRole() != null && !(message.getRole().trim().isEmpty())){
                roleImage.setText(" - "+message.getRole());
            }

            if(message.getSentAt() != null){
                timeImage.setText(DateHelper.getPrettyTime(message.getSentAt()));
            }

            String attachmentMime = message.getAttachmentMime();
            File appDir = FileUtils.getAppDirForMime(attachmentMime, false);

            if(appDir != null){
                File attachmentFile = new File(appDir, message.getMessageId()+message.getAttachmentExtension());
                if(attachmentFile.exists()){
                    imageDownload.setVisibility(View.INVISIBLE);

                    requestManager
                            .asBitmap()
                            .load(attachmentFile)
                            .into(imageAttached);
                }
                else{
                    imageDownload.setVisibility(View.VISIBLE);
                    imageAttached.setImageResource(R.drawable.attachment_default);
                }
            }

        }
        else{

            textMessageLayout.setVisibility(View.VISIBLE);
            attachmentMessageLayout.setVisibility(View.GONE);

            messageText.setText(message.getMessageText());
            if(showNewMessageHeader){
                newMessageHeader.setVisibility(View.VISIBLE);
            }
            else{
                newMessageHeader.setVisibility(View.GONE);
            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
            aliasText.setText(message.getAlias());
            if(message.getRole() != null && !(message.getRole().trim().isEmpty())){
                roleText.setText(" - "+message.getRole());
            }

            if(message.getNotify() == 1){
                ugentMessageImage.setVisibility(View.VISIBLE);
            }
            else{
                ugentMessageImage.setVisibility(View.INVISIBLE);
            }
        }





//        if (message.getIsAdmin() != null && message.getIsAdmin().equalsIgnoreCase("true")) {
//            //user is an admin
//            if (message.getRole() != null && !message.getRole().isEmpty()) {
//                aliasRoleText.setText(message.getAlias()+" "+message.getRole());  //there is a role to set
//            }
//        }
//        else{
//            aliasRoleText.setText(message.getAlias());
//        }


//        if (message.getNotify() == 1) {
//            messageText.setBackgroundResource(R.drawable.message_recvd_high);
//        } else {
//            messageText.setBackgroundResource(R.drawable.message_recvd);
//        }

        //load groupImage
//        loadImage(message.getSentBy());
//        ImageHelper.loadUserImage(message.getSentBy(), null, requestManager, handler, profileImage, null);
        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(itemView.getContext(),R.dimen.message_recvd_sender_image_width, message.getAlias());
        ImageHelper.getInstance().loadUserImage(message.getSentBy(), ImageTypeConstants.THUMBNAIL,message.getSentByImageTS(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null);

    }


//    private void loadImage(String userId){
//
////        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(ImagesUtilHelper.getGroupTNName(groupId));
////        final String imageName = ImagesUtilHelper.getGroupTNName(groupId);
////        final int signatureKey = AppConfigHelper.getGroupImageSignature(groupId);
////        URL cachedUrl = S3LoadingHelper.getCachedImageLink(ImagesUtilHelper.getGroupTNName(groupId));
//
//        String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(ImagesUtilHelper.getUserProfilePicName(userId));
//        final String imageName = ImagesUtilHelper.getUserProfilePicName(userId);
//        final int signatureKey = AppConfigHelper.getUserProfilePicSignature(userId);
//        URL cachedUrl = S3LoadingHelper.getCachedImageLink(ImagesUtilHelper.getUserProfilePicName(userId));
//
//
//        requestManager.applyDefaultRequestOptions(new RequestOptions()
//                .signature(new IntegerVersionSignature(signatureKey)))
//                .asBitmap()
//                .addListener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        Log.e(TAG, "Error loading groupImage from cache, going to server, excpetion - "+ e);
//
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadImageFromServer(imageName, signatureKey);
//                            }
//                        });
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        Log.d(TAG, "Loaded groupImage successfully from cache" );
//                        return false;
//                    }
//                })
//                .load(new GlideUrlWithQueryParameter(sourceUrl, cachedUrl))
//                .into(profileImage);
//
//    }
//
//    private void loadImageFromServer(final String imageName, final int signatureVersion) {
//
//        S3LoadingHelper.getPresignedImageLink(imageName, new ImageLinkHandler() {
//            @Override
//            public void onImageLinkReady(URL url) {
//
//                requestManager.applyDefaultRequestOptions(new RequestOptions()
//                        .signature(new IntegerVersionSignature(signatureVersion)))
//                        .asBitmap()
//                        .addListener(new RequestListener<Bitmap>() {
//                            @Override
//                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                                Log.d(TAG, "Error loading groupImage from server "+ e.getMessage());
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                                Log.d(TAG, "Loaded groupImage successfully from server" );
//                                return false;
//                            }
//                        })
//                        .load(new GlideUrlWithQueryParameter(S3LoadingHelper.getBaseUrlOfImage(imageName), url))
//                        .into(profileImage);
//            }
//
//            @Override
//            public void onImageLinkError(Exception e) {
//                Log.d(TAG, "Error in getting pre-signed URL");
//
//            }
//        });
//    }

//    private final void applyStyle(MessagesListStyle style) {
//
//        if (timeText != null) {
//            timeText.setTextColor(style.getIncomingTimeTextColor());
//            timeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getIncomingTimeTextSize());
//            timeText.setTypeface(timeText.getTypeface(), style.getIncomingTimeTextStyle());
//        }
//
//        if (profileImage != null) {
//            profileImage.getLayoutParams().width = style.getIncomingAvatarWidth();
//            profileImage.getLayoutParams().height = style.getIncomingAvatarHeight();
//        }
//
//        if (bubble != null) {
//            bubble.setPadding(style.getIncomingDefaultBubblePaddingLeft(),
//                    style.getIncomingDefaultBubblePaddingTop(),
//                    style.getIncomingDefaultBubblePaddingRight(),
//                    style.getIncomingDefaultBubblePaddingBottom());
//            ViewCompat.setBackground(bubble, style.getIncomingBubbleDrawable());
//        }
//
//        if(aliasRoleText != null) {
//            aliasRoleText.setTextColor(style.getIncomingAliasTextColor());
//            aliasRoleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getIncomingAliasTextSize());
//            aliasRoleText.setTypeface(aliasRoleText.getTypeface(), style.getIncomingAliasTextStyle());
//        }
//
//        if (messageText != null) {
//            messageText.setTextColor(style.getIncomingTextColor());
//            messageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getIncomingTextSize());
//            messageText.setTypeface(messageText.getTypeface(), style.getIncomingTextStyle());
//            messageText.setAutoLinkMask(style.getTextAutoLinkMask());
//            messageText.setLinkTextColor(style.getIncomingTextLinkColor());
////            configureLinksBehavior(text);
//        }
//    }

}
