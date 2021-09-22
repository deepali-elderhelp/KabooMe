package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.images.ImageLinkHandler;
import com.java.kaboome.presentation.images.S3LoadingHelper;

import java.io.File;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class AudioSentMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMAudioSentMsgHolder";
    private final CircleImageView profileImage;

    private TextView messageText, timeText, yourAliasText, yourRoleText, newMessageHeader;
    private ImageView urgentImage;
    private ImageView download, upload, alert, videoPlay;
    private ProgressBar attachmentLoadProgess;
    private FrameLayout playAudioFrame;
    private Context context;
    private DownloadClickListener downloadClickListener;
    private UploadClickListener uploadClickListener;
    private MediaPlayClickListener mediaPlayClickListener;
    private String errorMessage;
    private Uri audioUri;


    AudioSentMessageHolder(View itemView, Context context) {
        super(itemView);

        this.context = context;
        messageText = itemView.findViewById(R.id.audio_message_sent);
        timeText = itemView.findViewById(R.id.audio_time_sent);
        yourAliasText = itemView.findViewById(R.id.audio_user_alias_sent);
        yourRoleText = itemView.findViewById(R.id.audio_user_role_sent);
        profileImage = itemView.findViewById(R.id.audio_user_profile);

        urgentImage = itemView.findViewById(R.id.audio_urgent_sent);
        newMessageHeader = itemView.findViewById(R.id.audioNewMessagesLabel);
        playAudioFrame = itemView.findViewById(R.id.audio_play_frame);

        download = itemView.findViewById(R.id.audio_download);
        upload = itemView.findViewById(R.id.audio_upload);
        alert = itemView.findViewById(R.id.audio_alert);

        videoPlay = itemView.findViewById(R.id.video_play);

        attachmentLoadProgess = itemView.findViewById(R.id.audio_upload_progress);
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

        if (showNewMessageHeader) {
            newMessageHeader.setVisibility(View.VISIBLE);
        } else {
            newMessageHeader.setVisibility(View.GONE);
        }

        Drawable imageErrorAndPlaceholder = itemView.getContext().getResources().getDrawable(R.drawable.account_gray_192);

        ImageHelper.getInstance().loadGroupUserImage(message.getGroupId(), ImageTypeConstants.THUMBNAIL,message.getSentBy(), message.getSentByImageTS(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null);

        yourAliasText.setText(message.getAlias());
        if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
            yourRoleText.setText(" - " + message.getRole());
        }

        if (message.getSentAt() != null) {
            timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
        }

        if (message.getNotify() == 1) {
            urgentImage.setVisibility(View.VISIBLE);
        } else {
            urgentImage.setVisibility(View.INVISIBLE);
        }

        if (message.getDeleted()) {
            urgentImage.setVisibility(View.INVISIBLE);
            attachmentLoadProgess.setVisibility(View.GONE);
            playAudioFrame.setVisibility(View.GONE);
            messageText.setText("Message Deleted");
            download.setVisibility(View.GONE);
            errorMessage = "Message has been deleted";

        } else {

//            playAudioFrame.setVisibility(View.VISIBLE);
            messageText.setText(message.getMessageText());

            //first thing first, if any upload or download happening, show progress bar
            if (message.isAttachmentLoadingGoingOn()) {

                playAudioFrame.setVisibility(View.GONE);
                alert.setVisibility(View.GONE);
                upload.setVisibility(View.GONE);
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
//                if(attachmentUri == null) {
//                    String displayName = message.getGroupId() + "_Group_" + message.getMessageId() + message.getAttachmentExtension();
//                    attachmentUri = MediaHelper.getMediaImageUri(context.getContentResolver(), displayName);
//                }

                if(attachmentUri != null && MediaHelper.doesUriFileExists(context.getContentResolver(), attachmentUri)){ //this may not be a proper check of the the uri available - check that
                    audioUri = attachmentUri;

                    download.setVisibility(View.GONE);


                    if (!message.getAttachmentUploaded()) { //not uploaded
                        playAudioFrame.setVisibility(View.VISIBLE);
                        playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.video_play_background));
                        upload.setVisibility(View.VISIBLE);
                        videoPlay.setVisibility(View.GONE);
                        alert.setVisibility(View.VISIBLE);
                        errorMessage = "Upload of the file did not go through, please try again";

                    } else { //audio is uploaded and file exists
                        upload.setVisibility(View.GONE);
                        alert.setVisibility(View.GONE);
                        playAudioFrame.setVisibility(View.VISIBLE);
                        playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.audio_background));
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
            }




        }





//        String attachmentMime = message.getAttachmentMime();
//
//        File appDir = FileUtils.getAppDirForMime(attachmentMime, false);
//
//        if (appDir != null) {
//            File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getSentTo(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//            if (attachmentFile.exists()) {
//
//                audioUri = FileUtils.getUri(attachmentFile);
//                download.setVisibility(View.GONE);
//                if (!message.getAttachmentUploaded()) { //not uploaded
//                    playAudioFrame.setVisibility(View.VISIBLE);
//                    playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.video_play_background));
//                    download.setVisibility(View.GONE);
//                    upload.setVisibility(View.VISIBLE);
//                    videoPlay.setVisibility(View.GONE);
//                    alert.setVisibility(View.VISIBLE);
//                    errorMessage = "Upload of the file did not go through, please try again";
//
//                } else { //audio is uploaded and file exists
//                    download.setVisibility(View.GONE);
//                    upload.setVisibility(View.GONE);
//                    alert.setVisibility(View.GONE);
//                    playAudioFrame.setVisibility(View.VISIBLE);
//                    playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.audio_background));
//                    videoPlay.setVisibility(View.VISIBLE);
//                }
//
//
//            } else {
//                //file does not exist anymore - may be user deleted
//                upload.setVisibility(View.GONE);
//                if (message.getAttachmentUploaded()) {
//                    //not available locally, but uploaded
//                    //would play from there
//                    getS3Url(message.getMessageId(), message.getGroupId());
//
//                } else { //file is not uploaded and also does not exist locally
//                    download.setVisibility(View.GONE);
//                    alert.setVisibility(View.VISIBLE);
//                    errorMessage = "File does not exist either on server or local anymore";
//                    playAudioFrame.setVisibility(View.VISIBLE);
//                    playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.video_play_background));
//                    videoPlay.setVisibility(View.GONE);
//                }
//
//            }
//        }
//        else{ //the appdir does not exist
//            download.setVisibility(View.VISIBLE);
//            if (message.getAttachmentUploaded()) { //not available locally, but uploaded
//                getS3Url(message.getMessageId(), message.getGroupId());
//            }
//            else{//file is not uploaded and also does not exist locally
//                download.setVisibility(View.GONE);
//                alert.setVisibility(View.VISIBLE);
//                errorMessage = "File does not exist either on server or local anymore";
//                playAudioFrame.setVisibility(View.VISIBLE);
//                videoPlay.setVisibility(View.GONE);
//                playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.video_play_background));
//            }
//
//        }






        playAudioFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayClickListener.onMediaPlayClicked(message, audioUri);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                        .setTitle("Download Audio File")
                        .setMessage("Do you want to download this Audio File for offline viewing?")
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





    private void getS3Url(String messageId, String groupId) {
        String key = groupId+"_"+messageId;
        S3LoadingHelper.getPresignedImageLink(key, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {

                audioUri = Uri.parse(url.toString());
                if(audioUri != null){
                    download.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.GONE);
                    alert.setVisibility(View.GONE);
                    playAudioFrame.setVisibility(View.VISIBLE);
                    playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.audio_background));
                    videoPlay.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onImageLinkError(Exception e) {
                audioUri = null;
                upload.setVisibility(View.GONE);
                download.setVisibility(View.GONE);
                alert.setVisibility(View.VISIBLE);
                errorMessage = "File does not exist either on server or local anymore";
                playAudioFrame.setVisibility(View.VISIBLE);
                videoPlay.setVisibility(View.GONE);
                playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.video_play_background));
            }
        });
    }
}

