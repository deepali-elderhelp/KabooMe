package com.java.kaboome.presentation.views.features.conversations;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
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
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.viewModelProvider.CustomViewModelProvider;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.conversations.adapter.ConversationImageClickListener;
import com.java.kaboome.presentation.views.features.conversations.adapter.ConversationMessagesClickListener;
import com.java.kaboome.presentation.views.features.conversations.adapter.ConversationsListViewAdapter;
import com.java.kaboome.presentation.views.features.conversations.viewmodel.ConvsListViewModel;
import com.java.kaboome.presentation.views.features.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;


public class GroupConversationsFragment extends BaseFragment implements ConversationImageClickListener, ConversationMessagesClickListener {

    private static final String TAG = "KMGrpsConvsxFragment";

    private View rootView;
    private RecyclerView recyclerView;
    private ConvsListViewModel convsListViewModel;
    private UserGroupModel group;
    private ConversationsListViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
//    private boolean alreadyThere = false;
    private Toolbar mainToolbar;
    private ImageView networkOffImageView;
    private FloatingActionButton newGroupFAB;
    private MenuItem refreshMenu;
    private NavController navController;

    public GroupConversationsFragment() {
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

        this.group = (UserGroupModel)getArguments().getSerializable("group");
        convsListViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(getContext(), group.getGroupId())).get(ConvsListViewModel.class);
//        navController = NavHostFragment.findNavController(this);
//        alreadyThere = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");

        //toolbar needs to be set again since it was reset by other viewer fragments
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        mainToolbar.getMenu().clear(); //clearing old menu if any
        networkOffImageView = act.findViewById(R.id.mainToolbarNetworkOff);

        mainToolbar.inflateMenu(R.menu.convs_list_menu);
        mainToolbar.setSubtitle(group.getGroupName());
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUI.navigateUp(navController, (DrawerLayout) null);
            }
        });
        refreshMenu = mainToolbar.getMenu().findItem(R.id.conv_list_refresh);
        refreshMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(NetworkHelper.isOnline()){
                    getInitialConvsList();
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
        rootView = inflater.inflate(R.layout.fragment_convs_list, container, false);
        recyclerView = rootView.findViewById(R.id.fr_conv_li_recycler_view);
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
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
        rootView = null; //releasing rootview
        recyclerView = null;
        adapter = null;
//        mainToolbar.getMenu().clear();
//        unsubscribeObservers();
    }


    public void onFABClicked(){

        if(!NetworkHelper.isOnline()){
            Toast.makeText(getContext(), "No Network! Network connection needed to create a new group", Toast.LENGTH_SHORT).show();
        }
        else {
            DialogHelper.showAlert(getContext(), "Do you want to start a new Private Conversation?", "New Private Conversation", "Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //user clicked yes
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("group", group);
                            bundle.putParcelableArrayList("conversations", (ArrayList<UserGroupConversationModel>) convsListViewModel.getUserGroupConvsData().getValue());
                            if(navController.getCurrentDestination().getId() == R.id.groupConversationsFragment) {
                                navController.navigate(R.id.action_groupConversationsFragment_to_addAdminUserConversationFragment, bundle);
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

    private void initRecyclerView(){
        adapter = new ConversationsListViewAdapter(getContext(), this, this);
//        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
//        mRecyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //if there is data already, set it
        if(convsListViewModel.getUserGroupConvsData().getValue() != null) {
            adapter.setConversations(convsListViewModel.getUserGroupConvsData().getValue());
        }

    }

//    private RequestManager initGlide() {
//        return ImageHelper.getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey);
////        return ImageHelper.getRequestManager(getContext(), getContext().getResources().getDrawable(R.drawable.account_group_grey), AvatarHelper.generateAvatar(getContext(), 60, "Test String"));
//    }

    @Override
    public void onConvImageClick(UserGroupConversationModel conversation, View sharedTransitionView) {
        Log.d(TAG, "Group clicked for info - "+conversation.getGroupId());

        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(sharedTransitionView, conversation.getOtherUserId())
                .build();

        //just open the image, and some details of the user, nothing else

//        Bundle bundle = new Bundle();
//        bundle.putSerializable("conversation", conversation);
//
//        if(navController.getCurrentDestination().getId() == R.id.groupsListFragment) {
//            navController.navigate(R.id.action_groupsListFragment_to_groupActionsDialog, bundle, null, extras);
//        }
//
////        GroupActionsDialog groupActionsDialog = new GroupActionsDialog();
////        groupActionsDialog.setGroup(conversation);
////        groupActionsDialog.show(getChildFragmentManager(), "groupActionsDialog");

    }

    @Override
    public void onConvMessagesClick(UserGroupConversationModel conversation) {
        Log.d(TAG, "Group clicked for messages - "+conversation.getGroupId());
        Bundle bundle = new Bundle();
        bundle.putSerializable("conversation", conversation);
        bundle.putSerializable("group", group);
        if(navController.getCurrentDestination().getId() == R.id.groupConversationsFragment) {
            navController.navigate(R.id.action_groupConversationsFragment_to_groupAdminUserMessagesFragment, bundle);
        }
////        Intent groupMessagesIntent = new Intent(getActivity(), MessagesActivity.class);
//        groupMessagesIntent.putExtra("conversation", conversation);
//        startActivity(groupMessagesIntent);
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
//        convsListViewModel.getUserGroupConvsData().observe(this, new Observer<List<UserGroupConversationModel>>() {
//            @Override
//            public void onChanged(List<UserGroupConversationModel> userGroupConversationModels) {
//                Log.d(TAG, "onChanged: ");
//                if(adapter != null) {
//                    adapter.setConversations(userGroupConversationModels);
//                }
//
//
//            }
//        });
//
//        convsListViewModel.getEachConversationLiveData().observe(this, new Observer<UserGroupConversationModel>() {
//            @Override
//            public void onChanged(UserGroupConversationModel userGroupConversationModel) {
//                if(adapter != null) {
//                    adapter.updateConversation(userGroupConversationModel);
//                }
//
//            }
//        });

        convsListViewModel.getUserGroupConvsData().observe(getViewLifecycleOwner(), new Observer<List<UserGroupConversationModel>>() {
            @Override
            public void onChanged(List<UserGroupConversationModel> userGroupConversationModels) {
                Log.d(TAG, "List conversation changed ");
                if(adapter != null) {
                    Log.d(TAG, "Number of conversations - "+userGroupConversationModels.size());
                    adapter.setConversations(userGroupConversationModels);
                }


            }
        });

        convsListViewModel.getEachConversationLiveData().observe(getViewLifecycleOwner(), new Observer<UserGroupConversationModel>() {
            @Override
            public void onChanged(UserGroupConversationModel userGroupConversationModel) {
                Log.d(TAG, "Conversation data changed");
                if(adapter != null) {
                    adapter.updateConversation(userGroupConversationModel);
                }

            }
        });


    }

    private void unsubscribeObservers(){
        convsListViewModel.getEachConversationLiveData().removeObservers(this);
        convsListViewModel.getUserGroupConvsData().removeObservers(this);
    }

    private void getInitialConvsList() {
        Log.d(TAG, "getInitialGroupsList: ");
        //why was this check added? It will never work for offline if this check is there
//        if(CognitoHelper.getCurrSession() != null){
        convsListViewModel.loadInitialList();
//        }
    }


    @Override
    public void onLoginSuccess() {
        Log.d(TAG, "onLoginSuccess: ");

//        subscribeObservers();
        getInitialConvsList();

    }


    @Override
    public void onNetworkOff() {
        Log.d(TAG, "onNetworkOff: ");
        networkOffImageView.setVisibility(View.VISIBLE);
        newGroupFAB.hide();
        if(recyclerView != null) { recyclerView.clearOnScrollListeners();}
    }

    @Override
    public void whileLoginInProgress() {
        subscribeObservers();
        getInitialConvsList();
    }

    @Override
    public void onNetworkOn() {
        Log.d(TAG, "onNetworkOn: ");
        networkOffImageView.setVisibility(View.GONE);
        newGroupFAB.show();
        if(recyclerView != null) {recyclerView.addOnScrollListener(recyclerViewScrollListener);}
    }
}
