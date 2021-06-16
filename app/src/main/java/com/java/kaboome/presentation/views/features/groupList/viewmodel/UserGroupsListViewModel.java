package com.java.kaboome.presentation.views.features.groupList.viewmodel;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.java.kaboome.constants.GroupListStatusConstants;
import com.java.kaboome.constants.ReceivedGroupDataTypeConstants;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataGroupRequestRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.domain.usecases.GetGroupRequestsListSingleUseCase;
import com.java.kaboome.domain.usecases.GetLastWholeGroupMessageInCacheSingleUseCase;
import com.java.kaboome.domain.usecases.GetLastWholeGroupMessagesInCacheLiveDataUseCase;
import com.java.kaboome.domain.usecases.GetNetUnreadWholeGroupMessagesSingleUseCase;
import com.java.kaboome.domain.usecases.GetNetUnreadWholeGroupMessagesUseCase;
import com.java.kaboome.domain.usecases.GetUserGroupRequestsListUseCase;
import com.java.kaboome.domain.usecases.GetUserGroupsListUseCase;
import com.java.kaboome.helpers.GroupCleanupHelper;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.mappers.UserGroupModelMapper;
import com.java.kaboome.service.SyncAllMessagesFromServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserGroupsListViewModel extends AndroidViewModel {

    private static final String TAG = "KMUGroupsListViewModel";
    private Context context;
    private GetUserGroupsListUseCase getUserGroupsListUseCase;
    private UserGroupsListRepository userGroupsListRepository;
    private MessagesListRepository messagesListRepository;
//    private GetGroupMessagesAfterLastAccessUseCase getGroupMessagesAfterLastAccessUseCase;
//    private GetNetUnreadOnlyGroupMessagesUseCase getNetUnreadOnlyGroupMessagesUseCase;
    private GetNetUnreadWholeGroupMessagesUseCase getNetUnreadWholeGroupMessagesUseCase;
//    private GetNetUnreadOnlyGroupMessagesUseCase getNetUnreadOnlyGroupMessagesUseCase;
    private GetLastWholeGroupMessagesInCacheLiveDataUseCase getLastWholeGroupMessagesInCacheLiveDataUseCase;
    private GroupRequestRepository groupRequestRepository;
    private GetUserGroupRequestsListUseCase getUserGroupRequestsListUseCase;
    private MediatorLiveData<List<UserGroupModel>> userGroupsData = new MediatorLiveData<>();
    private  MediatorLiveData<UserGroupModel> eachGroupLiveData = new MediatorLiveData<>();
    private LiveData<DomainResource<List<DomainUserGroup>>> repositorySource;
    private List<LiveData<UserGroupModel>> eachUserGroupModelLiveDataList = new ArrayList<>(); //needed for cleanup/remove source purpose
    private Map<String, MutableLiveData<UserGroupModel>> mutableLiveDataForEachGroup = new HashMap<>();



    public UserGroupsListViewModel(@NonNull Application application) {
        super(application);
        context = application;

        userGroupsListRepository = DataUserGroupsListRepository.getInstance();
        getUserGroupsListUseCase = new GetUserGroupsListUseCase(userGroupsListRepository);

        messagesListRepository = DataGroupMessagesRepository.getInstance();
//        getNetUnreadOnlyGroupMessagesUseCase = new GetNetUnreadOnlyGroupMessagesUseCase(messagesListRepository);
        getNetUnreadWholeGroupMessagesUseCase = new GetNetUnreadWholeGroupMessagesUseCase(messagesListRepository);
        getLastWholeGroupMessagesInCacheLiveDataUseCase = new GetLastWholeGroupMessagesInCacheLiveDataUseCase(messagesListRepository);

        groupRequestRepository = DataGroupRequestRepository.getInstance();
        getUserGroupRequestsListUseCase = new GetUserGroupRequestsListUseCase(groupRequestRepository);
    }


    public MediatorLiveData<List<UserGroupModel>> getUserGroupsData() {
        return userGroupsData;
    }


    public MediatorLiveData<UserGroupModel> getEachGroupLiveData() {
        return eachGroupLiveData;
    }

    public void loadInitialList() {
        Log.d(TAG, "loadInitialList: ");

//        handleCleanUpFromLastTime();
        repositorySource = getUserGroupsListUseCase.execute(null);

        userGroupsData.addSource(repositorySource, new Observer<DomainResource<List<DomainUserGroup>>>() {
            @Override
            public void onChanged(@Nullable final DomainResource<List<DomainUserGroup>> listDomainResource) {
                Log.d(TAG, "UserGroupsData - add source");
                if (listDomainResource != null) {


                    if (listDomainResource.status == DomainResource.Status.SUCCESS) {

                        Log.d(TAG, "Status - SUCCESS");
                        if (listDomainResource.data != null) {
                            if (listDomainResource.data.size() == 0) {
                                Log.d(TAG, "no groups added...");
                                //show empty groups display
                                List<UserGroupModel> newUserGroupModels = UserGroupModelMapper.transformAllFromDomain(listDomainResource);
                                userGroupsData.setValue(newUserGroupModels);


                            } else {

                                //launch the background process for loading the messages
//                                MessagesDownloadHelper.updateMessagesForUserGroups(listDomainResource.data, AppExecutors2.getInstance());
                                Intent serviceIntent = new Intent(context, SyncAllMessagesFromServer.class);
                                serviceIntent.putExtra("groups", (Serializable)listDomainResource.data);

                                //stop old service intent if running
                                context.stopService(serviceIntent);

//                                ContextCompat.startForegroundService(context, serviceIntent);
                                //not keeping a foreground service, the user will see it in the device tray all the time
                                //it is annoying
                                context.startService(serviceIntent);



                                Log.d(TAG, "There are groups...");
                                //if the groups are deleted, remove their local messages and media content
                                GroupCleanupHelper.cleanUpAfterDeletedGroups(listDomainResource.data);
                                final List<UserGroupModel> newUserGroupModels = UserGroupModelMapper.transformAllFromDomain(listDomainResource);
                                //following has been moved to the line above -GroupCleanupHelper.cleanUpAfterDeletedGroups
                                //UserGroupModelMapper.transformAllFromDomain was filtering the deleted groups, so it was never being called
                                //launch the worker to delete the local messages and local media of the newly deleted groups
//                                if(AppConfigHelper.isRunDeletedGroupsMediaDelete()){
//                                    for(UserGroupModel userGroupModel: newUserGroupModels){
//                                        if(userGroupModel.getDeleted()){
//                                            WorkerBuilderHelper.callUpdateCacheClearTSWorker(userGroupModel.getGroupId(), (new Date()).getTime(), false);
//                                            WorkerBuilderHelper.callDeleteGroupAttachmentsWorker(userGroupModel.getGroupId());
//                                        }
//                                    }
//                                }

////                                updateNewGroupListWithLoadingData(newUserGroupModels);
//                                addGroupNeededDataTriggers(listDomainResource);
////                                userGroupsData.setValue(newUserGroupModels);

                                //TODO: this needs to be verified, I think it should be Get Last Group Message in cache, not the last
                                //TODO: message cache

                                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(UserGroupModel userGroupModel: newUserGroupModels){
//                                            GetUserGroupLastMessageCache getUserGroupLastMessageCache = new GetUserGroupLastMessageCache(messagesListRepository);
//                                            DomainMessage lastMessage = getUserGroupLastMessageCache.execute(GetUserGroupLastMessageCache.Params.forGroup(userGroupModel.getGroupId(), "Group"));

                                            GetLastWholeGroupMessageInCacheSingleUseCase getLastWholeGroupMessageInCacheSingleUseCase = new GetLastWholeGroupMessageInCacheSingleUseCase(messagesListRepository);
                                            DomainMessage lastMessage = getLastWholeGroupMessageInCacheSingleUseCase.execute(GetLastWholeGroupMessageInCacheSingleUseCase.Params.forGroup(userGroupModel.getGroupId()));
                                            if(lastMessage != null){
                                                userGroupModel.setLastMessageSentAt(lastMessage.getSentAt());
                                                userGroupModel.setLastMessageText(lastMessage.getMessageText());
                                                userGroupModel.setLastMessageSentBy(lastMessage.getAlias());
                                            }
                                            else{
                                                userGroupModel.setLastMessageSentAt(0L);
                                            }

                                            //get requests similarly so that request data is also considered while sorting groups when loading
                                            GetGroupRequestsListSingleUseCase getGroupRequestsListSingleUseCase = new GetGroupRequestsListSingleUseCase(groupRequestRepository);
                                            List<DomainGroupRequest> domainGroupRequests = getGroupRequestsListSingleUseCase.execute(GetGroupRequestsListSingleUseCase.Params.getRequestsForGroup(userGroupModel.getGroupId()));
                                            if(domainGroupRequests == null || domainGroupRequests.size() <= 0){
                                                //                                Log.d(TAG, "onChanged: request size 0");
                                                userGroupModel.setNumberOfRequests(0);
                                                userGroupModel.setLastRequestSentAt(0L);
                                            }
                                            else{
                                                //                                Log.d(TAG, "onChanged: request size - "+domainGroupRequests.size());
                                                userGroupModel.setNumberOfRequests(domainGroupRequests.size());
                                                userGroupModel.setLastRequestSentAt(domainGroupRequests.get(domainGroupRequests.size() - 1).getDateRequestMade());
                                            }

//                                            GetGroupMessagesAfterLastAccessSingleUseCase getGroupMessagesAfterLastAccessSingleUseCase = new GetGroupMessagesAfterLastAccessSingleUseCase(messagesListRepository);
//                                            List<DomainMessage> messagesAfterLastAccess = getGroupMessagesAfterLastAccessSingleUseCase.execute(GetGroupMessagesAfterLastAccessSingleUseCase.Params.getMessagesAfterLastAccessForGroupCache(userGroupModel.getGroupId(), userGroupModel.getLastAccessed()));
//                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
//                                                userGroupModel.setUnreadCount(0);
//                                            }
//                                            else{
//                                                userGroupModel.setUnreadCount(messagesAfterLastAccess.size());
//                                            }

                                            GetNetUnreadWholeGroupMessagesSingleUseCase getNetUnreadWholeGroupMessagesSingleUseCase = new GetNetUnreadWholeGroupMessagesSingleUseCase(messagesListRepository);
                                            List<DomainMessage> messagesAfterLastAccess = getNetUnreadWholeGroupMessagesSingleUseCase.execute(GetNetUnreadWholeGroupMessagesSingleUseCase.Params.getNetUnreadMessagesCacheForGroup(userGroupModel.getGroupId()));
                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
                                                userGroupModel.setUnreadCount(0);
                                            }
                                            else{
                                                userGroupModel.setUnreadCount(messagesAfterLastAccess.size());
                                            }

                                        }
                                        userGroupsData.postValue(newUserGroupModels);
                                        //We need to add triggers after the initial group data is loaded in the adapter
                                        //hence adding it here. Once all the groups come back, there last message is updated,
                                        //then we add the triggers.
                                        //This avoids the case where notifyDatasetChanged() in Adapter was depending upon
                                        //the group that comes later. It was losing data for the previous one and hence new messages
                                        //unread count was being lost.
                                        //For example, if we are updating unread for a group and that group's data is set later
                                        //it is set with default unread, so we were losing the unread
                                        AppExecutors2.getInstance().mainThread().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                addGroupNeededDataTriggers(listDomainResource);
                                            }
                                        });
                                    }
                                });



                            }
                        }
//                        userGroupsData.removeSource(repositorySource);
                    } else if (listDomainResource.status == DomainResource.Status.LOADING) {
                        Log.d(TAG, "Status - LOADING");
                        if (listDomainResource.data != null) {
                            if (listDomainResource.data.size() <= 0) {
                                Log.d(TAG, "no groups cached...");


                            } else {
                                Log.d(TAG, "There are groups cached...");

                                //following code was here - replacing it with new code - please check if it's right
//                                userGroupsData.setValue(UserGroupModelMapper.transformAllFromDomain(listDomainResource));
//                                addGroupNeededDataTriggers(listDomainResource);

                                final List<UserGroupModel> newUserGroupModels = UserGroupModelMapper.transformAllFromDomain(listDomainResource);
                                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(UserGroupModel userGroupModel: newUserGroupModels){
                                            if(userGroupModel.getGroupId().equals(GroupListStatusConstants.LOADING.toString())){
                                                continue;
                                            }
                                            GetLastWholeGroupMessageInCacheSingleUseCase getLastWholeGroupMessageInCacheSingleUseCase = new GetLastWholeGroupMessageInCacheSingleUseCase(messagesListRepository);
                                            DomainMessage lastMessage = getLastWholeGroupMessageInCacheSingleUseCase.execute(GetLastWholeGroupMessageInCacheSingleUseCase.Params.forGroup(userGroupModel.getGroupId()));

//                                            GetUserGroupLastMessageCache getUserGroupLastMessageCache = new GetUserGroupLastMessageCache(messagesListRepository);
//                                            DomainMessage lastMessage = getUserGroupLastMessageCache.execute(GetUserGroupLastMessageCache.Params.forGroup(userGroupModel.getGroupId()));
                                            if(lastMessage != null){
                                                userGroupModel.setLastMessageSentAt(lastMessage.getSentAt());
                                                userGroupModel.setLastMessageText(lastMessage.getMessageText());
                                                userGroupModel.setLastMessageSentBy(lastMessage.getAlias());
                                            }
                                            else{
                                                userGroupModel.setLastMessageSentAt(0L);
                                            }

                                            //get requests similarly so that request data is also considered while sorting groups when loading
                                            GetGroupRequestsListSingleUseCase getGroupRequestsListSingleUseCase = new GetGroupRequestsListSingleUseCase(groupRequestRepository);
                                            List<DomainGroupRequest> domainGroupRequests = getGroupRequestsListSingleUseCase.execute(GetGroupRequestsListSingleUseCase.Params.getRequestsForGroup(userGroupModel.getGroupId()));
                                            if(domainGroupRequests == null || domainGroupRequests.size() <= 0){
                                                //                                Log.d(TAG, "onChanged: request size 0");
                                                userGroupModel.setNumberOfRequests(0);
                                                userGroupModel.setLastRequestSentAt(0L);
                                            }
                                            else{
                                                //                                Log.d(TAG, "onChanged: request size - "+domainGroupRequests.size());
                                                userGroupModel.setNumberOfRequests(domainGroupRequests.size());
                                                userGroupModel.setLastRequestSentAt(domainGroupRequests.get(domainGroupRequests.size() - 1).getDateRequestMade());
                                            }


//                                            GetGroupMessagesAfterLastAccessSingleUseCase getGroupMessagesAfterLastAccessSingleUseCase = new GetGroupMessagesAfterLastAccessSingleUseCase(messagesListRepository);
//                                            List<DomainMessage> messagesAfterLastAccess = getGroupMessagesAfterLastAccessSingleUseCase.execute(GetGroupMessagesAfterLastAccessSingleUseCase.Params.getMessagesAfterLastAccessForGroupCache(userGroupModel.getGroupId(), userGroupModel.getLastAccessed()));
//                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
//                                                userGroupModel.setUnreadCount(0);
//                                            }
//                                            else{
//                                                userGroupModel.setUnreadCount(messagesAfterLastAccess.size());
//                                            }

                                            GetNetUnreadWholeGroupMessagesSingleUseCase getNetUnreadWholeGroupMessagesSingleUseCase = new GetNetUnreadWholeGroupMessagesSingleUseCase(messagesListRepository);
                                            List<DomainMessage> messagesAfterLastAccess = getNetUnreadWholeGroupMessagesSingleUseCase.execute(GetNetUnreadWholeGroupMessagesSingleUseCase.Params.getNetUnreadMessagesCacheForGroup(userGroupModel.getGroupId()));
                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
                                                userGroupModel.setUnreadCount(0);
                                            }
                                            else{
                                                userGroupModel.setUnreadCount(messagesAfterLastAccess.size());
                                            }

                                        }
                                        userGroupsData.postValue(newUserGroupModels);
                                    }
                                });
//                                for(UserGroupModel userGroupModel: newUserGroupModels){
//                                    GetUserGroupLastMessageCache getUserGroupLastMessageCache = new GetUserGroupLastMessageCache(messagesListRepository);
//                                    DomainMessage lastMessage = getUserGroupLastMessageCache.execute(GetUserGroupLastMessageCache.Params.forGroup(userGroupModel.getGroupId()));
//                                    if(lastMessage != null){
//                                        userGroupModel.setLastMessageSentAt(lastMessage.getSentAt());
//                                    }
//                                    else{
//                                        userGroupModel.setLastMessageSentAt(0L);
//                                    }
//
//                                }

                                //switched the order
//                                addGroupNeededDataTriggers(listDomainResource);
//                                updateNewGroupListWithLoadingData(newUserGroupModels);

//                                userGroupsData.setValue(newUserGroupModels);



                            }

                        }
                    } else if (listDomainResource.status == DomainResource.Status.ERROR) {
                        Log.d(TAG, "Status - ERROR");
                        addGroupNeededDataTriggers(listDomainResource);
                        userGroupsData.setValue(UserGroupModelMapper.transformAllFromDomain(listDomainResource));
                        //TODO: this following line analyze it in this respect -
                        //TODO: the first time, there is an error, so no continuous observing is being done
                        //TODO: since the data is coming from the cache, the user does not know there is an error
                        //TODO: user goes ahaead and creates a group which works, but since the observing is not happening
                        //TODO: there is no addition of that group when he comes back to GroupsListFragment
                        userGroupsData.removeSource(repositorySource);
//                        showNoNetworkErrorToast.setValue(true);
                    }
                } else {
                    userGroupsData.removeSource(repositorySource);
                }
            }
        });

    }

    private void addGroupNeededDataTriggers(@NonNull DomainResource<List<DomainUserGroup>> listDomainResource) {

        for(int i=0; i< listDomainResource.data.size(); i++){

            UserGroupModel userGroupModel = UserGroupModelMapper.getUserModelFromDomain(listDomainResource.data.get(i));

            if(userGroupModel.getDeleted()){
                //if the group is deleted, do not do anything, it does not need to be observed
                continue;
            }

            MutableLiveData<UserGroupModel> userGroupModelMutableLiveData = mutableLiveDataForEachGroup.get(userGroupModel.getGroupId());
            if(userGroupModelMutableLiveData == null){
                userGroupModelMutableLiveData = new MutableLiveData<>();
                mutableLiveDataForEachGroup.put(userGroupModel.getGroupId(), userGroupModelMutableLiveData);

                LiveData<UserGroupModel> userGroupReturnedLiveData = applyTransformationsForEachGroup(userGroupModelMutableLiveData);
//
                eachUserGroupModelLiveDataList.add(userGroupReturnedLiveData); //needed for cleanup purposes
                eachGroupLiveData.addSource(userGroupReturnedLiveData, new Observer<UserGroupModel>() {
                    @Override
                    public void onChanged(UserGroupModel userGroupModel) {
//                        Log.d(TAG, "onChanged: eachGroupLiveData source changed for Group "+userGroupModel.getGroupId());
                        eachGroupLiveData.setValue(userGroupModel);
                    }
                });
            }

            //what about if we set value only if it is different than before??
//            UserGroupModel newUserGroupModel = UserGroupModelMapper.getUserModelFromDomain(listDomainResource.data.get(i));
//            UserGroupModel existingUserGroupModel = userGroupModelMutableLiveData.getValue();
//            if(existingUserGroupModel != null && existingUserGroupModel.isSameGroup(newUserGroupModel))
//                continue;
            userGroupModelMutableLiveData.setValue(UserGroupModelMapper.getUserModelFromDomain(listDomainResource.data.get(i)));
        }
    }

    private LiveData<UserGroupModel> applyTransformationsForEachGroup(MutableLiveData<UserGroupModel> userGroupModelMutableLiveData) {
        return Transformations.switchMap(userGroupModelMutableLiveData, new Function<UserGroupModel, LiveData<UserGroupModel>>() {
                    @Override
                    public LiveData<UserGroupModel> apply(final UserGroupModel input) {


                        final MediatorLiveData<UserGroupModel> multipleFunctions = new MediatorLiveData<>();

////                        final Long groupLastMessageSeenTS = AppConfigHelper.getGroupLastSeenMsgTS(input.getGroupId());
                        final Long groupLastMessageSeenTS = input.getLastAccessed();

                        //how about right here I get all the conversations, iterate through them and get their unread count using some UseCase
                        //and add all that unread count to the group's unread count
                        //but I need order, so that the unread count is added, not replaced

                        multipleFunctions.addSource(getNetUnreadWholeGroupMessagesUseCase.execute(GetNetUnreadWholeGroupMessagesUseCase.Params.getNetUnreadMessagesForGroup(input.getGroupId())), new Observer<List<DomainMessage>>() {
                            @Override
                            public void onChanged(List<DomainMessage> domainMessages) {

                                Log.d(TAG, "onChanged: for getGroupMessagesAfterLastAccessUseCase");
                                if(domainMessages == null || domainMessages.size() <= 0){
                                    Log.d(TAG, "domain message is null, setting to 0");
                                    input.setUnreadCount(0);
                                }
                                else{
                                    Log.d(TAG, "domain messages unread are - "+domainMessages.size());
                                    input.setUnreadCount(domainMessages.size());
                                }
                                input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.UNREAD_COUNT);
                                multipleFunctions.setValue(input);
                            }
                        });

//                        multipleFunctions.addSource(getGroupMessagesAfterLastAccessUseCase.execute(GetGroupMessagesAfterLastAccessUseCase.Params.getMessagesAfterLastAccessForGroup(input.getGroupId(), groupLastMessageSeenTS)), new Observer<List<DomainMessage>>() {
//                            @Override
//                            public void onChanged(List<DomainMessage> domainMessages) {
//
//                                Log.d(TAG, "onChanged: for getGroupMessagesAfterLastAccessUseCase");
//                                //No need of the code below since with the changes, now last accessed is part of the UserGroupModel
//                                //which is being observed anyways - so, it triggers all this multipleFunctions code
//
//                                //Should work until proven otherwise
//                                //User goes to group, comes out, last visited is updated, it is greater than last message TS, so 0 unread
//                                //User does not go to group, new messages comes, last visited is less than last message TS, so unread increased
//                                //User goes to group, sends messages, comes out. Last visited is greater than last message TS, so 0 unread
////                                if((AppConfigHelper.getLastVisitedGroupTime(input.getGroupId()) != null ) && (AppConfigHelper.getLastVisitedGroupTime(input.getGroupId()) > groupLastMessageSeenTS)){
////                                    input.setUnreadCount(0);
////                                }
////                                else
//                                    if(domainMessages == null || domainMessages.size() <= 0){
//                                        Log.d(TAG, "domain message is null, setting to 0");
//                                    input.setUnreadCount(0);
//                                    }
//                                    else{
//                                        Log.d(TAG, "domain messages unread are - "+domainMessages.size());
//                                        input.setUnreadCount(domainMessages.size());
//                                    }
//
////                                else{
////                                        Log.d(TAG, "setting domain messages unread to "+domainMessages.size());
////                                        /**
////                                         * The idea is to check if there are messages that the user sent
////                                         * in the group and then came out of the group
////                                         * In that case, the messages should not show as unread
////                                         */
////                                        int unreadCount = domainMessages.size();
////                                        Long lastTimeGroupVisited = AppConfigHelper.getLastVisitedGroupTime(input.getGroupId());
////
////                                        for(DomainMessage message: domainMessages){
////                                            if(lastTimeGroupVisited != null && lastTimeGroupVisited > message.getSentAt() &&
////                                            message.getSentBy().equals(AppConfigHelper.getUserId())){
////                                                unreadCount--;
////                                            }
////                                        }
////
////                                    input.setUnreadCount(unreadCount);
////                                }
//                                input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.UNREAD_COUNT);
//                                multipleFunctions.setValue(input);
//                            }
//                        });

                        multipleFunctions.addSource(getLastWholeGroupMessagesInCacheLiveDataUseCase.execute(GetLastWholeGroupMessagesInCacheLiveDataUseCase.Params.forGroup(input.getGroupId())), new Observer<DomainMessage>() {
                            @Override
                            public void onChanged(DomainMessage domainMessage) {
                                Log.d(TAG, "onChanged: for getLastGroupMessagesInCacheUseCase");
                                if(domainMessage == null){
                                    Log.d(TAG, "domain message is null, setting to 0");
                                    input.setLastMessageText("");
                                    input.setLastMessageSentBy("");
                                    input.setLastMessageSentAt(0L);
                                    input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.LAST_MESSAGE);
                                }
                                else{
                                    /*
                                    Disregarding the below---now handling in the last unread count stuff----
                                    Though everything mentioned is still true -
                                This is how I am handling resetting the new count after a group messages are visited -
                                There is a HashMap in AppConfigHelper - lastVisitedGroupTime
                                It keeps a record of each group's last visited time - it is an app level variable - but not saved in shared preference
                                It is different than GroupLastSeenMsgTS - that is a TS of the last message seen in the Group
                                If the user has just visited the group, it's lastVisitedGroupTime is updated on back pressed from messages.
                                Now, if that time is higher than the last message in cache sentAt time, then we need to keep the count 0.
                                Need to re-confirm with notifications arriving.
                                 */

                                    input.setLastMessageText(domainMessage.getMessageText());
                                    input.setLastMessageSentBy(domainMessage.getAlias());
                                    input.setLastMessageSentAt(domainMessage.getSentAt());
//                                    Long lastTimeGroupVisited = AppConfigHelper.getLastVisitedGroupTime(input.getGroupId());
//                                    if(lastTimeGroupVisited != null && lastTimeGroupVisited > domainMessage.getSentAt()){
//                                        input.setUnreadCount(0);
//                                        input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.BOTH_UNREAD_AND_LAST);
//                                        Log.d(TAG, "setting unread to 0 in last text");
//                                    }
//                                    else{
//                                        Log.d(TAG, "not setting anything in last text");
//                                        input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.LAST_MESSAGE);
//                                    }
                                }
                                input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.LAST_MESSAGE);
                                multipleFunctions.setValue(input);

                            }
                        });

                        multipleFunctions.addSource(getUserGroupRequestsListUseCase.execute(GetUserGroupRequestsListUseCase.Params.getUserGroupRequests(input.getGroupId(), false)), new Observer<List<DomainGroupRequest>>() {
                            @Override
                            public void onChanged(List<DomainGroupRequest> domainGroupRequests) {
                                if(domainGroupRequests == null || domainGroupRequests.size() <= 0){
    //                                Log.d(TAG, "onChanged: request size 0");
                                    input.setNumberOfRequests(0);
                                    input.setLastRequestSentAt(0L);
                                }
                                else{
    //                                Log.d(TAG, "onChanged: request size - "+domainGroupRequests.size());
                                    input.setNumberOfRequests(domainGroupRequests.size());
                                    input.setLastRequestSentAt(domainGroupRequests.get(domainGroupRequests.size() - 1).getDateRequestMade());
                                }
                                input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.REQUESTS_DATA);
                                multipleFunctions.setValue(input);
                            }
                        });

                        return multipleFunctions;



    //
                    }
                });
    }

    private void handleCleanUpFromLastTime() {
        if(repositorySource != null){
            userGroupsData.removeSource(repositorySource);
        }
        removeLeftOverTriggersFromLastTime();

    }

    private void removeLeftOverTriggersFromLastTime() {
        if(eachUserGroupModelLiveDataList != null && eachUserGroupModelLiveDataList.size() > 0){
            for(LiveData<UserGroupModel> eachUserGroupModelLiveData : eachUserGroupModelLiveDataList){
                Log.d(TAG, "removing previous source for Group : "+eachUserGroupModelLiveData.getValue().getGroupId());
                eachGroupLiveData.removeSource(eachUserGroupModelLiveData);
            }

        }
    }


    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
        super.onCleared();
        userGroupsData.removeSource(repositorySource);
        
    }
    //this method is needed to get the trasnformation data updated to the loading list data- last message stuff, unread count and
    private void updateNewGroupListWithLoadingData(List<UserGroupModel> newUserGroupModels){
        for(UserGroupModel newUserGroupModel: newUserGroupModels){
            if(mutableLiveDataForEachGroup.get(newUserGroupModel.getGroupId()) != null){
                Log.d(TAG, "updateNewGroupListWithLoadingData: for group "+newUserGroupModel.getGroupName()+" is there");
                UserGroupModel existingUserGroupModel = mutableLiveDataForEachGroup.get(newUserGroupModel.getGroupId()).getValue();
                newUserGroupModel.setLastMessageSentBy(existingUserGroupModel.getLastMessageSentBy());
                newUserGroupModel.setLastMessageText(existingUserGroupModel.getLastMessageText());
                newUserGroupModel.setLastMessageSentAt(existingUserGroupModel.getLastMessageSentAt());
                newUserGroupModel.setUnreadCount(existingUserGroupModel.getUnreadCount());
                newUserGroupModel.setNumberOfRequests(existingUserGroupModel.getNumberOfRequests());

            }

        }

    }
}


















