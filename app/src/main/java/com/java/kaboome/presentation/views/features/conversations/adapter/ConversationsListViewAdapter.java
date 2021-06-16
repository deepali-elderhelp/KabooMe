/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */



package com.java.kaboome.presentation.views.features.conversations.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.constants.GroupListStatusConstants;
import com.java.kaboome.constants.ReceivedGroupDataTypeConstants;
import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.views.features.groupList.adapter.LoadingViewHolder;
import com.java.kaboome.presentation.views.features.groupList.adapter.NoGroupsViewHolder;
import com.java.kaboome.presentation.views.features.groupList.adapter.UserGroupMessagesClickListener;
import com.java.kaboome.presentation.views.features.groupList.adapter.UserGroupsListViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConversationsListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "KMConvsListViewAdapter";

    private static final int GROUPS_LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int NO_GROUPS_TYPE = 3;

    private final Handler handler = new Handler(); //needed for glide


    private List<UserGroupConversationModel> conversations = new ArrayList<>();
    private Context context;
    private Fragment callingFragment;
    private ConversationImageClickListener conversationImageClickListener;
    private ConversationMessagesClickListener conversationMessagesClickListener;

    public ConversationsListViewAdapter(Context context, ConversationImageClickListener conversationImageClickListener,
                                        ConversationMessagesClickListener conversationMessagesClickListener) {

        this.context = context;
        this.conversationImageClickListener = conversationImageClickListener;
        this.conversationMessagesClickListener = conversationMessagesClickListener;
    }

    public void setConversations(List<UserGroupConversationModel> conversations){

        if(conversations == null){
            return;
        }
        if(this.conversations != null){
            this.conversations.clear();
        }
        Collections.sort(conversations, new ConvsListLastMessageComparator());
        for(UserGroupConversationModel groupModel: conversations){
            Log.d(TAG, "Group - "+groupModel.getGroupId());
        }
        this.conversations = conversations;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
//        Log.d(TAG, "onCreateViewHolder: View Type is "+viewType);

        switch (viewType) {

            case GROUPS_LIST_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_conv_list_item, parent, false);
                return new ConversationsListViewHolder(view, context, conversationImageClickListener, conversationMessagesClickListener);
            }

            case LOADING_TYPE: {
//                Log.d(TAG, "onCreateViewHolder: Loading type view");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new ConversastionsLoadingViewHolder(view);
            }

            case NO_GROUPS_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_convs_added_item, parent, false);
                return new NoConversationsViewHolder(view);
            }

            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_conv_list_item, parent, false);
                return new ConversationsListViewHolder(view, context, conversationImageClickListener, conversationMessagesClickListener);
            }


        }
    }

    @Override
    public int getItemViewType(int position) {
//        Log.d(TAG, "getItemViewType: "+groups.get(position).getGroupId());
        if(conversations.get(position).getGroupId().equals(GroupListStatusConstants.LOADING.toString())) {
            return LOADING_TYPE;
        }
        else if(conversations.get(position).getGroupId().equals(GroupListStatusConstants.NO_GROUPS.toString())) {
            return NO_GROUPS_TYPE;
        }
        else{
            return GROUPS_LIST_TYPE;
        }
    }



    private boolean isLoading(){
        if(conversations != null){
            if(conversations.size() > 0){
                if(conversations.get(conversations.size() - 1).getGroupId().equals(GroupListStatusConstants.LOADING.toString())){
                    return true;
                }
            }
        }
        return false;
    }

    public void hideLoading(){
        if(isLoading()){
            if(conversations.get(0).getGroupId().equals(GroupListStatusConstants.LOADING.toString())){
                conversations.remove(0);
            }
            else if(conversations.get(conversations.size() - 1).getGroupId().equals(GroupListStatusConstants.LOADING.toString())){
                conversations.remove(conversations.size() - 1);
            }
            notifyDataSetChanged();
        }
    }

    private void clearGroupsList(){
        if(conversations == null){
            conversations = new ArrayList<>();
        }
        else{
            conversations.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        Log.d(TAG, "onBindViewHolder: called.");

        int itemViewType = getItemViewType(position);

        if(itemViewType == GROUPS_LIST_TYPE){
            //update map position for later use
//            String groupId = groups.get(position).getGroupId();
//            groupIdPositionMap.put(groupId, position);

            ((ConversationsListViewHolder)holder).onBind(conversations.get(position), handler);
        }
        if(itemViewType == NO_GROUPS_TYPE){
            //update map position for later use
//            String groupId = groups.get(position).getGroupId();
//            groupIdPositionMap.put(groupId, position);

            ((NoConversationsViewHolder)holder).onBind();
        }



    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        Log.d(TAG, "onViewRecycled: ");
        super.onViewRecycled(holder);
        if(holder instanceof ConversationsListViewHolder){
            ((ConversationsListViewHolder)holder).recycle();
        }
    }

    public void updateConversation(UserGroupConversationModel newConversationModel){

        ReceivedGroupDataTypeConstants dataTypeToUpdate = newConversationModel.getReceivedGroupDataType();
        int currentUserGroupIndex = conversations.indexOf(newConversationModel);
        if(currentUserGroupIndex == -1){ //it could happen for deleted groups too
            return;
        }
        UserGroupConversationModel currentConversation = conversations.get(conversations.indexOf(newConversationModel));

        //do nothing if all the values are same - only notify item changed if any values are different



        if(ReceivedGroupDataTypeConstants.UNREAD_COUNT.equals(dataTypeToUpdate)){
            Log.d(TAG, "unread count - "+newConversationModel.getUnreadCount());
            updateUnreadCount(newConversationModel, currentConversation);
        }
        if(ReceivedGroupDataTypeConstants.LAST_MESSAGE.equals(dataTypeToUpdate)){

            updateLastMessageText(newConversationModel, currentConversation);
        }

        if(ReceivedGroupDataTypeConstants.BOTH_UNREAD_AND_LAST.equals(dataTypeToUpdate)){
            updateUnreadCount(newConversationModel, currentConversation);
            updateLastMessageText(newConversationModel, currentConversation);
        }


        Log.d(TAG, "updateGroup: Item is changing for "+dataTypeToUpdate+" "+newConversationModel.getGroupId());
//        if(dataTypeToUpdate.equals(ReceivedGroupDataTypeConstants.LAST_MESSAGE)){
//            Log.d(TAG, "message changed from "+currentConversation.getLastMessageText()+" to "+newConversationModel.getLastMessageText());
//        }

        //the following code is for the re-order of groups based upon new messages or request received

        //check the perf when there are lots of groups
        conversations.set(conversations.indexOf(newConversationModel), currentConversation);
        Collections.sort(conversations, new ConvsListLastMessageComparator());
//        Log.d(TAG, "groups now - "+groups);
        notifyDataSetChanged();

//        groups.set(groups.indexOf(newConversationModel), currentConversation);
//        notifyItemChanged(groups.indexOf(currentConversation));
    }

    private void updateLastMessageText(UserGroupConversationModel newUserGroup, UserGroupConversationModel currentUserGroup) {
        if( currentUserGroup.isSameLastMessageText(newUserGroup.getLastMessageText()) &&
            currentUserGroup.isSameLastMessageSentBy(newUserGroup.getLastMessageSentBy()) &&
            currentUserGroup.getLastMessageSentAt() == (newUserGroup.getLastMessageSentAt())){
            return;
        }
        currentUserGroup.setLastMessageText(newUserGroup.getLastMessageText());
        currentUserGroup.setLastMessageSentBy(newUserGroup.getLastMessageSentBy());
        currentUserGroup.setLastMessageSentAt(newUserGroup.getLastMessageSentAt());
    }

    private void updateUnreadCount(UserGroupConversationModel newUserGroup, UserGroupConversationModel currentUserGroup) {
        if(currentUserGroup.getUnreadCount() == newUserGroup.getUnreadCount()){
            return;
        }
        currentUserGroup.setUnreadCount(newUserGroup.getUnreadCount());
    }

    @Override
    public int getItemCount() {
        if(conversations != null)
            return conversations.size();
        return 0; //  this could be when loading
    }

}

class ConvsListLastMessageComparator implements Comparator<UserGroupConversationModel> {
    private static final String TAG = "KMGroupsListLastTime";

    @Override
    public int compare(UserGroupConversationModel o1, UserGroupConversationModel o2) {

        if(o1 == null || (o1.getLastMessageSentAt() == null)){
            return -1;
        }
        if(o2 == null || (o2.getLastMessageSentAt()  == null)){
            return 1;
        }
        if (o1.getLastMessageSentAt() > o2.getLastMessageSentAt())
            return  -1; //no swap since we need reverse order
        //the following is very important, since lots of them are 0, so sort gets confused otherwise
        //It was giving a lot of java.lang.IllegalArgumentException: Comparison method violates its general contract!
        if(o1.getLastMessageSentAt() == o2.getLastMessageSentAt())
            return -1;
        return  1;

    }

}



