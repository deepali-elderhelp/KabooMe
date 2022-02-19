/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */



package com.java.kaboome.presentation.views.features.groupList.adapter;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.GroupListStatusConstants;
import com.java.kaboome.constants.ReceivedGroupDataTypeConstants;
import com.java.kaboome.presentation.entities.UserGroupModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserGroupsListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "KMUGrpsListViewAdapter";

    private static final int GROUPS_LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int NO_GROUPS_TYPE = 3;

    private final Handler handler = new Handler(); //needed for glide


    private List<UserGroupModel> groups = new ArrayList<>();
//    private HashMap<String, Integer> groupIdPositionMap = new HashMap<>();
    private Context context;
    private Fragment callingFragment;
    private UserGroupImageClickListener userGroupImageClickListener;
    private UserGroupMessagesClickListener userGroupMessagesClickListener;
    private UserGroupRequestsClickListener userGroupRequestsClickListener;

    public UserGroupsListViewAdapter(Context context, UserGroupImageClickListener userGroupImageClickListener,
                                     UserGroupMessagesClickListener userGroupMessagesClickListener,
                                     UserGroupRequestsClickListener userGroupRequestsClickListener) {

        this.context = context;
        this.userGroupImageClickListener = userGroupImageClickListener;
        this.userGroupMessagesClickListener = userGroupMessagesClickListener;
        this.userGroupRequestsClickListener = userGroupRequestsClickListener;
    }

//    public UserGroupsListViewAdapter(Context context, Fragment callingFragment, List<UserGroupModel> groups ) {
//
//        this.groups = groups;
//        mContext = context;
//        this.callingFragment = callingFragment;
//    }

    public void setGroups(List<UserGroupModel> groups){

        if(groups == null){
            return;
        }
        if(this.groups != null){
            this.groups.clear();
        }
        Collections.sort(groups, new GroupsListLastMessageComparator());
        for(UserGroupModel groupModel: groups){
            Log.d(TAG, "Group - "+groupModel.getGroupName());
        }
//        this.groups = groups;
        this.groups.addAll(groups);
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
//        Log.d(TAG, "onCreateViewHolder: View Type is "+viewType);

        switch (viewType) {

            case GROUPS_LIST_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_list_item, parent, false);
                return new UserGroupsListViewHolder(view, context, userGroupImageClickListener, userGroupMessagesClickListener, userGroupRequestsClickListener);
            }

            case LOADING_TYPE: {
//                Log.d(TAG, "onCreateViewHolder: Loading type view");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            }

            case NO_GROUPS_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_groups_added_item, parent, false);
                return new NoGroupsViewHolder(view);
            }

            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_list_item, parent, false);
                return new UserGroupsListViewHolder(view, context, userGroupImageClickListener, userGroupMessagesClickListener, userGroupRequestsClickListener);
            }


        }
    }

    @Override
    public int getItemViewType(int position) {
//        Log.d(TAG, "getItemViewType: "+groups.get(position).getGroupId());
        if(groups.get(position).getGroupId().equals(GroupListStatusConstants.LOADING.toString())) {
            return LOADING_TYPE;
        }
        else if(groups.get(position).getGroupId().equals(GroupListStatusConstants.NO_GROUPS.toString())) {
            return NO_GROUPS_TYPE;
        }
        else{
            return GROUPS_LIST_TYPE;
        }
    }

    public void displayLoading() {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        if (!isLoading()) {
            Log.d(TAG, "displayLoading: coming to only adding it to the list, with groups - "+groups.size());
            UserGroupModel userGroup = new UserGroupModel();
            userGroup.setGroupId(GroupListStatusConstants.LOADING.toString());
            groups.add(userGroup);
            notifyDataSetChanged();
        }
    }

    public void displayOnlyLoading(){
        clearGroupsList();
        UserGroupModel userGroup = new UserGroupModel();
        userGroup.setGroupId(GroupListStatusConstants.LOADING.toString());
        groups.add(userGroup);
        notifyDataSetChanged();
    }

//    public void displayLoading(){
//        if(!isLoading()){
//            UserGroup userGroup = new UserGroup();
//            userGroup.setGroupName("LOADING...");
//            List<UserGroup> loadingList = new ArrayList<>();
//            loadingList.add(userGroup);
//            groups = loadingList;
//            notifyDataSetChanged();
//        }
//    }

    private boolean isLoading(){
        if(groups != null){
            if(groups.size() > 0){
                if(groups.get(groups.size() - 1).getGroupId().equals(GroupListStatusConstants.LOADING.toString())){
                    return true;
                }
            }
        }
        return false;
    }

    public void hideLoading(){
        if(isLoading()){
            if(groups.get(0).getGroupId().equals(GroupListStatusConstants.LOADING.toString())){
                groups.remove(0);
            }
            else if(groups.get(groups.size() - 1).getGroupId().equals(GroupListStatusConstants.LOADING.toString())){
                groups.remove(groups.size() - 1);
            }
            notifyDataSetChanged();
        }
    }

    private void clearGroupsList(){
        if(groups == null){
            groups = new ArrayList<>();
        }
        else{
            groups.clear();
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

            ((UserGroupsListViewHolder)holder).onBind(groups.get(position), handler);
        }
        if(itemViewType == NO_GROUPS_TYPE){
            //update map position for later use
//            String groupId = groups.get(position).getGroupId();
//            groupIdPositionMap.put(groupId, position);

            ((NoGroupsViewHolder)holder).onBind();
        }



    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        Log.d(TAG, "onViewRecycled: ");
        super.onViewRecycled(holder);
        if(holder instanceof UserGroupsListViewHolder){
            ((UserGroupsListViewHolder)holder).recycle();
        }
    }

    public void updateGroup(UserGroupModel newUserGroup){

        ReceivedGroupDataTypeConstants dataTypeToUpdate = newUserGroup.getReceivedGroupDataType();
        int currentUserGroupIndex = groups.indexOf(newUserGroup);
        if(currentUserGroupIndex == -1){ //it could happen for deleted groups too
            return;
        }
        UserGroupModel currentUserGroup = groups.get(groups.indexOf(newUserGroup));

        //do nothing if all the values are same - only notify item changed if any values are different



        if(ReceivedGroupDataTypeConstants.UNREAD_COUNT.equals(dataTypeToUpdate)){
//            Log.d(TAG, "unread count - "+newUserGroup.getUnreadCount());
            updateUnreadCount(newUserGroup, currentUserGroup);
        }
        if(ReceivedGroupDataTypeConstants.UNREAD_PM_COUNT.equals(dataTypeToUpdate)){
//            Log.d(TAG, "unread count - "+newUserGroup.getUnreadCount());
            updateUnreadPMCount(newUserGroup, currentUserGroup);
        }
        if(ReceivedGroupDataTypeConstants.LAST_MESSAGE.equals(dataTypeToUpdate)){

            updateLastMessageText(newUserGroup, currentUserGroup);
        }

        if(ReceivedGroupDataTypeConstants.BOTH_UNREAD_AND_LAST.equals(dataTypeToUpdate)){
            updateUnreadCount(newUserGroup, currentUserGroup);
            updateLastMessageText(newUserGroup, currentUserGroup);
        }

        if(ReceivedGroupDataTypeConstants.REQUESTS_DATA.equals(dataTypeToUpdate)){
            if(currentUserGroup.getNumberOfRequests() == newUserGroup.getNumberOfRequests()){
                return; //nothing has changed
            }
            currentUserGroup.setNumberOfRequests(newUserGroup.getNumberOfRequests());
            currentUserGroup.setLastRequestSentAt(newUserGroup.getLastRequestSentAt());
        }

//        Log.d(TAG, "updateGroup: Item is changing for "+dataTypeToUpdate+" "+newUserGroup.getGroupName());
//        if(dataTypeToUpdate.equals(ReceivedGroupDataTypeConstants.LAST_MESSAGE)){
//            Log.d(TAG, "message changed from "+currentUserGroup.getLastMessageText()+" to "+newUserGroup.getLastMessageText());
//        }

        //the following code is for the re-order of groups based upon new messages or request received

        //check the perf when there are lots of groups
        groups.set(groups.indexOf(newUserGroup), currentUserGroup);
        Collections.sort(groups, new GroupsListLastMessageComparator());
//        Log.d(TAG, "groups now - "+groups);
        notifyDataSetChanged();

//        groups.set(groups.indexOf(newUserGroup), currentUserGroup);
//        notifyItemChanged(groups.indexOf(currentUserGroup));
    }

    private void updateLastMessageText(UserGroupModel newUserGroup, UserGroupModel currentUserGroup) {
        if( currentUserGroup.isSameLastMessageText(newUserGroup.getLastMessageText()) &&
            currentUserGroup.isSameLastMessageSentBy(newUserGroup.getLastMessageSentBy()) &&
            currentUserGroup.getLastMessageSentAt() == (newUserGroup.getLastMessageSentAt())){
            return;
        }
        currentUserGroup.setLastMessageText(newUserGroup.getLastMessageText());
        currentUserGroup.setLastMessageSentBy(newUserGroup.getLastMessageSentBy());
        currentUserGroup.setLastMessageSentAt(newUserGroup.getLastMessageSentAt());
    }

    private void updateUnreadCount(UserGroupModel newUserGroup, UserGroupModel currentUserGroup) {
        if(currentUserGroup.getUnreadCount() == newUserGroup.getUnreadCount()){
            return;
        }
        currentUserGroup.setUnreadCount(newUserGroup.getUnreadCount());
    }
    private void updateUnreadPMCount(UserGroupModel newUserGroup, UserGroupModel currentUserGroup) {
        if(currentUserGroup.getUnreadPMCount() == newUserGroup.getUnreadPMCount()){
            return;
        }
        Log.d(TAG, "currentUserGroup.getUnreadPMCount() - "+currentUserGroup.getUnreadPMCount());
        Log.d(TAG, "newUserGroup.getUnreadPMCount() - "+newUserGroup.getUnreadPMCount());
        currentUserGroup.setUnreadPMCount(newUserGroup.getUnreadPMCount());
    }

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
//
//        if(!payloads.isEmpty()) {
//
//            Log.d(TAG, "onBindViewHolder: payload not empty");
//            UserGroupModel group = groups.get(position);
//
//            //the payload class should be of type GroupLastAccessPayload
//            //if some group's last access needs to be updated
////            if (payloads.get(0) instanceof GroupLastAccessPayload) {
////                GroupLastAccessPayload payload = (GroupLastAccessPayload) payloads.get(0);
////
////                int itemViewType = getItemViewType(position);
////                if(itemViewType == GROUPS_LIST_TYPE){
////                    ((UserGroupsListViewHolder)holder).onBindGroupLastAccess(group, payload);
////                }
////            }
////            else
//                if(payloads.get(0) instanceof GroupNewUnreadCountPayload){
//                GroupNewUnreadCountPayload payload = (GroupNewUnreadCountPayload)payloads.get(0);
//                int itemViewType = getItemViewType(position);
//                if(itemViewType == GROUPS_LIST_TYPE){
//                    ((UserGroupsListViewHolder)holder).onBindGroupUnread(payload);
//                }
//            }
//        }
//        else {
//            super.onBindViewHolder(holder,position, payloads);
//        }
//    }

    @Override
    public int getItemCount() {
        if(groups != null)
            return groups.size();
        return 0; //  this could be when loading
    }

//    public void setNoGroupsAdded() {
//        UserGroupModel userGroup = new UserGroupModel();
//        userGroup.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
//        List<UserGroupModel> loadingList = new ArrayList<>();
//        loadingList.add(userGroup);
//        groups = loadingList;
//        notifyDataSetChanged();
//    }

//    public int getGroupsPosition(String groupId){
//        if(groupIdPositionMap.size() > 0)
//            return groupIdPositionMap.get(groupId);
//        return -1; //groupIdPositionMap not set yet
//    }


//    public void updateGroupForLastAccessChange(GroupLastAccessPayload groupLastAccessPayload) {
//        if(groupLastAccessPayload != null){
//            int groupsPosition = getGroupsPosition(groupLastAccessPayload.getGroupId());
//            if(groupsPosition != -1)
//                notifyItemChanged(groupsPosition, groupLastAccessPayload);
//        }
//
//    }

//    public void updateMessageCount(String groupId, int newCount, String lastMessageString){
//        if(groupId != null){
//            int groupsPosition = getGroupsPosition(groupId);
//            if(groupsPosition != -1)
//                notifyItemChanged(groupsPosition, new GroupNewUnreadCountPayload(groupId, newCount, lastMessageString));
//        }
//
//    }



}

class GroupsListLastMessageComparator implements Comparator<UserGroupModel> {
    private static final String TAG = "KMGroupsListLastTime";

    @Override
    public int compare(UserGroupModel o1, UserGroupModel o2) {

        if(o1 == null || (o1.getLastMessageSentAt() == null)){
            return -1;
        }
        if(o2 == null || (o2.getLastMessageSentAt()  == null)){
            return 1;
        }
        long o1LastActiveTime = o1.getLastMessageSentAt();
        if(o1.getLastRequestSentAt() != null && (o1.getLastRequestSentAt() > o1.getLastMessageSentAt())) {
            o1LastActiveTime = o1.getLastRequestSentAt();
        }

        long o2LastActiveTime = o2.getLastMessageSentAt();
        if(o2.getLastRequestSentAt() != null && (o2.getLastRequestSentAt() > o2.getLastMessageSentAt())) {
            o2LastActiveTime = o2.getLastRequestSentAt();
        }

        if (o1LastActiveTime > o2LastActiveTime)
            return  -1; //no swap since we need reverse order
        //the following is very important, since lots of them are 0, so sort gets confused otherwise
        //It was giving a lot of java.lang.IllegalArgumentException: Comparison method violates its general contract!
        if(o1LastActiveTime == o2LastActiveTime)
            return -1;
        return  1;



//        if(o1 == null || o1.getLastMessageSentAt() == null){
//            return -1;
//        }
//        if(o2 == null || o2.getLastMessageSentAt() == null){
//            return 1;
//        }
//        if(o1.getLastMessageSentAt() > o2.getLastMessageSentAt())
//            return -1; //no swap since we need reverse order
//        //the following is very important, since lots of them are 0, so sort gets confused otherwise
//        //It was giving a lot of java.lang.IllegalArgumentException: Comparison method violates its general contract!
//        if(o1.getLastMessageSentAt() == o2.getLastMessageSentAt())
//            return -1;
//        return  1;

        }

}



