package com.java.kaboome.presentation.views.features.groupInfo.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.java.kaboome.helpers.AppConfigHelper.getContext;

public class GroupUsersViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMGroupUsersViewHolder";

//    RequestManager requestManager;
    Handler handler; //Glide needs it
    CircleImageView groupUserImage;
    TextView groupUserAlias;
    TextView groupUserRole;
    ImageView groupUserAdminImage;
    ImageView editGroupUserRoleAndAlias;
    ProgressBar userImageLoadingProgress;

    private GroupAliasAndRoleEditClickListener groupAliasAndRoleEditClickListener;
//    private GroupUserImageClickListener groupUserImageClickListener;


//    public GroupUsersViewHolder(@NonNull View itemView, RequestManager requestManager, GroupAliasAndRoleEditClickListener groupAliasAndRoleEditClickListener) {
    public GroupUsersViewHolder(@NonNull View itemView, GroupAliasAndRoleEditClickListener groupAliasAndRoleEditClickListener) {
        super(itemView);
//        this.requestManager = requestManager;
        this.groupAliasAndRoleEditClickListener = groupAliasAndRoleEditClickListener;
//        this.groupUserImageClickListener = groupUserImageClickListener;
        groupUserImage = itemView.findViewById(R.id.group_info_group_users_image);
        groupUserAlias = itemView.findViewById(R.id.group_info_group_users_alias);
        groupUserRole = itemView.findViewById(R.id.group_info_group_users_role);
        groupUserAdminImage = itemView.findViewById(R.id.group_info_group_users_admin_image);
        editGroupUserRoleAndAlias = itemView.findViewById(R.id.group_info_group_users_role_alias_edit);
        userImageLoadingProgress = itemView.findViewById(R.id.group_info_group_users_image_progress);

    }

    public void onBind(final GroupUserModel user, Handler handler,View.OnLongClickListener groupUserLongClickListener,
                       View.OnClickListener groupUserImageClickListener,
                       View.OnClickListener groupUserClickListener) {

        this.handler = handler;
        itemView.setOnLongClickListener(groupUserLongClickListener);
        itemView.setOnClickListener(groupUserClickListener);
        groupUserImage.setOnClickListener(groupUserImageClickListener);

        if(AppConfigHelper.getUserId().equals(user.getUserId())){ //this is the current user
            editGroupUserRoleAndAlias.setVisibility(View.VISIBLE);
            editGroupUserRoleAndAlias.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupAliasAndRoleEditClickListener.onGroupAliasAndRoleEditClick(user);
                }
            });
        }
        else{
            editGroupUserRoleAndAlias.setVisibility(View.GONE);
        }

        //load groupImage
//        loadImage(user.getRequestUserId());
        //render image again if changed
//        ImageHelper.loadUserImage(user.getUserId(), user.getImageUpdateTimestamp(), requestManager, handler, groupUserImage, userImageLoadingProgress);
//        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(itemView.getContext(),R.dimen.group_user_list_image_width, user.getAlias());
//        ImageHelper.loadUserImage(ImagesUtilHelper.getGroupUserImageName(user.getGroupId(), user.getUserId()), user.getImageUpdateTimestamp(),
//                ImageHelper.getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, groupUserImage, null);

        Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
        ImageHelper.getInstance().loadGroupUserImage(user.getGroupId(), ImageTypeConstants.THUMBNAIL, user.getUserId(), user.getImageUpdateTimestamp(),
        ImageHelper.getInstance().getRequestManager(itemView.getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
        handler, groupUserImage, null);

        groupUserAlias.setText(user.getAlias());
        groupUserRole.setText(user.getRole() != null ? user.getRole() : " ");
        if(user.getIsAdmin().equals("true")){
            groupUserAdminImage.setVisibility(View.VISIBLE);
        }
        else{
            groupUserAdminImage.setVisibility(View.GONE);
        }
    }


}
