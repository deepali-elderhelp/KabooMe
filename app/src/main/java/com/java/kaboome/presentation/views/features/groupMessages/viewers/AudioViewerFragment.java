package com.java.kaboome.presentation.views.features.groupMessages.viewers;


import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.java.kaboome.R;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.images.ImageLinkHandler;
import com.java.kaboome.presentation.images.S3LoadingHelper;

import java.io.File;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioViewerFragment extends Fragment {

    private static final String TAG = "KMAudioViewerFragment";


    private View rootView;
    private ImageView audioGif;
//    private ImageView download, upload, play, pause, stop, alert;
    private ImageView play, pause, stop;
    private TextView audioTimePlayed, audioTotalTime;
//    private ImageView urgentImage;
//    private ProgressBar attachmentLoadProgess;
    private TimeBar seekBar;
    private DefaultDataSourceFactory dataSourceFactory;
    private SimpleExoPlayer exoPlayer;
    private Message message;
    private Uri audioUri;
//    private String errorMessage;
    private MediaSource audioSource;
    private long totalTime=0;
    final Handler handler = new Handler();
    Runnable runnable;

    public AudioViewerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle messageInfo = getArguments();
        message = (Message) messageInfo.getSerializable("message");
        audioUri = Uri.parse(messageInfo.getString("audioUri"));

        NavController navController = NavHostFragment.findNavController(this);
        navController.getBackStackEntry(R.id.groupMessagesFragment).getSavedStateHandle().set("attachments", null);
        navController.getBackStackEntry(R.id.groupMessagesFragment).getSavedStateHandle().set("contact", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_audio_viewer, container, false);
        audioGif = rootView.findViewById(R.id.audio_gif);
        Glide.with(getContext())
                .load(R.drawable.audio_snapshot)
                .into(audioGif);

//        download =  rootView.findViewById(R.id.audio_button_download);
//        upload = rootView.findViewById(R.id.audio_button_upload);
        play =  rootView.findViewById(R.id.audio_button_play);
        pause = rootView.findViewById(R.id.audio_button_pause);
        stop =  rootView.findViewById(R.id.audio_button_stop);
//        alert = rootView.findViewById(R.id.audio_button_alert);

        audioTimePlayed =  rootView.findViewById(R.id.audio_length_played);
        audioTotalTime =  rootView.findViewById(R.id.audio_total_length);
//        audioFileName =  itemView.findViewById(R.id.audio_file_name);

//        attachmentLoadProgess = rootView.findViewById(R.id.audio_upload_progress);
        seekBar = rootView.findViewById(R.id.audio_seek_bar);


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

//        download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
//                        .setTitle("Download Audio File")
//                        .setMessage("Do you want to download this Audio File for offline viewing?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                downloadClickListener.onDownloadClicked(message);
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//
//            }
//        });
//
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
////                    File appDir = FileUtils.getAppDirForMime(message.getAttachmentMime(), false);
////                    File attachmentFile = new File(appDir, message.getMessageId() + message.getAttachmentExtension());
//                if (attachmentFile.exists()) {
//                    uploadClickListener.onUploadClicked(message, attachmentFile);
//                }
//
//            }
//        });
//
//        alert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, errorMessage == null ? "Error accessing file":errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });



        TrackSelector trackSelector = new DefaultTrackSelector();

        dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "KabooMe"));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);


        //first thing first, if any upload or download happening, show progress bar
//        if(message.isAttachmentLoadingGoingOn()){
//            attachmentLoadProgess.setVisibility(View.VISIBLE);
//            int progress = message.getLoadingProgress();
//            attachmentLoadProgess.setProgress(progress);
//        }
//        else{
//            attachmentLoadProgess.setVisibility(View.GONE);
//        }


//        String attachmentMime = message.getAttachmentMime();
//
//        File appDir = FileUtils.getAppDirForMime(attachmentMime, false);
//
//        if (appDir != null) {
//            File attachmentFile = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//            if (attachmentFile.exists()) {
//
//                audioUri = FileUtils.getUri(attachmentFile);
//                download.setVisibility(View.GONE);
//
//                if (!message.getAttachmentUploaded()) { //not uploaded
//                    download.setVisibility(View.GONE);
//                    upload.setVisibility(View.VISIBLE);
//                    play.setEnabled(false);
//                    pause.setEnabled(false);
//                    stop.setEnabled(false);
//                    alert.setVisibility(View.VISIBLE);
//                    errorMessage = "Upload of the file did not go through, please try again";
//
//
//                } else { //audio is uploaded and file exists
//                    download.setVisibility(View.GONE);
//                    upload.setVisibility(View.GONE);
//                    alert.setVisibility(View.GONE);
//                    play.setEnabled(true);
//                    pause.setEnabled(false);
//                    stop.setEnabled(false);
//
//                }
//            } else {
//                //file does not exist anymore - may be user deleted
//                upload.setVisibility(View.GONE);
//                if(message.getAttachmentUploaded()){
//                    //not available locally, but uploaded
//                    //would play from there
//                    getS3Url(message.getMessageId(), message.getGroupId());
//                    play.setEnabled(true);
//                    pause.setEnabled(false);
//                    stop.setEnabled(false);
//                    alert.setVisibility(View.GONE);
//                    download.setVisibility(View.VISIBLE);
//                }
//                else{ //file is not uploaded and also does not exist
//                    download.setVisibility(View.GONE);
//                    play.setEnabled(false);
//                    pause.setEnabled(false);
//                    stop.setEnabled(false);
//                    alert.setVisibility(View.VISIBLE);
//                    errorMessage = "File does not exist either on server or local anymore";
//                }
//
//            }
//        }
//        else{ //the appdir does not exist
//            download.setVisibility(View.VISIBLE);
//            if (message.getAttachmentUploaded()) { //not available locally, but uploaded
//                getS3Url(message.getMessageId(), message.getGroupId());
//                play.setEnabled(true);
//                pause.setEnabled(false);
//                stop.setEnabled(false);
//                alert.setVisibility(View.GONE);
//            }
//            else{
//                download.setVisibility(View.GONE);
//                play.setEnabled(false);
//                pause.setEnabled(false);
//                stop.setEnabled(false);
//                alert.setVisibility(View.VISIBLE);
//                errorMessage = "File does not exist either on server or local anymore";
//            }
//
//        }

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
                            stop();
                            break;
                        case Player.STATE_IDLE:
                            break;
                        case Player.STATE_READY:
                            Log.d(TAG, "onPlayerStateChanged: Ready to play.");
                            Log.d(TAG, "Duration - "+exoPlayer.getDuration());
                            if(totalTime == 0){ //total time has not been set so far
                                Log.d(TAG, "calling setTimeData: ");
                                setTimeData();
                            }

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
//                    alert.setVisibility(View.VISIBLE);
//                    download.setVisibility(View.GONE);
//                    errorMessage = "Error accessing file locally and remotely";


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


        return rootView;
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

    private void play() {

        exoPlayer.setPlayWhenReady(true);
        Glide.with(getContext())
                .load(R.drawable.audio_transparent)
                .into(audioGif);

        runnable = new Runnable(){
            @Override
            public void run() {
                if(isPlaying()){
                    Log.d(TAG, "is playing and setting audio");
                    long contentPosition = exoPlayer.getContentPosition();
                    seekBar.setPosition(contentPosition);
                    audioTimePlayed.setText(DateHelper.getPrettyDuration(contentPosition));
                    handler.postDelayed(this, 300);
                }
            }
        };
        handler.postDelayed(runnable, 300);

    }

    private void pause() {

        handler.removeCallbacks(runnable);
        exoPlayer.setPlayWhenReady(false);
        Glide.with(getContext())
                .load(R.drawable.audio_snapshot)
                .into(audioGif);

    }
    private void stop() {
        handler.removeCallbacks(runnable);
        stopPlayer();
        Glide.with(getContext())
                .load(R.drawable.audio_snapshot)
                .into(audioGif);
    }

    private void stopPlayer() {
        if(play != null){
            play.setEnabled(true);
        }
        if(stop != null){
            stop.setEnabled(false);
        }
        if(pause != null){
            pause.setEnabled(false);
        }

        exoPlayer.seekTo(0);
        exoPlayer.setPlayWhenReady(false);
        seekBar.setPosition(0);
        audioTimePlayed.setText("0:0");

    }

    public boolean isPlaying() {
        return exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady();
    }

    private void setTimeData(){
        if(exoPlayer.getDuration() != C.TIME_UNSET){
            totalTime = exoPlayer.getDuration();
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

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(exoPlayer != null){
            exoPlayer.release();
        }
        if(dataSourceFactory != null){
            dataSourceFactory = null;
        }

        if(handler != null && runnable != null){
            handler.removeCallbacks(runnable);
        }

        }
}
