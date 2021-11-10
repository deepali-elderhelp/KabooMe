package com.java.kaboome.presentation.views.features.conversations.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.java.kaboome.presentation.views.adapters.RecyclerViewPayloads.GroupNewUnreadCountPayload;

public class ConversationsListViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMConvsListViewHolder";

    ConstraintLayout parentLayout;
    CircleImageView conversationImage;
    TextView conversationName;
    TextView lastMessage;
    TextView lastMessageDay;
    TextView lastMessageTime;
    TextView userLeft;
    FrameLayout unreadMessagesLayout;
    TextView unreadMessagesCount;
    FrameLayout newRequestsLayout;
    TextView newRequestsCount;
    ImageView privacyImage;
//    LinearLayout groupDetails;
    RequestManager requestManager;
    ConversationImageClickListener conversationImageClickListener;
    ConversationMessagesClickListener conversationMessagesClickListener;
    ProgressBar groupImageLoadingProgress;
    Context context;
    Handler handler; //Glide needs it



    public ConversationsListViewHolder(@NonNull View itemView, Context context, ConversationImageClickListener conversationImageClickListener, ConversationMessagesClickListener conversationMessagesClickListener) {
        super(itemView);
        this.context = context;
        parentLayout = itemView.findViewById(R.id.conv_li_fr_list_item_parent_layout);
        conversationImage = itemView.findViewById(R.id.conv_li_fr_list_item_conv_image);
//        groupImageLoadingProgress = itemView.findViewById(R.id.gr_li_fr_list_item_group_image_progress);
        conversationName = itemView.findViewById(R.id.conv_li_fr_list_item_conv_name);
        userLeft = itemView.findViewById(R.id.conv_li_fr_list_item_user_left);
        lastMessage = itemView.findViewById(R.id.conv_li_fr_list_item_conv_message);
        lastMessageDay = itemView.findViewById(R.id.conv_li_fr_list_item_message_day);
        lastMessageTime = itemView.findViewById(R.id.conv_li_fr_list_item_message_time);
        unreadMessagesLayout = itemView.findViewById(R.id.conv_li_fr_list_item_unread_count);
        unreadMessagesCount = itemView.findViewById(R.id.conv_li_fr_list_item_unread_count_number);
//        this.requestManager = requestManager;
        this.conversationImageClickListener = conversationImageClickListener;
        this.conversationMessagesClickListener = conversationMessagesClickListener;

    }

    public void onBind(final UserGroupConversationModel conversation, Handler handler){

        Log.d(TAG, "Group - "+conversation.getGroupId());

        this.handler = handler;
        //disable the request button for now
        //enable after implementation
       // newRequestsImage.setVisibility(View.GONE);

        //load groupImage
//        loadImage(conversation.getGroupId());
//        ImageHelper.loadGroupImage(conversation.getGroupId(), conversation.getImageUpdateTimestamp(), requestManager, handler, groupImage, groupImageLoadingProgress);


//        ImageHelper.loadGroupImage(conversation.getGroupId(), conversation.getImageUpdateTimestamp(), requestManager, handler, groupImage, null);

//        requestManager = ImageHelper.getInstance().getRequestManager(itemView.getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey);

//        requestManager.clear(groupImage);
//        groupImage.setImageDrawable(null);
//        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(context,R.dimen.user_group_list_image_width, conversation.getOtherUserName());
        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(context,R.dimen.user_group_list_image_width, "A");
        requestManager = ImageHelper.getInstance().getRequestManager(itemView.getContext(), imageErrorAndPlaceholder, imageErrorAndPlaceholder);
//        requestManager = ImageHelper.getInstance().getRequestManager(itemView.getContext(), imageErrorAndPlaceholder, imageErrorAndPlaceholder);
        //        this.requestManager = ImageHelper.getRequestManager(context, null, avatarHelper.generateAvatar(60, conversation.getGroupName()));

//        ImageHelper.loadGroupImage(conversation.getGroupId(), conversation.getImageUpdateTimestamp(), requestManager,  handler, groupImage, null);
        ImageHelper.getInstance().loadGroupUserImage(conversation.getGroupId(), ImageTypeConstants.MAIN,conversation.getOtherUserId(), conversation.getImageUpdateTimestamp(), requestManager,  imageErrorAndPlaceholder,imageErrorAndPlaceholder, handler, conversationImage, null);
        //set the transition property on the groupImage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            conversationImage.setTransitionName(conversation.getOtherUserId());
        }

//        //enter in the map - needed for update to specific data item
//        groupIdPositionMap.put(conversation.getGroupId(), new Integer(position));

        conversationName.setText(conversation.getOtherUserName()+" - "+conversation.getOtherUserRole());

        if(conversation.getDeleted() != null && conversation.getDeleted()){
            userLeft.setVisibility(View.VISIBLE);
        }
        else{
            userLeft.setVisibility(View.INVISIBLE);
        }

        if(conversation.getUnreadCount() <= 0){
            unreadMessagesLayout.setVisibility(View.INVISIBLE);
            unreadMessagesCount.setVisibility(View.INVISIBLE);
        }
        else{
            unreadMessagesLayout.setVisibility(View.VISIBLE);
            unreadMessagesCount.setVisibility(View.VISIBLE);
            String newCountString = String.valueOf(conversation.getUnreadCount());
            unreadMessagesCount.setText(newCountString);
        }

        lastMessage.setText(trimmedLastMessageSenderAlias(conversation.getLastMessageSentBy())+formattedLastMessage(conversation.getLastMessageText()));
        if(conversation.getLastMessageSentAt() != null && conversation.getLastMessageSentAt() > 0) {
            lastMessageDay.setText(DateHelper.getJustDate(conversation.getLastMessageSentAt()));
            lastMessageTime.setText(DateHelper.getPrettyTime(conversation.getLastMessageSentAt()));
        }
        else{
            lastMessageDay.setText("");
            lastMessageTime.setText("");
        }


        conversationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + conversation.getGroupId());

                //TODO: implement the listener

                conversationImageClickListener.onConvImageClick(conversation, conversationImage);

            }
        });

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: implement the listener
                conversationMessagesClickListener.onConvMessagesClick(conversation);
                Log.d(TAG, "onClick: clicked on: " + conversation.getGroupId());
                //get the groupImage view, that is the transition view
//                ImageView groupImageWithTransition = view.findViewById(R.id.groupImage);
//                ((GroupsListFragment)callingFragment).groupDetailsClicked(groups.get(position), groupImageWithTransition, conversation.getGroupId());
            }
        });


    }


    public void recycle(){
        Log.d(TAG, "recycle: for group - "+ conversationName.getText().toString());
        requestManager.clear(conversationImage);
    }

    private String trimmedLastMessage(String lastMessageString) {
        if(lastMessageString == null){
            return "";
        }
        if(lastMessageString.isEmpty()){
            return  "";
        }
        if(lastMessageString.length() > 50){
            return lastMessageString.substring(0, 49);
        }
        return lastMessageString;
    }

    private String formattedLastMessage(String lastMessageString) {
        if(lastMessageString == null){
            return "";
        }
        if(lastMessageString.isEmpty()){
            return  "";
        }
        return lastMessageString;
    }

    private String trimmedLastMessageSenderAlias(String lastMessageSenderAlias) {
        if(lastMessageSenderAlias == null){
            return "";
        }
        if(lastMessageSenderAlias.isEmpty()){
            return  "";
        }
        if(lastMessageSenderAlias.length() > 20){
            return lastMessageSenderAlias.substring(0, 19);
        }
        return lastMessageSenderAlias.isEmpty()? "" : lastMessageSenderAlias+" : ";
    }

}
