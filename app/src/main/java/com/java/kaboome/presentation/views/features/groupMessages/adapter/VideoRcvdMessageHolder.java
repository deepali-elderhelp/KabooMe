package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.GeneralHelper;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.images.ImageLinkHandler;
import com.java.kaboome.presentation.images.S3LoadingHelper;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class VideoRcvdMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMVideoRcvdMsgHolder";
    private final FrameLayout videoPlayFrame;
//    private final ImageView alert;
    private final FrameLayout videoBubbleFrame;

    private ImageView download;
    private TextView messageText, timeText, aliasText, roleText, newMessageHeader;
    private ImageView urgentImage, imageAttached, videoPlay;
    private CircleImageView profileImage;
    private ProgressBar attachmentLoadProgess;
//    private Context context;
    private DownloadClickListener downloadClickListener;
//    RequestManager requestManager;
    private Handler handler;
    private MediaPlayClickListener mediaPlayClickListener;
    private Uri pathToVideo;
    private String errorMessage;


//    VideoRcvdMessageHolder(View itemView, RequestManager requestManager, Context context) {
    VideoRcvdMessageHolder(View itemView) {
        super(itemView);

//        this.context = context;
//        this.requestManager = requestManager;
        messageText = itemView.findViewById(R.id.video_message_rcvd);
        timeText = itemView.findViewById(R.id.video_time_rcvd);
        aliasText = itemView.findViewById(R.id.video_user_alias_rcvd);
        roleText = itemView.findViewById(R.id.video_user_role_rcvd);

        profileImage = itemView.findViewById(R.id.image_user_profile_rcvd);

        urgentImage = itemView.findViewById(R.id.video_urgent_rcvd);
        newMessageHeader = itemView.findViewById(R.id.videoNewMessagesLabel_rcvd);

        download = itemView.findViewById(R.id.video_bubble_download_rcvd);

        imageAttached = itemView.findViewById(R.id.video_bubble_image_rcvd);
        videoPlayFrame = itemView.findViewById(R.id.video_play_frame_rcvd);
        videoBubbleFrame = itemView.findViewById(R.id.video_bubble_frame_rcvd);

        videoPlay = itemView.findViewById(R.id.video_play_rcvd);
//        alert = itemView.findViewById(R.id.video_alert_rcvd);

        attachmentLoadProgess = itemView.findViewById(R.id.video_upload_progress_rcvd);
        attachmentLoadProgess.setMax(100);

    }

    public void onBind(final Message message, Handler handler, final MediaPlayClickListener mediaPlayClickListener,
                       View.OnLongClickListener messageLongClickListener,
                       final DownloadClickListener downloadClickListener, boolean showNewMessageHeader) {


        itemView.setOnLongClickListener(messageLongClickListener);
        this.downloadClickListener = downloadClickListener;
        this.mediaPlayClickListener = mediaPlayClickListener;

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
        ImageHelper.getInstance().loadGroupUserImage(message.getGroupId(), ImageTypeConstants.MAIN,message.getSentBy(), message.getSentByImageTS(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null, false);

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
            messageText.setText("Message Deleted");
            aliasText.setText(message.getAlias());
            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
                roleText.setText(" - " + message.getRole());
            }

            if (message.getSentAt() != null) {
                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
            }

            //everything else GONE
            attachmentLoadProgess.setVisibility(View.GONE);
            download.setVisibility(View.GONE);
            imageAttached.setVisibility(View.GONE);
            videoBubbleFrame.setVisibility(View.GONE);
        } else {

            //first enable defaults
            imageAttached.setVisibility(View.VISIBLE);
            videoBubbleFrame.setVisibility(View.VISIBLE);

            if (message.getHasAttachment() != null && message.getHasAttachment()) {

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
                    attachmentLoadProgess.setVisibility(View.VISIBLE);
                    int progress = message.getLoadingProgress();
                    attachmentLoadProgess.setProgress(progress);
                    download.setVisibility(View.GONE);
//                    alert.setVisibility(View.GONE);
                } else {
                    attachmentLoadProgess.setVisibility(View.GONE);
                    String attachmentUriString = message.getAttachmentUri();
                    Uri attachmentUri = null;
                    if(attachmentUriString != null) {
                        attachmentUri = Uri.parse(attachmentUriString);
                    }
//                if(attachmentUri == null) {
//                    String displayName = message.getGroupId() + "_Group_" + message.getMessageId() + message.getAttachmentExtension();
//                    attachmentUri = MediaHelper.getMediaImageUri(itemView.getContext().getContentResolver(), displayName);
//                }

                    if(attachmentUri != null && MediaHelper.doesUriFileExists(itemView.getContext().getContentResolver(), attachmentUri)) {
                        pathToVideo = attachmentUri;
                        ImageHelper.getInstance().getRequestManager(itemView.getContext())
                                .asBitmap()
                                .load(attachmentUri)
                                .into(imageAttached);

                        download.setVisibility(View.GONE);

                        if (!message.getAttachmentUploaded()) { //not uploaded
                            videoPlay.setVisibility(View.GONE);
                            download.setVisibility(View.GONE);
//                            alert.setVisibility(View.VISIBLE);
                            errorMessage = "Upload of the file did not go through, please try again after sometime";

                        } else { //video is uploaded and file exists
                            download.setVisibility(View.GONE);
//                            alert.setVisibility(View.GONE);
                            videoPlay.setVisibility(View.VISIBLE);

                        }
                    }
                    else {
                        //file does not exist anymore - may be user deleted
                        if (message.getAttachmentUploaded()) {
                            //not available locally, but uploaded
                            download.setVisibility(View.VISIBLE);
                            //not available locally, but uploaded
                            getS3Url(message.getMessageId(), message.getGroupId());
                            videoPlay.setVisibility(View.VISIBLE);
                        } else { //file is not uploaded and also does not exist

                            download.setVisibility(View.GONE);
//                            alert.setVisibility(View.VISIBLE);
                            errorMessage = "File does not exist anymore";
                            videoPlay.setVisibility(View.GONE);
                        }

                    }

                }



//                String attachmentMime = message.getAttachmentMime();
//
//                File appDir = FileUtils.getAppDirForMime(attachmentMime, false);
//
//                if (appDir != null) {
//                    File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getSentTo(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//                    if (attachmentFile.exists()) {
//
//                        pathToVideo = FileUtils.getUri(attachmentFile);
////                        requestManager
//                        ImageHelper.getInstance().getRequestManager(itemView.getContext())
//                                .asBitmap()
//                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(itemView.getContext())))
//                                .load(attachmentFile)
//                                .into(imageAttached);
//
//                         //audio is uploaded and file exists
//                        download.setVisibility(View.GONE);
//                        alert.setVisibility(View.GONE);
//                        videoPlay.setVisibility(View.VISIBLE);
//
//                    } else {
//                        //file does not exist anymore - may be user deleted
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
//                } else { //the appdir does not exist
//
//                    if (message.getAttachmentUploaded()) {
//                        //not available locally, but uploaded
//                        getS3Url(message.getMessageId(), message.getGroupId());
//                    } else {//file is not uploaded and also does not exist locally
//                        download.setVisibility(View.GONE);
//                        alert.setVisibility(View.VISIBLE);
//                        errorMessage = "File does not exist either on server or local anymore";
//                        videoPlay.setVisibility(View.GONE);
//                    }

                videoPlayFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayClickListener.onMediaPlayClicked(message, pathToVideo);
                    }
                });

                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(itemView.getContext())
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



//                alert.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(itemView.getContext(), errorMessage == null ? "Error accessing file" : errorMessage, Toast.LENGTH_SHORT).show();
//                    }
//                });

            } //if attachment
        }

//        this.handler = handler;
//        itemView.setOnClickListener(messageClickListener);
//        itemView.setOnLongClickListener(messageLongClickListener);
//        this.downloadClickListener = downloadClickListener;
//
//        ImageHelper.loadUserImage(message.getSentBy(), null, requestManager, handler, profileImage, null);
//
//
//        if (showNewMessageHeader) {
//            newMessageHeader.setVisibility(View.VISIBLE);
//        } else {
//            newMessageHeader.setVisibility(View.GONE);
//        }
//
//        if(message.getDeleted()){
//            messageText.setText("Message Deleted");
//            aliasText.setText(message.getAlias());
//            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
//                roleText.setText(" - " + message.getRole());
//            }
//
//            if (message.getSentAt() != null) {
//                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
//            }
//
//            //everything else GONE
//            attachmentLoadProgess.setVisibility(View.GONE);
//            download.setVisibility(View.GONE);
//            imageAttached.setVisibility(View.GONE);
//            videoPlay.setVisibility(View.GONE);
//        }
//        else {
//
//            //first enable defaults
//            imageAttached.setVisibility(View.VISIBLE);
//            videoPlay.setVisibility(View.VISIBLE);
//
//            if (message.getHasAttachment() != null && message.getHasAttachment()) {
//
//                messageText.setText(message.getMessageText());
//                aliasText.setText(message.getAlias());
//                if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
//                    roleText.setText(" - " + message.getRole());
//                }
//
//                if (message.getSentAt() != null) {
//                    timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
//                }
//
//                //first thing first, if any upload or download happening, show progress bar
//                if (message.isAttachmentLoadingGoingOn()) {
//                    attachmentLoadProgess.setVisibility(View.VISIBLE);
//                    int progress = message.getLoadingProgress();
//                    attachmentLoadProgess.setProgress(progress);
//                } else {
//                    attachmentLoadProgess.setVisibility(View.GONE);
//                }
//
//                String attachmentMime = message.getAttachmentMime();
//
//                File appDir = FileUtils.getAppDirForMime(attachmentMime, false);
//
//                if (appDir != null) {
////                    File attachmentFile = new File(appDir, message.getMessageId() + message.getAttachmentExtension());
//                    File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//                    if (attachmentFile.exists()) {
//
//                        requestManager
//                                .asBitmap()
//                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(context)))
//                                .load(attachmentFile)
//                                .into(imageAttached);
//
//                        download.setVisibility(View.GONE);
//
//                        if (!message.getAttachmentUploaded()) { //not uploaded
//                            download.setVisibility(View.GONE);
//
//                        } else { //video is uploaded and file exists
//                            download.setVisibility(View.GONE);
//
//                        }
//                    } else {
//                        //file does not exist anymore - may be user deleted
////                    attachmentLoadProgess.setVisibility(View.GONE);
//                        if (message.getAttachmentUploaded()) {
//                            //not available locally, but uploaded
//                            //would play from there
////                        getS3Url(message.getMessageId());
//                            download.setVisibility(View.VISIBLE);
//                            videoPlay.setVisibility(View.VISIBLE);
//                        } else { //file is not uploaded and also does not exist
//                            download.setVisibility(View.GONE);
//                            //play will not do anything, disable it
//                            itemView.setOnClickListener(null);
//                            videoPlay.setVisibility(View.GONE);
//                        }
//
//                    }
//                } else {
//                    //the appdir does not exist
//                    if (message.getAttachmentUploaded()) { //not available locally, but uploaded
//                        download.setVisibility(View.VISIBLE);
//                        videoPlay.setVisibility(View.VISIBLE);
//                    } else { //not uploaded either
//                        download.setVisibility(View.GONE);
//                        itemView.setOnClickListener(null);
//                        videoPlay.setVisibility(View.GONE);
//                    }
//
//                }
//
//                if (message.getNotify() == 1) {
//                    urgentImage.setVisibility(View.VISIBLE);
//                } else {
//                    urgentImage.setVisibility(View.INVISIBLE);
//                }
//
//                download.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
//                                .setTitle("Download Audio File")
//                                .setMessage("Do you want to download this Video File for offline viewing?")
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        downloadClickListener.onDownloadClicked(message);
//                                    }
//                                })
//                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        AlertDialog alertDialog = alertDialogBuilder.create();
//                        alertDialog.show();
//
//                    }
//                });
//
//
//            }
//        }
//    }
//
//

    }

    private void getS3Url(String messageId, String groupId) {
        String key = ImagesUtilHelper.getMessageAttachmentKeyName(groupId, messageId);
        S3LoadingHelper.getPresignedImageLink(key, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {

                pathToVideo = Uri.parse(url.toString());
                if(pathToVideo != null){
                    download.setVisibility(View.VISIBLE);
//                    alert.setVisibility(View.GONE);
                    videoPlay.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onImageLinkError(Exception e) {
                pathToVideo = null;
                download.setVisibility(View.GONE);
//                alert.setVisibility(View.VISIBLE);
                errorMessage = "File does not exist either on server or local anymore";
                videoPlay.setVisibility(View.GONE);
            }
        });
    }




}
