package com.java.kaboome.presentation.views.features.groupActions;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataGroupRequestRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;
//import com.java.kaboome.domain.usecases.GetGroupMessagesAfterLastAccessUseCase;
import com.java.kaboome.domain.usecases.GetNetUnreadOnlyGroupMessagesUseCase;
import com.java.kaboome.domain.usecases.GetUserGroupLastAccessedCache;
import com.java.kaboome.domain.usecases.GetUserGroupRequestsListUseCase;

import java.util.List;

public class GroupActionsViewModel extends ViewModel {

    private static final String TAG = "KMGroupsActionViewModel";
    private final String groupId;
    private UserGroupRepository userGroupRepository;
    private GetUserGroupLastAccessedCache getUserGroupLastAccessedCache;
    private MessagesListRepository messagesListRepository;
//    private GetGroupMessagesAfterLastAccessUseCase getGroupMessagesAfterLastAccessUseCase;
    private GroupRequestRepository groupRequestRepository;
    private GetUserGroupRequestsListUseCase getUserGroupRequestsListUseCase;
    private MediatorLiveData<Integer> numberOfUnreadMessages = new MediatorLiveData<>();
    private MediatorLiveData<Integer> numberOfRequests = new MediatorLiveData<>();

    public GroupActionsViewModel(String groupIdPassed) {
        super();

        this.groupId = groupIdPassed;
        messagesListRepository = DataGroupMessagesRepository.getInstance();
//        getGroupMessagesAfterLastAccessUseCase = new GetGroupMessagesAfterLastAccessUseCase(messagesListRepository);

        groupRequestRepository = DataGroupRequestRepository.getInstance();
        getUserGroupRequestsListUseCase = new GetUserGroupRequestsListUseCase(groupRequestRepository);

        userGroupRepository = DataUserGroupRepository.getInstance();
        getUserGroupLastAccessedCache = new GetUserGroupLastAccessedCache(userGroupRepository);
    }

    public MediatorLiveData<Integer> getNumberOfUnreadMessages() {
        return numberOfUnreadMessages;
    }


    public MediatorLiveData<Integer> getNumberOfRequests() {
        return numberOfRequests;
    }

    public void loadUnreadAndRequests(){
        GetNetUnreadOnlyGroupMessagesUseCase getNetUnreadOnlyGroupMessagesUseCase = new GetNetUnreadOnlyGroupMessagesUseCase(messagesListRepository);
                numberOfUnreadMessages.addSource(getNetUnreadOnlyGroupMessagesUseCase.execute(GetNetUnreadOnlyGroupMessagesUseCase.Params.getNetUnreadMessagesForGroup(groupId)),
                        new Observer<List<DomainMessage>>() {
                            @Override
                            public void onChanged(List<DomainMessage> domainMessages) {
                                if (domainMessages != null) {
                                    numberOfUnreadMessages.setValue(domainMessages.size());
                                }
                            }
                        });
        numberOfRequests.addSource(getUserGroupRequestsListUseCase.execute(GetUserGroupRequestsListUseCase.Params.getUserGroupRequests(groupId, false)),
                        new Observer<List<DomainGroupRequest>>() {
                            @Override
                            public void onChanged(List<DomainGroupRequest> domainGroupRequests) {
                                if (domainGroupRequests != null) {
                                    numberOfRequests.setValue(domainGroupRequests.size());
                                }
                            }
                        });


    }



//    public void loadUnreadAndRequests() {
//
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                GetNetUnreadGroupMessagesUseCase getNetUnreadGroupMessagesUseCase = new GetNetUnreadGroupMessagesUseCase(messagesListRepository);
//                numberOfUnreadMessages.addSource(getNetUnreadGroupMessagesUseCase.execute(GetNetUnreadGroupMessagesUseCase.Params.getNetUnreadMessagesForGroup(groupId)),
//                        new Observer<List<DomainMessage>>() {
//                            @Override
//                            public void onChanged(List<DomainMessage> domainMessages) {
//                                if (domainMessages != null) {
//                                    numberOfUnreadMessages.postValue(domainMessages.size());
//                                }
//                            }
//                        });
//
////                Long lastAccessed = getUserGroupLastAccessedCache.execute(GetUserGroupLastAccessedCache.Params.forGroup(groupId));
////
////                if(lastAccessed != null){
////                   numberOfUnreadMessages.addSource(getGroupMessagesAfterLastAccessUseCase.execute(
////                           GetGroupMessagesAfterLastAccessUseCase.Params.getMessagesAfterLastAccessForGroup(groupId, lastAccessed)
////                   ), new Observer<List<DomainMessage>>() {
////                       @Override
////                       public void onChanged(List<DomainMessage> domainMessages) {
////                           if(domainMessages != null){
////                               numberOfUnreadMessages.postValue(domainMessages.size());
////                           }
////
////                       }
////                   });
////                }
////            }
////        });
//
//                numberOfRequests.addSource(getUserGroupRequestsListUseCase.execute(GetUserGroupRequestsListUseCase.Params.getUserGroupRequests(groupId, false)),
//                        new Observer<List<DomainGroupRequest>>() {
//                            @Override
//                            public void onChanged(List<DomainGroupRequest> domainGroupRequests) {
//                                if (domainGroupRequests != null) {
//                                    numberOfRequests.setValue(domainGroupRequests.size());
//                                }
//                            }
//                        });
//
//
//            }
//        });
//    }
}
