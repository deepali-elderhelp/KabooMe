package com.java.kaboome.presentation.views.features.groupSearch;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.java.kaboome.R;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.GeneralHelper;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.groupSearch.viewmodel.SearchGroupsListViewModel;
import com.java.kaboome.presentation.views.features.groupSearch.adapter.GroupsSearchListViewAdapter;
import com.java.kaboome.presentation.views.features.groupSearch.adapter.GroupClickListener;
import com.java.kaboome.presentation.views.features.groupSearch.adapter.JoinGroupClickListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends BaseFragment implements SearchView.OnQueryTextListener, GroupClickListener, JoinGroupClickListener {

    private static final String TAG = "KMSearchGroupFragment";

    private View rootView;
    private Toolbar mainToolbar;
    private SearchGroupsListViewModel searchGroupsListViewModel;
    private GroupsSearchListViewAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ImageView networkOffImageView;
    private NavController navController;
    private MenuItem itemSearchByQR;


    public SearchGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        searchGroupsListViewModel = ViewModelProviders.of(this).get(SearchGroupsListViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search_group, container, false);

        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        mainToolbar.getMenu().clear(); //clearing old menu if any
        mainToolbar.inflateMenu(R.menu.search_menu);
        networkOffImageView = act.findViewById(R.id.mainToolbarNetworkOff);

        Log.d(TAG, "onCreateView: width - "+mainToolbar.getWidth());

        MenuItem itemSearch = mainToolbar.getMenu().findItem(R.id.search_PTL);

        searchView = new SearchView(getContext());
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.clearFocus();
//        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search");
        searchView.setMaxWidth(((Double)(mainToolbar.getWidth() * 0.7)).intValue());



        MenuItemCompat.setActionView(itemSearch, searchView);

        itemSearchByQR = mainToolbar.getMenu().findItem(R.id.search_by_qr_code);
        itemSearchByQR.setOnMenuItemClickListener(searchByQRClicked);

        recyclerView = rootView.findViewById(R.id.group_search_recycler_view);

        navController = NavHostFragment.findNavController(SearchGroupFragment.this);

        return rootView;
    }

    private MenuItem.OnMenuItemClickListener searchByQRClicked = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
//                Intent searchByQR = new Intent(GroupSearchActivity.this, ScanQRCodeActivity.class);
//                startActivityForResult(searchByQR, SCANNED_QR_CODE);
            Navigation.findNavController(getActivity(), R.id.fragment).navigate(R.id.action_searchGroupFragment_to_scanQRCodeFragment);
            return true;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        initRecyclerView();
//        subscribeObservers();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("groupId");
        liveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received groupId from scan qr code - "+o);
                String groupId = (String) o;
                adapter.clearGroupsList();
                searchGroupsListViewModel.searchGroupsByText("GroupId", groupId, true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        mainToolbar.getMenu().clear();

        itemSearchByQR.setOnMenuItemClickListener(null);
        searchByQRClicked = null;

    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        //make query strip all spaces
        query = GeneralHelper.cleanString(query);
        query = query.replace(" ", "");
        Log.d(TAG, "onQueryTextSubmit: query is "+query);
//        if(!GeneralHelper.validateString(query)){
//            GeneralHelper.showAlert(getContext(), "Search contains invalid characters", "Please check", "Ok", new DialogInterface.OnClickListener(){
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//        }
//        else{
            searchView.clearFocus();
            //reset current adapter
            adapter.clearGroupsList();
            searchGroupsListViewModel.searchGroupsByText("GroupName", query, true);
//        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = GeneralHelper.cleanString(newText);
        newText = newText.replace(" ", "");
        searchGroupsListViewModel.searchGroupsByText("GroupName", newText, false);
        return false;
    }


    private void initRecyclerView(){
//        adapter = new GroupsSearchListViewAdapter(initGlide(),  this, this);
        adapter = new GroupsSearchListViewAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

//    private RequestManager initGlide() {
//
//        RequestOptions options = new RequestOptions()
////                .placeholder(R.drawable.account_group_grey)
//                .error(R.drawable.account_group_grey);
//
//        return Glide.with(this)
//                .setDefaultRequestOptions(options);
//
//    }

    @Override
    public void onGroupClick(GroupModel group) {

        //if user is a member, show the GroupActionsDialog
        //else show the JoinGroupDialog

        if(group.getCurrentUserGroupStatus() != null && (
                group.getCurrentUserGroupStatus() == UserGroupStatusConstants.REGULAR_MEMBER ||
                group.getCurrentUserGroupStatus() == UserGroupStatusConstants.ADMIN_MEMBER)){

            UserGroupModel userGroupModel = new UserGroupModel();
            userGroupModel.setGroupId(group.getGroupId());
            userGroupModel.setGroupName(group.getGroupName());
            if(group.getCurrentUserGroupStatus() == UserGroupStatusConstants.ADMIN_MEMBER){
                userGroupModel.setIsAdmin("true");
            }
            else{
                userGroupModel.setIsAdmin("false");
            }

            userGroupModel.setPrivate(group.getGroupPrivate());

            Bundle bundle = new Bundle();
            bundle.putSerializable("group", userGroupModel);

            if(navController.getCurrentDestination().getId() == R.id.searchGroupFragment) {
                navController.navigate(R.id.action_searchGroupFragment_to_groupActionsDialog, bundle);
            }

        }
        else if(group.getCurrentUserGroupStatus() != null && group.getCurrentUserGroupStatus() == UserGroupStatusConstants.PENDING){
            //the user request is already pending
            Toast.makeText(getContext(), "Your request to join is pending with the administrator", Toast.LENGTH_SHORT).show();
        }
        else{
            Bundle bundle = new Bundle();
            bundle.putSerializable("group", group);

            if(!group.getGroupPrivate()){ //public group
                if(navController.getCurrentDestination().getId() == R.id.searchGroupFragment) {
                    navController.navigate(R.id.action_searchGroupFragment_to_joinGroupDialog, bundle);
                }
            }
            else{
                if(navController.getCurrentDestination().getId() == R.id.searchGroupFragment) {
                    navController.navigate(R.id.action_searchGroupFragment_to_joinPrivateGroupDialog, bundle);
                }
            }
        }
    }

    @Override
    public void onJoinGroupClick(GroupModel group) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("group", group);


        if(!group.getGroupPrivate()){ //public group
            if(navController.getCurrentDestination().getId() == R.id.searchGroupFragment) {
                NavHostFragment.findNavController(this).navigate(R.id.action_searchGroupFragment_to_joinGroupDialog, bundle);
            }
        }
        else{
            if(navController.getCurrentDestination().getId() == R.id.searchGroupFragment) {
                NavHostFragment.findNavController(this).navigate(R.id.action_searchGroupFragment_to_joinPrivateGroupDialog, bundle);
            }
        }
    }

    private void subscribeObservers() {

        searchGroupsListViewModel.getGroups().removeObservers(getViewLifecycleOwner()); //if any old hanging there
        searchGroupsListViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<List<GroupModel>>() {
            @Override
            public void onChanged(List<GroupModel> groupModels) {
                Log.d(TAG, "onChanged: ");
                adapter.setGroups(groupModels);
            }
        });

    }

    @Override
    public void onLoginSuccess() {
        initRecyclerView();
        subscribeObservers();
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
