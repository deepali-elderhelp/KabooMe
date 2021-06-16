package com.java.kaboome.presentation.views.features.inviteContacts;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.java.kaboome.R;
import com.java.kaboome.constants.InvitationStatusConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.ContactModel;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.inviteContacts.viewmodel.ContactsViewModel;
import com.java.kaboome.presentation.views.features.inviteContacts.adapter.InvitationContactListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteContactsFragment extends BaseFragment implements InvitationContactListener, SearchView.OnQueryTextListener {

    private static final String TAG = "KMInviteContactsFrag";

    private View rootView;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=22;

    private ContactsViewModel contactsViewModel;

    private InviteContactsListViewAdapter inviteContactsListViewAdapter;
    private UserGroupModel group;
    private Toolbar mainToolbar;
    private SearchView searchView;
    private AppCompatButton doneButton;
    private MenuItem itemSearch;


    public InviteContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactsViewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);
        this.group = (UserGroupModel)getArguments().getSerializable("group");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_invite_contacts, container, false);

        doneButton = rootView.findViewById(R.id.invite_contacts_done_button);
        doneButton.setOnClickListener(selectionDone);

        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        mainToolbar.getMenu().clear(); //removing old menu if any
        mainToolbar.inflateMenu(R.menu.contact_search_menu);

        Log.d(TAG, "onCreateView: width - "+mainToolbar.getWidth());

        itemSearch = mainToolbar.getMenu().findItem(R.id.action_search);

        searchView = new SearchView(getContext());
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.clearFocus();
//        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search");
        searchView.setMaxWidth(((Double)(mainToolbar.getWidth() * 0.7)).intValue());

//        MenuItemCompat.setActionView(itemSearch, searchView);

        itemSearch.setActionView(searchView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("message");
        liveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received message from MessageDialog - "+o);
                String message = (String) o;
                onMessageWritten(message);

            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
//        this.group = (UserGroupModel)getArguments().getSerializable("group");
//
//        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//            loadContacts();
//        }
//        else{
//            requestRequiredPermissions();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        itemSearch.setActionView(null);

//        mainToolbar.getMenu().clear();

    }

    private void requestRequiredPermissions() {
        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                    .setTitle("Permissions requested")
                    .setMessage("Read contacts permission is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //request permission now
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
        else{
            //request permission directly
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS){
            if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loadContacts();
            }
            else{
                Log.d(TAG, "onRequestPermissionsResult: Permission DENIED, Cannot load contacts");
                Toast.makeText(getContext(), "Permission DENIED, Cannot load contacts", Toast.LENGTH_SHORT);

            }
        }

    }

    private void loadContacts(){
//        allContacts = contactsViewModel.getContacts();
//        initRecyclerView();
        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        Log.d(TAG, "setUpRecyclerView: ");
        if(inviteContactsListViewAdapter == null) { //first time
            RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_contact_list);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            inviteContactsListViewAdapter = new InviteContactsListViewAdapter(contactsViewModel.getContacts(), getContext(), this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(inviteContactsListViewAdapter);
        }
//        else{
//            inviteContactsListViewAdapter.updateData(contactsViewModel.getContacts());
//        }
        //Not sure why, but a bug is caused by the lines above
        //go to contact list, turn phone off, turn it on
        //it comes here to the else and there is data, but updateData resets the data to blank
        //not sure why

    }




    View.OnClickListener selectionDone = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "selection done");
            boolean onlySelfUser = false;

//            int size = contactsViewModel.getContactsSelected().size();
            for(int index = 0; index < contactsViewModel.getContactsSelected().size(); index++) {
                Log.d(TAG, "Selected: " + contactsViewModel.getContactsSelected().get(index).getName());
                String phoneNumberSelected = contactsViewModel.getContactsSelected().get(index).getPhone();
                if(phoneNumberSelected != null && PhoneNumberUtils.compare(phoneNumberSelected, CognitoHelper.getCurrUser())){
                    Log.d(TAG, "onClick: Same user is sending invitation to himself");
                    if(contactsViewModel.getContactsSelected().size() == 1){
                        onlySelfUser = true;
                    }
                    contactsViewModel.getContactsSelected().remove(index);
                }

            }

            if(contactsViewModel.getContactsSelected().size() > 0){
                //see if the user wants to send message

                NavHostFragment.findNavController(InviteContactsFragment.this).navigate(R.id.action_inviteContactsFragment_to_inviteContactsMessageDialog);
            }
            else{
                if(onlySelfUser){
                    DialogHelper.showDialogMessage(getActivity(), "Already Member", "You are already a member of this group");
                }
                else {
                    DialogHelper.showDialogMessage(getActivity(), "No contacts selected", "No contacts have been invited to join this group");
                }
            }



            //alert that they will be invited
        }
    };

    public void onMessageWritten(String message) {
        if(message == null){
            return; //it may not be gone to the message dialog yet
        }
        InvitationModel invitationModel = new InvitationModel();
        invitationModel.setGroupId(group.getGroupId());
        invitationModel.setGroupName(group.getGroupName());
        invitationModel.setInvitedBy(AppConfigHelper.getUserId());
        invitationModel.setInvitedByAlias(group.getAlias());
        invitationModel.setPrivateGroup(group.getPrivate());
        invitationModel.setInvitationStatus(InvitationStatusConstants.NO_ACTION);
        invitationModel.setMessageByInvitee(message);

        if(NetworkHelper.isOnline()){
            //add message to invitees later
            contactsViewModel.inviteContacts(invitationModel );
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Invitations Sent").setMessage("Invited contacts would get notified of the invitations soon")
                    .setNeutralButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //pop this fragment up too
                            NavController navController = NavHostFragment.findNavController(InviteContactsFragment.this);
                            navController.popBackStack();
                        }
                    });
            builder.create().show();
        }
        else{
            Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void contactChecked(ContactModel contactModel) {
        contactsViewModel.addCheckedContact(contactModel);
    }

    @Override
    public void contactCheckedRemoved(ContactModel contactModel) {
        contactsViewModel.removedCheckedContact(contactModel);
    }

    @Override
    public boolean isContactAlreadySelected(ContactModel contactToBeChecked) {
        if(contactsViewModel.getContactsSelected().contains(contactToBeChecked))
            return true;
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: newText - "+newText);
        inviteContactsListViewAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onLoginSuccess() {
        this.group = (UserGroupModel)getArguments().getSerializable("group");

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            loadContacts();
        }
        else{
            requestRequiredPermissions();
        }
    }

    @Override
    public void onNetworkOff() {
        if(doneButton != null) {doneButton.setEnabled(false);}
    }

    @Override
    public void onNetworkOn() {
        if(doneButton != null) {doneButton.setEnabled(true);}
    }
}
