package com.java.kaboome.presentation.views.features.home;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.java.kaboome.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DrawerBottomSheetFragment extends BottomSheetDialogFragment {

    View view;
    NavigationView navigationView;
    NavController navController;

    public DrawerBottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.drawer_bottom_sheet, container, false);
        navigationView = view.findViewById(R.id.navigation_view);
        navController = Navigation.findNavController(getActivity(), R.id.fragment);
        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.profileFragment:
                        NavigationUI.onNavDestinationSelected(menuItem, navController);
                        return true;
//                        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
//                        startActivity(profileIntent);
//                        DrawerBottomSheetFragment.this.dismiss();
//                        break;

                    case R.id.drawer_settings:
                        Toast.makeText(DrawerBottomSheetFragment.this.getContext(), "This feature has not been implemented yet", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.helpFragment:
//                        Toast.makeText(DrawerBottomSheetFragment.this.getContext(), "This feature has not been implemented yet", Toast.LENGTH_SHORT).show();
                        NavigationUI.onNavDestinationSelected(menuItem, navController);
                        return true;
                }
                return false;
            }
        });


    }


}
