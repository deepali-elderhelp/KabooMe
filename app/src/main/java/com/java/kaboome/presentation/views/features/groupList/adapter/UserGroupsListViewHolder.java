package com.java.kaboome.presentation.views.features.groupList.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.images.ImageHelper;
//import com.java.kaboome.presentation.views.adapters.RecyclerViewPayloads.GroupNewUnreadCountPayload;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserGroupsListViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMGroupsListViewHolder";

    ConstraintLayout parentLayout;
    CircleImageView groupImage;
//    GlideImageView groupImage;
    TextView groupName;
    TextView lastMessage;
    TextView lastMessageDay;
    TextView lastMessageTime;
    TextView groupExpiry;
    FrameLayout unreadMessagesLayout;
    TextView unreadMessagesCount;
    FrameLayout unreadPMMessagesLayout;
    TextView unreadPMMessagesCount;
    FrameLayout newRequestsLayout;
    TextView newRequestsCount;
    ImageView privacyImage;
    ImageView unicastImage;
//    LinearLayout groupDetails;
    RequestManager requestManager;
    UserGroupImageClickListener userGroupImageClickListener;
    UserGroupMessagesClickListener userGroupMessagesClickListener;
    UserGroupRequestsClickListener userGroupRequestsClickListener;
    ProgressBar groupImageLoadingProgress;
    Context context;
    Handler handler; //Glide needs it



    public UserGroupsListViewHolder(@NonNull View itemView, Context context, UserGroupImageClickListener userGroupImageClickListener, UserGroupMessagesClickListener userGroupMessagesClickListener, UserGroupRequestsClickListener userGroupRequestsClickListener) {
        super(itemView);
        this.context = context;
        parentLayout = itemView.findViewById(R.id.gr_li_fr_list_item_parent_layout);
        groupImage = itemView.findViewById(R.id.gr_li_fr_list_item_group_image);
        groupImageLoadingProgress = itemView.findViewById(R.id.gr_li_fr_list_item_group_image_progress);
        groupName = itemView.findViewById(R.id.gr_li_fr_list_item_group_name);
        lastMessage = itemView.findViewById(R.id.gr_li_fr_list_item_group_message);
        lastMessageDay = itemView.findViewById(R.id.gr_li_fr_list_item_message_day);
        lastMessageTime = itemView.findViewById(R.id.gr_li_fr_list_item_message_time);
        groupExpiry = itemView.findViewById(R.id.gr_li_fr_list_item_group_expiry);
        unreadMessagesLayout = itemView.findViewById(R.id.gr_li_fr_list_item_unread_count);
        unreadMessagesCount = itemView.findViewById(R.id.gr_li_fr_list_item_unread_count_number);
        unreadPMMessagesLayout = itemView.findViewById(R.id.gr_li_fr_list_item_unread_pm_count);
        unreadPMMessagesCount = itemView.findViewById(R.id.gr_li_fr_list_item_unread_pm_count_number);
        newRequestsLayout = itemView.findViewById(R.id.gr_li_fr_list_item_group_requests);
        newRequestsCount = itemView.findViewById(R.id.gr_li_fr_list_item_group_requests_count);
        privacyImage = itemView.findViewById(R.id.gr_li_fr_list_item_privacy);
        unicastImage = itemView.findViewById(R.id.gr_li_fr_list_item_unicast);
//        this.requestManager = requestManager;
        this.userGroupImageClickListener = userGroupImageClickListener;
        this.userGroupMessagesClickListener = userGroupMessagesClickListener;
        this.userGroupRequestsClickListener = userGroupRequestsClickListener;

    }

    public void onBind(final UserGroupModel group, Handler handlerPassed){

        Log.d(TAG, "Group - "+group.getGroupName()+" requests count "+group.getNumberOfRequests());

        this.handler = handlerPassed;
        //disable the request button for now
        //enable after implementation
       // newRequestsImage.setVisibility(View.GONE);

        //load groupImage
//        loadImage(group.getGroupId());
//        ImageHelper.loadGroupImage(group.getGroupId(), group.getImageUpdateTimestamp(), requestManager, handler, groupImage, groupImageLoadingProgress);


//        ImageHelper.loadGroupImage(group.getGroupId(), group.getImageUpdateTimestamp(), requestManager, handler, groupImage, null);

//        requestManager = ImageHelper.getInstance().getRequestManager(itemView.getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey);

//        requestManager.clear(groupImage);
//        groupImage.setImageDrawable(null);
        final Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(context,R.dimen.user_group_list_image_width, group.getGroupName());
        //no placeholder - looks ugly
        requestManager = ImageHelper.getInstance().getRequestManager(itemView.getContext(), null, imageErrorAndPlaceholder);
//        requestManager = ImageHelper.getInstance().getRequestManager(itemView.getContext(), imageErrorAndPlaceholder, imageErrorAndPlaceholder);
        //        this.requestManager = ImageHelper.getRequestManager(context, null, avatarHelper.generateAvatar(60, group.getGroupName()));

//        ImageHelper.loadGroupImage(group.getGroupId(), group.getImageUpdateTimestamp(), requestManager,  handler, groupImage, null);

//        final String imageName = ImagesUtilHelper.getGroupImageName(group.getGroupId(), ImageTypeConstants.MAIN);
//        final String sourceUrl = S3LoadingHelper.getBaseUrlOfImage(imageName);
//        final int signatureKey = AppConfigHelper.getImageSignature(imageName, group.getImageUpdateTimestamp());
//        final URL cachedUrl = S3LoadingHelper.getCachedImageLink(imageName);
//        Log.d(TAG, "loadGroupMNImage - "+imageName+" signature key "+signatureKey);
//
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//
//                final Bitmap image = BackgroundImageHelper.getInstance().loadBackgroundMainImage(requestManager,  sourceUrl, signatureKey, cachedUrl);
//                if(image != null){
//                    AppExecutors2.getInstance().mainThread().execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            groupImage.setImageBitmap(image);
//                        }
//                    });
//                }
//            }
//        });

        if(group.getGroupPicLoadingGoingOn()){
            groupImageLoadingProgress.setVisibility(View.VISIBLE);
        }
        else{
            groupImageLoadingProgress.setVisibility(View.GONE);
        }

        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN,group.getImageUpdateTimestamp(), requestManager,  imageErrorAndPlaceholder,imageErrorAndPlaceholder, handler, groupImage, null, true);
//        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.THUMBNAIL,group.getImageUpdateTimestamp(), requestManager,  imageErrorAndPlaceholder,imageErrorAndPlaceholder, handler, groupImage, null);
        //set the transition property on the groupImage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            groupImage.setTransitionName(group.getGroupId());
        }

//        //enter in the map - needed for update to specific data item
//        groupIdPositionMap.put(group.getGroupId(), new Integer(position));

        groupName.setText(group.getGroupName());

//        newRequestsLayout.setVisibility(View.INVISIBLE);
//        newRequestsCount.setVisibility(View.INVISIBLE);
//
//        unreadMessagesLayout.setVisibility(View.INVISIBLE);
//        unreadMessagesCount.setVisibility(View.INVISIBLE);

        if(group.getPrivate() != null && group.getPrivate()){
            privacyImage.setVisibility(View.VISIBLE);
        }
        else{
            privacyImage.setVisibility(View.INVISIBLE);
        }

        if(group.getUnicastGroup() != null && group.getUnicastGroup()){
            unicastImage.setVisibility(View.VISIBLE);
        }
        else{
            unicastImage.setVisibility(View.INVISIBLE);
        }

        if(group.getUnreadCount() <= 0){
            unreadMessagesLayout.setVisibility(View.INVISIBLE);
            unreadMessagesCount.setVisibility(View.INVISIBLE);
        }
        else{
            unreadMessagesLayout.setVisibility(View.VISIBLE);
            unreadMessagesCount.setVisibility(View.VISIBLE);
            String newCountString = String.valueOf(group.getUnreadCount());
            unreadMessagesCount.setText(newCountString);
        }

        if(group.getUnreadPMCount() <= 0){
            unreadPMMessagesLayout.setVisibility(View.INVISIBLE);
            unreadPMMessagesCount.setVisibility(View.INVISIBLE);
        }
        else{
            unreadPMMessagesLayout.setVisibility(View.VISIBLE);
            unreadPMMessagesCount.setVisibility(View.VISIBLE);
            String newCountString = String.valueOf(group.getUnreadPMCount());
            unreadPMMessagesCount.setText(newCountString);
        }

//        lastMessage.setEllipsize(TextUtils.TruncateAt.END);
//        lastMessage.setMaxLines(2);
//        lastMessage.setText(trimmedLastMessageSenderAlias(group.getLastMessageSentBy())+trimmedLastMessage(group.getLastMessageText()));
        lastMessage.setText(trimmedLastMessageSenderAlias(group.getLastMessageSentBy())+formattedLastMessage(group.getLastMessageText()));
        if(group.getLastMessageSentAt() != null && group.getLastMessageSentAt() > 0) {
            lastMessageDay.setText(DateHelper.getJustDate(group.getLastMessageSentAt()));
            lastMessageTime.setText(DateHelper.getPrettyTime(group.getLastMessageSentAt()));
        }
        else{
            lastMessageDay.setText("");
            lastMessageTime.setText("");
        }


        //set request counts
        //also need to show only if the user is an admin of the group
        if(group.getIsAdmin() != null && group.getIsAdmin().equalsIgnoreCase("true")){
            if(group.getNumberOfRequests() <= 0){
                newRequestsLayout.setVisibility(View.INVISIBLE);
                newRequestsCount.setVisibility(View.INVISIBLE);
            }
            else{
                newRequestsLayout.setVisibility(View.VISIBLE);
                newRequestsCount.setVisibility(View.VISIBLE);
                String newRequestCountString = String.valueOf(group.getNumberOfRequests());
                newRequestsCount.setText(newRequestCountString);
            }
        }
        else{
            newRequestsLayout.setVisibility(View.INVISIBLE);
            newRequestsCount.setVisibility(View.INVISIBLE);
        }


        if(DateHelper.isExpiryNear(group.getGroupExpiry())){
            groupExpiry.setVisibility(View.VISIBLE);
            groupExpiry.setText("Expires " + DateHelper.dateForChatMessages(group.getGroupExpiry()));
        }
        else{
            groupExpiry.setVisibility(View.GONE);
        }


        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + group.getGroupName());

                //TODO: implement the listener

                userGroupImageClickListener.onGroupImageClick(group, groupImage);

            }
        });
        
        newRequestsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: requests show");
                userGroupRequestsClickListener.onGroupRequestsClick(group);
            }
        });

//        groupDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //TODO: implement the listener
//                userGroupMessagesClickListener.onGroupMessagesClick(group);
//                Log.d(TAG, "onClick: clicked on: " + group.getGroupName());
//                //get the groupImage view, that is the transition view
////                ImageView groupImageWithTransition = view.findViewById(R.id.groupImage);
////                ((GroupsListFragment)callingFragment).groupDetailsClicked(groups.get(position), groupImageWithTransition, group.getGroupId());
//            }
//        });

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: implement the listener
                userGroupMessagesClickListener.onGroupMessagesClick(group);
                Log.d(TAG, "onClick: clicked on: " + group.getGroupName());
                //get the groupImage view, that is the transition view
//                ImageView groupImageWithTransition = view.findViewById(R.id.groupImage);
//                ((GroupsListFragment)callingFragment).groupDetailsClicked(groups.get(position), groupImageWithTransition, group.getGroupId());
            }
        });


    }


//    public void onBindGroupUnread(GroupNewUnreadCountPayload groupNewUnreadCountPayload){
//
//        if(groupNewUnreadCountPayload == null){
//            unreadMessagesLayout.setVisibility(View.INVISIBLE);
//            unreadMessagesCount.setVisibility(View.INVISIBLE);
//            lastMessage.setVisibility(View.INVISIBLE);
//            return;
//        }
//        int countUnread = groupNewUnreadCountPayload.getNewUnreadCount();
//
//        if(countUnread == 0){
//            unreadMessagesLayout.setVisibility(View.INVISIBLE);
//            unreadMessagesCount.setVisibility(View.INVISIBLE);
//            lastMessage.setVisibility(View.INVISIBLE);
//        }
//        else{
//            unreadMessagesLayout.setVisibility(View.VISIBLE);
//            unreadMessagesCount.setVisibility(View.VISIBLE);
//            String newCountString = String.valueOf(countUnread);
//            unreadMessagesCount.setText(newCountString);
//            lastMessage.setVisibility(View.VISIBLE);
//            lastMessage.setText(trimmedLastMessage(groupNewUnreadCountPayload.getLastMessageString()));
//        }
//
//    }

    public void recycle(){
        Log.d(TAG, "recycle: for group - "+groupName.getText().toString());
        requestManager.clear(groupImage);
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
