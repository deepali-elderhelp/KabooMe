package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.GeneralHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;


public class ImageRcvdMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMImageRcvdMsgHolder";

    private ImageView download, alert;
    private TextView messageText, timeText, aliasText, roleText, newMessageHeader;
    private ImageView urgentImage, imageAttached;
    private CircleImageView profileImage;
    private ProgressBar attachmentLoadProgess;
    private Context context;
    private DownloadClickListener downloadClickListener;
//    private RequestManager requestManager;
    private Handler handler;
//    private PlayerView audioPlayer;



//    ImageRcvdMessageHolder(View itemView, RequestManager requestManager, Context context) {
//    ImageRcvdMessageHolder(View itemView, Context context) {
    ImageRcvdMessageHolder(View itemView, Context context) {
        super(itemView);

        this.context = context;
//        this.requestManager = requestManager;
        messageText =  itemView.findViewById(R.id.image_message_rcvd);
        timeText =  itemView.findViewById(R.id.image_time_rcvd);
        aliasText = itemView.findViewById(R.id.image_user_alias_rcvd);
        roleText = itemView.findViewById(R.id.image_user_role_rcvd);

        urgentImage = itemView.findViewById(R.id.urgent_image_rcvd);
        alert = itemView.findViewById(R.id.alert_image_rcvd);
        newMessageHeader = itemView.findViewById(R.id.imageNewMessagesLabel_rcvd);

        download =  itemView.findViewById(R.id.image_bubble_download_rcvd);

        imageAttached = itemView.findViewById(R.id.image_bubble_image_rcvd);

        profileImage = itemView.findViewById(R.id.image_user_profile_rcvd);


        attachmentLoadProgess = itemView.findViewById(R.id.image_load_progress_rcvd);

    }

    public void onBind(final Message message, Handler handler, final View.OnClickListener messageClickListener,
                       View.OnLongClickListener messageLongClickListener,
                       final DownloadClickListener downloadClickListener, boolean showNewMessageHeader) {

        this.handler = handler;
        itemView.setOnClickListener(messageClickListener);
        itemView.setOnLongClickListener(messageLongClickListener);
        this.downloadClickListener = downloadClickListener;

        Bitmap tnBlob = GeneralHelper.base64ToBitmap(message.getTnBlob());
        if(tnBlob != null) {
            imageAttached.setImageBitmap(tnBlob);
        }


//        ImageHelper.loadUserImage(message.getSentBy(), null, requestManager, handler, profileImage, null);
//        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(itemView.getContext(),R.dimen.message_recvd_sender_image_width, message.getAlias());
//        ImageHelper.loadUserImage(message.getSentBy(), message.getSentByImageTS(),
//                ImageHelper.getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, profileImage, null);
        Drawable imageErrorAndPlaceholder = itemView.getContext().getResources().getDrawable(R.drawable.bs_profile);
        ImageHelper.getInstance().loadGroupUserImage(message.getGroupId(), ImageTypeConstants.THUMBNAIL, message.getSentBy(), message.getSentByImageTS(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null);

        if (showNewMessageHeader) {
            newMessageHeader.setVisibility(View.VISIBLE);
        } else {
            newMessageHeader.setVisibility(View.GONE);
        }

        if (message.getDeleted()) {
            imageAttached.setVisibility(View.GONE);
            download.setVisibility(View.GONE);
            attachmentLoadProgess.setVisibility(View.GONE);
            messageText.setText("Message Deleted");

            aliasText.setText(message.getAlias());
            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
                roleText.setText(" - " + message.getRole());
            }

            if (message.getSentAt() != null) {
                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
            }

        } else {
            imageAttached.setVisibility(View.VISIBLE);
            messageText.setText(message.getMessageText());

            aliasText.setText(message.getAlias());
            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
                roleText.setText(" - " + message.getRole());
            }

            if (message.getSentAt() != null) {
                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
            }

            //first thing first, if any upload or download happening, show progress bar
            if (message.isAttachmentLoadingGoingOn()) {
                alert.setVisibility(View.GONE);
                attachmentLoadProgess.setVisibility(View.VISIBLE);
                int progress = message.getLoadingProgress();
                Log.d(TAG, "Progress - "+progress);
                attachmentLoadProgess.setProgress(progress);
            } else {
                attachmentLoadProgess.setVisibility(View.GONE);
                String attachmentUriString = message.getAttachmentUri();
                Uri attachmentUri = null;
                if(attachmentUriString != null) {
                    attachmentUri = Uri.parse(attachmentUriString);
                }
//            if(attachmentUri == null) {
//                String displayName = message.getGroupId() + "_Group_" + message.getMessageId() + message.getAttachmentExtension();
//                attachmentUri = MediaHelper.getMediaImageUri(context.getContentResolver(), displayName);
//            }

                Log.d(TAG, "Uri returned "+attachmentUri);


//            String attachmentMime = message.getAttachmentMime();
//            String displayName =  message.getGroupId()+"_Group_"+message.getMessageId()+message.getAttachmentExtension();
//
//            Uri uriReturned = MediaHelper.getMediaImageUri(context.getContentResolver(), displayName);

//            Log.d(TAG, "Uri returned "+uriReturned);
//            if(uriReturned != null) {

                if(attachmentUri != null && MediaHelper.doesUriFileExists(context.getContentResolver(), attachmentUri)){
                    ImageHelper.getInstance().getRequestManager(itemView.getContext())
                            .asBitmap()
                            .load(attachmentUri)
                            .into(imageAttached);

                    download.setVisibility(View.GONE);
                    alert.setVisibility(View.GONE);

//                    if (!message.getAttachmentUploaded()) { //not uploaded
//                        download.setVisibility(View.GONE);
//
//                    } else { //image is uploaded and file exists
//                        download.setVisibility(View.GONE);
//
//                    }

                }
                else{
                    if (message.getAttachmentUploaded()) {
                        //not available locally, but uploaded
                        download.setVisibility(View.VISIBLE);
                        alert.setVisibility(View.GONE);
                    } else { //file is not uploaded and also does not exist
                        alert.setVisibility(View.VISIBLE);
                        download.setVisibility(View.GONE);
                        //play will not do anything, disable it
                        itemView.setOnClickListener(null);
                        //maybe show user that it does not exist anywhere
                    }
                }

            }

//            File appDir = FileUtils.getAppDirForMime(attachmentMime, false);
//
//            if (appDir != null) {
//                File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getSentTo(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
////                File attachmentFile = new File(appDir, message.getMessageId() + message.getAttachmentExtension());
//                if (attachmentFile.exists()) {
//
//                    ImageHelper.getInstance().getRequestManager(itemView.getContext())
//                            .asBitmap()
//                            .load(attachmentFile)
//                            .into(imageAttached);
//
//                    download.setVisibility(View.GONE);
//
//                    if (!message.getAttachmentUploaded()) { //not uploaded
//                        download.setVisibility(View.GONE);
//
//                    } else { //image is uploaded and file exists
//                        download.setVisibility(View.GONE);
//
//                    }
//                } else {
//                    //file does not exist anymore - may be user deleted
////                    attachmentLoadProgess.setVisibility(View.GONE);
//                    if (message.getAttachmentUploaded()) {
//                        //not available locally, but uploaded
//                        download.setVisibility(View.VISIBLE);
//                    } else { //file is not uploaded and also does not exist
//                        download.setVisibility(View.GONE);
//                        //play will not do anything, disable it
//                        itemView.setOnClickListener(null);
//                        //maybe show user that it does not exist anywhere
//                    }
//
//                }
//            } else {
//                //the appdir does not exist
//                if (message.getAttachmentUploaded()) { //not available locally, but uploaded
//                    download.setVisibility(View.VISIBLE);
//                } else {
//                    download.setVisibility(View.GONE);
//                    //play will not do anything, disable it
//                    itemView.setOnClickListener(null);
//                    //maybe show user that it does not exist anywhere
//                }
//
//            }

            if (message.getNotify() == 1) {
                urgentImage.setVisibility(View.VISIBLE);
            } else {
                urgentImage.setVisibility(View.INVISIBLE);
            }

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadClickListener.onDownloadClicked(message);
                }
            });

            alert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Error accessing file", Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

}
