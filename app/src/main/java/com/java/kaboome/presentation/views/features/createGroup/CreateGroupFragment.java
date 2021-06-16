package com.java.kaboome.presentation.views.features.createGroup;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.java.kaboome.R;
import com.java.kaboome.constants.CreateGroupStatusContants;
import com.java.kaboome.constants.UserActionConstants;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.entities.UserModel;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.views.features.BaseActivity;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.createGroup.adapter.SelectUserImageListener;
import com.java.kaboome.presentation.views.widgets.CreateGroupViewPager;
import com.java.kaboome.presentation.views.features.createGroup.adapter.HandleNextListener;
import com.java.kaboome.presentation.views.features.createGroup.viewmodel.CreateGroupViewModel;
import com.java.kaboome.presentation.views.features.createGroup.adapter.SelectGroupImageListener;
import com.java.kaboome.presentation.views.features.createGroup.adapter.ViewPagerNextListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateGroupFragment extends BaseFragment implements ViewPagerNextListener, SelectGroupImageListener, SelectUserImageListener, HandleNextListener {

    private static final String TAG = "KMGlideGroupFragment";

    private View rootView;
    private CreateGroupViewPager viewPager;
    private Button previousButton;
    private Button nextButton;
    private NewGroupPagerAdapter pageAdapter;
    private ProgressBar progressBar;
    private List<Fragment> fragmentList;
    private CreateGroupViewModel createGroupViewModel;
    private NavController navController;
//    private int originalSoftInputMode;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createGroupViewModel = ViewModelProviders.of(this).get(CreateGroupViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_group, container, false);
        progressBar = rootView.findViewById(R.id.create_group_full_progress_bar);
        viewPager = rootView.findViewById(R.id.co_new_gr_pager);
        previousButton = rootView.findViewById(R.id.create_group_previous_button);
        previousButton.setOnClickListener(previousButtonListener);
        nextButton = rootView.findViewById(R.id.create_group_next_button);
        nextButton.setOnClickListener(nextButtonListener);
        initializeFragments();

//        viewPager.disableScroll(true);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                Log.d(TAG, "Current index = "+viewPager.getCurrentItem());
                if(position == 0){
                    previousButton.setText("Cancel");
                    nextButton.setText("Next");
//                    nextButton.setBackgroundColor(getResources().getColor(R.color.white));
//                    nextButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                if(position == 1){
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
                    previousButton.setText("Previous");
                    nextButton.setText("Next");
//                    nextButton.setBackgroundColor(getResources().getColor(R.color.white));
//                    nextButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }

                if(position == 2){
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
                    previousButton.setText("Previous");
                    nextButton.setText("Create Group");
//                    nextButton.setBackgroundColor(getResources().getColor(R.color.white));
//                    nextButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }

            }
        });

        navController = NavHostFragment.findNavController(CreateGroupFragment.this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pageAdapter = new NewGroupPagerAdapter(getChildFragmentManager());
        //get the viewpager
        viewPager = rootView.findViewById(R.id.co_new_gr_pager);
        viewPager.setAdapter(pageAdapter);

        super.onViewCreated(view, savedInstanceState);


        final NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(R.id.createGroupFragment);

        final LifecycleEventObserver observer = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                Log.d(TAG, "onStateChanged: "+event);
                if (event.equals(Lifecycle.Event.ON_RESUME)
                        && navBackStackEntry.getSavedStateHandle().contains("imagePicked")) {

                    String[] imagePaths = navBackStackEntry.getSavedStateHandle().get("imagePicked");
                    String imagePath = ((String[])imagePaths)[0];
                    String userOrGroup = ((String[])imagePaths)[1];
                    String thumbnailImagePath = ((String[])imagePaths)[2];

                    if("Group".equalsIgnoreCase(userOrGroup)){
                        NewGroupInfo3Fragment fragment3 = (NewGroupInfo3Fragment) fragmentList.get(2);
                        fragment3.setPicturePath(imagePath, thumbnailImagePath);
                        viewPager.setCurrentItem(2);
                    }
                    else{
                        NewGroupInfo2Fragment fragment2 = (NewGroupInfo2Fragment) fragmentList.get(1);
                        fragment2.setPicturePath(imagePath, thumbnailImagePath);
                        viewPager.setCurrentItem(1);
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










//        MutableLiveData groupNewImageLiveData = navController.getCurrentBackStackEntry()
//                .getSavedStateHandle()
//                .getLiveData("imagePicked");
//        groupNewImageLiveData.observe(getViewLifecycleOwner(), new Observer() {
//            @Override
//            public void onChanged(Object o) {
//                Log.d(TAG, "Received new group image path - "+o);
//                String imagePath = ((String[])o)[0];
//                String userOrGroup = ((String[])o)[1];
//                String thumbnailImagePath = ((String[])o)[2];
//                if("Group".equalsIgnoreCase(userOrGroup)){
//                    NewGroupInfo3Fragment fragment3 = (NewGroupInfo3Fragment) fragmentList.get(2);
//                    fragment3.setPicturePath(imagePath, thumbnailImagePath);
//                    viewPager.setCurrentItem(2);
//                }
//                else{
//                    NewGroupInfo2Fragment fragment2 = (NewGroupInfo2Fragment) fragmentList.get(1);
//                    fragment2.setPicturePath(imagePath, thumbnailImagePath);
//                    viewPager.setCurrentItem(1);
//                }
//
//            }
//        });
    }

    View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleNext();
        }
    };

    View.OnClickListener previousButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //get the current index first, find out which fragment it is
            //then see if the important stuff has been filled or not
            //if not, then show a message to the user
            //if yes, then go to the next fragment

            int index = viewPager.getCurrentItem();
            if(index > 0){
                viewPager.setCurrentItem(index - 1);
            }
            else {
                Snackbar snackbar = Snackbar.make(getView(), "Cancelling Group Creation", Snackbar.LENGTH_SHORT);
                snackbar.show();
                navController.popBackStack();
            }


        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        subscribeObservers();
    }


    private void subscribeObservers() {
        createGroupViewModel.getCreateGroup().removeObservers(getViewLifecycleOwner()); //if any old hanging
        createGroupViewModel.getCreateGroup().observe(getViewLifecycleOwner(), new Observer<CreateGroupStatusContants>() {
            @Override
            public void onChanged(CreateGroupStatusContants createGroupStatusContants) {

                Log.d(TAG, "onChanged: ");
                if(createGroupStatusContants == CreateGroupStatusContants.LOADING_GROUP){
                    DialogHelper.showWaitDialog(getContext(), "Please wait..");
                }
//                if(createGroupStatusContants == CreateGroupStatusContants.SUCCESS_GROUP){
//                    DialogHelper.updateWaitDialogMessage("Please wait..group created, loading images");
//                }
//                if(createGroupStatusContants == CreateGroupStatusContants.SUCCESS_GROUP_IMAGE){
//                    DialogHelper.updateWaitDialogMessage("Group image loaded, loading your image");
//                }
                if(createGroupStatusContants == CreateGroupStatusContants.ERROR_GROUP){
                    progressBar.setVisibility(View.GONE);
                    DialogHelper.closeWaitDialog();
                    if(navController.getCurrentDestination().getId() == R.id.createGroupFragment){
                        DialogHelper.showOnlyYesAlert(getContext(),getString(R.string.group_creation_error), getString(R.string.group_join_create_success_label), "Got It" ,new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                navController.popBackStack(); //get out of alert dialog fragment
//                                navController.popBackStack(R.id.groupsListFragment, false);

                            }
                        });
                    }
//                    Toast.makeText(getContext(),"Sorry, something wrong happened. The group creation did not go through", Toast.LENGTH_SHORT).show();
//                    navController.navigate(R.id.groupsListFragment);
                    navController.popBackStack(R.id.groupsListFragment, false);
                }
                if(createGroupStatusContants == CreateGroupStatusContants.ERROR_GROUP_IMAGE){
//                    progressBar.setVisibility(View.GONE);
                    DialogHelper.closeWaitDialog();
                    if(navController.getCurrentDestination().getId() == R.id.createGroupFragment){
                        DialogHelper.showOnlyYesAlert(getContext(),getString(R.string.group_image_failed), getString(R.string.group_join_create_success_label), "Got It" ,new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                navController.popBackStack(); //get out of alert dialog fragment
//                                navController.popBackStack(R.id.groupsListFragment, false);

                            }
                        });
                    }
//                    Toast.makeText(getContext(),"Group created, but Group image upload failed. Please check in group details", Toast.LENGTH_SHORT).show();
//                    navController.navigate(R.id.groupsListFragment);
                    navController.popBackStack(R.id.groupsListFragment, false);
                }
                if(createGroupStatusContants == CreateGroupStatusContants.ERROR_GROUP_USER_IMAGE){
//                    progressBar.setVisibility(View.GONE);
                    DialogHelper.closeWaitDialog();
                    if(navController.getCurrentDestination().getId() == R.id.createGroupFragment){
                        DialogHelper.showOnlyYesAlert(getContext(),getString(R.string.group_user_image_failed), getString(R.string.group_join_create_success_label), "Got It" ,new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                navController.popBackStack(); //get out of alert dialog fragment
//                                navController.popBackStack(R.id.groupsListFragment, false);

                            }
                        });
                    }
//                    Toast.makeText(getContext(),"Group created, but your profile image upload failed. Please check in group details", Toast.LENGTH_SHORT).show();
//                    navController.navigate(R.id.groupsListFragment);
                    navController.popBackStack(R.id.groupsListFragment, false);
                }
                if(createGroupStatusContants == CreateGroupStatusContants.SUCCESS_GROUP_USER_IMAGE){
                    Log.d(TAG, "dialog being shown after create - "+System.currentTimeMillis());
//                    progressBar.setVisibility(View.GONE);
                    DialogHelper.closeWaitDialog();
                    //get the UserGroup from the viewmodel
                    UserGroupModel domainUserGroup = createGroupViewModel.getDomainUserGroup();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", domainUserGroup);
                    if(navController.getCurrentDestination().getId() == R.id.createGroupFragment) {
                        //first go to Group List and then go to GMF from there
                        navController.popBackStack(R.id.groupsListFragment, false);
                        navController.navigate(R.id.action_groupsListFragment_to_groupMessagesFragment, bundle);

                    }

//                    if(navController.getCurrentDestination().getId() == R.id.createGroupFragment){
//                        DialogHelper.showOnlyYesAlert(getContext(),getString(R.string.join_create_success), getString(R.string.group_join_create_success_label), "Got It" ,new DialogInterface.OnClickListener(){
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
////                                navController.popBackStack(); //get out of alert dialog fragment
////                                navController.popBackStack(R.id.groupsListFragment, false);
//
//                            }
//                        });
//                    }
//                    navController.popBackStack(R.id.groupsListFragment, false);

//                    Log.d(TAG, "current destination - "+navController.getCurrentDestination());
//                    if(navController.getCurrentDestination().getId() == R.id.createGroupFragment){
//                        Toast.makeText(getContext(),getString(R.string.join_create_success), Toast.LENGTH_SHORT).show();
//                    }
//                    navController.navigate(R.id.groupsListFragment);

                }
            }
        });
    }

    private void initializeFragments() {

        fragmentList = new ArrayList<>();

        fragmentList.add(new NewGroupInfo1Fragment(this));
        fragmentList.add(new NewGroupInfo2Fragment(this, this));
        fragmentList.add(new NewGroupInfo3Fragment(this));
    }

    @Override
    public void nextClicked() {
        handleNext();
    }

    @Override
    public void selectGroupImageClicked() {
        Bundle args = new Bundle();
        args.putBoolean("pictureToBeSavedOnServer", false);
        args.putString("userData", "Group");
        args.putString("imageId", "groupId");

        navController.navigate(R.id.action_createGroupFragment_to_pictureDialog, args);
    }

    @Override
    public void onLoginSuccess() {
        subscribeObservers();
    }

    @Override
    public void onNetworkOff() { }

    @Override
    public void onNetworkOn() { }

    @Override
    public void handleNext(boolean enable) {
        if(enable){
            nextButton.setEnabled(true);
        }
        else{
            nextButton.setEnabled(false);
        }
    }

    @Override
    public void selectUserImageClicked() {
        Bundle args = new Bundle();
        args.putBoolean("pictureToBeSavedOnServer", false);
        args.putString("userData", "User");
        args.putString("imageId", "userId");
        navController.navigate(R.id.action_createGroupFragment_to_pictureDialog, args);
    }

    class NewGroupPagerAdapter extends FragmentPagerAdapter {

        public NewGroupPagerAdapter(FragmentManager fm) {
//            super(fm);
            super(fm , FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }
        @Override
        public Fragment getItem(int position) {
//            Log.d(TAG, "Coming to getItem for position "+position);
            return fragmentList.get(position);
        }
        @Override
        public int getCount() {
            return 3;
        }

    }


    public void handleNext(){

        //get the current index first, find out which fragment it is
        //then see if the important stuff has been filled or not
        //if not, then show a message to the user
        //if yes, then go to the next fragment

        int index = viewPager.getCurrentItem();
        boolean readyToGoNext = true;

        //check if items required have been filled up
        Fragment fragmentActive = pageAdapter.getItem(index);
        if(fragmentActive instanceof  NewGroupInfo1Fragment){
            //check if group name and description has been filled up
            if(((NewGroupInfo1Fragment) fragmentActive).isFormValid()){
                ((NewGroupInfo1Fragment) fragmentActive).fillUpGroupObject();
            }
            else{
                readyToGoNext = false;
                Toast.makeText(getContext(), ((NewGroupInfo1Fragment) fragmentActive).getFormErrorMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        else if(fragmentActive instanceof  NewGroupInfo2Fragment){
            //check if alias and notification has been selected
            if(((NewGroupInfo2Fragment) fragmentActive).isFormValid()){
                ((NewGroupInfo2Fragment) fragmentActive).fillUpGroupObject();
            }
            else{
                readyToGoNext = false;
                Toast.makeText(getContext(), ((NewGroupInfo2Fragment) fragmentActive).getFormErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else if(fragmentActive instanceof NewGroupInfo3Fragment){
            ((NewGroupInfo3Fragment) fragmentActive).fillUpGroupObject();
            readyToGoNext = false;
//            progressBar.setVisibility(View.VISIBLE);
//            DialogHelper.showWaitDialog(getActivity(), "Please wait...");
//            BackendAPIImpl.getBackendAPIImpl().createGroup(this, new GroupCreateRequest(groupCreated), BroadcastConstants.GROUP_CREATED_BROADCAST.toString(), BroadcastConstants.ERROR_GROUP_CREATED_BROADCAST.toString());
            Log.d(TAG, "user clicked create - "+System.currentTimeMillis());
            createGroupViewModel.createGroup();

        }

        if(readyToGoNext)
            viewPager.setCurrentItem(index + 1);
    }


}


