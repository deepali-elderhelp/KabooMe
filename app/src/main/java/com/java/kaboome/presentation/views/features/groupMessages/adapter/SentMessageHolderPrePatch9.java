package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.presentation.helpers.DateFormatter;
import com.java.kaboome.presentation.views.widgets.MessagesListStyle;

import java.util.Date;


public class SentMessageHolderPrePatch9 extends RecyclerView.ViewHolder {

    private static final String TAG = "KMSentMessageHolder";

    TextView messageText, timeText;

    ImageView progressUploadImage;
//    CircleImageView profileImage;
//    TextView userAlias;
//    TextView userRole;
    Handler handler; //Glide needs it
    RequestManager requestManager;
    ViewGroup bubble;



    SentMessageHolderPrePatch9(View itemView, RequestManager requestManager, MessagesListStyle style) {
        super(itemView);

        messageText =  itemView.findViewById(R.id.text_message_body_sent);
        timeText =  itemView.findViewById(R.id.text_message_time_sent);
        bubble = itemView.findViewById(R.id.bubble);
//        userAlias = itemView.findViewById(R.id.text_user_alias) ;
//        userRole = itemView.findViewById(R.id.text_user_role);
        this.requestManager = requestManager;

//        profileImage = (CircleImageView) itemView.findViewById(R.id.image_user_profile);
        progressUploadImage = itemView.findViewById(R.id.progress_upload);

        applyStyle(style);
    }

    public void onBind(Message message, Handler handler, View.OnLongClickListener messageLongClickListener){

        itemView.setOnLongClickListener(messageLongClickListener);
        this.handler = handler;

        if(message.isUploadedToServer()){
            progressUploadImage.setVisibility(View.GONE);
        }
        else{
            progressUploadImage.setVisibility(View.VISIBLE);
        }

        if(message.isWaitingToBeDeleted()){
            progressUploadImage.setImageResource(R.drawable.delete_outline);
            progressUploadImage.setVisibility(View.VISIBLE);
        }
        else{
            progressUploadImage.setVisibility(View.GONE);
        }
        messageText.setText(message.getMessageText());


        // Format the stored timestamp into a readable String using method.
        if(message.getSentAt() != null){
            timeText.setText(DateFormatter.format(new Date(message.getSentAt()), DateFormatter.Template.TIME));
        }
//        timeText.setText(DateHelper.getPrettyDate(message.getSentAt()));

//        if (message.getNotify() == 1) {
//            messageText.setBackgroundResource(R.drawable.message_sent_high);
//        } else {
//            messageText.setBackgroundResource(R.drawable.message_sent);
//        }

//        userAlias.setText(message.getGroupCreatorAlias());
//        if (message.getIsAdmin() != null && message.getIsAdmin().equalsIgnoreCase("true")) {
//            //user is an admin
//            if (message.getRole() != null && !message.getRole().isEmpty()) {
//                userRole.setText(message.getRole());  //there is a role to set
//            }
//        }

        //load groupImage
//        loadImage(message.getSentBy());
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


    private final void applyStyle(MessagesListStyle style) {
        if (timeText != null) {
            timeText.setTextColor(style.getOutcomingTimeTextColor());
            timeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getOutcomingTimeTextSize());
            timeText.setTypeface(timeText.getTypeface(), style.getOutcomingTimeTextStyle());
        }
        if (bubble != null) {
            bubble.setPadding(style.getOutcomingDefaultBubblePaddingLeft(),
                    style.getOutcomingDefaultBubblePaddingTop(),
                    style.getOutcomingDefaultBubblePaddingRight(),
                    style.getOutcomingDefaultBubblePaddingBottom());
            ViewCompat.setBackground(bubble, style.getOutcomingBubbleDrawable());
        }

        if (messageText != null) {
            messageText.setTextColor(style.getOutcomingTextColor());
            messageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getOutcomingTextSize());
            messageText.setTypeface(messageText.getTypeface(), style.getOutcomingTextStyle());
            messageText.setAutoLinkMask(style.getTextAutoLinkMask());
            messageText.setLinkTextColor(style.getOutcomingTextLinkColor());
//            configureLinksBehavior(messageText);
        }
    }


//    protected void configureLinksBehavior(final TextView text) {
//        text.setLinksClickable(false);
//        text.setMovementMethod(new LinkMovementMethod() {
//            @Override
//            public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
//                boolean result = false;
//                if (!MessagesListAdapter.isSelectionModeEnabled) {
//                    result = super.onTouchEvent(widget, buffer, event);
//                }
//                itemView.onTouchEvent(event);
//                return result;
//            }
//        });
//    }
}
