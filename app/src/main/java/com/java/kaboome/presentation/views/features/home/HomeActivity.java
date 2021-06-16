package com.java.kaboome.presentation.views.features.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.bottomappbar.BottomAppBar;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.java.kaboome.R;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.views.features.conversations.GroupConversationsFragment;
import com.java.kaboome.presentation.views.features.groupList.GroupsListFragment;
import com.java.kaboome.presentation.views.features.home.viewmodel.HomeViewModel;
import com.java.kaboome.presentation.views.features.CameraActivity;
import com.java.kaboome.presentation.views.features.SignUpActivity;

import java.util.HashMap;


public class HomeActivity extends CameraActivity {

    private static final String TAG = "KMDataHomeActivity";


    private NavController navController;
    HomeViewModel homeViewModel;
    TextView numberOfNewInvitations;
    MenuItem groupListHome;
    FloatingActionButton newGroupFAB;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

//        connectivityLiveData.observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                Log.d(TAG, "Network connected ? "+aBoolean);
//            }
//        });
        setContentView(R.layout.activity_home);

        navController = Navigation.findNavController(this, R.id.fragment);

//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
//        NavigationUI.setupActionBarWithNavController(this, navController);
        NavigationUI.setupWithNavController(toolbar, navController);


        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        Log.d(TAG, "going to replace menu");
        bottomAppBar.replaceMenu(R.menu.bottom_menu);
        DrawerArrowDrawable hamburgerIcon = new DrawerArrowDrawable(bottomAppBar.getContext());
        hamburgerIcon.setColor(getResources().getColor(R.color.white));


        bottomAppBar.setNavigationIcon(hamburgerIcon);
        bottomAppBar.setBackground(getResources().getDrawable(R.drawable.gradient_left_right));


        setSupportActionBar(bottomAppBar);
//        NavigationUI.setupWithNavController(bottomAppBar, navController);

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onOptionsItemSelected: Hamburger");
                navController.navigate(R.id.drawerBottomSheetFragment);
            }
        });

        newGroupFAB = findViewById(R.id.createGroupFAB);
        Navigation.setViewNavController(newGroupFAB, navController);

        newGroupFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

                if(navController.getCurrentDestination().getId() == R.id.groupsListFragment){
                    ((GroupsListFragment)currentFragment).onFABClicked();
                }
                if(navController.getCurrentDestination().getId() == R.id.groupConversationsFragment){
                    ((GroupConversationsFragment)currentFragment).onFABClicked();
                }

            }
        });

//        newGroupFAB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!NetworkHelper.isOnline()){
//                    Toast.makeText(HomeActivity.this, "No Network! Network connection needed to create a new group", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    DialogHelper.showAlert(HomeActivity.this, "Do you want to create a new group?", "New Group", "Yes",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //user clicked yes
//                                    if (navController.getCurrentDestination().getId() == R.id.groupsListFragment) {
//                                        navController.navigate(R.id.createGroupFragment);
//                                    }
//                                }
//                            }, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                }
//            }
//        });


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.groupsListFragment){
                    Log.d(TAG, "onDestinationChanged: ");
                    getSupportActionBar().show();
                    newGroupFAB.show();
                    hideKeyboard();

                }
                if(destination.getId() == R.id.invitationsListFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                    toolbar.getMenu().clear();
                }
                if(destination.getId() == R.id.searchGroupFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                }
                if(destination.getId() == R.id.groupQRFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                }
                if(destination.getId() == R.id.inviteContactsFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                }
                if(destination.getId() == R.id.groupRequestsFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                }
                if(destination.getId() == R.id.groupInfoFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                }
                if(destination.getId() == R.id.profileFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                    toolbar.getMenu().clear();
                }
                if(destination.getId() == R.id.createGroupFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                    toolbar.getMenu().clear();
                }
                if(destination.getId() == R.id.groupMessagesFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                }
                if(destination.getId() == R.id.groupAdminUserMessagesFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                }
                if(destination.getId() == R.id.groupUserAdminMessagesFragment){
                    getSupportActionBar().hide();
                    newGroupFAB.hide();
                }
                if(destination.getId() == R.id.imageViewerFragment){
                    toolbar.getMenu().clear();
                }
                if(destination.getId() == R.id.audioViewerFragment){
                    toolbar.getMenu().clear();
                }
                if(destination.getId() == R.id.videoViewerFragment){
                    toolbar.getMenu().clear();
                }
                if(destination.getId() == R.id.finishAttachmentFragment){
                    toolbar.getMenu().clear();
                }
            }
        });
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        subscribeObservers();

    }

    private void subscribeObservers() {

        Log.d(TAG, "subscribeObservers: ");
        homeViewModel.getNumberOfInvitations().removeObservers(this); //if any old hanging there
        homeViewModel.getNumberOfInvitations().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d(TAG, "onChanged got invitations back - "+String.valueOf(integer));
                //get the number of invitations and update the app bar menu item badge
                if(numberOfNewInvitations != null){
                    if(integer != null && integer > 0){
                        numberOfNewInvitations.setVisibility(View.VISIBLE);
                        numberOfNewInvitations.setText(String.valueOf(integer));
                    }
                    else{
                        numberOfNewInvitations.setVisibility(View.GONE);
                    }

                }
            }
        });
        homeViewModel.startObservingUploadAndDownload();

        homeViewModel.getObserveUpload().observe(this, new Observer<DomainResource<HashMap<String, Object>>>() {
            @Override
            public void onChanged(DomainResource<HashMap<String, Object>> domainMessageDomainResource) {
                Log.d(TAG, "onChanged: from homeactivity - upload");
            }
        });

        homeViewModel.getObserveDownload().observe(this, new Observer<DomainResource<HashMap<String, Object>>>() {
            @Override
            public void onChanged(DomainResource<HashMap<String, Object>> domainMessageDomainResource) {
                Log.d(TAG, "onChanged: from homeactivity - download");
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

//        //cancel old notifications still hanging in the tray
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AppConfigHelper.getContext());
//        notificationManager.cancelAll();
//
//        //also remove from shared preference
//        AppConfigHelper.deletePersistedNotifications();

    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout) null);
    }

    //It comes here for top toolbar, not bottom app bar
    //since the top toolbar is set as supportActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
//        return super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.bottom_menu, menu);

        groupListHome = menu.findItem(R.id.groupsListFragment);
        final MenuItem menu_invitation = menu.findItem(R.id.invitationsListFragment);
        menu_invitation.setActionView(R.layout.new_invitation_count);
//
        final View menu_invitation_action_bar = menu_invitation.getActionView();
        menu_invitation_action_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: coming to invitations clicked");
                NavigationUI.onNavDestinationSelected(menu_invitation, navController);
            }
        });
        numberOfNewInvitations = (TextView) menu_invitation_action_bar.findViewById(R.id.new_invitations);

        numberOfNewInvitations.setText("");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: coming here");

        switch (item.getItemId()) {
            case R.id.invitationsListFragment: {
                Log.d(TAG, "onOptionsItemSelected: FragmentTwo");
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }

            case R.id.searchGroupFragment:{
                Log.d(TAG, "onOptionsItemSelected: FragmentThree");
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }


            case android.R.id.home:
            {
                Log.d(TAG, "onOptionsItemSelected: home");
//                DrawerBottomSheetFragment drawerBottomSheetFragment = new DrawerBottomSheetFragment();
//                drawerBottomSheetFragment.show(getSupportFragmentManager(), "drawerBottomSheet");
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }
//
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void resetToolbarBackButton(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUI.navigateUp(navController, (DrawerLayout) null);
            }
        });
    }

    @Override
    public void onLoginSuccess() {
        //some cleanup
        homeViewModel.cleanUpOldDeletedUserGroups();
        //this will trigger getting the number from backend and update the badge accordingly
        homeViewModel.getNumberOfInvitationsFromBackend();
    }


//    @Override
//    public void onLoginSuccess() {
//        //if success, render group list again
//        Log.d(TAG, "onLoginSuccess: ");
//        //not sure why we need the following line?
//        //If the login was successful, just stay where you are...right?
//        //why need to go to GLF again?
//        //this is the reason why -
//        //once the user is logged in, we need to show the GLF
//        //but what happens is, log-in is async, so the app comes to this HomeActivity, initially when the user is not
//        //logged in, it still comes to GLF, works in kind of offline mode
//        //but when the log-in happens, the app needs to refresh the GLF if it is there
//        //so that new refreshed data is shown
//        //this does result in a flicker on GLF, maybe we can make it an PagedList and do diff
//        //or something else to handle the flicker
//        if(navController.getCurrentDestination().getId() == R.id.groupsListFragment){
//            navController.navigate(R.id.action_groupsListFragment_self);
//        }
//
//
//
//    }
//
    @Override
    public void onLoginFailure(Exception exception) {
        //user is not logged in, go to sign up
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

//    private void hideKeyboardFrom(Context context, View view) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
////        imm.hideSoftInputFromWindow(view, 0);
//    }

    private void hideKeyboard() {

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View currentFocusedView = this.getCurrentFocus();
        if (currentFocusedView != null) {
            imm.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
