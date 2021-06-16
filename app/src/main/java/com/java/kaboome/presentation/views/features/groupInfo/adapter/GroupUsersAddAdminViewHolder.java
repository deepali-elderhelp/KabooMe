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
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.java.kaboome.helpers.AppConfigHelper.getContext;

public class GroupUsersAddAdminViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMGroupUsersViewHolder";

//    RequestManager requestManager;
    Handler handler; //Glide needs it
    CircleImageView groupUserImage;
    TextView groupUserAlias;
    TextView groupUserRole;
    ImageView checkBoxForSelection;
    ProgressBar userImageLoadingProgress;

    private GroupEditAddAdminClickListener groupEditAddAdminClickListener;


//    public GroupUsersAddAdminViewHolder(@NonNull View itemView, RequestManager requestManager, GroupEditAddAdminClickListener groupEditAddAdminClickListener) {
    public GroupUsersAddAdminViewHolder(@NonNull View itemView, GroupEditAddAdminClickListener groupEditAddAdminClickListener) {
        super(itemView);
//        this.requestManager = requestManager;
        this.groupEditAddAdminClickListener = groupEditAddAdminClickListener;
        groupUserImage = itemView.findViewById(R.id.group_info_group_users_add_admin_image);
        groupUserAlias = itemView.findViewById(R.id.group_info_group_users_add_admin_alias);
        groupUserRole = itemView.findViewById(R.id.group_info_group_users_add_admin_role);
        checkBoxForSelection = itemView.findViewById(R.id.group_info_group_users_add_admin_checkbox);
        userImageLoadingProgress = itemView.findViewById(R.id.group_info_group_users_add_admin_image_progress);

    }

    public void onBind(final GroupUserModel user, Handler handler, View.OnClickListener groupUserImageClickListener) {

        this.handler = handler;
        groupUserImage.setOnClickListener(groupUserImageClickListener);

        //load groupImage
//        loadImage(user.getRequestUserId());
//        ImageHelper.loadGroupImage(user.getUserId(), user.getImageUpdateTimestamp(), requestManager, handler, groupUserImage, userImageLoadingProgress);
//        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(itemView.getContext(),R.dimen.group_user_list_image_width, user.getAlias());
//        ImageHelper.loadUserImage(ImagesUtilHelper.getGroupUserImageName(user.getGroupId(), user.getUserId()), user.getImageUpdateTimestamp(),
//                ImageHelper.getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, groupUserImage, null);
//        ImageHelper.loadUserImage(user.getUserId(), user.getImageUpdateTimestamp(),
//                ImageHelper.getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, groupUserImage, null);

        Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.bs_profile);
        ImageHelper.getInstance().loadGroupUserImage(user.getGroupId(), ImageTypeConstants.THUMBNAIL, user.getUserId(), user.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                handler, groupUserImage, null);

        groupUserAlias.setText(user.getAlias());
        groupUserRole.setText(user.getRole() != null ? user.getRole() : "");

        checkBoxForSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getCheckedToBeAdmin()){
                    checkBoxForSelection.setImageResource(R.drawable.checkbox_empty);
                    user.setCheckedToBeAdmin(false);
                }
                else{
                    checkBoxForSelection.setImageResource(R.drawable.check_list);
                    user.setCheckedToBeAdmin(true);
                }
            }
        });
    }

}
