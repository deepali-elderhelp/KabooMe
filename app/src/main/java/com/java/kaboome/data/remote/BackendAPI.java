/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.remote;

import com.java.kaboome.constants.AWSConstants;

public interface BackendAPI {

    String baseRemoteUrl = AWSConstants.API_GATEWAY_DEVELOPMENT_URL.toString();

    String getUserGroups = "users/{userid}/groups";

//    String getUserGroup = "users/{userid}/groups/{groupid}";

    String createGroup = "users/{userid}/groups";

    String updateUserGroups = "users/{userid}/groups";

    String joinGroup = "users/{userid}/groups/{groupid}";

    String getUser = "users/{userid}";

    String updateUser = "users/{userid}";

    String getGroup = "users/{userid}/groups/{groupid}";

    String updateGroup = "users/{userid}/groups/{groupid}";

    String removeGroup = "users/{userid}/groups/{groupid}";

    String createRequest = "users/{userid}/groups/{groupid}/requests";

    String getGroupRequests = "users/{userid}/groups/{groupid}/requests";

    String deleteRequest = "users/{userid}/groups/{groupid}/requests";

    String updateGroupUser = "users/{userid}/groups/{groupid}/users/{groupUserid}";

    String removeGroupUser = "users/{userid}/groups/{groupid}/users/{groupUserid}";

    String getGroupUsers = "users/{userid}/groups/{groupid}/users/";

    String searchGroupsByName = "search/group/{groupnameorid}";

    String searchGroupsByNameOrId = "users/{userid}/search/{searchText}";

    String getMessagesForGroup = "users/{userid}/groups/{groupid}/messages";

    String deleteMessage = "users/{userid}/groups/{groupid}/messages/{messageid}";

    String getUserGroupConversations = "users/{userid}/groups/{groupid}/conversations";

    String createUserGroupConversations = "users/{userid}/groups/{groupid}/conversations";

    String updateUserGroupConversations = "users/{userid}/groups/{groupid}/conversations/{conversationid}";

    String getUserInvitations = "users/{userid}/invite"; //get all invitations

    String rejectUserInvitation = "users/{userid}/invite";

    String inviteContactsForGroup = "users/{userid}/invite";
    String rejectOrCancelUserInvitation = "users/{userid}/invite";

    String sendHelpFeedback = "users/{userid}/help";

}
