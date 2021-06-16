/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */



package com.java.kaboome.presentation.views.features.groupSearch.adapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.GroupListStatusConstants;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.views.features.groupList.adapter.LoadingViewHolder;
import com.java.kaboome.presentation.views.features.groupList.adapter.NoGroupsViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GroupsSearchListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "KMGroupsSearchLVAdapter";

    private static final int GROUPS_LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int NO_GROUPS_TYPE = 3;

    private final Handler handler = new Handler(); //needed for glide


    private List<GroupModel> groups = new ArrayList<>();
//    private RequestManager requestManager;
    private GroupClickListener groupClickListener;
    private JoinGroupClickListener joinGroupClickListener;

//    public GroupsSearchListViewAdapter(RequestManager requestManager, GroupClickListener groupClickListener, JoinGroupClickListener joinGroupClickListener) {
    public GroupsSearchListViewAdapter( GroupClickListener groupClickListener, JoinGroupClickListener joinGroupClickListener) {

//        this.requestManager = requestManager;
        this.groupClickListener = groupClickListener;
        this.joinGroupClickListener = joinGroupClickListener;
    }

    public void setGroups(List<GroupModel> groups){
        if(this.groups != null){
            this.groups.clear();
        }
        this.groups.addAll(groups);
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
//        Log.d(TAG, "onCreateViewHolder: View Type is "+viewType);

        switch (viewType) {

            case GROUPS_LIST_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_search_group_list_item, parent, false);
//                return new SearchGroupsListViewHolder(view, requestManager, groupClickListener, joinGroupClickListener);
                return new SearchGroupsListViewHolder(view,  groupClickListener, joinGroupClickListener);

            }

            case LOADING_TYPE: {
                Log.d(TAG, "onCreateViewHolder: Loading type view");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            }

            case NO_GROUPS_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_groups_matched_search, parent, false);
                return new NoGroupsViewHolder(view);
            }

            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_list_item, parent, false);
//                return new SearchGroupsListViewHolder(view, requestManager, groupClickListener, joinGroupClickListener);
                return new SearchGroupsListViewHolder(view, groupClickListener, joinGroupClickListener);
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

//    public void displayLoading() {
//        if (groups == null) {
//            groups = new ArrayList<>();
//        }
//        if (!isLoading()) {
//            Log.d(TAG, "displayLoading: coming to only adding it to the list, with groups - "+groups.size());
//            UserGroupModel userGroup = new UserGroupModel();
//            userGroup.setGroupId(GroupListStatusConstants.LOADING.toString());
//            groups.add(userGroup);
//            notifyDataSetChanged();
//        }
//    }
//
//    public void displayOnlyLoading(){
//        clearGroupsList();
//        UserGroupModel userGroup = new UserGroupModel();
//        userGroup.setGroupId(GroupListStatusConstants.LOADING.toString());
//        groups.add(userGroup);
//        notifyDataSetChanged();
//    }

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

    public void clearGroupsList(){
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
        Log.d(TAG, "onBindViewHolder: called.");

        int itemViewType = getItemViewType(position);

        if(itemViewType == GROUPS_LIST_TYPE){
            //update map position for later use
//            String groupId = groups.get(position).getGroupId();
//            groupIdPositionMap.put(groupId, position);

            ((SearchGroupsListViewHolder)holder).onBind(groups.get(position), handler);
        }



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



