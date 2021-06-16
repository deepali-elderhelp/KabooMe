package com.java.kaboome.presentation.views.features.groupList;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.kaboome.R;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.groupList.adapter.UserGroupImageClickListener;
import com.java.kaboome.presentation.views.features.groupList.adapter.UserGroupMessagesClickListener;
import com.java.kaboome.presentation.views.features.groupList.adapter.UserGroupRequestsClickListener;
import com.java.kaboome.presentation.views.features.groupList.adapter.UserGroupsListViewAdapter;
import com.java.kaboome.presentation.views.features.groupList.viewmodel.UserGroupsListViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsListFragment extends BaseFragment implements UserGroupImageClickListener, UserGroupMessagesClickListener, UserGroupRequestsClickListener {

    private static final String TAG = "KMGrpsListFragment";

    private View rootView;
    private RecyclerView recyclerView;
    private ImageView splashScreen;
    private UserGroupsListViewModel userGroupsListViewModel;
    private UserGroupsListViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean alreadyThere = false;
    private Toolbar mainToolbar;
    private ImageView networkOffImageView;
    private FloatingActionButton newGroupFAB;
    private MenuItem refreshMenu;
    private NavController navController;

    public GroupsListFragment() {
        // Required empty public constructor
    }

    RecyclerView.OnScrollListener recyclerViewScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(newGroupFAB != null){
                if (dy < 0) {
                    newGroupFAB.show();

                } else if (dy > 0) {
                    newGroupFAB.hide();
                }
            }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        userGroupsListViewModel = ViewModelProviders.of(this).get(UserGroupsListViewModel.class);
//        navController = NavHostFragment.findNavController(this);
        alreadyThere = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");

        //toolbar needs to be set again since it was reset by other viewer fragments
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        mainToolbar.setSubtitle("");
        mainToolbar.getMenu().clear(); //clearing old menu if any
        networkOffImageView = act.findViewById(R.id.mainToolbarNetworkOff);

        mainToolbar.inflateMenu(R.menu.groups_list_menu);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUI.navigateUp(navController, (DrawerLayout) null);
            }
        });
        refreshMenu = mainToolbar.getMenu().findItem(R.id.group_list_refresh);
        refreshMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(NetworkHelper.isOnline()){
                    getInitialGroupsList();
                }
                else{
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

//        if(alreadyThere)
//            return rootView;

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_groups_list, container, false);
        recyclerView = rootView.findViewById(R.id.fr_gr_li_recycler_view);
        splashScreen = rootView.findViewById(R.id.fr_gr_li_splash);
        if(!alreadyThere){
            splashScreen.setVisibility(View.VISIBLE);
        }
        newGroupFAB = getActivity().findViewById(R.id.createGroupFAB);



        recyclerView.addOnScrollListener(recyclerViewScrollListener);

        initRecyclerView();
        // ending recy
//        swipeRefreshLayout = rootView.findViewById(R.id.fr_gr_li_swipe_refresh);
//        swipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        Log.d(TAG, "onRefresh called from SwipeRefreshLayout");
//
//                        // This method performs the actual data-refresh operation.
//                        // The method calls setRefreshing(false) when it's finished.
//                        getInitialGroupsList();
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }
//        );

//        initRecyclerView();
//        userGroupsListViewModel = ViewModelProviders.of(this).get(UserGroupsListViewModel.class);
//        subscribeObservers();
//        getInitialGroupsList();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        navController = NavHostFragment.findNavController(this);

//        if(alreadyThere)
//            return;
//
////        userGroupsListViewModel = ViewModelProviders.of(this).get(UserGroupsListViewModel.class);
//
//        initRecyclerView();
//        subscribeObservers();
//        getInitialGroupsList();
//
//        alreadyThere = true;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

        //trial crash
//        throw new RuntimeException("Test Crash"); // Force a crash

    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
        rootView = null; //releasing rootview
        recyclerView = null;
        splashScreen = null;
        adapter = null;
//        mainToolbar.getMenu().clear();
//        unsubscribeObservers();
    }


    private void initRecyclerView(){
        adapter = new UserGroupsListViewAdapter(getContext(), this, this, this);
//        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
//        mRecyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //if there is data already, set it
        if(userGroupsListViewModel.getUserGroupsData().getValue() != null) {
            splashScreen.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setGroups(userGroupsListViewModel.getUserGroupsData().getValue());
        }

    }

    public void onFABClicked(){

        if(!NetworkHelper.isOnline()){
            Toast.makeText(getContext(), "No Network! Network connection needed to create a new group", Toast.LENGTH_SHORT).show();
        }
        else {
            DialogHelper.showAlert(getContext(), "Do you want to create a new group?", "New Group", "Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //user clicked yes
                            if (navController.getCurrentDestination().getId() == R.id.groupsListFragment) {
                                navController.navigate(R.id.createGroupFragment);
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }
    }


//    private RequestManager initGlide() {
//        return ImageHelper.getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey);
////        return ImageHelper.getRequestManager(getContext(), getContext().getResources().getDrawable(R.drawable.account_group_grey), AvatarHelper.generateAvatar(getContext(), 60, "Test String"));
//    }

    @Override
    public void onGroupImageClick(UserGroupModel group, View sharedTransitionView) {
        Log.d(TAG, "Group clicked for info - "+group.getGroupName());

        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(sharedTransitionView, group.getGroupId())
                .build();

        Bundle bundle = new Bundle();
        bundle.putSerializable("group", group);

        if(navController.getCurrentDestination().getId() == R.id.groupsListFragment) {
            navController.navigate(R.id.action_groupsListFragment_to_groupActionsDialog, bundle, null, extras);
        }

//        GroupActionsDialog groupActionsDialog = new GroupActionsDialog();
//        groupActionsDialog.setGroup(group);
//        groupActionsDialog.show(getChildFragmentManager(), "groupActionsDialog");

    }

    @Override
    public void onGroupMessagesClick(UserGroupModel group) {
        Log.d(TAG, "Group clicked for messages - "+group.getGroupName());
        Bundle bundle = new Bundle();
        bundle.putSerializable("group", group);
        if(navController.getCurrentDestination().getId() == R.id.groupsListFragment) {
            navController.navigate(R.id.action_groupsListFragment_to_groupMessagesFragment, bundle);
        }
//        Intent groupMessagesIntent = new Intent(getActivity(), MessagesActivity.class);
//        groupMessagesIntent.putExtra("group", group);
//        startActivity(groupMessagesIntent);
    }

    @Override
    public void onGroupRequestsClick(UserGroupModel group) {
        Log.d(TAG, "Group clicked for requests - "+group.getGroupName());
        Bundle bundle = new Bundle();
        bundle.putSerializable("group", group);
        if(navController.getCurrentDestination().getId() == R.id.groupsListFragment) {
            navController.navigate(R.id.action_groupsListFragment_to_groupRequestsFragment, bundle);
        }
//        Intent groupRequestsIntent = new Intent(getActivity(), GroupRequestsListActivity.class);
//        groupRequestsIntent.putExtra("group", group);
//        startActivity(groupRequestsIntent);
    }

    @SuppressLint("FragmentLiveDataObserve")
    private void subscribeObservers(){



        //base login subscriber
//        baseViewModel.getUserLoggedIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                if(aBoolean != null && !aBoolean){
//                    Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        userGroupsListViewModel.getUserGroupsData().observe(getViewLifecycleOwner(), new Observer<List<UserGroupModel>>() {
//            @Override
//            public void onChanged(List<UserGroupModel> userGroupModels) {
//                Log.d(TAG, "onChanged: ");
//                adapter.setGroups(userGroupModels);
//            }
//        });

        //intentionally keeping it this and not LifeCycleOwnerView
        //reason being - when it is view, this lifecycleowner dies when the fragment is replaced
        //hence, the observers also die. I do not want that. I want the observers to be alive
        //I am making sure they are not registered twice, since they are registered only in on Create()
        //so, I want them around till the view model is around.
        userGroupsListViewModel.getUserGroupsData().observe(this, new Observer<List<UserGroupModel>>() {
            @Override
            public void onChanged(List<UserGroupModel> userGroupModels) {
                Log.d(TAG, "onChanged: ");

                    splashScreen.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if(adapter != null) {
                        adapter.setGroups(userGroupModels);
                    }


            }
        });

        userGroupsListViewModel.getEachGroupLiveData().observe(this, new Observer<UserGroupModel>() {
            @Override
            public void onChanged(UserGroupModel userGroupModel) {
                if(adapter != null) {
                    adapter.updateGroup(userGroupModel);
                }

            }
        });


    }

    private void unsubscribeObservers(){
        userGroupsListViewModel.getEachGroupLiveData().removeObservers(this);
        userGroupsListViewModel.getUserGroupsData().removeObservers(this);
    }

    private void getInitialGroupsList() {
        Log.d(TAG, "getInitialGroupsList: ");
        //why was this check added? It will never work for offline if this check is there
//        if(CognitoHelper.getCurrSession() != null){
            userGroupsListViewModel.loadInitialList();
//        }
    }


    @Override
    public void onLoginSuccess() {
        Log.d(TAG, "onLoginSuccess: ");

//        initRecyclerView();

        if(alreadyThere)
            return;

//        userGroupsListViewModel = ViewModelProviders.of(this).get(UserGroupsListViewModel.class);

//        initRecyclerView();
        subscribeObservers();
        getInitialGroupsList();

        alreadyThere = true;
    }

    @Override
    public void onNetworkOff() {
        Log.d(TAG, "onNetworkOff: ");
        networkOffImageView.setVisibility(View.VISIBLE);
        newGroupFAB.hide();
        if(recyclerView != null) { recyclerView.clearOnScrollListeners();}
    }

    @Override
    public void onNetworkOn() {
        Log.d(TAG, "onNetworkOn: ");
        networkOffImageView.setVisibility(View.GONE);
        newGroupFAB.show();
        if(recyclerView != null) {recyclerView.addOnScrollListener(recyclerViewScrollListener);}
    }
}
