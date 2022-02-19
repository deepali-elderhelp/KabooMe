package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.GroupRequestDataDomainMapper;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UpdateResourceDomainResourceMapper;
import com.java.kaboome.data.persistence.GroupRequestsDao;
import com.java.kaboome.data.remote.requests.RequestCreateRequest;
import com.java.kaboome.data.remote.requests.RequestDeleteRequest;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.GroupRequestsResponse;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataGroupRequestRepository implements GroupRequestRepository {

    private static final String TAG = "KMDataGrpsRequestsRepo";

    private static DataGroupRequestRepository instance;
    private GroupRequestsDao groupRequestsDao;

    public DataGroupRequestRepository() {
        groupRequestsDao = AppConfigHelper.getKabooMeDatabaseInstance().getGroupRequestsDao();
    }

    public static DataGroupRequestRepository getInstance(){
        if(instance == null){
            instance = new DataGroupRequestRepository();
        }
        return instance;
    }

    @Override
    public LiveData<DomainUpdateResource<String>> createRequest(DomainGroupRequest groupRequest, String groupName, String privateGroup, String action) {
        return Transformations.map(createRequestAtServer(groupRequest, groupName, privateGroup, action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }


    private LiveData<UpdateResource<String>> createRequestAtServer(final DomainGroupRequest groupRequest, final String groupName, final String privateGroup, final String action){
        return new NewNetworkBoundUpdateRes<String, Void>(AppExecutors2.getInstance()){

            @Override
            protected String processResult(Void aVoid) {
                return action;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                RequestCreateRequest requestCreateRequest = new RequestCreateRequest();
                requestCreateRequest.setUserId(AppConfigHelper.getUserId());
                requestCreateRequest.setGroupId(groupRequest.getGroupId());
                requestCreateRequest.setGroupName(groupName);
                requestCreateRequest.setPrivateGroup(privateGroup);
                requestCreateRequest.setRequestMessage(groupRequest.getRequestMessage());
                requestCreateRequest.setUserAlias(groupRequest.getUserAlias());
                requestCreateRequest.setUserRole(groupRequest.getUserRole());
                requestCreateRequest.setUserImageTimestamp(groupRequest.getImageUpdateTimestamp());
                return AppConfigHelper.getBackendApiServiceProvider().createRequest(groupRequest.getUserId(), groupRequest.getGroupId(), requestCreateRequest);
            }

            @Override
            protected void reflectDataInDB(Void aVoid) {
                //doing nothing - the request does not live here, it lives in the cache of the admin
//                //invitation is not added to the cache here, it will show up in the "Invitations" list
            }
        }.getAsLiveData();
//        return new NetworkBoundUpdateResource(AppExecutors2.getInstance(), action){
//
//
//            @Override
//            protected boolean shouldUpdate() {
//                return true;
//            }
//
//            @NonNull
//            @Override
//            protected Call<ResponseBody> createCall() {
//                RequestCreateRequest requestCreateRequest = new RequestCreateRequest();
//                requestCreateRequest.setUserId(AppConfigHelper.getUserId());
//                requestCreateRequest.setGroupId(groupRequest.getGroupId());
//                requestCreateRequest.setGroupName(groupName);
//                requestCreateRequest.setPrivateGroup(privateGroup);
//                requestCreateRequest.setRequestMessage(groupRequest.getRequestMessage());
//                requestCreateRequest.setUserAlias(groupRequest.getUserAlias());
//                requestCreateRequest.setUserRole(groupRequest.getUserRole());
//                requestCreateRequest.setUserImageTimestamp(groupRequest.getImageUpdateTimestamp());
//                return AppConfigHelper.getBackendApiServiceProvider().createRequest(groupRequest.getUserId(), groupRequest.getGroupId(), requestCreateRequest);
//            }
//
//            @Override
//            protected void uploadToDB() {
//                //doing nothing - the request does not live here, it lives in the cache of the admin
//                //invitation is not added to the cache here, it will show up in the "Invitations" list
//            }
//
//            @Override
//            protected void rollbackDatabase() {
//                //nothing to rollback since nothing has been added
//            }
//        }.getAsLiveData();
    }


    /**
     * This is getting all the requests for all the groups
     * This is already being done when the UserGroupsList is being refreshed
     * So, for this one, only getting the data from cache
     * So far there is no need to get this data fresh from server
     * @param refreshFromServer
     * @return
     */
    @Override
    public LiveData<List<DomainGroupRequest>> getRequestsForUserGroups(boolean refreshFromServer) {
        return Transformations.map(groupRequestsDao.getAllGroupsAllRequests(), new Function<List<GroupRequest>, List<DomainGroupRequest>>() {
            @Override
            public List<DomainGroupRequest> apply(List<GroupRequest> input) {
                return GroupRequestDataDomainMapper.transform(input);
            }
        });
    }


    /**
     * This is getting all the requests for all the groups
     * This is already being done when the UserGroupsList is being refreshed
     * So, for this one, only getting the data from cache
     * So far there is no need to get this data fresh from server
     * @param refreshFromServer - expecting it to be false always, hence right now, not checking it
     * @return
     */
    @Override
    public LiveData<List<DomainGroupRequest>> getRequestsForUserGroup(final String groupId, boolean refreshFromServer) {
        return Transformations.map(groupRequestsDao.getGroupRequests(groupId), new Function<List<GroupRequest>, List<DomainGroupRequest>>() {
            @Override
            public List<DomainGroupRequest> apply(List<GroupRequest> input) {
                Log.d(TAG, "GroupRequests for groupId "+groupId+" - "+input.size());
                return GroupRequestDataDomainMapper.transform(input);
            }
        });
    }

    //    @Override
//    public LiveData<DomainResource<List<DomainGroupRequest>>> getRequestsForUserGroups(final boolean refreshFromServer) {
//        return Transformations.map(getRequestForUserGroupsFromCacheAndOrServer(refreshFromServer), new Function<Resource<List<GroupRequest>>, DomainResource<List<DomainGroupRequest>>>() {
//            @Override
//            public DomainResource<List<DomainGroupRequest>> apply(Resource<List<GroupRequest>> input) {
//                Log.d(TAG, "apply: response is here");
//                return ResourceDomainResourceMapper.transform(input.status, GroupRequestDataDomainMapper.transform(input.data), input.message);
//            }
//        });
//    }
//
//    /**
//     * Server call not implemented as there had been no need as of yet
//     * @param refreshFromServer - should always be false
//     * @return
//     */
//
//    private LiveData<Resource<List<GroupRequest>>> getRequestForUserGroupsFromCacheAndOrServer(final boolean refreshFromServer){
//        return new NetworkBoundResource<List<GroupRequest>, Object>(AppExecutors2.getInstance()) {
//            @Override
//            protected void saveCallResult(@NonNull Object item) {
//
//            }
//
//            @Override
//            protected boolean shouldFetch(@Nullable List<GroupRequest> data) {
//                return refreshFromServer;
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<List<GroupRequest>> loadFromDb() {
//                return groupRequestsDao.getAllGroupsAllRequests();
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<ApiResponse<Object>> createCall() {
//                return null;
//            }
//        }.getAsLiveData();
//    }


    @Override
    public LiveData<DomainResource<List<DomainGroupRequest>>> getRequestsForGroup(String groupId) {
        return Transformations.map(getRequestsForGroupFromServer(groupId), new Function<Resource<List<GroupRequest>>, DomainResource<List<DomainGroupRequest>>>() {
            @Override
            public DomainResource<List<DomainGroupRequest>> apply(Resource<List<GroupRequest>> input) {
                return ResourceDomainResourceMapper.transform(input.status, GroupRequestDataDomainMapper.transform(input.data), input.message);
            }
        });
    }

    /**
     *
     * This should be called on a background thread - this is user's responsibility to
     * call this method on a background thread, otherwise a runtime error is thrown.
     * @param groupId
     * @return
     */
    @Override
    public List<DomainGroupRequest> getRequestsForGroupSingle(String groupId) {
        List<GroupRequest> groupRequests = groupRequestsDao.getGroupRequestsSingle(groupId);

        if(groupRequests == null){
            return null;
        }
        return GroupRequestDataDomainMapper.transform(groupRequests);
    }

    @Override
    public LiveData<DomainResource<List<DomainGroupRequest>>> finishRequestForGroup(String groupId, DomainGroupRequest domainGroupRequest, boolean accept, String groupName, String privateGroup) {
        return Transformations.map(finishRequest(groupId, domainGroupRequest, accept, groupName, privateGroup), new Function<Resource<List<GroupRequest>>, DomainResource<List<DomainGroupRequest>>>() {
            @Override
            public DomainResource<List<DomainGroupRequest>> apply(Resource<List<GroupRequest>> input) {
                Log.d(TAG, "apply: response is here");
                return ResourceDomainResourceMapper.transform(input.status, GroupRequestDataDomainMapper.transform(input.data), input.message);
            }
        });
    }

    @Override
    public void deleteRequestOnlyLocal(final String userId, final String groupId ) {
        Log.d(TAG, "deleteRequestOnlyLocal: ");
        if(userId == null || groupId == null){
            return;
        }
        if(userId.isEmpty() || groupId.isEmpty()){
            return;
        }
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    groupRequestsDao.deleteRequest(userId, groupId);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in deleteRequestOnlyLocal "+exception.getMessage());
                }
            }
        });
    }

    @Override
    public void deleteAllRequestsForGroupOnlyLocal(final String groupId) {
        Log.d(TAG, "deleteAllRequestForGroupOnlyLocal: ");

        if(groupId == null || groupId.isEmpty()){
            return;
        }
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    groupRequestsDao.deleteAllRequestsForGroup(groupId);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in deleteAllRequestsForGroupOnlyLocal "+exception.getMessage());
                }
            }
        });
    }

    @Override
    public void addRequestOnlyLocal(DomainGroupRequest domainGroupRequest) {
        Log.d(TAG, "addRequestOnlyLocal: ");
        if(domainGroupRequest == null){
            return;
        }
        if(domainGroupRequest.getUserId() == null || domainGroupRequest.getGroupId() == null){
            return;
        }
        if(domainGroupRequest.getUserId().isEmpty() || domainGroupRequest.getGroupId().isEmpty()){
            return;
        }

        final GroupRequest groupRequest = GroupRequestDataDomainMapper.transformFromDomainRequest(domainGroupRequest);
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    groupRequestsDao.insertGroupRequest(groupRequest);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in addRequestOnlyLocal "+exception.getMessage());
                }
            }
        });
    }

    private LiveData<Resource<List<GroupRequest>>> getRequestsForGroupFromServer(final String groupId) {
        return new NetworkBoundResource<List<GroupRequest>, GroupRequestsResponse>(AppExecutors2.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GroupRequestsResponse item) {

                //there would be old requests which were removed from database but hanging here in cache
                //so it is a good idea to delete all of them first, hence using the transaction which deletes and then inserts


                Log.d(TAG, "saveCallResult");
                if (item.getRequests() != null) {

                    GroupRequest[] requests = new GroupRequest[item.getRequests().size()];

//                    int index = 0;
//                    for (long rowid : invitiationDao.insertInvitations((Invitation[]) (item.getInvitations().toArray(invitations)))) {
//                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
//                        index++;
//                    }

                    //the data coming from server does not have groupId in it
                    for(GroupRequest groupRequest: item.getRequests()){
                        groupRequest.setGroupId(groupId);
                    }

                    int index = 0;
                    for (long rowid : groupRequestsDao.deleteAndInsert(groupId, (GroupRequest[]) (item.getRequests().toArray(requests)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<GroupRequest> data) {
                Log.d(TAG, "shouldFetch");
                if (NetworkHelper.isOnline()) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<GroupRequest>> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                return groupRequestsDao.getGroupRequests(groupId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupRequestsResponse>> createCall() {
                Log.d(TAG, "createCall");
                return AppConfigHelper.getBackendApiServiceProvider().getGroupRequests(AppConfigHelper.getUserId(), groupId);
            }
        }.getAsLiveData();

    }


    private LiveData<Resource<List<GroupRequest>>> finishRequest(final String groupId, final DomainGroupRequest groupRequest,
                                                                 final boolean accept, final String groupName,
                                                                 final String privateGroup) {
        return new NetworkBoundResource<List<GroupRequest>, GroupRequestsResponse>(AppExecutors2.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GroupRequestsResponse item) {

                //there would be old requests which were removed from database but hanging here in cache
                //so it is a good idea to delete all of them first, hence using the transaction which deletes and then inserts


                Log.d(TAG, "saveCallResult");
                if (item.getRequests() != null) {

                    GroupRequest[] requests = new GroupRequest[item.getRequests().size()];

                    //the data coming from server does not have groupId in it
                    for (GroupRequest groupRequest : item.getRequests()) {
                        groupRequest.setGroupId(groupId);
                    }

                    int index = 0;
                    for (long rowid : groupRequestsDao.deleteAndInsert(groupId, (GroupRequest[]) (item.getRequests().toArray(requests)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<GroupRequest> data) {
                Log.d(TAG, "shouldFetch");
                if (NetworkHelper.isOnline()) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<GroupRequest>> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                return groupRequestsDao.getGroupRequests(groupId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupRequestsResponse>> createCall() {
                Log.d(TAG, "createCall");
                RequestDeleteRequest requestDeleteRequest = new RequestDeleteRequest();
                requestDeleteRequest.setRequestUserId(groupRequest.getUserId());
                requestDeleteRequest.setUserAlias(groupRequest.getUserAlias());
                requestDeleteRequest.setUserRole(groupRequest.getUserRole());
                requestDeleteRequest.setImageUpdateTimestamp(groupRequest.getImageUpdateTimestamp());
                requestDeleteRequest.setAcceptUser(accept);
                requestDeleteRequest.setGroupName(groupName);
                requestDeleteRequest.setPrivateGroup(privateGroup);
                return AppConfigHelper.getBackendApiServiceProvider().deleteGroupRequest(AppConfigHelper.getUserId(), groupId,requestDeleteRequest);
            }
        }.getAsLiveData();
    }
}

