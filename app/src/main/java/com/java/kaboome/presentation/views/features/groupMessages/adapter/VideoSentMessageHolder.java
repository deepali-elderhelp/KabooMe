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
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.GeneralHelper;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.images.ImageLinkHandler;
import com.java.kaboome.presentation.images.S3LoadingHelper;

import java.io.File;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class VideoSentMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMVideoSentMsgHolder";
    private final FrameLayout videoBubbleFrame;

    private ImageView download, upload;
    private TextView messageText, timeText, yourAliasText, yourRoleText, newMessageHeader;
    private ImageView urgentImage, imageAttached, videoPlay, alert;
    FrameLayout videoPlayFrame;
    private CircleImageView profileImage;
    private ProgressBar attachmentLoadProgess;
    private Context context;
    private DownloadClickListener downloadClickListener;
    private UploadClickListener uploadClickListener;
    private MediaPlayClickListener mediaPlayClickListener;
    RequestManager requestManager;
    private Uri pathToVideo;
    private String errorMessage;
//    private PlayerView audioPlayer;



    VideoSentMessageHolder(View itemView, RequestManager requestManager, Context context) {
        super(itemView);

        this.context = context;
        this.requestManager = requestManager;
        messageText =  itemView.findViewById(R.id.video_message_sent);
        timeText =  itemView.findViewById(R.id.video_time_sent);
        yourAliasText = itemView.findViewById(R.id.video_user_alias_sent);
        yourRoleText = itemView.findViewById(R.id.video_user_role_sent);
        profileImage = (CircleImageView) itemView.findViewById(R.id.video_user_profile);

        urgentImage = itemView.findViewById(R.id.urgent_video_sent);
        newMessageHeader = itemView.findViewById(R.id.videoNewMessagesLabel);

        download =  itemView.findViewById(R.id.video_bubble_download);
        upload = itemView.findViewById(R.id.video_bubble_upload);

        imageAttached = itemView.findViewById(R.id.video_bubble_image);
        videoPlayFrame = itemView.findViewById(R.id.video_play_frame);
        videoBubbleFrame = itemView.findViewById(R.id.video_bubble_frame);

        videoPlay = itemView.findViewById(R.id.video_play);
        alert = itemView.findViewById(R.id.video_alert);

        attachmentLoadProgess = itemView.findViewById(R.id.video_upload_progress);
        attachmentLoadProgess.setMax(100);

    }

    public void onBind(final Message message, Handler handler, final MediaPlayClickListener mediaPlayClickListener,
                       View.OnLongClickListener messageLongClickListener,
                       final DownloadClickListener downloadClickListener,
                       final UploadClickListener uploadClickListener, boolean showNewMessageHeader) {

//        itemView.setOnClickListener(messageClickListener);
        itemView.setOnLongClickListener(messageLongClickListener);
        this.downloadClickListener = downloadClickListener;
        this.uploadClickListener = uploadClickListener;
        this.mediaPlayClickListener = mediaPlayClickListener;

        Bitmap tnBlob = GeneralHelper.base64ToBitmap(message.getTnBlob());
        if(tnBlob != null) {
            imageAttached.setImageBitmap(tnBlob);
        }

        if (showNewMessageHeader) {
            newMessageHeader.setVisibility(View.VISIBLE);
        } else {
            newMessageHeader.setVisibility(View.GONE);
        }

        Drawable imageErrorAndPlaceholder = itemView.getContext().getResources().getDrawable(R.drawable.account_gray_192);

        ImageHelper.getInstance().loadGroupUserImage(message.getGroupId(), ImageTypeConstants.MAIN,message.getSentBy(), message.getSentByImageTS(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null, false);

        if (message.getNotify() == 1) {
            urgentImage.setVisibility(View.VISIBLE);
        } else {
            urgentImage.setVisibility(View.INVISIBLE);
        }

        if(message.getDeleted()){
            urgentImage.setVisibility(View.INVISIBLE);
            messageText.setText("Message Deleted");
            yourAliasText.setText(message.getAlias());
            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
                yourRoleText.setText(" - " + message.getRole());
            }

            if (message.getSentAt() != null) {
                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
            }

            //everything else GONE
            attachmentLoadProgess.setVisibility(View.GONE);
            download.setVisibility(View.GONE);
            upload.setVisibility(View.GONE);
            imageAttached.setVisibility(View.GONE);
            videoBubbleFrame.setVisibility(View.GONE);
        }
        else {

            //first enable defaults
            imageAttached.setVisibility(View.VISIBLE);
            videoBubbleFrame.setVisibility(View.VISIBLE);

            if (message.getHasAttachment() != null && message.getHasAttachment()) {

                messageText.setText(message.getMessageText());
                yourAliasText.setText(message.getAlias());
                if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
                    yourRoleText.setText(" - " + message.getRole());
                }

                if (message.getSentAt() != null) {
                    timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
                }

                //first thing first, if any upload or download happening, show progress bar
                if (message.isAttachmentLoadingGoingOn()) {
                    videoPlayFrame.setVisibility(View.GONE);
                    alert.setVisibility(View.GONE);
                    download.setVisibility(View.GONE);
                    upload.setVisibility(View.GONE);
                    attachmentLoadProgess.setVisibility(View.VISIBLE);
                    int progress = message.getLoadingProgress();
                    Log.d(TAG, "Progress - "+progress);
                    attachmentLoadProgess.setProgress(progress);
                } else {
                    videoPlayFrame.setVisibility(View.VISIBLE);
                    attachmentLoadProgess.setVisibility(View.GONE);




                String attachmentMime = message.getAttachmentMime();
                String attachmentUriString = message.getAttachmentUri();
                Uri attachmentUri = null;
                if(attachmentUriString != null) {
                    attachmentUri = Uri.parse(attachmentUriString);
                }
//                if(attachmentUri == null) {
//                    String displayName = message.getGroupId() + "_Group_" + message.getMessageId() + message.getAttachmentExtension();
//                    attachmentUri = MediaHelper.getMediaImageUri(context.getContentResolver(), displayName);
//                }

                if(attachmentUri != null && MediaHelper.doesUriFileExists(context.getContentResolver(), attachmentUri)){
                    pathToVideo = attachmentUri;
                    requestManager
                            .asBitmap()
                            .load(attachmentUri)
                            .into(imageAttached);

                    download.setVisibility(View.GONE);

                    if (!message.getAttachmentUploaded()) { //not uploaded
                        videoPlay.setVisibility(View.GONE);
                        download.setVisibility(View.GONE);
                        upload.setVisibility(View.VISIBLE);
                        alert.setVisibility(View.VISIBLE);
                        errorMessage = "Upload of the file did not go through, please try again";

                    } else { //audio is uploaded and file exists
                        download.setVisibility(View.GONE);
                        upload.setVisibility(View.GONE);
                        alert.setVisibility(View.GONE);
                        videoPlay.setVisibility(View.VISIBLE);

                    }
                }
                else {
                    //file does not exist anymore - may be user deleted
                    upload.setVisibility(View.GONE);
//                    attachmentLoadProgess.setVisibility(View.GONE);
                    if (message.getAttachmentUploaded()) {
                        //not available locally, but uploaded
                        download.setVisibility(View.VISIBLE);
                        //not available locally, but uploaded
                        getS3Url(message.getMessageId(), message.getGroupId());
                        videoPlay.setVisibility(View.VISIBLE);
                    } else { //file is not uploaded and also does not exist

                        download.setVisibility(View.GONE);
                        alert.setVisibility(View.VISIBLE);
                        errorMessage = "File does not exist either on server or local anymore";
                        videoPlay.setVisibility(View.GONE);
                    }

                }

                } //if loading not going on

//                File appDir = FileUtils.getAppDirForMime(attachmentMime, false);
//
//                if (appDir != null) {
//                    File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getSentTo(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//                    if (attachmentFile.exists()) {
//
//                        pathToVideo = FileUtils.getUri(attachmentFile);
//                        requestManager
//                                .asBitmap()
//                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(context)))
//                                .load(attachmentFile)
//                                .into(imageAttached);
//
//                        download.setVisibility(View.GONE);
//                        if (!message.getAttachmentUploaded()) { //not uploaded
//
//                            videoPlay.setVisibility(View.GONE);
//                            download.setVisibility(View.GONE);
//                            upload.setVisibility(View.VISIBLE);
//                            alert.setVisibility(View.VISIBLE);
//                            errorMessage = "Upload of the file did not go through, please try again";
//
//                        } else { //audio is uploaded and file exists
//                            download.setVisibility(View.GONE);
//                            upload.setVisibility(View.GONE);
//                            alert.setVisibility(View.GONE);
//                            videoPlay.setVisibility(View.VISIBLE);
//                        }
//
//
//                    } else {
//                        //file does not exist anymore - may be user deleted
//                        upload.setVisibility(View.GONE);
//                        if (message.getAttachmentUploaded()) {
//                            //not available locally, but uploaded
//                            //would play from there
//                            getS3Url(message.getMessageId(), message.getGroupId());
//
//                        } else { //file is not uploaded and also does not exist locally
//                            download.setVisibility(View.GONE);
//                            alert.setVisibility(View.VISIBLE);
//                            errorMessage = "File does not exist either on server or local anymore";
//                            videoPlay.setVisibility(View.GONE);
//                        }
//
//                    }
//                }
//                else{ //the appdir does not exist
//
//                    if (message.getAttachmentUploaded()) {
//                        //not available locally, but uploaded
//                        getS3Url(message.getMessageId(), message.getGroupId());
//                    }
//                    else{//file is not uploaded and also does not exist locally
//                        download.setVisibility(View.GONE);
//                        alert.setVisibility(View.VISIBLE);
//                        errorMessage = "File does not exist either on server or local anymore";
//                        videoPlay.setVisibility(View.GONE);
//                    }
//
//                }



                videoPlayFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayClickListener.onMediaPlayClicked(message, pathToVideo);
                    }
                });

                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                                .setTitle("Download Audio File")
                                .setMessage("Do you want to download this Video File for offline viewing?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        downloadClickListener.onDownloadClicked(message);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

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
                        Toast.makeText(context, errorMessage == null ? "Error accessing file":errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }


    private void getS3Url(String messageId, String groupId) {
        String key = ImagesUtilHelper.getMessageAttachmentKeyName(groupId, messageId);
        S3LoadingHelper.getPresignedImageLink(key, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {

                pathToVideo = Uri.parse(url.toString());
                if(pathToVideo != null){
                    download.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.GONE);
                    alert.setVisibility(View.GONE);
                    videoPlay.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onImageLinkError(Exception e) {
                pathToVideo = null;
                upload.setVisibility(View.GONE);
                download.setVisibility(View.GONE);
                alert.setVisibility(View.VISIBLE);
                errorMessage = "File does not exist either on server or local anymore";
                videoPlay.setVisibility(View.GONE);
            }
        });
    }



}
