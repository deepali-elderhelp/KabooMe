package com.java.kaboome.presentation.views.features.createGroup;


import android.app.DatePickerDialog;
import android.os.Bundle;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.java.kaboome.R;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.views.features.createGroup.viewmodel.CreateGroupViewModel;
import com.java.kaboome.presentation.views.features.createGroup.adapter.SelectGroupImageListener;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGroupInfo3Fragment extends Fragment {

    private static final String TAG = "KMNewGroupInfo3Frag";

    View view;
    Calendar selectedDate;
    private ImageView groupImage;
    private String picturePath;
    private String thumbnailPicturePath;
    private FloatingActionButton edit;
    private CreateGroupViewModel createGroupViewModel;
    private SwitchCompat publicOrPrivateSwitch;
    boolean privacy = false;
    private SelectGroupImageListener selectGroupImageListener;


    public NewGroupInfo3Fragment() {
        // Required empty public constructor
    }

    public NewGroupInfo3Fragment(SelectGroupImageListener selectGroupImageListener) {
        this.selectGroupImageListener = selectGroupImageListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createGroupViewModel = ViewModelProviders.of(getParentFragment()).get(CreateGroupViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_new_group_info3, container, false);
        publicOrPrivateSwitch = view.findViewById(R.id.fr_cr_gr_3_pu_pr_sw);
        publicOrPrivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    DialogHelper.showDialogMessage(getActivity(), "Private Group", getResources().getString(R.string.group_private_alert));
                    privacy = true;
                } else {
                    DialogHelper.showDialogMessage(getActivity(), "Public Group", getResources().getString(R.string.group_public_alert));
                    privacy = false;
                }
            }
        });
        final EditText datePicker = view.findViewById(R.id.fr_cr_gr_3_expiry);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //upload the values to main activity
                                datePicker.setText(monthOfYear+"/"+dayOfMonth+"/"+year);
                                selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();



            }
        });

        groupImage = view.findViewById(R.id.fr_cr_gr_3_pic);
        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PictureDialog pictureDialog = new PictureDialog();
                selectGroupImageListener.selectGroupImageClicked();

            }
        });

        edit = view.findViewById(R.id.fr_cr_gr_3_pic_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGroupImageListener.selectGroupImageClicked();
            }
        });


        return view;
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d(TAG, "isVisible and picture path is "+this.picturePath);
            //if selected pic is already there, that means user is revisiting by pressing previous
            //he had selected a pic before
            if(this.picturePath != null){
               edit.show();
                Glide.with(this)
                        .asBitmap()
                        .load(this.picturePath)
                        .into(groupImage);
            }
            else{
                edit.hide();
            }
        }
        else {
//            Log.d(TAG, "Review Fragment is not visible to the user");
        }
    }


    public void fillUpGroupObject(){

        Long dateSelected = 0L;
        if(selectedDate != null && !DateHelper.isTodaysDate(selectedDate)){
            dateSelected = selectedDate.getTimeInMillis();
        }

        createGroupViewModel.addGroupPrivacyExpiryAndPicturePath(dateSelected, privacy, picturePath, thumbnailPicturePath);

    }

    public void setPicturePath(String imagePath, String thumbnailPicturePath) {
        this.picturePath = imagePath;
        this.thumbnailPicturePath = thumbnailPicturePath;
        if(this.picturePath != null){
            edit.show();

            Glide.with(this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .asBitmap()
                    .load(this.picturePath)
                    .into(groupImage);
        }
        else{
            edit.hide();
        }
    }

//    @Override
//    public void pictureDone(String imagePath, String thumbnailPath) {
//        this.picturePath = imagePath;
//        this.thumbnailPath = thumbnailPath;
//
//        Glide.with(this)
//                .asBitmap()
//                .load(imagePath)
//                .into(groupImage);
//    }
}
