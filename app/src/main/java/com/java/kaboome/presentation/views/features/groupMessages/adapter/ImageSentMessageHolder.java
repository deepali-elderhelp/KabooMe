package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.GeneralHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.images.BlurTransformation;
import com.java.kaboome.presentation.images.ImageHelper;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;


public class ImageSentMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMImageSentMsgHolder";

    private FrameLayout imageBubble;
    private ImageView download, upload, alert;
    private TextView messageText, timeText, yourAliasText, yourRoleText, newMessageHeader;
    private ImageView urgentImage, imageAttached;
    private ProgressBar attachmentLoadProgess;
    private CircleImageView profileImage;
    private Context context;
    private DownloadClickListener downloadClickListener;
    private UploadClickListener uploadClickListener;
    RequestManager requestManager;
//    private PlayerView audioPlayer;



    ImageSentMessageHolder(View itemView, RequestManager requestManager, Context context) {
        super(itemView);

        this.context = context;
        this.requestManager = requestManager;
        messageText =  itemView.findViewById(R.id.image_message_sent);
        timeText =  itemView.findViewById(R.id.image_time_sent);
        yourAliasText = itemView.findViewById(R.id.image_user_alias_sent);
        yourRoleText = itemView.findViewById(R.id.image_user_role_sent);
        profileImage = (CircleImageView) itemView.findViewById(R.id.image_user_profile);

        urgentImage = itemView.findViewById(R.id.urgent_image_sent);
        alert = itemView.findViewById(R.id.image_sent_alert);
        newMessageHeader = itemView.findViewById(R.id.imageNewMessagesLabel);

        download =  itemView.findViewById(R.id.image_bubble_download);
        upload = itemView.findViewById(R.id.image_bubble_upload);

        imageAttached = itemView.findViewById(R.id.image_bubble_image);
        imageBubble = itemView.findViewById(R.id.image_bubble_frame);


        attachmentLoadProgess = itemView.findViewById(R.id.image_load_progress);

    }

    public void onBind(final Message message, Handler handler, View.OnClickListener messageClickListener,
                       View.OnLongClickListener messageLongClickListener,
                       final DownloadClickListener downloadClickListener,
                       final UploadClickListener uploadClickListener, boolean showNewMessageHeader) {

        itemView.setOnClickListener(messageClickListener);
        itemView.setOnLongClickListener(messageLongClickListener);
        this.downloadClickListener = downloadClickListener;
        this.uploadClickListener = uploadClickListener;

        Bitmap tnBlob = GeneralHelper.base64ToBitmap(message.getTnBlob());
        if(tnBlob != null) {
            imageAttached.setImageBitmap(tnBlob);
        }

        Drawable imageErrorAndPlaceholder = itemView.getContext().getResources().getDrawable(R.drawable.account_gray_192);

        ImageHelper.getInstance().loadGroupUserImage(message.getGroupId(), ImageTypeConstants.THUMBNAIL, message.getSentBy(), message.getSentByImageTS(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null);

        if (showNewMessageHeader) {
            newMessageHeader.setVisibility(View.VISIBLE);
        } else {
            newMessageHeader.setVisibility(View.GONE);
        }

        if (message.getNotify() == 1) {
            urgentImage.setVisibility(View.VISIBLE);
        } else {
            urgentImage.setVisibility(View.INVISIBLE);
        }

        if (message.getDeleted()) {
            urgentImage.setVisibility(View.INVISIBLE);
            imageBubble.setVisibility(View.GONE);
//            imageAttached.setVisibility(View.GONE);
            download.setVisibility(View.GONE);
            upload.setVisibility(View.GONE);
            attachmentLoadProgess.setVisibility(View.GONE);
            messageText.setText("Message Deleted");

            yourAliasText.setText(message.getAlias());
            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
                yourRoleText.setText(" - " + message.getRole());
            }

            if (message.getSentAt() != null) {
                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
            }

        } else {
            imageBubble.setVisibility(View.VISIBLE);
            messageText.setText(message.getMessageText());
//        }

//        if (message.getHasAttachment() != null && message.getHasAttachment()) {


            yourAliasText.setText(message.getAlias());
            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
                yourRoleText.setText(" - " + message.getRole());
            }

            if (message.getSentAt() != null) {
                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
            }

            //first thing first, if any upload or download happening, show progress bar
            if (message.isAttachmentLoadingGoingOn()) {

                alert.setVisibility(View.GONE);
                upload.setVisibility(View.GONE);
                attachmentLoadProgess.setVisibility(View.VISIBLE);
                int progress = message.getLoadingProgress();
                Log.d(TAG, "Progress - "+progress);
                attachmentLoadProgess.setProgress(progress);
            } else {
                attachmentLoadProgess.setVisibility(View.GONE);
                String attachmentMime = message.getAttachmentMime();
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

                if(attachmentUri != null && MediaHelper.doesUriFileExists(context.getContentResolver(), attachmentUri)){
                    requestManager
                            .asBitmap()
                            .load(attachmentUri)
                            .into(imageAttached);

                    download.setVisibility(View.GONE);
                    alert.setVisibility(View.GONE);

                    if (!message.getAttachmentUploaded()) { //not uploaded
                        upload.setVisibility(View.VISIBLE);

                    } else { //image is uploaded and file exists
                        upload.setVisibility(View.GONE);

                    }
                }
                else {
                    //file does not exist anymore - may be user deleted
                    upload.setVisibility(View.GONE);
//                    attachmentLoadProgess.setVisibility(View.GONE);
                    if (message.getAttachmentUploaded()) {
                        //not available locally, but uploaded
                        download.setVisibility(View.VISIBLE);
                        alert.setVisibility(View.GONE);
                    } else { //file is not uploaded and also does not exist
                        download.setVisibility(View.GONE);
                        alert.setVisibility(View.VISIBLE);
                        //play will not do anything, disable it
                        itemView.setOnClickListener(null);
                        //maybe show user that it does not exist
                    }

                }
            }



//            File appDir = FileUtils.getAppDirForMime(attachmentMime, false);

//            if (appDir != null) {
//                File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getSentTo(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
////                File attachmentFile = new File(appDir, message.getMessageId() + message.getAttachmentExtension());
//                if (attachmentFile.exists()) {
//
//                    requestManager
//                            .asBitmap()
//                            .load(attachmentFile)
//                            .into(imageAttached);
//
//                    download.setVisibility(View.GONE);
//
//                    if (!message.getAttachmentUploaded()) { //not uploaded
//                        download.setVisibility(View.GONE);
//                        upload.setVisibility(View.VISIBLE);
//
//                    } else { //audio is uploaded and file exists
//                        download.setVisibility(View.GONE);
//                        upload.setVisibility(View.GONE);
//
//                    }
//                } else {
//                    //file does not exist anymore - may be user deleted
//                    upload.setVisibility(View.GONE);
////                    attachmentLoadProgess.setVisibility(View.GONE);
//                    if (message.getAttachmentUploaded()) {
//                        //not available locally, but uploaded
//                        download.setVisibility(View.VISIBLE);
//                    } else { //file is not uploaded and also does not exist
//                        download.setVisibility(View.GONE);
//                        //play will not do anything, disable it
//                        itemView.setOnClickListener(null);
//                        //maybe show user that it does not exist
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
//                    //maybe show user that it does not exist
//                }
//
//            }


            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadClickListener.onDownloadClicked(message);
                }
            });

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getSentTo(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//                    File appDir = FileUtils.getAppDirForMime(message.getAttachmentMime(), false);
//                    File attachmentFile = new File(appDir, message.getMessageId() + message.getAttachmentExtension());
                    if (attachmentFile.exists()) {
                        uploadClickListener.onUploadClicked(message, attachmentFile);
                    }
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
