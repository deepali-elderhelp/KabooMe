package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.java.kaboome.data.entities.Invitation;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.InvitationDataDomainMapper;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.persistence.InvitiationDao;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.InvitationsResponse;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.util.List;

public class DataInvitationsListRepository implements InvitationsListRepository {

    private static final String TAG = "KMDataInviListRepo";
    private static DataInvitationsListRepository instance;
    private InvitiationDao invitiationDao;

    private DataInvitationsListRepository() {
        invitiationDao = AppConfigHelper.getKabooMeDatabaseInstance().getInvitationDao();

    }

    public static DataInvitationsListRepository getInstance(){
        if(instance == null){
            instance = new DataInvitationsListRepository();
        }
        return instance;
    }

    @Override
    public LiveData<DomainResource<List<DomainInvitation>>> getInvitationsList(boolean shouldFetchFromServer) {
        return Transformations.map(getInvitationsListFromServer(shouldFetchFromServer), new Function<Resource<List<Invitation>>, DomainResource<List<DomainInvitation>>>() {
            @Override
            public DomainResource<List<DomainInvitation>> apply(Resource<List<Invitation>> input) {
                return ResourceDomainResourceMapper.transform(input.status, InvitationDataDomainMapper.transformAllFromInvitation(input.data), input.message);
            }
        });
    }


    public LiveData<Resource<List<Invitation>>> getInvitationsListFromServer(final boolean shouldFetchFromServer) {
        return new NetworkBoundResource<List<Invitation>, InvitationsResponse>(AppExecutors2.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull InvitationsResponse item) {

                //there would be old invitations which were removed from database but hanging here in cache
                //so it is a good idea to delete all of them first, hence using the transaction which deletes and then inserts


                Log.d(TAG, "saveCallResult");
                if (item.getInvitations() != null) {

                    Invitation[] invitations = new Invitation[item.getInvitations().size()];

//                    int index = 0;
//                    for (long rowid : invitiationDao.insertInvitations((Invitation[]) (item.getInvitations().toArray(invitations)))) {
//                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
//                        index++;
//                    }

                    int index = 0;
                    for (long rowid : invitiationDao.deleteAndInsert((Invitation[]) (item.getInvitations().toArray(invitations)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Invitation> data) {
                Log.d(TAG, "shouldFetch");
                if (NetworkHelper.isOnline() && shouldFetchFromServer) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Invitation>> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                return invitiationDao.getInvitations();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<InvitationsResponse>> createCall() {
                Log.d(TAG, "createCall");
                return AppConfigHelper.getBackendApiServiceProvider().getUserInvitations(AppConfigHelper.getUserId());
            }
        }.getAsLiveData();


    }

//    @Override
//    public LiveData<DomainDeleteResource<String>> rejectInvitation(final String groupId) {
//        return Transformations.map(rejectInvitationAtServerAndLocal(groupId), new Function<DeleteResource<String>, DomainDeleteResource<String>>() {
//            @Override
//            public DomainDeleteResource<String> apply(DeleteResource<String> input) {
//                Log.d(TAG, "apply: response is here");
//                return DeleteResourceDomainResourceMapper.transform(input.status, input.data, input.message);
//            }
//        });
//    }


//     private LiveData<DeleteResource<String>> rejectInvitationAtServerAndLocal(final String groupId){
//
//           return new NetworkBoundDeleteResource<String>(AppExecutors2.getInstance(),"rejectInvitation"){
//
//                @Override
//                protected boolean shouldDelete() {
//                    return true;
//                }
//
//                @NonNull
//                @Override
//                protected Call<ResponseBody> createCall() {
//                    return AppConfigHelper.getBackendApiServiceProvider().rejectUserInvitation(AppConfigHelper.getRequestUserId(), groupId);
//
//                }
//
//                @Override
//                protected void deleteInDB() {
//                   invitiationDao.deleteInvitationForGroup(groupId);
//
//                }
//            }.getAsLiveData();
//
//    }

    @Override
    public LiveData<DomainResource<List<DomainInvitation>>> rejectInvitation(final String groupId) {
        return Transformations.map(rejectInvitationAtServerAndLocal(groupId), new Function<Resource<List<Invitation>>, DomainResource<List<DomainInvitation>>>() {
            @Override
            public DomainResource<List<DomainInvitation>> apply(Resource<List<Invitation>> input) {
                Log.d(TAG, "apply: response is here");
                return ResourceDomainResourceMapper.transform(input.status, InvitationDataDomainMapper.transformAllFromInvitation(input.data), input.message);
            }
        });
    }

    @Override
    public void addNewInvitation(final DomainInvitation invitation) {
        //only create in the cache, server one gets created in the cloud
        //this case only happens when user requests to join a private group
        //in that case a pending invitation is created

        //needs to be done in a background thread
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
               invitiationDao.insertInvitation(InvitationDataDomainMapper.transformFromDomain(invitation));
            }
        });
    }

    @Override
    public void rejectInvitationFromCache(final String groupId) {
        //only delete in the cache, server one gets deleted in the cloud
        //this case only happens when user joins an invited public group
        //in case of private group, a request is created, but the invitation
        //is still there

        //needs to be done in a background thread
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                invitiationDao.deleteInvitationForGroup(groupId);
            }
        });
    }


//    private LiveData<Resource<List<Invitation>>> rejectInvitationAtServerAndLocal(final String groupId){
//
//        return new NewNetworkBoundDelRes<List<Invitation>, InvitationsResponse>(AppExecutors2.getInstance()){
//
//            @Override
//            protected List<Invitation> processResult(InvitationsResponse invitationsResponse) {
//                return invitationsResponse.getInvitations();
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<ApiResponse<InvitationsResponse>> createCall() {
//                return AppConfigHelper.getBackendApiServiceProvider().rejectOrCancelUserInvitation(AppConfigHelper.getRequestUserId(), groupId);
//            }
//
//            @Override
//            protected void reflectDataInDB(InvitationsResponse item) {
//                //since the delete method is bringin back all the invitations
//                //just delete all of them and insert new ones
//                if (item.getInvitations() != null) {
//
//                    Invitation[] invitations = new Invitation[item.getInvitations().size()];
//
//                    int index = 0;
//                    for (long rowid : invitiationDao.deleteAndInsert((Invitation[]) (item.getInvitations().toArray(invitations)))) {
//                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
//                        index++;
//                    }
//                }
//
//            }
//
////
//        }.getAsLiveData();
//
//    }


    private LiveData<Resource<List<Invitation>>> rejectInvitationAtServerAndLocal(final String groupId){
        return new NetworkBoundResource<List<Invitation>, InvitationsResponse>(AppExecutors2.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull InvitationsResponse item) {

                //there would be old invitations which were removed from database but hanging here in cache
                //so it is a good idea to delete all of them first, hence using the transaction which deletes and then inserts


                Log.d(TAG, "saveCallResult");
                if (item.getInvitations() != null) {

                    Invitation[] invitations = new Invitation[item.getInvitations().size()];

                    int index = 0;
                    for (long rowid : invitiationDao.deleteAndInsert((Invitation[]) (item.getInvitations().toArray(invitations)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Invitation> data) {
                Log.d(TAG, "shouldFetch");
                if (NetworkHelper.isOnline()) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Invitation>> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                return invitiationDao.getInvitations();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<InvitationsResponse>> createCall() {
                Log.d(TAG, "createCall");
                return AppConfigHelper.getBackendApiServiceProvider().rejectOrCancelUserInvitation(AppConfigHelper.getUserId(), groupId);
            }
        }.getAsLiveData();
    }




}

