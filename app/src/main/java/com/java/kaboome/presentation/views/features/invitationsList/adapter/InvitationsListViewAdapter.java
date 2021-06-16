/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */



package com.java.kaboome.presentation.views.features.invitationsList.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.InvitationListStatusConstants;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.views.features.groupList.adapter.LoadingViewHolder;

import java.util.ArrayList;
import java.util.List;

public class InvitationsListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "KMInviListViewAdapter";

    private static final int INVITATIONS_LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int NO_INVITATIONS_TYPE = 3;

    private final Handler handler = new Handler(); //needed for glide


    private List<InvitationModel> invitations = new ArrayList<>();
    private Context mContext;
    private RequestManager requestManager;
    private InvitationGroupImageClickListener invitationGroupImageClickListener;
    private InvitationGroupRejectClickListener invitationGroupRejectClickListener;
    private JoinInvitedGroupClickListener joinInvitedGroupClickListener;

    public InvitationsListViewAdapter(RequestManager requestManager,
                                      InvitationGroupImageClickListener invitationGroupImageClickListener,
                                      InvitationGroupRejectClickListener invitationGroupRejectClickListener,
                                      JoinInvitedGroupClickListener joinInvitedGroupClickListener) {

//        mContext = context;
        this.requestManager = requestManager;
        this.invitationGroupImageClickListener = invitationGroupImageClickListener;
        this.invitationGroupRejectClickListener = invitationGroupRejectClickListener;
        this.joinInvitedGroupClickListener = joinInvitedGroupClickListener;
    }


    public void setInvitationGroups(List<InvitationModel> invitationGroups){
        if(this.invitations != null){
            this.invitations.clear();
        }
        this.invitations.addAll(invitationGroups);
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        Log.d(TAG, "onCreateViewHolder: View Type is "+viewType);

        switch (viewType) {

            case INVITATIONS_LIST_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_invitation_list_item, parent, false);
                return new InvitationsListViewHolder(view, requestManager, invitationGroupImageClickListener, invitationGroupRejectClickListener, joinInvitedGroupClickListener);

            }

            case LOADING_TYPE: {
                Log.d(TAG, "onCreateViewHolder: Loading type view");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            }

            case NO_INVITATIONS_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_invitations_item, parent, false);
                return new NoInvitationsViewHolder(view);
            }

            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_invitation_list_item, parent, false);
                return new InvitationsListViewHolder(view, requestManager, invitationGroupImageClickListener, invitationGroupRejectClickListener, joinInvitedGroupClickListener);
            }


        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: "+invitations.get(position).getGroupId());
        if(invitations.get(position).getGroupId().equals(InvitationListStatusConstants.LOADING.toString())) {
            return LOADING_TYPE;
        }
        else if(invitations.get(position).getGroupId().equals(InvitationListStatusConstants.NO_INVITATIONS.toString())) {
            return NO_INVITATIONS_TYPE;
        }
        else{
            return INVITATIONS_LIST_TYPE;
        }
    }

    public void displayLoading() {
        if (invitations == null) {
            invitations = new ArrayList<>();
        }
        if (!isLoading()) {
            Log.d(TAG, "displayLoading: coming to only adding it to the list, with invitations - "+invitations.size());
            InvitationModel invitationModel = new InvitationModel();
            invitationModel.setGroupId(InvitationListStatusConstants.LOADING.toString());
            invitations.add(invitationModel);
            notifyDataSetChanged();
        }
    }

    public void displayOnlyLoading(){
        clearInvitationsList();
        InvitationModel invitationModel = new InvitationModel();
        invitationModel.setGroupId(InvitationListStatusConstants.LOADING.toString());
        invitations.add(invitationModel);
        notifyDataSetChanged();
    }

    private boolean isLoading(){
        if(invitations != null){
            if(invitations.size() > 0){
                if(invitations.get(invitations.size() - 1).getGroupId().equals(InvitationListStatusConstants.LOADING.toString())){
                    return true;
                }
            }
        }
        return false;
    }

    public void hideLoading(){
        if(isLoading()){
            if(invitations.get(0).getGroupId().equals(InvitationListStatusConstants.LOADING.toString())){
                invitations.remove(0);
            }
            else if(invitations.get(invitations.size() - 1).getGroupId().equals(InvitationListStatusConstants.LOADING.toString())){
                invitations.remove(invitations.size() - 1);
            }
            notifyDataSetChanged();
        }
    }

    private void clearInvitationsList(){
        if(invitations == null){
            invitations = new ArrayList<>();
        }
        else{
            invitations.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        InvitationModel invitationModel = invitations.get(position);
        if(invitationModel != null){

            int itemViewType = getItemViewType(position);
            if(itemViewType == INVITATIONS_LIST_TYPE){

                ((InvitationsListViewHolder)holder).onBind(invitations.get(position), getInvitationClickListener(invitationModel), handler);
            }

        }



    }

    @Override
    public int getItemCount() {
        if(invitations != null)
            return invitations.size();
        return 0; //  this could be when loading
    }

    private View.OnClickListener getInvitationClickListener(final  InvitationModel invitationModel){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationGroupImageClickListener.onGroupImageClick(invitationModel);
            }
        };
    }



}



