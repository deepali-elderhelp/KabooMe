package com.java.kaboome.presentation.views.features.requestsList.adapter;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupRequestsListViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMIGrpReqsLVHolder";

    ConstraintLayout parentLayout;
    CircleImageView userImage;
    TextView userAlias;
    TextView userRole;
    TextView userMessage;
    AppCompatButton confirmButton;
    AppCompatButton rejectButton;
//    RequestManager requestManager;
    GroupRequestImageClickListener groupRequestImageClickListener;
    GroupRequestAcceptClickListener groupRequestAcceptClickListener;
    GroupRequestRejectClickListener groupRequestRejectClickListener;
    ProgressBar userImageLoadingProgress;
    Handler handler; //Glide needs it



//    public GroupRequestsListViewHolder(@NonNull View itemView, RequestManager requestManager, GroupRequestImageClickListener groupRequestImageClickListener, GroupRequestAcceptClickListener groupRequestAcceptClickListener, GroupRequestRejectClickListener groupRequestRejectClickListener) {
    public GroupRequestsListViewHolder(@NonNull View itemView, GroupRequestImageClickListener groupRequestImageClickListener, GroupRequestAcceptClickListener groupRequestAcceptClickListener, GroupRequestRejectClickListener groupRequestRejectClickListener) {
        super(itemView);

        parentLayout = itemView.findViewById(R.id.group_requests_list_item_parent_layout);
        userImage = itemView.findViewById(R.id.group_requests_list_item_user_image);
        userImageLoadingProgress = itemView.findViewById(R.id.group_requests_list_item_user_image_progress);
        userAlias = itemView.findViewById(R.id.group_requests_list_item_user_name);
        userRole = itemView.findViewById(R.id.group_requests_list_item_alias_role);
        userMessage = itemView.findViewById(R.id.group_requests_list_item_message);
        confirmButton = itemView.findViewById(R.id.group_requests_list_item_confirm_button);
        rejectButton = itemView.findViewById(R.id.group_requests_list_item_reject_button);
//        this.requestManager = requestManager;
        this.groupRequestImageClickListener = groupRequestImageClickListener;
        this.groupRequestAcceptClickListener = groupRequestAcceptClickListener;
        this.groupRequestRejectClickListener = groupRequestRejectClickListener;
    }

    public void onBind(final GroupRequestModel groupRequestModel, Handler handler){

        this.handler = handler;

        //load groupImage
//        ImageHelper.loadUserImage(groupRequestModel.getUserId(), null, requestManager, handler, userImage, userImageLoadingProgress);
        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(itemView.getContext(),R.dimen.user_group_list_image_width, groupRequestModel.getUserAlias() != null? groupRequestModel.getUserAlias():"K M");

        ImageHelper.getInstance().loadGroupUserImage(groupRequestModel.getGroupId(), ImageTypeConstants.MAIN, groupRequestModel.getUserId(), groupRequestModel.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, userImage, null, true);

        userAlias.setText(groupRequestModel.getUserAlias());
        userRole.setText(groupRequestModel.getUserRole());
        userMessage.setText(groupRequestModel.getRequestMessage());

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupRequestAcceptClickListener.onGroupRequestAcceptClick(groupRequestModel);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupRequestRejectClickListener.onGroupRequestRejectClick(groupRequestModel);
            }
        });






    }



}
