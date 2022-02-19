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

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.images.ImageLinkHandler;
import com.java.kaboome.presentation.images.S3LoadingHelper;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class AudioRcvdMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMAudioRcvdMsgHolder";

//    private ImageView download, alert, videoPlay;
    private ImageView download, videoPlay;
    private TextView messageText, timeText, aliasText, roleText, newMessageHeader;
    private CircleImageView profileImage;
    private ImageView urgentImage;
    private ProgressBar attachmentLoadProgess;
    private FrameLayout playAudioFrame;
    private Context context;
    private Uri audioUri;
    private MediaPlayClickListener mediaPlayClickListener;
    private String errorMessage;
    private DownloadClickListener downloadClickListener;
    private Handler handler;
//    private RequestManager requestManager;
//    private PlayerView audioPlayer;



//    AudioRcvdMessageHolder(View itemView, Context context, RequestManager requestManager) {
    AudioRcvdMessageHolder(View itemView, Context context) {
        super(itemView);

        this.context = context;
//        this.requestManager = requestManager;
        messageText =  itemView.findViewById(R.id.audio_message_rcvd);
        timeText =  itemView.findViewById(R.id.audio_time_rcvd);
        aliasText = itemView.findViewById(R.id.audio_user_alias_rcvd);
        roleText = itemView.findViewById(R.id.audio_user_role_rcvd);

        urgentImage = itemView.findViewById(R.id.audio_urgent_rcvd);
        newMessageHeader = itemView.findViewById(R.id.audioNewMessagesLabel_rcvd);

        playAudioFrame = itemView.findViewById(R.id.audio_play_frame);

        download = itemView.findViewById(R.id.audio_download);
//        alert = itemView.findViewById(R.id.audio_alert);

        videoPlay = itemView.findViewById(R.id.video_play);

        attachmentLoadProgess = itemView.findViewById(R.id.audio_upload_progress);
        attachmentLoadProgess.setMax(100);

//        audioFileName =  itemView.findViewById(R.id.audio_file_name_rcvd);

        profileImage = (CircleImageView) itemView.findViewById(R.id.image_user_profile_rcvd);


    }

    public void onBind(final Message message, Handler handler, final MediaPlayClickListener mediaPlayClickListener,
                       View.OnLongClickListener messageLongClickListener,
                       final DownloadClickListener downloadClickListener,
                       boolean showNewMessageHeader) {


        itemView.setOnLongClickListener(messageLongClickListener);
        this.downloadClickListener = downloadClickListener;
        this.mediaPlayClickListener = mediaPlayClickListener;

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


        aliasText.setText(message.getAlias());
        if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
            roleText.setText(" - " + message.getRole());
        }

        if (message.getSentAt() != null) {
            timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
        }


        if (message.getNotify() == 1) {
            urgentImage.setVisibility(View.VISIBLE);
        } else {
            urgentImage.setVisibility(View.INVISIBLE);
        }

        playAudioFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayClickListener.onMediaPlayClicked(message, audioUri);
            }
        });

        if (message.getDeleted()) {
            urgentImage.setVisibility(View.INVISIBLE);
            playAudioFrame.setVisibility(View.GONE);
            messageText.setText("Message Deleted");
            download.setVisibility(View.GONE);
            errorMessage = "Message has been deleted";
//            alert.setVisibility(View.GONE);
//            alert.setVisibility(View.VISIBLE);

        } else {
            playAudioFrame.setVisibility(View.VISIBLE);
//            alert.setVisibility(View.GONE);
            messageText.setText(message.getMessageText());

            //first thing first, if any upload or download happening, show progress bar

            if (message.isAttachmentLoadingGoingOn()) {
                attachmentLoadProgess.setVisibility(View.VISIBLE);
                int progress = message.getLoadingProgress();
                attachmentLoadProgess.setProgress(progress);
            } else {
                attachmentLoadProgess.setVisibility(View.GONE);
                String attachmentUriString = message.getAttachmentUri();
                Uri attachmentUri = null;
                if (attachmentUriString != null) {
                    attachmentUri = Uri.parse(attachmentUriString);
                }

                Log.d(TAG, "Uri returned " + attachmentUri);

                if (attachmentUri != null && MediaHelper.doesUriFileExists(context.getContentResolver(), attachmentUri)) {

                    //audio is uploaded and file exists
                    download.setVisibility(View.GONE);
//                    alert.setVisibility(View.GONE);
                    playAudioFrame.setVisibility(View.VISIBLE);
                    playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.audio_background));
                    videoPlay.setVisibility(View.VISIBLE);

                } else {
                    if (message.getAttachmentUploaded()) {
                        //not available locally, but uploaded
//                        alert.setVisibility(View.GONE);
                        download.setVisibility(View.VISIBLE);
                        getS3Url(message.getMessageId(), message.getGroupId());
                        videoPlay.setVisibility(View.VISIBLE);
                    } else { //file is not uploaded and also does not exist
                        download.setVisibility(View.GONE);
//                        alert.setVisibility(View.VISIBLE);
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
//                 //audio is uploaded and file exists
//                download.setVisibility(View.GONE);
//                alert.setVisibility(View.GONE);
//                playAudioFrame.setVisibility(View.VISIBLE);
//                playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.audio_background));
//                videoPlay.setVisibility(View.VISIBLE);
//
//
//            } else {
//                //file does not exist anymore - may be user deleted
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


//        alert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, errorMessage == null ? "Error accessing file":errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });

        playAudioFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayClickListener.onMediaPlayClicked(message, audioUri);
            }
        });




////        itemView.setOnClickListener(messageClickListener);
//        itemView.setOnLongClickListener(messageLongClickListener);
//        this.downloadClickListener = downloadClickListener;
//        this.mediaPlayClickListener = mediaPlayClickListener;
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
//            download.setVisibility(View.GONE);
//            play.setVisibility(View.GONE);
//            pause.setVisibility(View.GONE);
//            stop.setVisibility(View.GONE);
//            attachmentLoadProgess.setVisibility(View.GONE);
//            seekBar.setEnabled(false);
//
//
//        }
//        else{
//            seekBar.setEnabled(true);
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
//                if(message.isAttachmentLoadingGoingOn()){
//                    attachmentLoadProgess.setVisibility(View.VISIBLE);
//                    int progress = message.getLoadingProgress();
//                    attachmentLoadProgess.setProgress(progress);
//                }
//                else{
//                    attachmentLoadProgess.setVisibility(View.GONE);
//                }
//
//                String attachmentMime = message.getAttachmentMime();
//
//                File appDir = FileUtils.getAppDirForMime(attachmentMime, false);
//
//                if (appDir != null) {
////                File attachmentFile = new File(appDir, message.getMessageId() + message.getAttachmentExtension());
////                    File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//                    File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//                    if (attachmentFile.exists()) {
//
//                        audioUri = FileUtils.getUri(attachmentFile);
//                        download.setVisibility(View.GONE);
//
//                        if (!message.getAttachmentUploaded()) { //not uploaded
//                            download.setVisibility(View.VISIBLE);
//                            play.setEnabled(false);
//                            pause.setEnabled(false);
//                            stop.setEnabled(false);
//                        } else { //audio is uploaded and file exists
//                            download.setVisibility(View.GONE);
//                            play.setEnabled(true);
//                            pause.setEnabled(false);
//                            stop.setEnabled(false);
//                        }
//                    } else {
//                        //file does not exist anymore - may be user deleted
////                    attachmentLoadProgess.setVisibility(View.GONE);
//                        if(message.getAttachmentUploaded()){
//                            //not available locally, but uploaded
//                            //would play from there
//                            getS3Url(message.getMessageId(), message.getGroupId());
//                            play.setEnabled(true);
//                            pause.setEnabled(false);
//                            stop.setEnabled(false);
//                            download.setVisibility(View.VISIBLE);
//                        }
//                        else{ //file is not uploaded and also does not exist
//                            download.setVisibility(View.GONE);
//                            play.setEnabled(false);
//                            pause.setEnabled(false);
//                            stop.setEnabled(false);
//                        }
//
//                    }
//                }
//                else{ //the appdir does not exist
//                    download.setVisibility(View.VISIBLE);
//                    if (message.getAttachmentUploaded()) { //not available locally, but uploaded
//                        getS3Url(message.getMessageId(), message.getGroupId());
//                        play.setEnabled(true);
//                        pause.setEnabled(false);
//                        stop.setEnabled(false);
//                    }
//                    else{
//                        download.setVisibility(View.GONE);
//                        play.setEnabled(false);
//                        pause.setEnabled(false);
//                        stop.setEnabled(false);
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
//                play.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        play();
//                        play.setEnabled(false);
//                        pause.setEnabled(true);
//                        stop.setEnabled(true);
//                    }
//                });
//
//                pause.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pause();
//                        pause.setEnabled(false);
//                        play.setEnabled(true);
//                        stop.setEnabled(true);
//                    }
//                });
//
//                stop.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        stop();
//                        stop.setEnabled(false);
//                        play.setEnabled(true);
//                        pause.setEnabled(false);
//                    }
//                });
//
//                download.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
//                                .setTitle("Download Audio File")
//                                .setMessage("Do you want to download this Audio File for offline viewing?")
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
//
//                if(audioUri != null){
//                    audioSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(audioUri);
//                    exoPlayer.prepare(audioSource);
//                    exoPlayer.addListener(new Player.EventListener() {
//                        @Override
//                        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
//
//                        }
//
//                        @Override
//                        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
//                        }
//
//                        @Override
//                        public void onLoadingChanged(boolean isLoading) {
//
//                        }
//
//                        @Override
//                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                            switch (playbackState) {
//
//                                case Player.STATE_BUFFERING:
//                                    Log.d(TAG, "onPlayerStateChanged: Buffering video.");
//                                    break;
//                                case Player.STATE_ENDED:
//                                    Log.d(TAG, "onPlayerStateChanged: Video ended.");
//                                    exoPlayer.seekTo(0);
//                                    exoPlayer.setPlayWhenReady(false);
//
//                                    break;
//                                case Player.STATE_IDLE:
//                                    break;
//                                case Player.STATE_READY:
//                                    Log.d(TAG, "onPlayerStateChanged: Ready to play.");
//                                    Log.d(TAG, "Duration - "+exoPlayer.getDuration());
//
//                                    setTimeData();
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//
//                        @Override
//                        public void onRepeatModeChanged(int repeatMode) {
//
//                        }
//
//                        @Override
//                        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//
//                        }
//
//                        @Override
//                        public void onPlayerError(ExoPlaybackException error) {
//                            exoPlayer.stop();
//                            exoPlayer.prepare(audioSource);
//                            exoPlayer.setPlayWhenReady(true);
//                        }
//
//                        @Override
//                        public void onPositionDiscontinuity(int reason) {
//
//                        }
//
//                        @Override
//                        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
//                        }
//
//                        @Override
//                        public void onSeekProcessed() {
//
//                        }
//                    });
//
//                }
//            }
//        }
//

    }

    private void getS3Url(String messageId, String groupId) {
        String key = ImagesUtilHelper.getMessageAttachmentKeyName(groupId, messageId);
        S3LoadingHelper.getPresignedImageLink(key, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {

                audioUri = Uri.parse(url.toString());
                if(audioUri != null){
                    download.setVisibility(View.VISIBLE);
//                    alert.setVisibility(View.GONE);
                    playAudioFrame.setVisibility(View.VISIBLE);
                    playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.audio_background));
                    videoPlay.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onImageLinkError(Exception e) {
                audioUri = null;
                download.setVisibility(View.GONE);
//                alert.setVisibility(View.VISIBLE);
                errorMessage = "File does not exist either on server or local anymore";
                playAudioFrame.setVisibility(View.VISIBLE);
                videoPlay.setVisibility(View.GONE);
                playAudioFrame.setBackground(context.getResources().getDrawable(R.drawable.video_play_background));
            }
        });
    }

//    private void getS3Url(String messageId, String groupId) {
//        String key = groupId+"_"+messageId;
//        S3LoadingHelper.getPresignedImageLink(key, new ImageLinkHandler() {
//            @Override
//            public void onImageLinkReady(URL url) {
//                audioUri = Uri.parse(url.toString());
//            }
//
//            @Override
//            public void onImageLinkError(Exception e) {
//                audioUri = null;
//            }
//        });
//    }

//    public boolean isPlaying() {
//        return exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady();
//    }

//    private void setTimeData(){
//        if(exoPlayer.getDuration() != C.TIME_UNSET){
//            long totalTime = exoPlayer.getDuration();
//            Log.d(TAG, "Total Time - "+totalTime);
//            audioTotalTime.setText(DateHelper.getPrettyDuration(totalTime));
//            seekBar.setEnabled(true);
//            seekBar.setDuration(exoPlayer.getDuration());
//
//            seekBar.addListener(new TimeBar.OnScrubListener() {
//                @Override
//                public void onScrubStart(TimeBar timeBar, long position) {
//
//                }
//
//                @Override
//                public void onScrubMove(TimeBar timeBar, long position) {
//                    exoPlayer.seekTo(position);
//                }
//
//                @Override
//                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
//                    exoPlayer.seekTo(position);
//                }
//            });
//
//            final Handler handler = new Handler();
//            final Runnable runnable = new Runnable(){
//                @Override
//                public void run() {
//                    if(isPlaying()){
//                        long contentPosition = exoPlayer.getContentPosition();
//                        seekBar.setPosition(contentPosition);
//                        audioTimePlayed.setText(DateHelper.getPrettyDuration(contentPosition));
//                        handler.postDelayed(this, 300);
//                    }
//                }
//            };
//            handler.postDelayed(runnable, 300);
//
//        }
//    }
//
//    private void play() {
//
//        exoPlayer.setPlayWhenReady(true);
//
//    }
//
//    private void pause() {
//        exoPlayer.setPlayWhenReady(false);
//    }
//    private void stop() {
//        stopPlayer();
//    }
//
//    private void stopPlayer() {
//        if(play != null){
//            play.setEnabled(true);
//        }
//        if(stop != null){
//            stop.setEnabled(false);
//        }
//
//        exoPlayer.seekTo(0);
//        exoPlayer.setPlayWhenReady(false);
//        seekBar.setPosition(0);
//        audioTimePlayed.setText("0:0");
//
//    }
}
