package com.java.kaboome.presentation.views.features.conversations.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.constants.ReceivedGroupDataTypeConstants;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.repositories.DataConversationsRepository;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.usecases.GetConversationLastMessageCache;
import com.java.kaboome.domain.usecases.GetConversationsListUseCase;
import com.java.kaboome.domain.usecases.GetLastConvMessagesInCacheUseCase;
import com.java.kaboome.domain.usecases.GetNetUnreadGroupConvMessagesSingleUseCase;
import com.java.kaboome.domain.usecases.GetNetUnreadGroupConversationMessagesUseCase;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.mappers.UserGroupConversationModelMapper;
import com.java.kaboome.service.SyncAllConversationMessagesFromServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvsListViewModel extends ViewModel {

    private static final String TAG = "KMConvsListViewModel";

    private Context context;
    private String groupId;
    private GetConversationsListUseCase getConversationsListUseCase;
    private ConversationsRepository conversationsRepository;

//    private GetConvMessagesAfterLastAccessUseCase getConvMessagesAfterLastAccessUseCase;
    private GetNetUnreadGroupConversationMessagesUseCase getNetUnreadGroupConversationMessagesUseCase;
    private MessagesListRepository messagesListRepository;
    private GetLastConvMessagesInCacheUseCase getLastConvMessagesInCacheUseCase;
    private MediatorLiveData<List<UserGroupConversationModel>> userGroupConvsData = new MediatorLiveData<>();
    private  MediatorLiveData<UserGroupConversationModel> eachGroupConversationLiveData = new MediatorLiveData<>();
    private LiveData<DomainResource<List<DomainUserGroupConversation>>> repositorySource;
    private List<LiveData<UserGroupConversationModel>> eachUserGroupConversationModelLiveDataList = new ArrayList<>(); //needed for cleanup/remove source purpose
    private Map<String, MutableLiveData<UserGroupConversationModel>> mutableLiveDataForEachGroup = new HashMap<>();
    private volatile DomainResource.Status threadSafeStatus;

    public ConvsListViewModel(Context context, String groupId) {


        this.groupId = groupId;
        this.context = context;
        conversationsRepository = DataConversationsRepository.getInstance();
        getConversationsListUseCase = new GetConversationsListUseCase(conversationsRepository);

        messagesListRepository = DataGroupMessagesRepository.getInstance();
        getLastConvMessagesInCacheUseCase = new GetLastConvMessagesInCacheUseCase((messagesListRepository));
        getNetUnreadGroupConversationMessagesUseCase = new GetNetUnreadGroupConversationMessagesUseCase(messagesListRepository);
    }


    public MediatorLiveData<List<UserGroupConversationModel>> getUserGroupConvsData() {
        return userGroupConvsData;
    }


    public MediatorLiveData<UserGroupConversationModel> getEachConversationLiveData() {
        return eachGroupConversationLiveData;
    }


    public void loadInitialList() {
        Log.d(TAG, "loadInitialList: ");

//        handleCleanUpFromLastTime();
        repositorySource = getConversationsListUseCase.execute(GetConversationsListUseCase.Params.getConversationForGroup(groupId));

        userGroupConvsData.addSource(repositorySource, new Observer<DomainResource<List<DomainUserGroupConversation>>>() {
            @Override
            public void onChanged(@Nullable final DomainResource<List<DomainUserGroupConversation>> listDomainResource) {
                Log.d(TAG, "UserGroupsData - add source");
                if (listDomainResource != null) {


                    if (listDomainResource.status == DomainResource.Status.SUCCESS) {
                        threadSafeStatus = DomainResource.Status.SUCCESS;
                        Log.d(TAG, "Status - SUCCESS");
                        if (listDomainResource.data != null) {
                            if (listDomainResource.data.size() == 0) {
                                Log.d(TAG, "no conversations added...");
                                //show empty groups display
                                List<UserGroupConversationModel> userGroupConversationModels = UserGroupConversationModelMapper.transformAllFromDomain(listDomainResource);
                                userGroupConvsData.setValue(userGroupConversationModels);


                            } else {

                                //launch the background process for loading the messages
//                                MessagesDownloadHelper.updateMessagesForUserGroups(listDomainResource.data, AppExecutors2.getInstance());
                                Intent serviceIntent = new Intent(context, SyncAllConversationMessagesFromServer.class);
                                serviceIntent.putExtra("conversations", (Serializable)listDomainResource.data);
//
//                                //stop old service intent if running
                                context.stopService(serviceIntent);
//
////                                ContextCompat.startForegroundService(context, serviceIntent);
//                                //not keeping a foreground service, the user will see it in the device tray all the time
//                                //it is annoying
                                context.startService(serviceIntent);



                                Log.d(TAG, "There are conversations...");
                                //if the groups are deleted, remove their local messages and media content
//                                GroupCleanupHelper.cleanUpAfterDeletedGroups(listDomainResource.data);
//                                final List<UserGroupModel> newUserGroupModels = UserGroupModelMapper.transformAllFromDomain(listDomainResource);
                                final List<UserGroupConversationModel> newUserGroupConversationModels = UserGroupConversationModelMapper.transformAllFromDomain(listDomainResource);
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

                                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(UserGroupConversationModel userGroupConversationModel: newUserGroupConversationModels){
                                            GetConversationLastMessageCache getUserGroupLastConvMessageCache = new GetConversationLastMessageCache(messagesListRepository);
                                            DomainMessage lastMessage = getUserGroupLastConvMessageCache.execute(GetConversationLastMessageCache.Params.forGroupConversation(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId(), false));
                                            if(lastMessage != null){
                                                userGroupConversationModel.setLastMessageSentAt(lastMessage.getSentAt());
                                                userGroupConversationModel.setLastMessageText(lastMessage.getMessageText());
                                                userGroupConversationModel.setLastMessageSentBy(lastMessage.getAlias());
                                            }
                                            else{
                                                userGroupConversationModel.setLastMessageSentAt(0L);
                                            }


//                                            GetGroupConvsAfterLastAccessSingleUseCase getGroupConvsAfterLastAccessSingleUseCase = new GetGroupConvsAfterLastAccessSingleUseCase(messagesListRepository);
//                                            List<DomainMessage> messagesAfterLastAccess = getGroupConvsAfterLastAccessSingleUseCase.execute(GetGroupConvsAfterLastAccessSingleUseCase.Params.getMessagesAfterLastAccessForGroupConvCache(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId(), userGroupConversationModel.getLastAccessed()));
//                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
//                                                userGroupConversationModel.setUnreadCount(0);
//                                            }
//                                            else{
//                                                userGroupConversationModel.setUnreadCount(messagesAfterLastAccess.size());
//                                            }

                                            GetNetUnreadGroupConvMessagesSingleUseCase getNetUnreadGroupConvMessagesSingleUseCase = new GetNetUnreadGroupConvMessagesSingleUseCase(messagesListRepository);
                                            List<DomainMessage> messagesAfterLastAccess = getNetUnreadGroupConvMessagesSingleUseCase.execute(GetNetUnreadGroupConvMessagesSingleUseCase.Params.getNetUnreadMessagesCacheForGroup(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId()));
                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
                                                userGroupConversationModel.setUnreadCount(0);
                                            }
                                            else{
                                                userGroupConversationModel.setUnreadCount(messagesAfterLastAccess.size());
                                            }

                                        }
                                        userGroupConvsData.postValue(newUserGroupConversationModels);
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
                        threadSafeStatus = DomainResource.Status.LOADING;
                        Log.d(TAG, "Status - LOADING");
                        if (listDomainResource.data != null) {
                            if (listDomainResource.data.size() <= 0) {
                                Log.d(TAG, "no conversations cached...");


                            } else {
                                Log.d(TAG, "There are conversations cached...");
                                final List<UserGroupConversationModel> newUserGroupConversationModels = UserGroupConversationModelMapper.transformAllFromDomain(listDomainResource);
                                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(UserGroupConversationModel userGroupConversationModel: newUserGroupConversationModels){
                                            GetConversationLastMessageCache getUserGroupLastConvMessageCache = new GetConversationLastMessageCache(messagesListRepository);
                                            DomainMessage lastMessage = getUserGroupLastConvMessageCache.execute(GetConversationLastMessageCache.Params.forGroupConversation(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId(), false));
                                            if(lastMessage != null){
                                                userGroupConversationModel.setLastMessageSentAt(lastMessage.getSentAt());
                                                userGroupConversationModel.setLastMessageText(lastMessage.getMessageText());
                                                userGroupConversationModel.setLastMessageSentBy(lastMessage.getAlias());
                                            }
                                            else{
                                                userGroupConversationModel.setLastMessageSentAt(0L);
                                            }


//                                            GetGroupConvsAfterLastAccessSingleUseCase getGroupConvsAfterLastAccessSingleUseCase = new GetGroupConvsAfterLastAccessSingleUseCase(messagesListRepository);
//                                            List<DomainMessage> messagesAfterLastAccess = getGroupConvsAfterLastAccessSingleUseCase.execute(GetGroupConvsAfterLastAccessSingleUseCase.Params.getMessagesAfterLastAccessForGroupConvCache(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId(), userGroupConversationModel.getLastAccessed()));
//                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
//                                                userGroupConversationModel.setUnreadCount(0);
//                                            }
//                                            else{
//                                                userGroupConversationModel.setUnreadCount(messagesAfterLastAccess.size());
//                                            }

                                            GetNetUnreadGroupConvMessagesSingleUseCase getNetUnreadGroupConvMessagesSingleUseCase = new GetNetUnreadGroupConvMessagesSingleUseCase(messagesListRepository);
                                            List<DomainMessage> messagesAfterLastAccess = getNetUnreadGroupConvMessagesSingleUseCase.execute(GetNetUnreadGroupConvMessagesSingleUseCase.Params.getNetUnreadMessagesCacheForGroup(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId()));
                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
                                                userGroupConversationModel.setUnreadCount(0);
                                            }
                                            else{
                                                userGroupConversationModel.setUnreadCount(messagesAfterLastAccess.size());
                                            }
                                        }
                                        Log.d(TAG, "Status loading post is  " + threadSafeStatus);
                                        if (threadSafeStatus == DomainResource.Status.LOADING) {
                                            userGroupConvsData.postValue(newUserGroupConversationModels);
                                        }
                                    }
                                });
//                                for(UserGroupModel userGroupModel: newUserGroupConversationsModels){
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
//                                updateNewGroupListWithLoadingData(newUserGroupConversationsModels);

//                                userGroupsData.setValue(newUserGroupConversationsModels);



                            }

                        }
                    } else if (listDomainResource.status == DomainResource.Status.ERROR) {
                        threadSafeStatus = DomainResource.Status.ERROR;
                        Log.d(TAG, "Status - ERROR");

                        final List<UserGroupConversationModel> newUserGroupConversationModels = UserGroupConversationModelMapper.transformAllFromDomain(listDomainResource);

                        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                for(UserGroupConversationModel userGroupConversationModel: newUserGroupConversationModels){
                                    GetConversationLastMessageCache getUserGroupLastConvMessageCache = new GetConversationLastMessageCache(messagesListRepository);
                                    DomainMessage lastMessage = getUserGroupLastConvMessageCache.execute(GetConversationLastMessageCache.Params.forGroupConversation(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId(), false));
                                    if(lastMessage != null){
                                        userGroupConversationModel.setLastMessageSentAt(lastMessage.getSentAt());
                                        userGroupConversationModel.setLastMessageText(lastMessage.getMessageText());
                                        userGroupConversationModel.setLastMessageSentBy(lastMessage.getAlias());
                                    }
                                    else{
                                        userGroupConversationModel.setLastMessageSentAt(0L);
                                    }


//                                            GetGroupConvsAfterLastAccessSingleUseCase getGroupConvsAfterLastAccessSingleUseCase = new GetGroupConvsAfterLastAccessSingleUseCase(messagesListRepository);
//                                            List<DomainMessage> messagesAfterLastAccess = getGroupConvsAfterLastAccessSingleUseCase.execute(GetGroupConvsAfterLastAccessSingleUseCase.Params.getMessagesAfterLastAccessForGroupConvCache(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId(), userGroupConversationModel.getLastAccessed()));
//                                            if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
//                                                userGroupConversationModel.setUnreadCount(0);
//                                            }
//                                            else{
//                                                userGroupConversationModel.setUnreadCount(messagesAfterLastAccess.size());
//                                            }

                                    GetNetUnreadGroupConvMessagesSingleUseCase getNetUnreadGroupConvMessagesSingleUseCase = new GetNetUnreadGroupConvMessagesSingleUseCase(messagesListRepository);
                                    List<DomainMessage> messagesAfterLastAccess = getNetUnreadGroupConvMessagesSingleUseCase.execute(GetNetUnreadGroupConvMessagesSingleUseCase.Params.getNetUnreadMessagesCacheForGroup(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId()));
                                    if(messagesAfterLastAccess == null || messagesAfterLastAccess.size() <= 0){
                                        userGroupConversationModel.setUnreadCount(0);
                                    }
                                    else{
                                        userGroupConversationModel.setUnreadCount(messagesAfterLastAccess.size());
                                    }

                                }
                                userGroupConvsData.postValue(newUserGroupConversationModels);
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

//                        addGroupNeededDataTriggers(listDomainResource);
//                        userGroupConvsData.setValue(UserGroupConversationModelMapper.transformAllFromDomain(listDomainResource));
//                        userGroupConvsData.removeSource(repositorySource);
                    }
                } else {
                    userGroupConvsData.removeSource(repositorySource);
                }
            }
        });

    }

    private void addGroupNeededDataTriggers(@NonNull DomainResource<List<DomainUserGroupConversation>> listDomainResource) {

        for(int i=0; i< listDomainResource.data.size(); i++){

            UserGroupConversationModel userGroupConversationModel = UserGroupConversationModelMapper.getUserModelFromDomain(listDomainResource.data.get(i));

            if(userGroupConversationModel.getDeleted()){
                //if the group is deleted, do not do anything, it does not need to be observed
                continue;
            }

            MutableLiveData<UserGroupConversationModel> userGroupConversationModelMutableLiveData = mutableLiveDataForEachGroup.get(userGroupConversationModel.getGroupId());
            if(userGroupConversationModelMutableLiveData == null){
                userGroupConversationModelMutableLiveData = new MutableLiveData<>();
                mutableLiveDataForEachGroup.put(userGroupConversationModel.getGroupId(), userGroupConversationModelMutableLiveData);

                LiveData<UserGroupConversationModel> userGroupConversationReturnedLiveData = applyTransformationsForEachGroup(userGroupConversationModelMutableLiveData);
//
                eachUserGroupConversationModelLiveDataList.add(userGroupConversationReturnedLiveData); //needed for cleanup purposes
                eachGroupConversationLiveData.addSource(userGroupConversationReturnedLiveData, new Observer<UserGroupConversationModel>() {
                    @Override
                    public void onChanged(UserGroupConversationModel userGroupConversationModel) {
//                        Log.d(TAG, "onChanged: eachGroupLiveData source changed for Group "+userGroupConversationModel.getGroupId());
                        eachGroupConversationLiveData.setValue(userGroupConversationModel);
                    }
                });
            }

            //what about if we set value only if it is different than before??
//            UserGroupModel newUserGroupModel = UserGroupModelMapper.getUserModelFromDomain(listDomainResource.data.get(i));
//            UserGroupModel existingUserGroupModel = userGroupConversationModelMutableLiveData.getValue();
//            if(existingUserGroupModel != null && existingUserGroupModel.isSameGroup(newUserGroupModel))
//                continue;
            userGroupConversationModelMutableLiveData.setValue(UserGroupConversationModelMapper.getUserModelFromDomain(listDomainResource.data.get(i)));
        }
    }

    private LiveData<UserGroupConversationModel> applyTransformationsForEachGroup(MutableLiveData<UserGroupConversationModel> userGroupConversationModelMutableLiveData) {
        return Transformations.switchMap(userGroupConversationModelMutableLiveData, new Function<UserGroupConversationModel, LiveData<UserGroupConversationModel>>() {
            @Override
            public LiveData<UserGroupConversationModel> apply(final UserGroupConversationModel input) {


                final MediatorLiveData<UserGroupConversationModel> multipleFunctions = new MediatorLiveData<>();

////                        final Long groupLastMessageSeenTS = AppConfigHelper.getGroupLastSeenMsgTS(input.getGroupId());
                final Long groupLastMessageSeenTS = input.getLastAccessed();


                multipleFunctions.addSource(getNetUnreadGroupConversationMessagesUseCase.execute(GetNetUnreadGroupConversationMessagesUseCase.Params.getNetUnreadMessagesForGroup(input.getGroupId(), input.getOtherUserId())), new Observer<List<DomainMessage>>() {
                    @Override
                    public void onChanged(List<DomainMessage> domainMessages) {

                        Log.d(TAG, "onChanged: for getNetUnreadGroupConversationMessagesUseCase");
                        //No need of the code below since with the changes, now last accessed is part of the UserGroupModel
                        //which is being observed anyways - so, it triggers all this multipleFunctions code

                        //Should work until proven otherwise
                        //User goes to group, comes out, last visited is updated, it is greater than last message TS, so 0 unread
                        //User does not go to group, new messages comes, last visited is less than last message TS, so unread increased
                        //User goes to group, sends messages, comes out. Last visited is greater than last message TS, so 0 unread
//                                if((AppConfigHelper.getLastVisitedGroupTime(input.getGroupId()) != null ) && (AppConfigHelper.getLastVisitedGroupTime(input.getGroupId()) > groupLastMessageSeenTS)){
//                                    input.setUnreadCount(0);
//                                }
//                                else
                        if(domainMessages == null || domainMessages.size() <= 0){
                            Log.d(TAG, "domain message is null, setting to 0");
                            input.setUnreadCount(0);
                        }
                        else{
                            Log.d(TAG, "domain messages unread are - "+domainMessages.size());
                            input.setUnreadCount(domainMessages.size());
                        }

//                                else{
//                                        Log.d(TAG, "setting domain messages unread to "+domainMessages.size());
//                                        /**
//                                         * The idea is to check if there are messages that the user sent
//                                         * in the group and then came out of the group
//                                         * In that case, the messages should not show as unread
//                                         */
//                                        int unreadCount = domainMessages.size();
//                                        Long lastTimeGroupVisited = AppConfigHelper.getLastVisitedGroupTime(input.getGroupId());
//
//                                        for(DomainMessage message: domainMessages){
//                                            if(lastTimeGroupVisited != null && lastTimeGroupVisited > message.getSentAt() &&
//                                            message.getSentBy().equals(AppConfigHelper.getUserId())){
//                                                unreadCount--;
//                                            }
//                                        }
//
//                                    input.setUnreadCount(unreadCount);
//                                }
                        input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.UNREAD_COUNT);
                        multipleFunctions.setValue(input);
                    }
                });

                multipleFunctions.addSource(getLastConvMessagesInCacheUseCase.execute(GetLastConvMessagesInCacheUseCase.Params.forConversation(input.getGroupId(), input.getOtherUserId())), new Observer<DomainMessage>() {
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

                            input.setLastMessageText(domainMessage.getMessageText());
                            input.setLastMessageSentBy(domainMessage.getAlias());
                            input.setLastMessageSentAt(domainMessage.getSentAt());
                        }
                        input.setReceivedGroupDataType(ReceivedGroupDataTypeConstants.LAST_MESSAGE);
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
            userGroupConvsData.removeSource(repositorySource);
        }
        removeLeftOverTriggersFromLastTime();

    }

    private void removeLeftOverTriggersFromLastTime() {
        if(eachUserGroupConversationModelLiveDataList != null && eachUserGroupConversationModelLiveDataList.size() > 0){
            for(LiveData<UserGroupConversationModel> eachUserGroupModelLiveData : eachUserGroupConversationModelLiveDataList){
                Log.d(TAG, "removing previous source for Group : "+eachUserGroupModelLiveData.getValue().getGroupId());
                eachGroupConversationLiveData.removeSource(eachUserGroupModelLiveData);
            }

        }
    }


    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
        super.onCleared();
        userGroupConvsData.removeSource(repositorySource);

    }
    //this method is needed to get the trasnformation data updated to the loading list data- last message stuff, unread count and
    private void updateNewGroupListWithLoadingData(List<UserGroupConversationModel> newUserGroupModels){
        for(UserGroupConversationModel newUserGroupModel: newUserGroupModels){
            if(mutableLiveDataForEachGroup.get(newUserGroupModel.getGroupId()) != null){
                Log.d(TAG, "updateNewConvListWithLoadingData: for group "+newUserGroupModel.getGroupId()+" is there");
                UserGroupConversationModel existingUserGroupModel = mutableLiveDataForEachGroup.get(newUserGroupModel.getGroupId()).getValue();
                newUserGroupModel.setLastMessageSentBy(existingUserGroupModel.getLastMessageSentBy());
                newUserGroupModel.setLastMessageText(existingUserGroupModel.getLastMessageText());
                newUserGroupModel.setLastMessageSentAt(existingUserGroupModel.getLastMessageSentAt());
                newUserGroupModel.setUnreadCount(existingUserGroupModel.getUnreadCount());

            }

        }

    }
}
