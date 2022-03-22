/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.remote;

import androidx.lifecycle.LiveData;

import com.google.gson.JsonObject;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.entities.Invitation;
import com.java.kaboome.data.entities.User;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.data.remote.requests.ConversationCreateRequest;
import com.java.kaboome.data.remote.requests.GroupCreateRequest;
import com.java.kaboome.data.remote.requests.HelpFeedbackRequest;
import com.java.kaboome.data.remote.requests.InviteContactsRequest;
import com.java.kaboome.data.remote.requests.RequestCreateRequest;
import com.java.kaboome.data.remote.requests.RequestDeleteRequest;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.CreateGroupResponse;
import com.java.kaboome.data.remote.responses.DeleteMessageResponse;
import com.java.kaboome.data.remote.responses.GroupMessagesResponse;
import com.java.kaboome.data.remote.responses.GroupRequestsResponse;
import com.java.kaboome.data.remote.responses.GroupResponse;
import com.java.kaboome.data.remote.responses.GroupUsersResponse;
import com.java.kaboome.data.remote.responses.GroupsResponse;
import com.java.kaboome.data.remote.responses.InvitationsResponse;
import com.java.kaboome.data.remote.responses.UserGroupConversationsResponse;
import com.java.kaboome.data.remote.responses.UserGroupResponse;
import com.java.kaboome.data.remote.responses.UserGroupsResponse;
import com.java.kaboome.data.remote.responses.UserResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendAPIInterface {

    @GET(BackendAPI.getUser)
//    Call<User> getUser(@Path(value = "userid") String userId, @Header(value = "token") String token);
    LiveData<ApiResponse<UserResponse>> getUser(@Path(value = "userid") String userId);
//
//
    @PUT(BackendAPI.updateUser)
    LiveData<ApiResponse<Void>> updateUser(@Path(value = "userid") String userId, @Body User user, @Query(value = "action") String action);
//    Call<ResponseBody> updateUser(@Path(value = "userid") String userId, @Body User user, @Query(value = "action") String action);

    @PUT(BackendAPI.updateUser)
    Call<User> updateUserForToken(@Path(value = "userid") String userId, @Body User user, @Query(value = "action") String action);

    //
    @GET(BackendAPI.getUserGroups)
//    Call<UserGroupsResponse> getUserGroups(@Path(value = "userid") String userId, @Header(value = "token") String token);
//    LiveData<ApiResponse<UserGroupsResponse>> getUserGroups(@Path(value = "userid") String userId, @Header(value = "token") String token);
//    LiveData<ApiResponse<UserGroupsResponse>> getUserGroups(@Path(value = "userid") String userId, @Header(value = "Authorization") String authorization);
    LiveData<ApiResponse<UserGroupsResponse>> getUserGroups(@Path(value = "userid") String userId);

//    @GET(BackendAPI.getUserGroup)
//    LiveData<ApiResponse<UserGroup>> getUserGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId);


    @PUT(BackendAPI.updateUserGroups)
    Call<ResponseBody> updateUserGroup(@Path(value = "userid") String userId, @Body UserGroup userGroup, @Query(value = "action") String action);
//    @POST(BackendAPI.createGroup)
//    Call<CreateGroupResponse> createGroup(@Path(value = "userid") String userId, @Header(value = "token") String token, @Body GroupCreateRequest group);

    @POST(BackendAPI.createGroup)
//    Call<CreateGroupResponse> createGroup(@Path(value = "userid") String userId, @Body GroupCreateRequest group);
    LiveData<ApiResponse<GroupUser>>createGroup(@Path(value = "userid") String userId, @Body GroupCreateRequest group);
//
    @POST(BackendAPI.joinGroup)
    LiveData<ApiResponse<UserGroupResponse>> joinGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body UserGroup group);
//    Call<ResponseBody> joinGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body UserGroup group);
//
    /**
     *
     * @param groupNameOrId - can be either "GroupName" or "GroupId" for search by either groupName or groupID
     * @param searchText - search text - complete or partial
     * @return
     */

//    @GET(BackendAPI.searchGroupsByName)
//    LiveData<ApiResponse<GroupsResponse>> getGroupsMatchByName(@Path(value = "groupnameorid") String groupNameOrId, @Query(value = "groupText") String searchText);

    @GET(BackendAPI.searchGroupsByNameOrId)
    LiveData<ApiResponse<GroupsResponse>> getGroupsMatchByNameOrId(@Path(value = "userid") String userId, @Path(value = "searchText") String searchText, @Query(value = "groupnameorid") String groupNameOrId);


    //
////    @GET(BackendAPI.getGroup)
////    Call<Group> getGroup(@Path(value = "userid") String userId,@Path(value = "groupid") String groupId, @Header(value = "token") String token);
//
    @GET(BackendAPI.getGroup)
    LiveData<ApiResponse<GroupResponse>> getGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId);
//
//
//    @GET(BackendAPI.getMessagesForGroup)
//    Call<MessagesResponse> getMessagesForGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Header(value = "token") String token, @Query(value = "lastAccessedTime") Long lastAccessedTime);

//    @GET(BackendAPI.getMessagesForGroup)
//    Call<GroupMessagesResponse> getMessagesForGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Query(value = "lastAccessedTime") Long lastAccessedTime);

//    @GET(BackendAPI.getMessagesForGroup)
//    LiveData<ApiResponse<GroupMessagesResponse>> getMessagesForGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Query(value = "lastAccessedTime") Long lastAccessedTime, @Query(value = "limit") int limit, @Query(value = "scanDirection") String scanDirection);

    @GET(BackendAPI.getMessagesForGroup)
    LiveData<ApiResponse<GroupMessagesResponse>> getMessagesForGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Query(value = "lastAccessedTime") Long lastAccessedTime,@Query(value = "cacheClearTS") Long cacheClearTS, @Query(value = "limit") int limit, @Query(value = "scanDirection") String scanDirection, @Query(value = "sentTo") String sentTo);

    @GET(BackendAPI.getMessagesForGroup)
    Call<GroupMessagesResponse> getMessagesForGroupInBackground(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Query(value = "lastAccessedTime") Long lastAccessedTime, @Query(value = "cacheClearTS") Long cacheClearTS, @Query(value = "limit") int limit, @Query(value = "scanDirection") String scanDirection, @Query(value = "sentTo") String sentTo);


    @DELETE(BackendAPI.deleteMessage)
    Call<DeleteMessageResponse> deleteMessage(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Path(value = "messageid") String messageId);


    @GET(BackendAPI.getUserGroupConversations)
    LiveData<ApiResponse<UserGroupConversationsResponse>> getUserGroupConversations(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId);

    @POST(BackendAPI.createUserGroupConversations)
    LiveData<ApiResponse<UserGroupConversation>> createUserGroupConversation(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body ConversationCreateRequest conversationCreateRequest);

    @PUT(BackendAPI.updateUserGroupConversations)
    Call<ResponseBody> updateUserGroupConversation(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Path(value = "conversationid") String conversationId, @Body UserGroupConversation userGroupConversation, @Query(value = "action") String action);
//    @POST
//    @GET(BackendAPI.getMessagesForGroup)
//    LiveData<ApiResponse<MessagesResponse>> getMessagesForGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Header(value = "token") String token, @Query(value = "lastAccessedTime") Long lastAccessedTime);
//
//
//    @PUT(BackendAPI.updateGroup)
//    Call<ResponseBody> updateGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body Group group, @Query(value = "action") String action);


//    @DELETE(BackendAPI.removeGroup)
//    Call<ResponseBody> removeGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId);
    @DELETE(BackendAPI.removeGroup)
    LiveData<ApiResponse<Void>> removeGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId);


//    @PUT(BackendAPI.updateGroupUser)
//    Call<ResponseBody> updateGroupUser(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Path(value="groupUserid") String groupUserId, @Body GroupUser groupUser, @Query(value = "action") String action);

    @PUT(BackendAPI.updateGroupUser)
    LiveData<ApiResponse<Void>> updateGroupUser(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Path(value="groupUserid") String groupUserId, @Body GroupUser groupUser, @Query(value = "action") String action);


    @DELETE(BackendAPI.removeGroupUser)
    LiveData<ApiResponse<Void>> removeGroupUser(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Path(value="groupUserid") String groupUserId);
//    Call<ResponseBody> removeGroupUser(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Path(value="groupUserid") String groupUserId);

    @GET(BackendAPI.getGroupUsers)
    LiveData<ApiResponse<GroupUsersResponse>> getGroupUsers(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId);
////
////
////
    @PUT(BackendAPI.updateGroup)
    LiveData<ApiResponse<Void>> updateGroup(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body Group group, @Query(value = "action") String action);

////    @GET(BackendAPI.getUserInvitations)
////    Call<InvitationsResponse> getUserInvitations(@Path(value = "userid") String userId, @Header(value = "token") String token);
//
    @GET(BackendAPI.getUserInvitations)
    LiveData<ApiResponse<InvitationsResponse>> getUserInvitations(@Path(value = "userid") String userId);
//
//
//    @POST(BackendAPI.inviteContactsForGroup)
//    Call<ResponseBody> inviteContactsForGroup(@Path(value = "userid") String userId, @Header(value = "token") String token, @Body Invitation invitation);
//
    @DELETE(BackendAPI.rejectUserInvitation)
    Call<ResponseBody> rejectUserInvitation(@Path(value = "userid") String userId, @Query(value = "groupId") String groupId);

    @DELETE(BackendAPI.rejectOrCancelUserInvitation)
    LiveData<ApiResponse<InvitationsResponse>> rejectOrCancelUserInvitation(@Path(value = "userid") String userId, @Query(value = "groupId") String groupId);

    @POST(BackendAPI.createRequest)
    LiveData<ApiResponse<Void>> createRequest(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body RequestCreateRequest requestCreateRequest);
//    Call<ResponseBody> createRequest(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body RequestCreateRequest requestCreateRequest);

    @GET(BackendAPI.getGroupRequests)
    LiveData<ApiResponse<GroupRequestsResponse>> getGroupRequests(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId);

//    @DELETE(BackendAPI.deleteRequest)
//    LiveData<ApiResponse<GroupRequestsResponse>> deleteGroupRequest(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body RequestDeleteRequest requestDeleteRequest);

    //I had to use DELETE as HTTP method and not @DELETE because, retrofit does not support body for delete query right now
    @HTTP(method = "DELETE", path = BackendAPI.deleteRequest, hasBody = true)
    LiveData<ApiResponse<GroupRequestsResponse>> deleteGroupRequest(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Body RequestDeleteRequest requestDeleteRequest);

//    @POST(BackendAPI.inviteContactsForGroup)
//    LiveData<ApiResponse<String>> inviteContactsToJoinGroup(@Path(value = "userid") String userId, @Body InviteContactsRequest inviteContactsRequest);


    @POST(BackendAPI.inviteContactsForGroup)
    Call<ResponseBody> inviteContactsToJoinGroup(@Path(value = "userid") String userId, @Body InviteContactsRequest inviteContactsRequest);

//    LiveData<ApiResponse<Void>> inviteContactsToJoinGroup(@Path(value = "userid") String userId, @Body InviteContactsRequest inviteContactsRequest);

    @POST(BackendAPI.sendHelpFeedback)
//    Call<ResponseBody> sendHelpFeedbackMessage(@Path(value = "userid") String userId, @Field("messageText") String messageText);
//    Call<ResponseBody> sendHelpFeedbackMessage(@Path(value = "userid") String userId, @Body HelpFeedbackRequest helpFeedbackRequest);
    Call<ResponseBody> sendHelpFeedbackMessage(@Path(value = "userid") String userId, @Body HelpFeedbackRequest helpFeedbackRequest);
//    Call<ResponseBody> sendHelpFeedbackMessage(@Path(value = "userid") String userId, @Body JsonObject jsonObject);


    @PUT(BackendAPI.updateMessage)
    Call<ResponseBody> updateMessage(@Path(value = "userid") String userId, @Path(value = "groupid") String groupId, @Path(value = "messageid") String messageId, @Query(value = "action") String action);

}
