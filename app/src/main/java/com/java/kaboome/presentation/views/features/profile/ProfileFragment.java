package com.java.kaboome.presentation.views.features.profile;


import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.exceptions.CognitoInternalErrorException;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.UserActionConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.UserModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.BaseViewModel;
import com.java.kaboome.presentation.views.features.profile.viewmodel.ProfileViewModel;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.java.kaboome.helpers.AppConfigHelper.getContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment {

    private static final String TAG = "KMProfileFragment";


    private View rootView;
    private ProfileViewModel profileViewModel;
    TextView profileName;
    TextView profileEmail;
    TextView profilePhone;
    CircleImageView profileImage;
    ProgressBar profileImageProgressBar;
    ProgressBar profileFullProgressBar;
    private Handler handler = new Handler(); //needed for Glide
    private UserModel userModel;
    private NavController navController;
    private Toolbar mainToolbar;
    private ImageView networkOffImageView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = rootView.findViewById(R.id.profile_name);
        profileEmail = rootView.findViewById(R.id.profile_email);
        profilePhone = rootView.findViewById(R.id.profile_phone);
        profileImage = rootView.findViewById(R.id.profile_image);
        profileImageProgressBar = rootView.findViewById(R.id.profile_image_progress);
        profileFullProgressBar = rootView.findViewById(R.id.profile_full_progress_bar);

        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        mainToolbar.getMenu().clear();
        networkOffImageView = act.findViewById(R.id.mainToolbarNetworkOff);

        navController = NavHostFragment.findNavController(ProfileFragment.this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }

    private void subscribeObservers() {

        profileViewModel.getUser().removeObservers(getViewLifecycleOwner()); //if any old hanging there
        profileViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModelPassed) {
//                Log.d(TAG, "onChanged: This should show after update too");
                //render new data
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if(userModelPassed != null){
                    Log.d(TAG, "UserModelStatus - "+userModelPassed.getStatus());
                    userModel = userModelPassed;
                    profileName.setText(userModel.getUserName());
                    profileEmail.setText(userModel.getEmail());
                    profilePhone.setText(userModel.getPhoneNumber());
                    //load image again in case the timestamp has changed on fresh successful update from server
                    if(!"Loading".equalsIgnoreCase(userModelPassed.getStatus()) && userModelPassed.getImageUpdateTimestamp() != null){

//                        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, userModel.getUserName() != null? userModel.getUserName():"K M");
                        Drawable imageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
                        ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN, userModelPassed.getImageUpdateTimestamp(),
                                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                                handler, profileImage, null);
                    }
                }
            }
        });

        profileViewModel.getUpdatedUser().removeObservers(getViewLifecycleOwner()); //if any old hanging there
        profileViewModel.getUpdatedUser().observe(getViewLifecycleOwner(), new Observer<UserEditDetails>() {
            @Override
            public void onChanged(UserEditDetails userEditDetails) {
                if(userEditDetails != null && (userEditDetails.getStatus() == UserEditDetails.Status.UPDATING)){
                    profileFullProgressBar.setVisibility(View.VISIBLE);
                }
                else if(userEditDetails != null && (userEditDetails.getStatus() == UserEditDetails.Status.SUCCESS)){
                    Log.d(TAG, "User has been updated");
                    profileFullProgressBar.setVisibility(View.GONE);
                    if(userEditDetails.getAction() == UserActionConstants.UPDATE_USER_PROFILE_IMAGE_TS){
                        Drawable imageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
                        ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN,userEditDetails.getImageUpdatedTimestamp(),
                                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                                handler, profileImage, null);
                    }
                }
                else if(userEditDetails != null && (userEditDetails.getStatus() == UserEditDetails.Status.ERROR)){
                    profileFullProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Something went wrong in update, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        profileViewModel.getUploadingUserImage().removeObservers(getViewLifecycleOwner()); //if any old hanging there
        profileViewModel.getUploadingUserImage().observe(getViewLifecycleOwner(), new Observer<UserEditDetails>() {
            @Override
            public void onChanged(UserEditDetails userEditDetails) {
                if(userEditDetails != null && (userEditDetails.getStatus() == UserEditDetails.Status.UPDATING)){
                    profileImageProgressBar.setVisibility(View.VISIBLE);
                }
                else if(userEditDetails != null && (userEditDetails.getStatus() == UserEditDetails.Status.SUCCESS)){
                    Log.d(TAG, "User Image has been updated");
                    profileImageProgressBar.setVisibility(View.GONE);
                }
                else if(userEditDetails != null && (userEditDetails.getStatus() == UserEditDetails.Status.ERROR)){
                    profileImageProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Something went wrong in update, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addListeners() {

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", userModel);
                if(navController.getCurrentDestination().getId() == R.id.profileFragment){
                    navController.navigate(R.id.action_profileFragment_to_userPicDisplayFragment, bundle);
                }
            }
        });

        ImageView profileImageEdit = rootView.findViewById(R.id.profile_edit_image);
        profileImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", userModel);
                if(navController.getCurrentDestination().getId() == R.id.profileFragment){
                    navController.navigate(R.id.action_profileFragment_to_editUserPicFragment, bundle);
                }

            }
        });
        ImageView profileNameEdit = rootView.findViewById(R.id.profile_edit_profile_name);
        profileNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", userModel);
                if(navController.getCurrentDestination().getId() == R.id.profileFragment) {
                    navController.navigate(R.id.action_profileFragment_to_editUserName, bundle);
                }
            }
        });
        ImageView profileEmailEdit = rootView.findViewById(R.id.profile_edit_email);
        profileEmailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", userModel);
                if(navController.getCurrentDestination().getId() == R.id.profileFragment) {
                    navController.navigate(R.id.action_profileFragment_to_editUserEmailFragment, bundle);
                }
            }
        });

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(R.id.profileFragment);

        final LifecycleEventObserver observer = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event.equals(Lifecycle.Event.ON_RESUME)
                        && navBackStackEntry.getSavedStateHandle().contains("userName")) {

                    UserModel userModelTemp = navBackStackEntry.getSavedStateHandle().get("userName");
                    //update user only when network is there
                    if(NetworkHelper.isOnline()){
                        profileViewModel.updateUser(userModelTemp, UserActionConstants.UPDATE_USER_NAME.getAction());
                    }
                    else{
                        Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
                    }

                }
                if (event.equals(Lifecycle.Event.ON_RESUME)
                        && navBackStackEntry.getSavedStateHandle().contains("userEmail")) {
                    UserModel userModelTemp = navBackStackEntry.getSavedStateHandle().get("userEmail");
                    //update user only when network is there
                    if(NetworkHelper.isOnline()){
                        profileViewModel.updateUser(userModelTemp, UserActionConstants.UPDATE_USER_EMAIL.getAction());
                    }
                    else{
                        Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.equals(Lifecycle.Event.ON_RESUME)
                        && navBackStackEntry.getSavedStateHandle().contains("userImage")) {
                    UserModel userModelTemp = navBackStackEntry.getSavedStateHandle().get("userImage");
                    if(NetworkHelper.isOnline()){
                        profileViewModel.uploadUserImage(userModelTemp);
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
    public void onLoginSuccess() {
//        subscribeObservers();
        //timestamp null will load the old existing image
        Drawable imageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
        ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(),ImageTypeConstants.MAIN, AppConfigHelper.getCurrentUserImageTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null);
//        addListeners();

        //initiate the loading
        profileViewModel.loadUser();
    }

    @Override
    public void whileLoginInProgress() {
        subscribeObservers();
        //timestamp null will load the old existing image
        Drawable imageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
        ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(),ImageTypeConstants.MAIN, AppConfigHelper.getCurrentUserImageTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null);
        addListeners();

        //initiate the loading
        profileViewModel.loadUser();
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
