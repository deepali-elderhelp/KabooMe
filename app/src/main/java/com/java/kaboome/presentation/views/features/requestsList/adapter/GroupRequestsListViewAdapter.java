/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */



package com.java.kaboome.presentation.views.features.requestsList.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.GroupRequestsListStatusConstants;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.views.features.groupList.adapter.LoadingViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GroupRequestsListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "KMGrpReqsListViewAdap";

    private static final int REQUESTS_LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int NO_REQUESTS_TYPE = 3;

    private final Handler handler = new Handler(); //needed for glide


    private List<GroupRequestModel> requests = new ArrayList<>();
    private Context mContext;
//    private RequestManager requestManager;
    private GroupRequestImageClickListener groupRequestImageClickListener;
    private GroupRequestAcceptClickListener groupRequestAcceptClickListener;
    private GroupRequestRejectClickListener groupRequestRejectClickListener;

//    public GroupRequestsListViewAdapter(RequestManager requestManager,
//                                        GroupRequestImageClickListener groupRequestImageClickListener,
//                                        GroupRequestAcceptClickListener groupRequestAcceptClickListener,
//                                        GroupRequestRejectClickListener groupRequestRejectClickListener) {

        public GroupRequestsListViewAdapter(
                                        GroupRequestImageClickListener groupRequestImageClickListener,
                                        GroupRequestAcceptClickListener groupRequestAcceptClickListener,
                                        GroupRequestRejectClickListener groupRequestRejectClickListener) {

//        mContext = context;
//        this.requestManager = requestManager;
        this.groupRequestImageClickListener = groupRequestImageClickListener;
        this.groupRequestAcceptClickListener = groupRequestAcceptClickListener;
        this.groupRequestRejectClickListener = groupRequestRejectClickListener;
    }


    public void setGroupRequests(List<GroupRequestModel> groupRequestModels){
        if(this.requests != null){
            this.requests.clear();
        }
        this.requests = groupRequestModels;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        Log.d(TAG, "onCreateViewHolder: View Type is "+viewType);

        switch (viewType) {

            case REQUESTS_LIST_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_request_list_item, parent, false);
//                return new GroupRequestsListViewHolder(view, requestManager, groupRequestImageClickListener, groupRequestAcceptClickListener, groupRequestRejectClickListener);
                return new GroupRequestsListViewHolder(view, groupRequestImageClickListener, groupRequestAcceptClickListener, groupRequestRejectClickListener);

            }

            case LOADING_TYPE: {
                Log.d(TAG, "onCreateViewHolder: Loading type view");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            }

            case NO_REQUESTS_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_group_requests_item, parent, false);
                return new NoGroupRequestsViewHolder(view);
            }

            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_request_list_item, parent, false);
//                return new GroupRequestsListViewHolder(view, requestManager, groupRequestImageClickListener, groupRequestAcceptClickListener, groupRequestRejectClickListener);
                return new GroupRequestsListViewHolder(view,  groupRequestImageClickListener, groupRequestAcceptClickListener, groupRequestRejectClickListener);
            }


        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: "+ requests.get(position).getGroupId());
        if(requests.get(position).getGroupId().equals(GroupRequestsListStatusConstants.LOADING.toString())) {
            return LOADING_TYPE;
        }
        else if(requests.get(position).getGroupId().equals(GroupRequestsListStatusConstants.NO_REQUESTS.toString())) {
            return NO_REQUESTS_TYPE;
        }
        else{
            return REQUESTS_LIST_TYPE;
        }
    }

    public void displayLoading() {
        if (requests == null) {
            requests = new ArrayList<>();
        }
        if (!isLoading()) {
            Log.d(TAG, "displayLoading: coming to only adding it to the list, with requests - "+ requests.size());
            GroupRequestModel groupRequestModel = new GroupRequestModel();
            groupRequestModel.setGroupId(GroupRequestsListStatusConstants.LOADING.toString());
            requests.add(groupRequestModel);
            notifyDataSetChanged();
        }
    }

    public void displayOnlyLoading(){
        clearGroupRequestsList();
        GroupRequestModel groupRequestModel = new GroupRequestModel();
        groupRequestModel.setGroupId(GroupRequestsListStatusConstants.LOADING.toString());
        requests.add(groupRequestModel);
        notifyDataSetChanged();
    }

    private boolean isLoading(){
        if(requests != null){
            if(requests.size() > 0){
                if(requests.get(requests.size() - 1).getGroupId().equals(GroupRequestsListStatusConstants.LOADING.toString())){
                    return true;
                }
            }
        }
        return false;
    }

    public void hideLoading(){
        if(isLoading()){
            if(requests.get(0).getGroupId().equals(GroupRequestsListStatusConstants.LOADING.toString())){
                requests.remove(0);
            }
            else if(requests.get(requests.size() - 1).getGroupId().equals(GroupRequestsListStatusConstants.LOADING.toString())){
                requests.remove(requests.size() - 1);
            }
            notifyDataSetChanged();
        }
    }

    private void clearGroupRequestsList(){
        if(requests == null){
            requests = new ArrayList<>();
        }
        else{
            requests.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        int itemViewType = getItemViewType(position);

        if(itemViewType == REQUESTS_LIST_TYPE){

            ((GroupRequestsListViewHolder)holder).onBind(requests.get(position), handler);
        }



    }

    @Override
    public int getItemCount() {
        if(requests != null)
            return requests.size();
        return 0; //  this could be when loading
    }



}



