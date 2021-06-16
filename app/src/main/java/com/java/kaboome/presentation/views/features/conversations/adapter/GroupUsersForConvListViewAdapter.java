package com.java.kaboome.presentation.views.features.conversations.adapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupAliasAndRoleEditClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserImageClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserLongClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUsersAddAdminViewHolder;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUsersViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupUsersForConvListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "KMGUForConvListViewAdap";

    private final Handler handler = new Handler(); //needed for glide
    private NewConversationClickListener newConversationClickListener;


    private List<GroupUserModel> groupUsers = new ArrayList<>();


    public GroupUsersForConvListViewAdapter(NewConversationClickListener newConversationClickListener) {
        this.newConversationClickListener = newConversationClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_user_add_conv_item, parent, false);
            return new GroupUsersForConvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupUserModel groupUser = groupUsers.get(position);
        ((GroupUsersForConvViewHolder)holder).onBind(groupUsers.get(position), handler, getNewConversationClickListener(groupUser));

    }

    @Override
    public int getItemCount() {
        //over here if the type is members_user_type, return only 10 - max users for that screen
        if(groupUsers != null){
            return groupUsers.size();
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
//        if(this.groupUsers != null) {
//            this.groupUsers.set(this.groupUsers.indexOf(groupUser), groupUser);
//            notifyItemChanged(this.groupUsers.indexOf(groupUser));
//        }
//    }


    private View.OnClickListener getNewConversationClickListener(final GroupUserModel groupUser) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newConversationClickListener.onNewConvClick(groupUser);
            }
        };
    }


}
