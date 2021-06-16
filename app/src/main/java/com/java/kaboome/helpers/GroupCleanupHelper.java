/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.helpers;

import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.repositories.DataConversationsRepository;
import com.java.kaboome.data.repositories.DataGroupRepository;
import com.java.kaboome.data.repositories.DataGroupRequestRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.domain.usecases.DeleteUserGroupConvFromCacheUseCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GroupCleanupHelper {

    private static final String TAG = "KMGrpCleanUpHelper";


    public static void cleanUpAfterDeletedGroups(List<DomainUserGroup> userGroups){
        if(userGroups == null || userGroups.size() <= 0){
            return;
        }

        for(final DomainUserGroup domainUserGroup: userGroups){
                if(domainUserGroup.getDeleted()){
                    //this is called from the UGLVM when the new groups list returns. The UGDao is being observed.
                    //In that case, I cannot update the UGDao here because otherwise, the whole thing goes off in a cycle
                    //the UserGroups list gets continuously gets updated and things go off in a loop.
                    //hence, the last param here is false, so that the UserGroupDao is not updated.
                    //it is true when called from the messages itself
                    WorkerBuilderHelper.callUpdateCacheClearTSWorker(domainUserGroup.getGroupId(), (new Date()).getTime(), false, false);
                    WorkerBuilderHelper.callDeleteGroupAttachmentsWorker(domainUserGroup.getGroupName(), "Group");



                    if(domainUserGroup.getIsAdmin().equals("false")){
                        WorkerBuilderHelper.callUpdateAdminCacheClearTSWorker(domainUserGroup.getGroupId(),(new Date()).getTime(), false, false );
                        WorkerBuilderHelper.callDeleteGroupAttachmentsWorker(domainUserGroup.getGroupName(), AppConfigHelper.getUserId());
                    }
                    else {
                        //if this user is an admin, and had conversations, go through all the conversations
                        //and call these two cleanup workers
                        //get conversations for the group
                        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<DomainUserGroupConversation> conversations = DataConversationsRepository.getInstance().getConversationsForUserGroupsFromCache(domainUserGroup.getGroupId());
                                if (conversations != null && conversations.size() > 0) {
                                    for (DomainUserGroupConversation conversation : conversations) {
                                        WorkerBuilderHelper.callUpdateConvCacheClearTSWorker(domainUserGroup.getGroupId(), conversation.getOtherUserId(), (new Date()).getTime());
                                        WorkerBuilderHelper.callDeleteGroupAttachmentsWorker(domainUserGroup.getGroupName(), conversation.getOtherUserId());
                                        //TODO: now the conversation isDeleted should also be updated
                                        //TODO: user domain use case for this
                                        ConversationsRepository conversationsRepository = DataConversationsRepository.getInstance();
                                        conversationsRepository.removeUserGroupConversation(conversation.getOtherUserId(), domainUserGroup.getGroupId());
                                    }
                                }
                            }
                        });

                    }


                    //how about now we remove the UserGroup from the UserGroupDAO as well
                    //what is the need of it hanging around?
                    //I think it can be done
                    //TODO: other cleanup should have - remove the Group from Group Cache, GroupUsers from GroupUser cache
                    //TODO: and UserGroup from UserGroup cache
                    //TODO: have use cases for these

                    //lets check if this deletes GroupUsers too
                    GroupRepository groupRepository = DataGroupRepository.getInstance();
                    groupRepository.removeGroupFromCache(domainUserGroup.getGroupId());

                    UserGroupRepository userGroupRepository = DataUserGroupRepository.getInstance();
                    userGroupRepository.removeUserGroup(domainUserGroup.getGroupId());

                    //remove requests
                    GroupRequestRepository groupRequestRepository = DataGroupRequestRepository.getInstance();
                    groupRequestRepository.deleteAllRequestsForGroupOnlyLocal(domainUserGroup.getGroupId());


                    //this is needed to tell the server that the group's clean up has been done by the client app
                    WorkerBuilderHelper.callUpdateUGCLeanDoneWorker(domainUserGroup.getGroupId());


                }

        }
    }
}
