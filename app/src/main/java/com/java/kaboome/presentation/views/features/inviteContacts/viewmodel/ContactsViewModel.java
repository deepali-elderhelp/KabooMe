package com.java.kaboome.presentation.views.features.inviteContacts.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.constants.InviteContactsConstants;
import com.java.kaboome.data.remote.requests.InviteContactsRequest;
import com.java.kaboome.data.repositories.DataContactsRepository;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.ContactsRepository;
import com.java.kaboome.domain.usecases.GetContactListUseCase;
import com.java.kaboome.domain.usecases.InviteContactsUseCase;
import com.java.kaboome.presentation.entities.ContactModel;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.mappers.ContactModelDomainMapper;
import com.java.kaboome.presentation.mappers.InvitationModelMapper;

import java.util.ArrayList;
import java.util.List;

public class ContactsViewModel extends ViewModel {

    private static final String TAG = "KMContactsViewModel";

    private List<ContactModel> contactsSelected;
    private List<ContactModel> allContacts;
    private ContactsRepository contactsRepository;
    private GetContactListUseCase getContactListUseCase;
    private InviteContactsUseCase inviteContactsUseCase;
//    private MediatorLiveData<String> inviteContactsStatus = new MediatorLiveData<>();

//    public MediatorLiveData<String> getInviteContactsStatus() {
//        return inviteContactsStatus;
//    }

    public ContactsViewModel() {
        contactsSelected = new ArrayList<>();
        allContacts = new ArrayList<>();
        this.contactsRepository = new DataContactsRepository();
        getContactListUseCase = new GetContactListUseCase(contactsRepository);
        inviteContactsUseCase = new InviteContactsUseCase(contactsRepository);
    }


    public List<ContactModel> getContacts() {
        if(allContacts.isEmpty()){
            allContacts = ContactModelDomainMapper.transformAllFromDomainToModel(getContactListUseCase.execute(null));
        }

//        return ContactModelDomainMapper.transformAllFromDomainToModel(contactsRepository.fetchContacts());

        return allContacts;

    }

    public List<ContactModel> getContactsSelected() {
        return contactsSelected;
    }

    public void inviteContacts(InvitationModel invitationModel){

        inviteContactsUseCase.execute(InviteContactsUseCase.Params.inviteContacts(ContactModelDomainMapper.transformAllFromContactModelToDomain(contactsSelected),
                InvitationModelMapper.transformFromInvitation(invitationModel), InviteContactsConstants.INVITE_CONTACTS.toString()));

    }

    public void addCheckedContact(ContactModel contactChecked){
        this.contactsSelected.add(contactChecked);
    }

    public void removedCheckedContact(ContactModel contactRemoved){
        this.contactsSelected.remove(contactRemoved);
    }

    public boolean isContactAlreadySelected(ContactModel contactToBeChecked){
        if(this.contactsSelected.contains(contactToBeChecked))
            return true;
        return false;
    }




}
