/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.views.features.createGroup;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.createGroup.adapter.HandleNextListener;
import com.java.kaboome.presentation.views.features.createGroup.adapter.SelectUserImageListener;
import com.java.kaboome.presentation.views.features.createGroup.viewmodel.CreateGroupViewModel;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewGroupInfo2Fragment extends Fragment implements TextWatcher {


    private static final String TAG = "KMNewGroupInfo2Fragment";

    private View rootView;
    private TextView alias;
    private CreateGroupViewModel createGroupViewModel;
    private TextView creatorRole;
    private HandleNextListener handleNextListener;
    private CircleImageView userImage;
    private String picturePath;
    private String thumbnailPath;
    private boolean imageChanged = false;
    private TextView changeUserPicLink;
    private SelectUserImageListener selectUserImageListener;

//    TextView groupAlertMessage;




    public NewGroupInfo2Fragment() {
        // Required empty public constructor
    }

    public NewGroupInfo2Fragment(HandleNextListener handleNextListener, SelectUserImageListener selectUserImageListener) {
        this.handleNextListener = handleNextListener;
        this.selectUserImageListener = selectUserImageListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createGroupViewModel = ViewModelProviders.of(getParentFragment()).get(CreateGroupViewModel.class);
    }


    @Override
    public String toString() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_new_group_info2, container, false);



//        groupAlertMessage = rootView.findViewById(R.id.fr_cr_gr_2_group_alert);
        alias = rootView.findViewById(R.id.fr_cr_gr_2_alias);
        alias.addTextChangedListener(this);
        creatorRole = rootView.findViewById(R.id.fr_cr_gr_3_role);
        creatorRole.addTextChangedListener(this);
        creatorRole.setText("Admin");

        userImage = rootView.findViewById(R.id.fr_cr_gr_2_user_image);

        Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
        ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN, AppConfigHelper.getCurrentUserImageTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                new Handler(), userImage, null);

        changeUserPicLink = rootView.findViewById(R.id.fr_cr_gr_2_change_picture_label);
        if(imageChanged){
            changeUserPicLink.setText(getString(R.string.reset_your_group_picture));
        }
        else{
            changeUserPicLink.setText(R.string.change_your_group_picture);
        }

        changeUserPicLink.setOnClickListener(userImageChangeClicked);
        userImage.setOnClickListener(userImageChangeClicked);
        
        return rootView;
    }

    private View.OnClickListener userImageChangeClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageChanged){
                    //user had selected some other image, but now wants tp reset to the default one
                    Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
                    ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(),ImageTypeConstants.MAIN, AppConfigHelper.getCurrentUserImageTimestamp(),
                            ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                            new Handler(), userImage, null);
                    imageChanged = false;
                }
                else{
                    selectUserImageListener.selectUserImageClicked();
                    imageChanged = true;
                }


            }
        };


//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser){
//            hideKeyboardFrom(getContext(), getView());
//        }
//    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        handleNext();
    }

    private void handleNext() {
        String alias = getAlias();
        String role = getCreatorRole();
        //weird but coming back from camera, it has failed here once
        if(handleNextListener != null) {
            handleNextListener.handleNext(!alias.isEmpty() && !role.isEmpty());
        }
    }


//    public void hideKeyboardFrom(Context context, View view) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    public String getAlias(){
        return alias.getText().toString().trim();
    }

    public String getCreatorRole(){
        return creatorRole.getText().toString().trim();
    }


    public boolean isFormValid(){
        String aliasText = getAlias();
        String creatorRoleText = getCreatorRole();


        if(aliasText != null && !(aliasText.isEmpty())){
            return true;
        }
        if(creatorRoleText != null && !(creatorRoleText.isEmpty())){
            return true;
        }

        return false;
    }

    public String getFormErrorMessage(){
        String aliasText = getAlias();
        String creatorRoleText = getCreatorRole();
        if(aliasText == null || (aliasText.isEmpty())){
            return "Your Alias for the group is required";
        }
        if(creatorRoleText == null && (creatorRoleText.isEmpty())){
            return "Your Role for the group is required";
        }

        return "Some information is missing, please fill it up";

    }

    public void fillUpGroupObject(){

        createGroupViewModel.addGroupAliasAndRole(getAlias(), getCreatorRole(), picturePath, imageChanged, thumbnailPath);
//        groupCreated.setGroupCreatorAlias(getGroupCreatorAlias());
//        groupCreated.setPrivateGroup(privacy);
    }

    /**
     * Not caching Glide images intentionally when loaded from local machine - like when user selects an image from the camera..
     * because new one selected next time overwrites it,
     * but glide loads the old one, sicne now we are keeping the image id same so that not a lot of images are stored in the app directory.
     * @param imagePath
     * @param thumbnailPath
     */
    public void setPicturePath(String imagePath, String thumbnailPath) {

        this.picturePath = imagePath;
        this.thumbnailPath = thumbnailPath;
        if(this.picturePath != null){
            Glide.with(this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .asBitmap()
                    .load(this.picturePath)
                    .into(userImage);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
       handleNext();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
