package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.images.ImageLinkHandler;
import com.java.kaboome.presentation.images.S3LoadingHelper;

import java.io.File;
import java.net.URL;


public class AudioSentMessageHolder_old extends RecyclerView.ViewHolder {

    private static final String TAG = "KMAudioSentMsgHolder";

    private ImageView download, upload, play, pause, stop, alert;
    private TextView messageText, timeText, yourAliasText, yourRoleText, newMessageHeader;
    private TextView audioTimePlayed, audioTotalTime;
    private ImageView urgentImage;
    private ProgressBar attachmentLoadProgess;
    private TimeBar seekBar;
    private Context context;
    private Uri audioUri;
    private ExoPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;
    private MediaSource audioSource;
    private DownloadClickListener downloadClickListener;
    private UploadClickListener uploadClickListener;
    private String errorMessage = null;
//    private PlayerView audioPlayer;



    AudioSentMessageHolder_old(View itemView, Context context, ExoPlayer exoPlayer, DataSource.Factory dataSourceFactory) {
        super(itemView);

        this.context = context;
        messageText =  itemView.findViewById(R.id.audio_message_sent);
        timeText =  itemView.findViewById(R.id.audio_time_sent);
        yourAliasText = itemView.findViewById(R.id.audio_user_alias_sent);
        yourRoleText = itemView.findViewById(R.id.audio_user_role_sent);

        urgentImage = itemView.findViewById(R.id.audio_urgent_sent);
        newMessageHeader = itemView.findViewById(R.id.audioNewMessagesLabel);

        download =  itemView.findViewById(R.id.audio_button_download);
        upload = itemView.findViewById(R.id.audio_button_upload);
        play =  itemView.findViewById(R.id.audio_button_play);
        pause = itemView.findViewById(R.id.audio_button_pause);
        stop =  itemView.findViewById(R.id.audio_button_stop);
        alert = itemView.findViewById(R.id.audio_button_alert);

        audioTimePlayed =  itemView.findViewById(R.id.audio_length_played);
        audioTotalTime =  itemView.findViewById(R.id.audio_total_length);
//        audioFileName =  itemView.findViewById(R.id.audio_file_name);

        attachmentLoadProgess = itemView.findViewById(R.id.audio_upload_progress);
        seekBar = itemView.findViewById(R.id.audio_seek_bar);
//        seekBar.setMax(100);

//        audioPlayer = itemView.findViewById(R.id.audio_view);
        this.exoPlayer = exoPlayer;
        this.dataSourceFactory = dataSourceFactory;
//        audioPlayer.setPlayer(exoPlayer);
//        audioPlayer.setUseController(true);

//        TrackSelector trackSelector = new DefaultTrackSelector();
//
//        dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "KabooMe"));
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);


    }

    public void onBind(final Message message, Handler handler, View.OnClickListener messageClickListener,
                       View.OnLongClickListener messageLongClickListener,
                       final DownloadClickListener downloadClickListener,
                       final UploadClickListener uploadClickListener, boolean showNewMessageHeader) {

        itemView.setOnClickListener(messageClickListener);
        itemView.setOnLongClickListener(messageLongClickListener);
        this.downloadClickListener = downloadClickListener;
        this.uploadClickListener = uploadClickListener;

        if (showNewMessageHeader) {
            newMessageHeader.setVisibility(View.VISIBLE);
        } else {
            newMessageHeader.setVisibility(View.GONE);
        }

        if(message.getDeleted()){
            messageText.setText("Message Deleted");
            yourAliasText.setText(message.getAlias());
            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
                yourRoleText.setText(" - " + message.getRole());
            }

            if (message.getSentAt() != null) {
                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
            }

            //everything else GONE
            download.setVisibility(View.GONE);
            upload.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            pause.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            alert.setVisibility(View.GONE);
            attachmentLoadProgess.setVisibility(View.GONE);
            seekBar.setEnabled(false);


        }
        else{
            seekBar.setEnabled(true);
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
                if(message.isAttachmentLoadingGoingOn()){
                    attachmentLoadProgess.setVisibility(View.VISIBLE);
                    int progress = message.getLoadingProgress();
                    attachmentLoadProgess.setProgress(progress);
                }
                else{
                    attachmentLoadProgess.setVisibility(View.GONE);
                }

                String attachmentMime = message.getAttachmentMime();

                File appDir = FileUtils.getAppDirForMime(attachmentMime, false);

                if (appDir != null) {
//                File attachmentFile = new File(appDir, message.getMessageId() + message.getAttachmentExtension());
//                    File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
                    File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getSentTo(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
                    if (attachmentFile.exists()) {

                        audioUri = FileUtils.getUri(attachmentFile);
                        download.setVisibility(View.GONE);

                        if (!message.getAttachmentUploaded()) { //not uploaded
//                        if (message.isAttachmentLoadingGoingOn()) {//still going om
//                            attachmentLoadProgess.setVisibility(View.VISIBLE);
//                            int progress = message.getLoadingProgress();
//                            attachmentLoadProgess.setProgress(progress);
//                            download.setVisibility(View.GONE);
//                            upload.setVisibility(View.GONE);
//                            play.setEnabled(false);
//                            pause.setEnabled(false);
//                            stop.setEnabled(false);
//
//                        } else { //not going on, something went wrong, never uploaded
//                            attachmentLoadProgess.setVisibility(View.GONE);
                            download.setVisibility(View.GONE);
                            upload.setVisibility(View.VISIBLE);
                            play.setEnabled(false);
                            pause.setEnabled(false);
                            stop.setEnabled(false);
                            alert.setVisibility(View.VISIBLE);
                            errorMessage = "Upload of the file did not happen, please try again";


//                        }
                        } else { //audio is uploaded and file exists
                            download.setVisibility(View.GONE);
                            upload.setVisibility(View.GONE);
                            alert.setVisibility(View.GONE);
//                        attachmentLoadProgess.setVisibility(View.GONE);
                            play.setEnabled(true);
                            pause.setEnabled(false);
                            stop.setEnabled(false);

                        }
                    } else {
                        //file does not exist anymore - may be user deleted
                        upload.setVisibility(View.GONE);
//                    attachmentLoadProgess.setVisibility(View.GONE);
                        if(message.getAttachmentUploaded()){
                            //not available locally, but uploaded
                            //would play from there
                            getS3Url(message.getMessageId(), message.getGroupId());
                            play.setEnabled(true);
                            pause.setEnabled(false);
                            stop.setEnabled(false);
                            alert.setVisibility(View.GONE);
                            download.setVisibility(View.VISIBLE);
                        }
                        else{ //file is not uploaded and also does not exist
                            download.setVisibility(View.GONE);
                            play.setEnabled(false);
                            pause.setEnabled(false);
                            stop.setEnabled(false);
                            alert.setVisibility(View.VISIBLE);
                            errorMessage = "File does not exist either on server or local anymore";
                        }

                    }
                }
                else{ //the appdir does not exist
                    download.setVisibility(View.VISIBLE);
                    if (message.getAttachmentUploaded()) { //not available locally, but uploaded
                        getS3Url(message.getMessageId(), message.getGroupId());
                        play.setEnabled(true);
                        pause.setEnabled(false);
                        stop.setEnabled(false);
                        alert.setVisibility(View.GONE);
                    }
                    else{
                        download.setVisibility(View.GONE);
                        play.setEnabled(false);
                        pause.setEnabled(false);
                        stop.setEnabled(false);
                        alert.setVisibility(View.VISIBLE);
                        errorMessage = "File does not exist either on server or local anymore";
                    }

                }

                if (message.getNotify() == 1) {
                    urgentImage.setVisibility(View.VISIBLE);
                } else {
                    urgentImage.setVisibility(View.INVISIBLE);
                }

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play();
                        play.setEnabled(false);
                        pause.setEnabled(true);
                        stop.setEnabled(true);
                    }
                });

                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pause();
                        pause.setEnabled(false);
                        play.setEnabled(true);
                        stop.setEnabled(true);
                    }
                });

                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stop();
                        stop.setEnabled(false);
                        play.setEnabled(true);
                        pause.setEnabled(false);
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


                if(audioUri != null){
                    audioSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(audioUri);
                    exoPlayer.prepare(audioSource);
                    exoPlayer.addListener(new Player.EventListener() {
                        @Override
                        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                        }

                        @Override
                        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                        }

                        @Override
                        public void onLoadingChanged(boolean isLoading) {

                        }

                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                            switch (playbackState) {

                                case Player.STATE_BUFFERING:
                                    Log.d(TAG, "onPlayerStateChanged: Buffering audio.");
                                    break;
                                case Player.STATE_ENDED:
                                    Log.d(TAG, "onPlayerStateChanged: Audio ended.");
                                    exoPlayer.seekTo(0);
                                    exoPlayer.setPlayWhenReady(false);

                                    break;
                                case Player.STATE_IDLE:
                                    break;
                                case Player.STATE_READY:
                                    Log.d(TAG, "onPlayerStateChanged: Ready to play.");
                                    Log.d(TAG, "Duration - "+exoPlayer.getDuration());

                                    setTimeData();
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onRepeatModeChanged(int repeatMode) {

                        }

                        @Override
                        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                        }

                        @Override
                        public void onPlayerError(ExoPlaybackException error) {
//                            exoPlayer.stop();
//                            exoPlayer.prepare(audioSource);
//                            exoPlayer.setPlayWhenReady(false);

                            //if there is some error, disable play and show alert
                            exoPlayer.stop();
                            exoPlayer.seekTo(0);
                            exoPlayer.setPlayWhenReady(false);
                            seekBar.setPosition(0);
                            audioTimePlayed.setText("0:0");
                            play.setVisibility(View.GONE);
                            alert.setVisibility(View.VISIBLE);
                            download.setVisibility(View.GONE);
                            errorMessage = "Error accessing file locally and remotely";


                            switch (error.type) {
                                case ExoPlaybackException.TYPE_SOURCE:
                                    Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());
                                    break;

                                case ExoPlaybackException.TYPE_RENDERER:
                                    Log.e(TAG, "TYPE_RENDERER: " + error.getRendererException().getMessage());
                                    break;

                                case ExoPlaybackException.TYPE_UNEXPECTED:
                                    Log.e(TAG, "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                                    break;
                            }
                        }

                        @Override
                        public void onPositionDiscontinuity(int reason) {

                        }

                        @Override
                        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                        }

                        @Override
                        public void onSeekProcessed() {

                        }
                    });

                }
            }
        }


    }

    private void getS3Url(String messageId, String groupId) {
        String key = groupId+"_"+messageId;
        S3LoadingHelper.getPresignedImageLink(key, new ImageLinkHandler() {
            @Override
            public void onImageLinkReady(URL url) {
                audioUri = Uri.parse(url.toString());
            }

            @Override
            public void onImageLinkError(Exception e) {
                audioUri = null;
            }
        });
    }

    public boolean isPlaying() {
        return exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady();
    }

    private void setTimeData(){
        if(exoPlayer.getDuration() != C.TIME_UNSET){
            long totalTime = exoPlayer.getDuration();
            Log.d(TAG, "Total Time - "+totalTime);
            audioTotalTime.setText(DateHelper.getPrettyDuration(totalTime));
            seekBar.setEnabled(true);
            seekBar.setDuration(exoPlayer.getDuration());

            seekBar.addListener(new TimeBar.OnScrubListener() {
                @Override
                public void onScrubStart(TimeBar timeBar, long position) {

                }

                @Override
                public void onScrubMove(TimeBar timeBar, long position) {
                    exoPlayer.seekTo(position);
                }

                @Override
                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                    exoPlayer.seekTo(position);
                }
            });

            final Handler handler = new Handler();
            final Runnable runnable = new Runnable(){
                @Override
                public void run() {
                    if(isPlaying()){
                        long contentPosition = exoPlayer.getContentPosition();
                        seekBar.setPosition(contentPosition);
                        audioTimePlayed.setText(DateHelper.getPrettyDuration(contentPosition));
                        handler.postDelayed(this, 300);
                    }
                }
            };
            handler.postDelayed(runnable, 300);

        }
    }

    private void play() {

        exoPlayer.setPlayWhenReady(true);

    }

    private void pause() {
        exoPlayer.setPlayWhenReady(false);
    }
    private void stop() {
        stopPlayer();
    }

    private void stopPlayer() {
        if(play != null){
            play.setEnabled(true);
        }
        if(stop != null){
            stop.setEnabled(false);
        }

        exoPlayer.seekTo(0);
        exoPlayer.setPlayWhenReady(false);
        seekBar.setPosition(0);
        audioTimePlayed.setText("0:0");

    }
}
