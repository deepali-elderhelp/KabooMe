package com.java.kaboome.presentation.views.features.invitationsList;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.UserActionConstants;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.entities.UserModel;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.invitationsList.viewmodel.InvitationsListViewModel;
import com.java.kaboome.presentation.views.features.invitationsList.adapter.InvitationGroupImageClickListener;
import com.java.kaboome.presentation.views.features.invitationsList.adapter.InvitationGroupRejectClickListener;
import com.java.kaboome.presentation.views.features.invitationsList.adapter.InvitationsListViewAdapter;
import com.java.kaboome.presentation.views.features.invitationsList.adapter.JoinInvitedGroupClickListener;
import com.java.kaboome.presentation.views.features.profile.ProfileFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvitationsListFragment extends BaseFragment implements InvitationGroupImageClickListener, InvitationGroupRejectClickListener, JoinInvitedGroupClickListener {

    private static final String TAG = "KMInviListFragment";

    private InvitationsListViewModel invitationsListViewModel;

    private View rootView;
    private RecyclerView recyclerView;
    private InvitationsListViewAdapter adapter;
    private ProgressBar progressBar;
    private Toolbar mainToolbar;
    private ImageView networkOffImageView;
    private NavController navController;
    private SwipeRefreshLayout swipeRefreshLayout;


    public InvitationsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);


        invitationsListViewModel = ViewModelProviders.of(this).get(InvitationsListViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        rootView = inflater.inflate(R.layout.fragment_invitations_list, container, false);
        recyclerView = rootView.findViewById(R.id.invi_list_recycler_view);
        progressBar = rootView.findViewById(R.id.invitations_full_progress_bar);

        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        mainToolbar.getMenu().clear();
        networkOffImageView = act.findViewById(R.id.mainToolbarNetworkOff);

        navController = NavHostFragment.findNavController(InvitationsListFragment.this);

        swipeRefreshLayout = rootView.findViewById(R.id.fr_invi_li_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        invitationsListViewModel.getInvitationsFromServer();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(R.id.invitationsListFragment);

        final LifecycleEventObserver observer = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event.equals(Lifecycle.Event.ON_RESUME)
                        && navBackStackEntry.getSavedStateHandle().contains("deletedGroup")) {

                    String groupId = navBackStackEntry.getSavedStateHandle().get("deletedGroup");
                    //update user only when network is there
                    if(NetworkHelper.isOnline()){
                        invitationsListViewModel.rejectInvitation(groupId);
                    }
                    else{
                        Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        };

        navBackStackEntry.getLifecycle().addObserver(observer);

        // As addObserver() does not automatically remove the observer, we
        // call removeObserver() manually when the view lifecycle is destroyed
        getViewLifecycleOwner().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event.equals(Lifecycle.Event.ON_DESTROY)) {
                    navBackStackEntry.getLifecycle().removeObserver(observer);
                }
            }
        });

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
//        initRecyclerView();
//        subscribeObservers();
//        invitationsListViewModel.getInvitationsFromServer();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private void initRecyclerView() {

        adapter = new InvitationsListViewAdapter(initGlide(), this, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private RequestManager initGlide() {
        return ImageHelper.getInstance().getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey);

    }

    @Override
    public void onGroupImageClick(InvitationModel invitationGroup) {
        Log.d(TAG, "onGroupImageClick: group clicked "+invitationGroup);
        if(NetworkHelper.isOnline()){
            Bundle bundle = new Bundle();
            bundle.putSerializable("invitation", invitationGroup);

            if(!invitationGroup.getPrivateGroup()){ //public group
                NavHostFragment.findNavController(this).navigate(R.id.action_invitationsListFragment_to_joinInvitedGroupDialog, bundle);
            }
            else{
                NavHostFragment.findNavController(this).navigate(R.id.action_invitationsListFragment_to_joinInvitedPrivateGroupDialog, bundle);
            }
        }
        else{
            Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onGroupRejectClick(InvitationModel invitationGroup) {
        if(NetworkHelper.isOnline()){
            invitationsListViewModel.rejectInvitation(invitationGroup.getGroupId());
        }
        else{
            Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void subscribeObservers() {
        //first remove existing ones
        invitationsListViewModel.getInvitations().removeObservers(getViewLifecycleOwner());
        //now add
        invitationsListViewModel.getInvitations().observe(getViewLifecycleOwner(), new Observer<List<InvitationModel>>() {
            @Override
            public void onChanged(List<InvitationModel> invitationModels) {
                Log.d(TAG, "onChanged: Get - " + invitationModels);
                //display the adapter
                adapter.setInvitationGroups(invitationModels);
            }
        });

        //first remove existing ones
        invitationsListViewModel.getRejectInvitation().removeObservers(getViewLifecycleOwner());
        //now add
        invitationsListViewModel.getRejectInvitation().observe(getViewLifecycleOwner(), new Observer<List<InvitationModel>>() {
            @Override
            public void onChanged(List<InvitationModel> invitationModels) {
                Log.d(TAG, "onChanged: Reject - " + invitationModels);
                //display the adapter
                adapter.setInvitationGroups(invitationModels);
            }
        });
    }

    @Override
    public void onJoinGroupClick(InvitationModel invitation) {

        if(NetworkHelper.isOnline()){
            Bundle bundle = new Bundle();
            bundle.putSerializable("invitation", invitation);

            if(!invitation.getPrivateGroup()){ //public group
                NavHostFragment.findNavController(this).navigate(R.id.action_invitationsListFragment_to_joinInvitedGroupDialog, bundle);
            }
            else{
                NavHostFragment.findNavController(this).navigate(R.id.action_invitationsListFragment_to_joinInvitedPrivateGroupDialog, bundle);
            }
        }
        else{
            Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoginSuccess() {
//        initRecyclerView();
//        subscribeObservers();
        invitationsListViewModel.getInvitationsFromServer();
    }

    @Override
    public void whileLoginInProgress() {
        initRecyclerView();
        subscribeObservers();
        invitationsListViewModel.getInvitationsFromServer();
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
