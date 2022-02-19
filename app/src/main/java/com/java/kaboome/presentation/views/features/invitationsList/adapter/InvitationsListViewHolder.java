package com.java.kaboome.presentation.views.features.invitationsList.adapter;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.InvitationStatusConstants;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class InvitationsListViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMInviListViewHolder";

    ConstraintLayout parentLayout;
    CircleImageView groupImage;
    TextView groupName;
    TextView invitedBy;
    ImageView privacyImage;
    AppCompatButton joinButton;
    AppCompatButton rejectButton;
    AppCompatButton cancelButton;
//    LinearLayout groupDetails;
    RequestManager requestManager;
    InvitationGroupImageClickListener invitationGroupImageClickListener;
    InvitationGroupRejectClickListener invitationGroupRejectClickListener;
    JoinInvitedGroupClickListener joinInvitedGroupClickListener;
    ProgressBar groupImageLoadingProgress;
    Handler handler; //Glide needs it



    public InvitationsListViewHolder(@NonNull View itemView, RequestManager requestManager,
                                     InvitationGroupImageClickListener invitationGroupImageClickListener,
                                     InvitationGroupRejectClickListener invitationGroupRejectClickListener,
                                     JoinInvitedGroupClickListener joinInvitedGroupClickListener) {
        super(itemView);

        parentLayout = itemView.findViewById(R.id.invi_list_item_parent_layout);
        groupImage = itemView.findViewById(R.id.invi_list_item_group_image);
        groupImageLoadingProgress = itemView.findViewById(R.id.invi_list_item_group_image_progress);
        groupName = itemView.findViewById(R.id.invi_list_item_group_name);
        invitedBy = itemView.findViewById(R.id.invi_list_item_invited_by);
        privacyImage = itemView.findViewById(R.id.invi_list_item_privacy);
        joinButton = itemView.findViewById(R.id.invi_list_item_join_button);
        rejectButton = itemView.findViewById(R.id.invi_list_item_reject_button);
        cancelButton = itemView.findViewById(R.id.invi_list_item_cancel_button);
        this.requestManager = requestManager;
        this.invitationGroupImageClickListener = invitationGroupImageClickListener;
        this.invitationGroupRejectClickListener = invitationGroupRejectClickListener;
        this.joinInvitedGroupClickListener = joinInvitedGroupClickListener;
    }

    public void onBind(final InvitationModel invitation, View.OnClickListener invitationClickListener, Handler handler){

        itemView.setOnClickListener(invitationClickListener);
        this.handler = handler;

        //load groupImage
//        ImageHelper.loadGroupImage(invitation.getGroupId(), null, requestManager, handler, groupImage, groupImageLoadingProgress);
        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(itemView.getContext(),R.dimen.user_group_list_image_width, invitation.getGroupName());
        ImageHelper.getInstance().loadGroupImage(invitation.getGroupId(), ImageTypeConstants.MAIN,null,
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, groupImage, null, true);

        groupName.setText(invitation.getGroupName());


        if(invitation.getPrivateGroup() != null && invitation.getPrivateGroup()){
            privacyImage.setVisibility(View.VISIBLE);
        }
        else{
            privacyImage.setVisibility(View.INVISIBLE);
        }

        if(invitation.getInvitationStatus() == InvitationStatusConstants.NO_ACTION){
            invitedBy.setText("Invited by "+invitation.getInvitedByAlias());
            cancelButton.setVisibility(View.INVISIBLE);
            joinButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);
        }
        else if(invitation.getInvitationStatus() == InvitationStatusConstants.PENDING){
            invitedBy.setText("Pending");
            cancelButton.setVisibility(View.VISIBLE);
            joinButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        }

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + invitation.getGroupName());

                //TODO: implement the listener

                invitationGroupImageClickListener.onGroupImageClick(invitation);

            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationGroupRejectClickListener.onGroupRejectClick(invitation);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationGroupRejectClickListener.onGroupRejectClick(invitation);
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                joinInvitedGroupClickListener.onJoinGroupClick(invitation);
            }
        });



    }



}
