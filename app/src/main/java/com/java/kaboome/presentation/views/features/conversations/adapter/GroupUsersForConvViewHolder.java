package com.java.kaboome.presentation.views.features.conversations.adapter;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupAliasAndRoleEditClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.java.kaboome.helpers.AppConfigHelper.getContext;

public class GroupUsersForConvViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMGroupUsersViewHolder";

//    RequestManager requestManager;
    Handler handler; //Glide needs it
    CircleImageView groupUserImage;
    TextView groupUserAlias;
    TextView groupUserRole;
    ImageView groupUserAdminImage;
    ProgressBar userImageLoadingProgress;

    private NewConversationClickListener newConversationClickListener;
//    private GroupUserImageClickListener groupUserImageClickListener;


//    public GroupUsersViewHolder(@NonNull View itemView, RequestManager requestManager, GroupAliasAndRoleEditClickListener groupAliasAndRoleEditClickListener) {
    public GroupUsersForConvViewHolder(@NonNull View itemView) {
        super(itemView);
        groupUserImage = itemView.findViewById(R.id.group_add_conv_image);
        groupUserAlias = itemView.findViewById(R.id.group_add_conv_alias);
        groupUserRole = itemView.findViewById(R.id.group_add_conv_role);
        userImageLoadingProgress = itemView.findViewById(R.id.group_add_conv_image_progress);

    }

    public void onBind(final GroupUserModel user, Handler handler,
                       View.OnClickListener newConvUserClicked) {

        this.handler = handler;
        itemView.setOnClickListener(newConvUserClicked);

        Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
        ImageHelper.getInstance().loadGroupUserImage(user.getGroupId(), ImageTypeConstants.THUMBNAIL, user.getUserId(), user.getImageUpdateTimestamp(),
        ImageHelper.getInstance().getRequestManager(itemView.getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
        handler, groupUserImage, null);

        groupUserAlias.setText(user.getAlias());
        groupUserRole.setText(user.getRole() != null ? user.getRole() : " ");

    }


}
