/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.paging.AsyncPagedListDiffer;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.GroupStatusConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.views.widgets.MessagesListStyle;

public class MessageListViewAdapter extends PagedListAdapter<Message, RecyclerView.ViewHolder>{
    private static final String TAG = "KMMessageListViewAdap";

    private static final int MESSAGE_TEXT_SENT_TYPE = 1;
    private static final int MESSAGE_RECEIVED_TYPE = 2;
    private static final int LOADING_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;
    private static final int MESSAGE_DATE_HEADER_TYPE = 5;
    private static final int MESSAGE_AUDIO_SENT_TYPE = 6;
    private static final int MESSAGE_VIDEO_SENT_TYPE = 7;
    private static final int MESSAGE_IMAGE_SENT_TYPE = 8;

    private static final int MESSAGE_TEXT_RCVD_TYPE = 10;
    private static final int MESSAGE_AUDIO_RCVD_TYPE = 16;
    private static final int MESSAGE_VIDEO_RCVD_TYPE = 17;
    private static final int MESSAGE_IMAGE_RCVD_TYPE = 18;

    private static final int MESSAGE_CREATED_WELCOME_TYPE = 20;
    private static final int MESSAGE_JOINED_WELCOME_TYPE = 21;

//    private final DefaultDataSourceFactory dataSourceFactory;
//    private final SimpleExoPlayer exoPlayer;

    private String userId;
    private int indexToShowNewMessageHeaderAt;

    private Context context;
    private final Handler handler = new Handler(); //needed for glide
    private RequestManager requestManager;
    private MessagesListStyle messagesListStyle;
    private OnMessageClickListener<Message> onMessageClickListener;
    private OnMessageLongClickListener<Message> onMessageLongClickListener;
    private DownloadClickListener downloadClickListener;
    private UploadClickListener uploadClickListener;
    private MediaPlayClickListener mediaPlayClickListener;
    private WelcomeMessageClickListener welcomeMessageClickListener;
    private boolean privateGroup = false;

    private final AsyncPagedListDiffer<Message> mDiffer
            = new AsyncPagedListDiffer(this, DIFF_CALLBACK);
    private UserImageClickListener userImageClickListener;


    @Override
    public int getItemCount() {
        return mDiffer.getItemCount();
    }

    @Override
    public void onCurrentListChanged(PagedList<Message> currentList) {
        Log.d(TAG, "onCurrentListChanged: ");
        super.onCurrentListChanged(currentList);
    }

    public void submitList(final PagedList<Message> pagedList, int indexToShowNewMessageHeaderAt) {

        this.indexToShowNewMessageHeaderAt = indexToShowNewMessageHeaderAt;
//        pagedList.addWeakCallback(pagedList.snapshot(), new PagedList.Callback() {
//            @Override
//            public void onChanged(int position, int count) {
//                Log.d(TAG, "onChanged: ");
//            }
//
//            @Override
//            public void onInserted(int position, int count) {
//                Log.d(TAG, "onInserted: ");
//                mDiffer.submitList(pagedList);
//            }
//
//            @Override
//            public void onRemoved(int position, int count) {
//                Log.d(TAG, "onRemoved: ");
//            }
//        });


//        this.indexToShowNewMessageHeaderAt = indexToShowNewMessageHeaderAt;
//        for(Message message: pagedList){
////            Log.d(TAG, "Message Id "+message.getMessageId()+"  text  "+message.getMessageText()+" message server status "+message.isUploadedToServer());
//            Log.d(TAG, "Group Id - "+message.getGroupId()+" Sent at - "+message.getSentAt());
//            Log.d(TAG, "Alias - "+message.getAlias());
//        }
        mDiffer.submitList(pagedList);
    }

//    public MessageListViewAdapter() {
//        userId = AppConfigHelper.getRequestUserId();
//    }


//    public MessageListViewAdapter(Context context, SimpleExoPlayer exoPlayer, DefaultDataSourceFactory dataSourceFactory) {
    public MessageListViewAdapter(Context context) {
        super(DIFF_CALLBACK);
        userId = AppConfigHelper.getUserId();
        this.context = context;

//        this.exoPlayer = exoPlayer;
//        this.dataSourceFactory = dataSourceFactory;
    }



    public static final DiffUtil.ItemCallback<Message> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Message>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Message oldMessage, @NonNull Message newMessage) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return (oldMessage.getMessageId().equals(newMessage.getMessageId()) &&
                            oldMessage.getGroupId().equals(newMessage.getGroupId()));
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(
                        @NonNull Message oldMessage, @NonNull Message newMessage) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
//                    Log.d(TAG, "areContentsTheSame: "+oldMessage.equals(newMessage)+" "+oldMessage.getMessageId()+" - "+newMessage.getMessageId());
                    return oldMessage.equals(newMessage);
                }

            };

    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }





    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        Message message = (Message) mDiffer.getItem(position);

        if(message.getGroupId().equals("DateHeaderGroup")){
                return MESSAGE_DATE_HEADER_TYPE;
        }
        if (message.getSentBy() != null && message.getSentBy().equals(this.userId)) {
            // If the current user is the sender of the message
            if(message.getHasAttachment() && message.getAttachmentMime().contains("audio")){
                return MESSAGE_AUDIO_SENT_TYPE;
            }
            if(message.getHasAttachment() && message.getAttachmentMime().contains("video")){
                return MESSAGE_VIDEO_SENT_TYPE;
            }
            if(message.getHasAttachment() && message.getAttachmentMime().contains("image")){
                return MESSAGE_IMAGE_SENT_TYPE;
            }
                return MESSAGE_TEXT_SENT_TYPE;

        } else if(message.getSentBy() != null && !message.getSentBy().equals(this.userId)){

            if(message.getSentBy().equals(GroupStatusConstants.CREATED_GROUP.getStatus())){
                //this is a welcome message
                return MESSAGE_CREATED_WELCOME_TYPE;
            }
            if(message.getSentBy().equals(GroupStatusConstants.JOINED_GROUP.getStatus())){
                //this is a welcome message
                return MESSAGE_JOINED_WELCOME_TYPE;
            }

            if(message.getHasAttachment() && message.getAttachmentMime().contains("audio")){
                return MESSAGE_AUDIO_RCVD_TYPE;
            }
            if(message.getHasAttachment() && message.getAttachmentMime().contains("video")){
                return MESSAGE_VIDEO_RCVD_TYPE;
            }
            if(message.getHasAttachment() && message.getAttachmentMime().contains("image")){
                return MESSAGE_IMAGE_RCVD_TYPE;
            }
            return MESSAGE_TEXT_RCVD_TYPE;

        }

//        else if(message.getMessageText().equals("LOADING...")){
//            return LOADING_TYPE;
//        }
//        else if(message.getMessageText().equals("EXHAUSTED...")){
//            return EXHAUSTED_TYPE;
//        }
//        else if(position == mMessageList.size() - 1
//                && position != 0
//                && !message.getMessageText().equals("EXHAUSTED...")){
//            return LOADING_TYPE;
//        }
        else{
            return MESSAGE_RECEIVED_TYPE;
        }

    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case MESSAGE_TEXT_SENT_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_text_message_sent, parent, false);
                return new TextSentMessageHolder(view, context, requestManager, messagesListStyle);
            }

            case MESSAGE_AUDIO_SENT_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_audio_message_sent, parent, false);
                return new AudioSentMessageHolder(view, context);
            }

            case MESSAGE_VIDEO_SENT_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_video_message_sent, parent, false);
                return new VideoSentMessageHolder(view, requestManager, context);
            }

            case MESSAGE_IMAGE_SENT_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_image_message_sent, parent, false);
                return new ImageSentMessageHolder(view, requestManager, context);
            }

            case MESSAGE_TEXT_RCVD_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_text_message_recvd, parent, false);
                return new TextRcvdMessageHolder(view, context, requestManager,messagesListStyle);
            }

            case MESSAGE_AUDIO_RCVD_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_audio_message_rcvd, parent, false);
//                return new AudioRcvdMessageHolder(view, context, requestManager);
                return new AudioRcvdMessageHolder(view, context);
            }

            case MESSAGE_VIDEO_RCVD_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_video_message_rcvd, parent, false);
//                return new VideoRcvdMessageHolder(view, requestManager, context);
                return new VideoRcvdMessageHolder(view);
            }

            case MESSAGE_IMAGE_RCVD_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_image_message_rcvd, parent, false);
//                return new ImageRcvdMessageHolder(view, requestManager,context);
                return new ImageRcvdMessageHolder(view, context);
            }

            case MESSAGE_CREATED_WELCOME_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_welcome_message, parent, false);
//                return new ImageRcvdMessageHolder(view, requestManager,context);
                return new WelcomeMessageHolder(view, true);
            }
            case MESSAGE_JOINED_WELCOME_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_welcome_message, parent, false);
//                return new ImageRcvdMessageHolder(view, requestManager,context);
                return new WelcomeMessageHolder(view, false);
            }



//            case MESSAGE_RECEIVED_TYPE: {
//                view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.content_group_message_item_recvd, parent, false);
//                return new ReceivedMessageHolder(view, context, requestManager, messagesListStyle);
//            }

            case MESSAGE_DATE_HEADER_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_message_date_header, parent, false);
                return  new DateHeaderMessageHolder(view);
            }

//            case LOADING_TYPE: {
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
//                return new LoadingViewHolder(view);
//            }
//
//            case EXHAUSTED_TYPE: {
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_messages_exhausted, parent, false);
//                return new MessagesExhaustedViewHolder(view);
//            }



            default: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_group_message_item_recvd, parent, false);
                return new ReceivedMessageHolder(view, context, requestManager, messagesListStyle);
            }

        }
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mDiffer.getItem(position);

        if(message != null){
            int itemViewType = getItemViewType(position);


//
//            if (itemViewType == MESSAGE_RECEIVED_TYPE) {
//
//                ((ReceivedMessageHolder)holder).onBind(message, handler, getMessageClickListener(message), getMessageLongClickListener(message), position == indexToShowNewMessageHeaderAt? true: false);
//
//            }

            if (itemViewType == MESSAGE_DATE_HEADER_TYPE) {
                ((DateHeaderMessageHolder)holder).onBind(message, position == indexToShowNewMessageHeaderAt? true: false);
            }
            if (itemViewType == MESSAGE_TEXT_SENT_TYPE) {
                ((TextSentMessageHolder)holder).onBind(message, handler, getMessageClickListener(message), getMessageLongClickListener(message), userImageClickListener, position == indexToShowNewMessageHeaderAt? true: false);
            }

            if(itemViewType == MESSAGE_AUDIO_SENT_TYPE) {
                ((AudioSentMessageHolder)holder).onBind(message, handler, mediaPlayClickListener, getMessageLongClickListener(message), downloadClickListener, uploadClickListener, position == indexToShowNewMessageHeaderAt? true: false);
            }

            if(itemViewType == MESSAGE_VIDEO_SENT_TYPE) {
                ((VideoSentMessageHolder)holder).onBind(message, handler, mediaPlayClickListener, getMessageLongClickListener(message), downloadClickListener, uploadClickListener, position == indexToShowNewMessageHeaderAt? true: false);
            }

            if(itemViewType == MESSAGE_IMAGE_SENT_TYPE) {
                ((ImageSentMessageHolder)holder).onBind(message, handler, getMessageClickListener(message), getMessageLongClickListener(message), downloadClickListener, uploadClickListener, position == indexToShowNewMessageHeaderAt? true: false);
            }

            if (itemViewType == MESSAGE_TEXT_RCVD_TYPE) {
                ((TextRcvdMessageHolder)holder).onBind(message, handler, getMessageClickListener(message), getMessageLongClickListener(message), userImageClickListener, position == indexToShowNewMessageHeaderAt? true: false);
            }

            if(itemViewType == MESSAGE_AUDIO_RCVD_TYPE) {
                ((AudioRcvdMessageHolder)holder).onBind(message, handler, mediaPlayClickListener, getMessageLongClickListener(message), downloadClickListener, position == indexToShowNewMessageHeaderAt? true: false);
            }

            if(itemViewType == MESSAGE_VIDEO_RCVD_TYPE) {
                ((VideoRcvdMessageHolder)holder).onBind(message, handler, mediaPlayClickListener, getMessageLongClickListener(message), downloadClickListener, position == indexToShowNewMessageHeaderAt? true: false);
            }

            if(itemViewType == MESSAGE_IMAGE_RCVD_TYPE) {
                ((ImageRcvdMessageHolder)holder).onBind(message, handler, getMessageClickListener(message), getMessageLongClickListener(message), downloadClickListener, position == indexToShowNewMessageHeaderAt? true: false);
            }

            if(itemViewType == MESSAGE_CREATED_WELCOME_TYPE) {
                ((WelcomeMessageHolder)holder).onBind(message, privateGroup, welcomeMessageClickListener);
            }
            if(itemViewType == MESSAGE_JOINED_WELCOME_TYPE) {
                ((WelcomeMessageHolder)holder).onBind(message, privateGroup, welcomeMessageClickListener);
            }
        }



    }

//    @Override
//    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        context = recyclerView.getContext();
//    }
//
//    @Override
//    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView);
//        context = null;
//    }


    public void setPrivateGroup(boolean privateGroup) {
        this.privateGroup = privateGroup;
    }

    public void setStyle(MessagesListStyle messagesListStyle) {
        this.messagesListStyle = messagesListStyle;
    }

//    public void setLayoutManager(LinearLayoutManager layoutManager) {
//            this.layoutManager = layoutManager;
//    }

    public void setOnMessageClickListener(OnMessageClickListener onMessageClickListener){
        this.onMessageClickListener = onMessageClickListener;
    }

    public void setOnMessageLongClickListener(OnMessageLongClickListener<Message> onMessageLongClickListener) {
        this.onMessageLongClickListener = onMessageLongClickListener;
    }

    public void setDownloadClickListener(DownloadClickListener downloadClickListener){
        this.downloadClickListener = downloadClickListener;
    }

    public void setUploadClickListener(UploadClickListener uploadClickListener){
        this.uploadClickListener = uploadClickListener;
    }

    public void setMediaPlayClickListener(MediaPlayClickListener mediaPlayClickListener){
        this.mediaPlayClickListener = mediaPlayClickListener;
    }

    public void setUserImageClickListener(UserImageClickListener userImageClickListener){
        this.userImageClickListener = userImageClickListener;
    }

    public void copyToClipboard(Context context, String copiedText) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(copiedText, copiedText);
        clipboard.setPrimaryClip(clip);
    }

    private View.OnLongClickListener getMessageLongClickListener(final Message message) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onMessageLongClickListener.onMessageLongClick(message);
                    return true;
            }
        };
    }

    private View.OnClickListener getMessageClickListener(final  Message message){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onMessageClickListener.onMessageClick(message);
            }
        };
    }

    public void setWelcomeMessageClickListener(WelcomeMessageClickListener welcomeMessageClickListener) {
        this.welcomeMessageClickListener = welcomeMessageClickListener;

    }

    /**
     * Interface definition for a callback to be invoked when message item is long clicked.
     */
    public interface OnMessageLongClickListener<Message> {

        /**
         * Fires when message is long clicked.
         *
         * @param message clicked message.
         */
        void onMessageLongClick(com.java.kaboome.data.entities.Message message);
    }

    /**
     * Interface definition for a callback to be invoked when message item is clicked.
     */
    public interface OnMessageClickListener<Message> {

        /**
         * Fires when message is clicked.
         *
         * @param message clicked message.
         */
        void onMessageClick(com.java.kaboome.data.entities.Message message);

    }
}


