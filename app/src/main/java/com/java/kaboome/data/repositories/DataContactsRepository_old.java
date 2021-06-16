package com.java.kaboome.data.repositories;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.java.kaboome.data.entities.Contact;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.ContactDataDomainMapper;
import com.java.kaboome.data.remote.requests.InviteContactsRequest;
import com.java.kaboome.domain.entities.DomainContact;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.repositories.ContactsRepository;
import com.java.kaboome.helpers.AppConfigHelper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataContactsRepository_old implements ContactsRepository {

    private static final String TAG = "DataContactsRepository";

    @Override
    public List<DomainContact> fetchContacts() {

        List<Contact> contacts = new ArrayList<>();


        // Easy way to limit the query to contacts with phone numbers.
        String selection =
                ContactsContract.CommonDataKinds.Contactables.HAS_PHONE_NUMBER + " = " + 1 + " and "+

        ContactsContract.CommonDataKinds.Phone.TYPE +"=="+  ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

        // Sort results such that rows for the same contact stay together.
        String sortBy = ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME_PRIMARY;



//        return new CursorLoader(
//                mContext,  // Context
//                uri,       // URI representing the table/resource to be queried
//                null,      // projection - the list of columns to return.  Null means "all"
//                selection, // selection - Which rows to return (condition rows must match)
//                null,      // selection args - can be provided separately and subbed into selection.
//                sortBy);   // string specifying sort order
        // END_INCLUDE(cursor_loader)


        Cursor cursor = AppConfigHelper.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, selection, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if ((cursor != null ? cursor.getCount() : 0) > 0) {
//            while (cursor.moveToNext()) {
//
//                Contact contact = new Contact();
//
//                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
//
//                Log.e(TAG, "getAllContacts: " + name + " " + phoneNo + " " + photoUri);
//                contact.setName(name);
//                contact.setPhone(phoneNo);
//                contact.setPhotoURI(photoUri);
//
//                contacts.add(contact);
//
//            }

            int phoneColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int emailColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME);
            int lookupColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.LOOKUP_KEY);
            int typeColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.MIMETYPE);
            int photoUriColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
            int photoThumbnailUriColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
            // END_INCLUDE(get_columns)

            cursor.moveToFirst();
            // Lookup key is the easiest way to verify a row of data is for the same
            // contact as the previous row.
            String lookupKey = "";
            Contact contact = null;
            do {


                // BEGIN_INCLUDE(lookup_key)
                String currentLookupKey = cursor.getString(lookupColumnIndex);
                if (!lookupKey.equals(currentLookupKey)) {
                    if(contact != null){ //checking for null, cause coming first one might be null
                        //add it to the contact list array
                        contacts.add(contact);
                    }
                    //now create a new contact, it's a different person
                    contact = new Contact();
                    String displayName = cursor.getString(nameColumnIndex);
                    //tv.append(displayName + "\n");
//                    Log.d(TAG, "displayName - "+displayName+ "\n");
                    //contact.setLookupKey(lookupKey);
                    contact.setLookupKey(currentLookupKey);
                    contact.setName(displayName);
                    contact.setPhone(cursor.getString(phoneColumnIndex));
                    contact.setPhotoURI(cursor.getString(photoUriColumnIndex));
                    lookupKey = currentLookupKey;
                    Log.d(TAG, "Contact "+contact);
                }
                // END_INCLUDE(lookup_key)

                // BEGIN_INCLUDE(retrieve_data)
                // The data type can be determined using the mime type column.
//                String mimeType = cursor.getString(typeColumnIndex);
//                if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
//
//                    //tv.append("\tPhone Number: " + cursor.getString(phoneColumnIndex) + "\n");
//                    Log.d(TAG, " Phone Number:  - "+cursor.getString(phoneColumnIndex)+ "\n");
//                    contact.setPhone(cursor.getString(phoneColumnIndex));
//                }

//
//                else if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
//                    // tv.append("\tEmail Address: " + cursor.getString(emailColumnIndex) + "\n");
//                    Log.d(TAG, " Email Address:  - "+cursor.getString(emailColumnIndex)+ "\n");
//                    contact.addEmailAddress(cursor.getString(emailColumnIndex));
//                }
                // END_INCLUDE(retrieve_data)

                // Look at DDMS to see all the columns returned by a query to Contactables.
                // Behold, the firehose!
//                for(String column : cursor.getColumnNames()) {
//                    Log.d(TAG, column + column + ": " +
//                            cursor.getString(cursor.getColumnIndex(column)) + "\n");
//                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return ContactDataDomainMapper.transformAllFromContactsToDomain(contacts);
    }

    @Override
    public Void inviteSelectedContacts(List<DomainContact> domainContacts, DomainInvitation invitation) {
        sendInvitationsToContacts(domainContacts, invitation);
        return null;
    }

    //    @Override
//    public LiveData<DomainResource<String>> inviteSelectedContacts(List<DomainContact> domainContacts, DomainInvitation invitation) {
//        return Transformations.map(sendSelectedContactsToServer(domainContacts, invitation), new Function<Resource<String>, DomainResource<String>>() {
//            @Override
//            public DomainResource<String> apply(Resource<String> input) {
//                return ResourceDomainResourceMapper.transform(input.status, input.data, input.message);
//            }
//        });
//    }



//    private LiveData<UpdateResource<String>> sendInvitationsToContacts(final List<DomainContact> domainContacts, final DomainInvitation invitation, final String action) {
//        return new NewNetworkBoundUpdateRes<String, Void>(AppExecutors2.getInstance()){
//
//
//            @Override
//            protected String processResult(Void aVoid) {
//                return action;
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<ApiResponse<Void>> createCall() {
//                InviteContactsRequest inviteContactsRequest = new InviteContactsRequest();
//                inviteContactsRequest.setInvitedBy(AppConfigHelper.getUserId());
//                inviteContactsRequest.setInvitedByAlias(invitation.getInvitedByAlias());
//                inviteContactsRequest.setGroupId(invitation.getGroupId());
//                inviteContactsRequest.setGroupName(invitation.getGroupName());
//                inviteContactsRequest.setMessageByInvitee(invitation.getMessageByInvitee());
//                inviteContactsRequest.setPrivateGroup(String.valueOf(invitation.getPrivateGroup()));
//                inviteContactsRequest.setContactList(ContactDataDomainMapper.transformAllFromDomainToContacts(domainContacts));
//
//                return AppConfigHelper.getBackendApiServiceProvider().inviteContactsToJoinGroup(AppConfigHelper.getUserId(), inviteContactsRequest);
//            }
//
//            @Override
//            protected void reflectDataInDB(Void aVoid) {
//                //do nothing
//            }
//
//        }.getAsLiveData();
//    }

    private void sendInvitationsToContacts(final List<DomainContact> domainContacts, final DomainInvitation invitation){

        new NetworkBoundNoReturn(AppExecutors2.getInstance()){

            @NonNull
            @Override
            protected Call<ResponseBody> createCall() {
                InviteContactsRequest inviteContactsRequest = new InviteContactsRequest();
                inviteContactsRequest.setInvitedBy(AppConfigHelper.getUserId());
                inviteContactsRequest.setInvitedByAlias(invitation.getInvitedByAlias());
                inviteContactsRequest.setGroupId(invitation.getGroupId());
                inviteContactsRequest.setGroupName(invitation.getGroupName());
                inviteContactsRequest.setMessageByInvitee(invitation.getMessageByInvitee());
                inviteContactsRequest.setPrivateGroup(String.valueOf(invitation.getPrivateGroup()));
                inviteContactsRequest.setContactList(ContactDataDomainMapper.transformAllFromDomainToContacts(domainContacts));

                return AppConfigHelper.getBackendApiServiceProvider().inviteContactsToJoinGroup(AppConfigHelper.getUserId(), inviteContactsRequest);
            }
        };
    }
}
