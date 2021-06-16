package com.java.kaboome.presentation.views.features.requestsList;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.viewModelProvider.CustomViewModelProvider;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.requestsList.viewmodel.GroupRequestsListViewModel;
import com.java.kaboome.presentation.views.features.requestsList.adapter.GroupRequestAcceptClickListener;
import com.java.kaboome.presentation.views.features.requestsList.adapter.GroupRequestImageClickListener;
import com.java.kaboome.presentation.views.features.requestsList.adapter.GroupRequestRejectClickListener;
import com.java.kaboome.presentation.views.features.requestsList.adapter.GroupRequestsListViewAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupRequestsFragment extends BaseFragment implements GroupRequestImageClickListener, GroupRequestAcceptClickListener, GroupRequestRejectClickListener {

    private static final String TAG = "KMGroupRequestsFragment";
    private View rootView;
    private RecyclerView recyclerView;
    private GroupRequestsListViewAdapter adapter;
    private ProgressBar progressBar;
    private UserGroupModel group;
    private GroupRequestsListViewModel groupRequestsListViewModel;
    private Toolbar mainToolbar;
    private ImageView networkOffImageView;


    public GroupRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.group = (UserGroupModel)getArguments().getSerializable("group");
        groupRequestsListViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(group.getGroupId())).get(GroupRequestsListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group_requests, container, false);
        recyclerView = rootView.findViewById(R.id.group_requests_list_recycler_view);

        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        networkOffImageView = act.findViewById(R.id.mainToolbarNetworkOff);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }


    private void initRecyclerView() {

        adapter = new GroupRequestsListViewAdapter(this, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private RequestManager initGlide() {
        return ImageHelper.getInstance().getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey);

    }

    private void subscribeObservers() {
        //first remove existing ones
//        Log.d(TAG, "hasActiveObservers - : "+ groupRequestsListViewModel.getRequests().hasActiveObservers());
        groupRequestsListViewModel.getRequests().removeObservers(getViewLifecycleOwner());
//        Log.d(TAG, "hasActiveObservers - after removal : "+ groupRequestsListViewModel.getRequests().hasObservers());
        //now add
        groupRequestsListViewModel.getRequests().observe(getViewLifecycleOwner(), new Observer<List<GroupRequestModel>>() {
            @Override
            public void onChanged(List<GroupRequestModel> groupRequestModels) {
                Log.d(TAG, "onChanged: Get requests");
                adapter.setGroupRequests(groupRequestModels);
            }
        });

        //first remove existing ones
        groupRequestsListViewModel.getRequestsPostFinish().removeObservers(getViewLifecycleOwner());
        //now add
        groupRequestsListViewModel.getRequestsPostFinish().observe(getViewLifecycleOwner(), new Observer<List<GroupRequestModel>>() {
            @Override
            public void onChanged(List<GroupRequestModel> groupRequestModels) {
                Log.d(TAG, "onChanged: Post finish");
                adapter.setGroupRequests(groupRequestModels);
            }
        });

    }

    @Override
    public void onGroupImageClick(GroupRequestModel groupRequestModel) {
        Log.d(TAG, "onGroupImageClick: ");

    }

    @Override
    public void onGroupRequestAcceptClick(GroupRequestModel groupRequestModel) {
        Log.d(TAG, "onGroupRequestAcceptClick: ");
        if(NetworkHelper.isOnline()){
            groupRequestsListViewModel.finishRequest(groupRequestModel.getGroupId(), groupRequestModel, true, group.getGroupName(), "true");
        }
        else{
            Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onGroupRequestRejectClick(GroupRequestModel groupRequestModel) {
        Log.d(TAG, "onGroupRequestRejectClick: ");
        if(NetworkHelper.isOnline()){
            groupRequestsListViewModel.finishRequest(groupRequestModel.getGroupId(), groupRequestModel, false, group.getGroupName(), "true");
        }
        else{
            Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoginSuccess() {
        initRecyclerView();
        subscribeObservers();
        groupRequestsListViewModel.getRequestsFromServer();
    }


    @Override
    public void onNetworkOff() {
        networkOffImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkOn() {
        networkOffImageView.setVisibility(View.GONE);
    }
}
