package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.FileUtils;

import java.io.File;
import java.io.IOException;


public class AudioSentMessageHolder_MP {
//        extends RecyclerView.ViewHolder {

//    private static final String TAG = "KMAudioSentMsgHolder";
//
//    private ImageView download, upload, play, pause, stop;
//    private TextView messageText, timeText, yourAliasText, yourRoleText, newMessageHeader;
//    private TextView audioTimePlayed, audioTotalTime, audioFileName;
//    private ImageView urgentImage;
//    private ProgressBar attachmentUploadProgess;
//    private SeekBar seekBar;
//    private Context context;
//    private Uri audioUri;
//    private MediaPlayer mediaPlayer;
//    private Handler handler;
//
//    AudioSentMessageHolder_MP(View itemView, Context context, MediaPlayer mediaPlayer) {
//        super(itemView);
//
//        this.mediaPlayer = mediaPlayer;
//        this.context = context;
//        messageText =  itemView.findViewById(R.id.audio_message_sent);
//        timeText =  itemView.findViewById(R.id.audio_time_sent);
//        yourAliasText = itemView.findViewById(R.id.audio_user_alias_sent);
//        yourRoleText = itemView.findViewById(R.id.audio_user_role_sent);
//
//        urgentImage = itemView.findViewById(R.id.audio_urgent_sent);
//        newMessageHeader = itemView.findViewById(R.id.audioNewMessagesLabel);
//
//        download =  itemView.findViewById(R.id.audio_button_download);
//        upload = itemView.findViewById(R.id.audio_button_upload);
//        play =  itemView.findViewById(R.id.audio_button_play);
//        pause = itemView.findViewById(R.id.audio_button_pause);
//        stop =  itemView.findViewById(R.id.audio_button_stop);
//
//        audioTimePlayed =  itemView.findViewById(R.id.audio_length_played);
//        audioTotalTime =  itemView.findViewById(R.id.audio_total_length);
//        audioFileName =  itemView.findViewById(R.id.audio_file_name);
//
//        attachmentUploadProgess = itemView.findViewById(R.id.audio_upload_progress);
//        seekBar = itemView.findViewById(R.id.audio_seek_bar);
//        seekBar.setMax(100);
//    }
//
//    public void onBind(Message message, Handler handler, View.OnClickListener messageClickListener, View.OnLongClickListener messageLongClickListener, boolean showNewMessageHeader) {
//
//        this.handler = handler;
//        itemView.setOnClickListener(messageClickListener);
//        itemView.setOnLongClickListener(messageLongClickListener);
//
//        if (showNewMessageHeader) {
//            newMessageHeader.setVisibility(View.VISIBLE);
//        } else {
//            newMessageHeader.setVisibility(View.GONE);
//        }
//
//        if (message.getHasAttachment() != null && message.getHasAttachment()) {
//
//            messageText.setText(message.getMessageText());
//            yourAliasText.setText(message.getAlias());
//            if (message.getRole() != null && !(message.getRole().trim().isEmpty())) {
//                yourRoleText.setText(" - " + message.getRole());
//            }
//
//            if (message.getSentAt() != null) {
//                timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
//            }
//
//            String attachmentMime = message.getAttachmentMime();
//
//            File appDir = FileUtils.getAppDirForMime(attachmentMime, false);
//
//            if (appDir != null) {
//                File attachmentFile = new File(appDir, message.getMessageId() + message.getAttachmentExtension());
//                if (attachmentFile.exists()) {
//
//                    audioUri = FileUtils.getUri(attachmentFile);
//
//                    download.setVisibility(View.GONE);
//
//                    if (!message.getAttachmentUploaded()) { //not uploaded
//                        if (message.isAttachmentLoadingGoingOn()) {//still going om
//                            attachmentUploadProgess.setVisibility(View.VISIBLE);
//                            int progress = message.getLoadingProgress();
//                            attachmentUploadProgess.setProgress(progress);
//                            download.setVisibility(View.GONE);
//                            upload.setVisibility(View.GONE);
//                            play.setEnabled(false);
//                            pause.setEnabled(false);
//                            stop.setEnabled(false);
//
//                        } else { //not going on, something went wrong, never uploaded
//                            attachmentUploadProgess.setVisibility(View.GONE);
//                            download.setVisibility(View.GONE);
//                            upload.setVisibility(View.VISIBLE);
//                            play.setEnabled(false);
//                            pause.setEnabled(false);
//                            stop.setEnabled(false);
//
//
//                        }
//                    } else { //audio is uploaded and file exists
//                        download.setVisibility(View.GONE);
//                        upload.setVisibility(View.GONE);
//                        attachmentUploadProgess.setVisibility(View.GONE);
//                        play.setEnabled(true);
//                        pause.setEnabled(false);
//                        stop.setEnabled(false);
//
//                    }
//                } else {
//                    download.setVisibility(View.VISIBLE);
//                    upload.setVisibility(View.GONE);
//                    attachmentUploadProgess.setVisibility(View.GONE);
//                    play.setEnabled(false);
//                    pause.setEnabled(false);
//                    stop.setEnabled(false);
//                }
//            }
//
//
//            if (message.getNotify() == 1) {
//                urgentImage.setVisibility(View.VISIBLE);
//            } else {
//                urgentImage.setVisibility(View.INVISIBLE);
//            }
//
//            play.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    play();
//                    play.setEnabled(false);
//                    pause.setEnabled(true);
//                    stop.setEnabled(true);
//                }
//            });
//
//            pause.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    pause();
//                    pause.setEnabled(false);
//                    play.setEnabled(true);
//                    stop.setEnabled(true);
//                }
//            });
//
//            stop.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    stop();
//                    stop.setEnabled(false);
//                    play.setEnabled(true);
//                    pause.setEnabled(false);
//                }
//            });
//
//            if(audioUri != null){
//                prepareMediaPlayer();
//                seekBar.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        SeekBar seekBar = (SeekBar) v;
//                        int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
//                        Log.d(TAG, "onTouch: play position = "+playPosition);
//                        mediaPlayer.seekTo(playPosition);
//                        seekBar.setProgress(playPosition);
//                        return false;
//                    }
//                });
//            }
//
//
//
//        }
//    }
//
//    private void play() {
//        if(mediaPlayer != null){
//            mediaPlayer.start();
//            updateSeekBar();
//        }
//    }
//
//    private void pause() {
//        if (mediaPlayer != null) {
//            mediaPlayer.pause();
//        }
//    }
//    private void stop() {
//        stopPlayer();
//    }
//    private void stopPlayer() {
//        if (mediaPlayer != null) {
//            mediaPlayer.reset();
////            mediaPlayer.release();
////            mediaPlayer = null;
//        }
//        if(play != null){
//            play.setEnabled(true);
//        }
//        if(stop != null){
//            stop.setEnabled(false);
//        }
//    }
//
//    private void prepareMediaPlayer(){
//        try {
//            if(mediaPlayer == null){
//                mediaPlayer = new MediaPlayer();
//            }
//            mediaPlayer.setDataSource(context, audioUri);
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    stopPlayer();
//                    seekBar.setProgress(0);
//                }
//            });
//            mediaPlayer.prepare();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateSeekBar(){
//        if(mediaPlayer.isPlaying()){
//            seekBar.setProgress(mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration() * 100);
//            handler.postDelayed(updateRunning, 1000);
//        }
//    }
//
//    Runnable updateRunning = new Runnable() {
//        @Override
//        public void run() {
//            updateSeekBar();
////            audioTimePlayed.setText(DateHelper.getPrettyTime());
//
//
//        }
//    };

}
