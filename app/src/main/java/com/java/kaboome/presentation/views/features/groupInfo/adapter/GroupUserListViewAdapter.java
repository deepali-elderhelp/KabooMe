package com.java.kaboome.presentation.views.features.groupInfo.adapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupUserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupUserListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "KMGroupUserListViewAdap";

    private final Handler handler = new Handler(); //needed for glide


    private List<GroupUserModel> groupUsers = new ArrayList<>();

//    private RequestManager requestManager;
    private UserRecyclerType userRecyclerType;
    private GroupUserLongClickListener groupUserLongClickListener;
    private GroupAliasAndRoleEditClickListener groupAliasAndRoleEditClickListener;
    private GroupUserImageClickListener groupUserImageClickListener;
    private GroupUserClickListener groupUserClickListener;

    public enum UserRecyclerType {ADMIN_USERS_TYPE, MEMBER_USERS_TYPE, ADD_ADMINS_TYPE};


//    public GroupUserListViewAdapter(RequestManager requestManager,UserRecyclerType userRecyclerType,
//                                    GroupAliasAndRoleEditClickListener groupAliasAndRoleEditClickListener,
//                                    GroupUserLongClickListener groupUserLongClickListener,
//                                    GroupUserImageClickListener groupUserImageClickListener,
//                                    GroupUserClickListener groupUserClickListener) {

    public GroupUserListViewAdapter(UserRecyclerType userRecyclerType,
                                    GroupAliasAndRoleEditClickListener groupAliasAndRoleEditClickListener,
                                    GroupUserLongClickListener groupUserLongClickListener,
                                    GroupUserImageClickListener groupUserImageClickListener,
                                    GroupUserClickListener groupUserClickListener) {
//        this.requestManager = requestManager;
        this.userRecyclerType = userRecyclerType;
        this.groupAliasAndRoleEditClickListener = groupAliasAndRoleEditClickListener;
        this.groupUserLongClickListener = groupUserLongClickListener;
        this.groupUserImageClickListener = groupUserImageClickListener;
        this.groupUserClickListener = groupUserClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
//        Log.d(TAG, "onCreateViewHolder: View Type is "+viewType);

        if(userRecyclerType == UserRecyclerType.ADMIN_USERS_TYPE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_user_item, parent, false);
//            return new GroupUsersViewHolder(view, requestManager, groupAliasAndRoleEditClickListener);
            return new GroupUsersViewHolder(view, groupAliasAndRoleEditClickListener);
        }
        if(userRecyclerType == UserRecyclerType.MEMBER_USERS_TYPE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_user_item, parent, false);
//            return new GroupUsersViewHolder(view, requestManager, groupAliasAndRoleEditClickListener);
            return new GroupUsersViewHolder(view, groupAliasAndRoleEditClickListener);
        }
        if(userRecyclerType == UserRecyclerType.ADD_ADMINS_TYPE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_user_add_admin_item, parent, false);
//            return new GroupUsersAddAdminViewHolder(view, requestManager, null);
            return new GroupUsersAddAdminViewHolder(view, null);
        }

//        return new GroupUsersViewHolder(view, requestManager, groupAliasAndRoleEditClickListener); //default
        return new GroupUsersViewHolder(view, groupAliasAndRoleEditClickListener); //default


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupUserModel groupUser = groupUsers.get(position);
        if(userRecyclerType == UserRecyclerType.ADMIN_USERS_TYPE) {
            ((GroupUsersViewHolder) holder).onBind(groupUsers.get(position), handler, getGroupUserLongClickListener(groupUser), getGroupUserImageClickListener(groupUser), getGroupUserClickListener(groupUser));
        }
        if(userRecyclerType == UserRecyclerType.MEMBER_USERS_TYPE){
            ((GroupUsersViewHolder) holder).onBind(groupUsers.get(position), handler, getGroupUserLongClickListener(groupUser), getGroupUserImageClickListener(groupUser), getGroupUserClickListener(groupUser));
        }
        if(userRecyclerType == UserRecyclerType.ADD_ADMINS_TYPE){
            ((GroupUsersAddAdminViewHolder)holder).onBind(groupUsers.get(position), handler, getGroupUserImageClickListener(groupUser));
        }
    }

    @Override
    public int getItemCount() {
        //over here if the type is members_user_type, return only 10 - max users for that screen
        if(groupUsers != null){
            if(userRecyclerType == UserRecyclerType.MEMBER_USERS_TYPE){
                if(groupUsers.size() > 10){
                    return 10;
                }
                else{
                    return groupUsers.size();
                }
            }
            else{
                return groupUsers.size();
            }
        }

        return 0; //  this could be when loading
    }

    public void setGroupUsers(List<GroupUserModel> groupUsersPassed){
        Log.d(TAG, "setGroupUsers: size is "+groupUsersPassed.size());
        if(this.groupUsers != null){
            this.groupUsers.clear();
        }
        this.groupUsers.addAll(groupUsersPassed);
        notifyDataSetChanged();
    }

//    public void setGroupUser(GroupUserModel groupUser){
//        Log.d(TAG, "setGroupUsers: size is "+groupUsers.size());
//        if(this.groupUsers != null && this.groupUsers.indexOf(groupUser) != -1) {
//            this.groupUsers.set(this.groupUsers.indexOf(groupUser), groupUser);
//            notifyItemChanged(this.groupUsers.indexOf(groupUser));
//        }
//    }

//    public void updateCurrentGroupUserImageTS(Long timestamp){
//        String userId = AppConfigHelper.getUserId();
//        for(GroupUserModel groupUser: groupUsers){
//            if(groupUser.getUserId().equals(userId)){
//                if(Objects.equals(groupUser.getImageUpdateTimestamp(), timestamp)){
//                    return;
//                }
//                groupUser.setImageUpdateTimestamp(timestamp);
//                setGroupUser(groupUser);
//            }
//        }
//    }
//
//    public void updateCurrentGroupUserImagePath(String imagePath){
//        String userId = AppConfigHelper.getUserId();
//        for(GroupUserModel groupUser: groupUsers){
//            if(groupUser.getUserId().equals(userId)){
//                if(Objects.equals(groupUser.getImagePath(), imagePath)){
//                    return;
//                }
//                groupUser.setImagePath(imagePath);
//                setGroupUser(groupUser);
//            }
//        }
//    }

    private View.OnLongClickListener getGroupUserLongClickListener(final GroupUserModel groupUser) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                groupUserLongClickListener.onGroupUserLongClick(groupUser);
                return true;
            }
        };
    }

    private View.OnClickListener getGroupUserImageClickListener(final GroupUserModel groupUser) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
             groupUserImageClickListener.onGroupUserImageClick(groupUser);
            }
        };
    }

    private View.OnClickListener getGroupUserClickListener(final GroupUserModel groupUser) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                groupUserClickListener.onGroupUserClick(groupUser);
            }
        };
    }
}
