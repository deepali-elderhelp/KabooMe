package com.java.kaboome.presentation.views.features.groupSearch.adapter;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchGroupsListViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMGroupsListViewHolder";

    ConstraintLayout parentLayout;
    CircleImageView groupImage;
    TextView groupName;
//    RequestManager requestManager;
    GroupClickListener groupClickListener;
    JoinGroupClickListener joinGroupClickListener;
    ProgressBar groupImageLoadingProgress;
    Handler handler; //Glide needs it
    AppCompatButton joinButton;
    ImageView groupPrivacy;
    Resources resources;



//    public SearchGroupsListViewHolder(@NonNull View itemView, RequestManager requestManager, GroupClickListener groupClickListener, JoinGroupClickListener joinGroupClickListener) {
    public SearchGroupsListViewHolder(@NonNull View itemView, GroupClickListener groupClickListener, JoinGroupClickListener joinGroupClickListener) {
        super(itemView);

        parentLayout = itemView.findViewById(R.id.group_search_list_item_parent_layout);
        groupImage = itemView.findViewById(R.id.group_search_list_item_group_image);
        groupImageLoadingProgress = itemView.findViewById(R.id.group_search_list_item_group_image_progress);
        groupName = itemView.findViewById(R.id.group_search_list_item_group_name);
        joinButton = itemView.findViewById(R.id.group_search_join_group);
//        this.requestManager = requestManager;
        this.groupClickListener = groupClickListener;
        this.joinGroupClickListener = joinGroupClickListener;
        this.groupPrivacy = itemView.findViewById(R.id.group_search_list_item_privacy);
        resources = itemView.getResources();
    }

    public void onBind(final GroupModel group, Handler handler){

        this.handler = handler;
        
        //load groupImage
//        ImageHelper.loadGroupImage(group.getGroupId(), group.getImageUpdateTimestamp(), requestManager, handler, groupImage, groupImageLoadingProgress);
        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(itemView.getContext(),R.dimen.group_search_list_item_image_width, group.getGroupName());
        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(groupImage.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, groupImage, null, false);


        groupName.setText(group.getGroupName());
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to join page for now
                groupClickListener.onGroupClick(group);
            }
        });

        if(group.getCurrentUserGroupStatus() != null && (group.getCurrentUserGroupStatus() == UserGroupStatusConstants.REGULAR_MEMBER
        || (group.getCurrentUserGroupStatus() == UserGroupStatusConstants.ADMIN_MEMBER))){
            joinButton.setVisibility(View.INVISIBLE);

        }
        else if(group.getCurrentUserGroupStatus() != null && group.getCurrentUserGroupStatus() == UserGroupStatusConstants.PENDING){
            joinButton.setVisibility(View.VISIBLE);
            joinButton.setEnabled(false);
            joinButton.setText("Pending");
            joinButton.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_button_light_background));
            joinButton.setTextColor(resources.getColor(R.color.colorPrimary));
            joinButton.setOnClickListener(null);
        }
        else if(group.getCurrentUserGroupStatus() != null && group.getCurrentUserGroupStatus() == UserGroupStatusConstants.INVITED){
            joinButton.setVisibility(View.VISIBLE);
            joinButton.setEnabled(false);
            joinButton.setText("Invited");
            joinButton.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_button_light_background));
            joinButton.setTextColor(resources.getColor(R.color.colorPrimary));

        }
        else{
            //not member, invited or pending invitations
            //so show join or send request based upon the privacy of the group
            joinButton.setVisibility(View.VISIBLE);
            joinButton.setEnabled(true);
            joinButton.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_button_gradient_background));
            joinButton.setTextColor(resources.getColor(R.color.white));
            if(group.getGroupPrivate()){
                joinButton.setText("Send Request");
            }
            else{
                joinButton.setText("Join");
            }

        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroupClickListener.onJoinGroupClick(group);
            }
        });

        if(group.getGroupPrivate() != null && group.getGroupPrivate() == true){
            groupPrivacy.setVisibility(View.VISIBLE);
        }
        else{
            groupPrivacy.setVisibility(View.INVISIBLE);
        }



    }




}
