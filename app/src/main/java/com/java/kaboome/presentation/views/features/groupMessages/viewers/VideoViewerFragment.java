package com.java.kaboome.presentation.views.features.groupMessages.viewers;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.java.kaboome.R;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.images.ImageLinkHandler;
import com.java.kaboome.presentation.images.S3LoadingHelper;

import java.io.File;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoViewerFragment extends Fragment {

    private View rootView;
//    private String messageId;
//    private String groupId;
//    private String pathToPicture;
//    VideoView videoView;
    private PlayerView videoPlayerView;
    private SimpleExoPlayer videoPlayer;
    private Uri videoPathUri;

    private static final String TAG = "KMVideoViewerFragment";

    public VideoViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        Bundle messageInfo = getArguments();
        Message message = (Message) messageInfo.getSerializable("message");
        videoPathUri = Uri.parse(messageInfo.getString("videoUri"));

//        File attachment = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//        if(attachment != null && attachment.exists()){
//            pathToPicture = attachment.getPath();
//        }
//
//        messageId = message.getMessageId();
//        groupId = message.getGroupId();

//        pathToPicture = messageInfo.getString("imagePath");
//        messageId = messageInfo.getString("messageId");
//        uri = messageInfo.getString("uri");

        //the following lines are needed because we need to set the attachments
        //so that new messageId is not created in the GMF onCreateView observer for attachments
        //there we check for attachments being null and if null, we do not do anything
        //otherwise when a new video message is added, then played in the same window
        //then come back to GMF, a new message was getting added.

        NavController navController = NavHostFragment.findNavController(this);
        navController.getBackStackEntry(R.id.groupMessagesFragment).getSavedStateHandle().set("attachments", null);
        navController.getBackStackEntry(R.id.groupMessagesFragment).getSavedStateHandle().set("contact", null);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_video_viewer, container, false);
//        videoView = rootView.findViewById(R.id.video_view);
//
////        Uri uri_passed = Uri.parse(uri);
////        videoView.setVideoURI(uri_passed);
//        videoView.setVideoPath(pathToPicture);
//
//        MediaController mediaController = new MediaController(getContext());
//        videoView.setMediaController(mediaController);
//        mediaController.setAnchorView(videoView);

        videoPlayerView = rootView.findViewById(R.id.exo_video_view);
        videoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        videoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        // Bind the player to the view.
        videoPlayerView.setUseController(true);
        videoPlayerView.setPlayer(videoPlayer);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                getContext(), Util.getUserAgent(getContext(), "Kaboo Me"));

        //if pathToPicture is not an existing downloaded file, then get the URL from S3


//        if (pathToPicture != null && pathToPictureExists()) {
//            play(dataSourceFactory, Uri.parse(pathToPicture));
//        }
//        else{
//            playFromS3(dataSourceFactory, messageId, groupId);
//        }

        if(videoPathUri != null && MediaHelper.doesUriFileExists(getContext().getContentResolver(), videoPathUri)){
            play(dataSourceFactory, videoPathUri);
        }





    }

//    private void playFromS3(final DataSource.Factory dataSourceFactory, String messageId, String groupId) {
//        String key = groupId+"_"+messageId;
//        S3LoadingHelper.getPresignedImageLink(key, new ImageLinkHandler() {
//            @Override
//            public void onImageLinkReady(URL url) {
//                play(dataSourceFactory, Uri.parse(url.toString()));
//            }
//
//            @Override
//            public void onImageLinkError(Exception e) {
//                Toast.makeText(getContext(), "Sorry, file does not exist on locally or on the server", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void play(DataSource.Factory dataSourceFactory, Uri videoUri){
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
        videoPlayer.prepare(videoSource);
        videoPlayer.setPlayWhenReady(true);
    }

//    private boolean pathToPictureExists() {
//        File videoFile = FileUtils.getFile(getContext(), Uri.parse(pathToPicture));
//        if(videoFile != null){
//            return videoFile.exists();
//        }
//        return false;
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(videoPlayer != null){
            videoPlayer.release();
        }
        videoPlayer = null;
    }

}
