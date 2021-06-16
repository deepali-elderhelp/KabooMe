package com.java.kaboome.presentation.views.features.groupMessages;


import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

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
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.java.kaboome.R;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.views.widgets.MessageInput;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Email;
import ezvcard.property.Photo;
import ezvcard.property.Telephone;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishAttachmentFragment extends Fragment implements MessageInput.InputListener, MessageInput.TypingListener{


    private static final String TAG = "KMFinishAttachFragment";
    private View rootView;
    private Button urgentButton;
    private Button normalButton;
    private MessageInput messageInput;
    private boolean urgentChecked;
    private String[] attachmentURIs;
    private String attachmentType;
    private String attachmentPath;
    private String goingBackTo;

    private ImageView selectedImage;
    private CircleImageView contactImage;
    private TextView fileName;
//    private VideoView videoView;
    private PlayerView videoView;
    private SimpleExoPlayer videoPlayer;
    private ImageView audioImageView;
    private LinearLayout audioButtonsLayout;
    private TextView audioLengthPlayed;
    private TextView audioTotalLength;
    private DefaultTimeBar audioSeekBar;
    private SimpleExoPlayer exoPlayer;
    private ImageView play;
    private ImageView pause;
    private ImageView stop;
    private long totalTime=0;
    final Handler handler = new Handler();
    Runnable runnable;
    private DefaultDataSourceFactory dataSourceFactory;

    public FinishAttachmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachmentURIs = (String[]) getArguments().getSerializable("attachmentURIs");
        attachmentType = getArguments().getString("attachmentType");
        attachmentPath = getArguments().getString("attachmentPath");
        goingBackTo = getArguments().getString("goingBackTo");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_finish_attachment, container, false);

        selectedImage = rootView.findViewById(R.id.finish_attach_image);
        contactImage = rootView.findViewById(R.id.finish_attach_contact_image);
        fileName = rootView.findViewById(R.id.finish_attach_audio_name);
        videoView = rootView.findViewById(R.id.finish_attach_video);
        urgentButton = rootView.findViewById(R.id.urgent_button);
        urgentButton.setOnClickListener(urgentButtonClicked);
        normalButton = rootView.findViewById(R.id.normal_button);
        normalButton.setOnClickListener(normalButtonClicked);
        messageInput = rootView.findViewById(R.id.fr_gr_me_layout_chatbox);

        audioImageView = rootView.findViewById(R.id.audio_gif);
        audioButtonsLayout = rootView.findViewById(R.id.audio_buttons);
        audioLengthPlayed = rootView.findViewById(R.id.audio_length_played);
        audioTotalLength = rootView.findViewById(R.id.audio_total_length);
        audioSeekBar = rootView.findViewById(R.id.audio_seek_bar);

        messageInput.setInputListener(this);
        messageInput.setTypingListener(this);

        selectedImage.setAdjustViewBounds(true);


        if("camera".equals(attachmentType) || "gallery".equals(attachmentType)){
            setAudioViews(false);
            setVideoViews(false);
            setImageViews(true);
            setContactViews(false);
            Glide.with(FinishAttachmentFragment.this)
                    .asBitmap()
                    .load(attachmentURIs[0])
                    .into(selectedImage);
        }
        if("video".equals(attachmentType) || "video_record".equals(attachmentType)){
            setAudioViews(false);
            setVideoViews(true);
            setImageViews(false);
            setContactViews(false);
            String uriString = attachmentURIs[0];
            Uri videoUri = Uri.parse(uriString);
//            videoView.setVideoURI(videoUri);
//            videoView.start();

            videoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            videoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            // Bind the player to the view.
            videoView.setUseController(true);
            videoView.setPlayer(videoPlayer);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                    getContext(), Util.getUserAgent(getContext(), "Kaboo Me"));

            if(videoUri != null){
                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(videoUri);
                videoPlayer.prepare(videoSource);
                videoPlayer.setPlayWhenReady(true);
            }


        }
        if("audio".equals(attachmentType) || "audio_record".equals(attachmentType)){
            setAudioViews(true);
            setVideoViews(false);
            setImageViews(false);
            setContactViews(false);
            fileName.setText(FileUtils.getFile(getContext(), Uri.parse(attachmentURIs[0])).getName());
        }
        if("contact".equals(attachmentType)){
            setAudioViews(false);
            setVideoViews(false);
            setImageViews(false);
            setContactViews(true);
            String uriString = attachmentURIs[0];
            Uri selectedUri = Uri.parse(uriString);
            List<String> items = selectedUri.getPathSegments();

            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI,items.get(2) );

            AssetFileDescriptor fd;
            try {
                fd = getContext().getContentResolver()
                        .openAssetFileDescriptor(uri, "r");
                FileInputStream fis = fd.createInputStream();
                byte[] b = new byte[(int) fd.getDeclaredLength()];
                fis.read(b);
                String str = new String(b);
                VCard vcard = Ezvcard.parse(str).first();
                String fullName = vcard.getFormattedName().getValue();
                String lastName = vcard.getStructuredName().getFamily();
                List<Telephone> telephones = vcard.getTelephoneNumbers();
                String phoneNumber="";
                for(Telephone telephone:telephones){
                    if(telephone.getTypes() != null && telephone.getTypes().contains(TelephoneType.PREF)){
                        phoneNumber = telephone.getText();
                    }
                }
                List<Email> emails = vcard.getEmails();
                String emailAddress = "";
                for(Email email:emails){
                    if(email.getTypes() != null && email.getTypes().contains(EmailType.PREF)){
                        emailAddress = email.getValue();
                    }
                }

                List<Photo> photos = vcard.getPhotos();
                byte[] image = null;
                if(photos.get(0) != null){
                    image = photos.get(0).getData();
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);
                Glide.with(FinishAttachmentFragment.this)
                        .asBitmap()
                        .load(bitmap)
                        .into(contactImage);
//                selectedImage.setImageBitmap(bitmap);

                messageInput.getInputEditText().setText(fullName+" \n "+phoneNumber+" \n "+emailAddress);

                Log.i(TAG, str);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return rootView;
    }

    private View.OnClickListener urgentButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            urgentButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_button_red_gradient_background));
            ViewCompat.setBackground(messageInput.getButton(), getResources().getDrawable(R.drawable.send_urgent_background));
            urgentChecked =  true;
            urgentButton.setEnabled(false);
            normalButton.setEnabled(true);
        }
    };

    private View.OnClickListener normalButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            urgentButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_button_grey_gradient_background));
            ViewCompat.setBackground(messageInput.getButton(), getResources().getDrawable(R.drawable.send_background));
            urgentChecked =  false;
            urgentButton.setEnabled(true);
            normalButton.setEnabled(false);
        }
    };

    @Override
    public boolean onSubmit(CharSequence input) {
        String caption  = String.valueOf(input);
        Attachment[] attachments = new Attachment[attachmentURIs.length];
        for(int i = 0; i< attachmentURIs.length; i++){
            Attachment attachment = new Attachment(attachmentURIs[0], caption, attachmentPath, urgentChecked);
            attachments[i] = attachment;
        }

        //now, we got what we wanted, let's go back to messages
        NavController navController = NavHostFragment.findNavController(this);
        if(goingBackTo.equals("GroupMessages")) {
            navController.getBackStackEntry(R.id.groupMessagesFragment).getSavedStateHandle().set("attachments", attachments);
//                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupId", result.getText());
            navController.popBackStack(R.id.groupMessagesFragment, false);
        }
        else if(goingBackTo.equals("UserAdminMessages")){
            navController.getBackStackEntry(R.id.groupUserAdminMessagesFragment).getSavedStateHandle().set("attachments", attachments);
            navController.popBackStack(R.id.groupUserAdminMessagesFragment, false);
        }
        else if(goingBackTo.equals("AdminUserMessages")){
            navController.getBackStackEntry(R.id.groupAdminUserMessagesFragment).getSavedStateHandle().set("attachments", attachments);
            navController.popBackStack(R.id.groupAdminUserMessagesFragment, false);
        }
        return false;
    }

    @Override
    public void onStartTyping() {

    }

    @Override
    public void onStopTyping() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(videoPlayer != null){
            videoPlayer.release();
        }
        videoPlayer = null;
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

    private void setAudioViews(boolean show){
        if(show){
            audioImageView.setVisibility(View.VISIBLE);
            audioButtonsLayout.setVisibility(View.VISIBLE);
            audioLengthPlayed.setVisibility(View.VISIBLE);
            audioTotalLength.setVisibility(View.VISIBLE);
            audioSeekBar.setVisibility(View.VISIBLE);
            fileName.setVisibility(View.VISIBLE);
            final ImageView play =  rootView.findViewById(R.id.audio_button_play);
            final ImageView pause = rootView.findViewById(R.id.audio_button_pause);
            final ImageView stop =  rootView.findViewById(R.id.audio_button_stop);

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

            TrackSelector trackSelector = new DefaultTrackSelector();

            dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "KabooMe"));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

            Uri audioUri = Uri.parse(attachmentURIs[0]);
            if(audioUri != null){
                MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(audioUri);
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
//                                exoPlayer.seekTo(0);
//                                exoPlayer.setPlayWhenReady(false);
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
                        audioSeekBar.setPosition(0);
                        audioLengthPlayed.setText("0:0");
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


        }
        else{
            audioImageView.setVisibility(View.GONE);
            audioButtonsLayout.setVisibility(View.GONE);
            audioLengthPlayed.setVisibility(View.GONE);
            audioTotalLength.setVisibility(View.GONE);
            audioSeekBar.setVisibility(View.GONE);
            fileName.setVisibility(View.GONE);
        }
    }

    private void play() {

        exoPlayer.setPlayWhenReady(true);
        Glide.with(getContext())
                .load(R.drawable.audio_transparent)
                .into(audioImageView);

        runnable = new Runnable(){
            @Override
            public void run() {
                if(isPlaying()){
                    Log.d(TAG, "is playing and setting audio");
                    long contentPosition = exoPlayer.getContentPosition();
                    audioSeekBar.setPosition(contentPosition);
                    audioLengthPlayed.setText(DateHelper.getPrettyDuration(contentPosition));
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
                .into(audioImageView);

    }
    private void stop() {
        handler.removeCallbacks(runnable);
        stopPlayer();
        Glide.with(getContext())
                .load(R.drawable.audio_snapshot)
                .into(audioImageView);
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
        audioSeekBar.setPosition(0);
        audioLengthPlayed.setText("0:0");

    }

    public boolean isPlaying() {
        return exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady();
    }

    private void setTimeData(){
        if(exoPlayer.getDuration() != C.TIME_UNSET){
            totalTime = exoPlayer.getDuration();
            Log.d(TAG, "Total Time - "+totalTime);
            audioTotalLength.setText(DateHelper.getPrettyDuration(totalTime));
            audioSeekBar.setEnabled(true);
            audioSeekBar.setDuration(exoPlayer.getDuration());

            audioSeekBar.addListener(new TimeBar.OnScrubListener() {
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

    private void setVideoViews(boolean show){
        if(show){
            videoView.setVisibility(View.VISIBLE);
        }
        else{
            videoView.setVisibility(View.GONE);
        }
    }

    private void setImageViews(boolean show){
        if(show){
            selectedImage.setVisibility(View.VISIBLE);
        }
        else{
            selectedImage.setVisibility(View.GONE);
        }
    }

    private void setContactViews(boolean show){
        if(show){
            contactImage.setVisibility(View.VISIBLE);
        }
        else{
            contactImage.setVisibility(View.GONE);
        }
    }
}
