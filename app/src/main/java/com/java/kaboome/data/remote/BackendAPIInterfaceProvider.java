/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.remote;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.exceptions.CognitoInternalErrorException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.entities.Invitation;
import com.java.kaboome.data.entities.User;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.data.remote.requests.ConversationCreateRequest;
import com.java.kaboome.data.remote.requests.HelpFeedbackRequest;
import com.java.kaboome.data.remote.requests.InviteContactsRequest;
import com.java.kaboome.data.remote.requests.RequestCreateRequest;
import com.java.kaboome.data.remote.requests.RequestDeleteRequest;
import com.java.kaboome.data.remote.responses.DeleteMessageResponse;
import com.java.kaboome.data.remote.responses.GroupMessagesResponse;
import com.java.kaboome.data.remote.responses.GroupRequestsResponse;
import com.java.kaboome.data.remote.responses.GroupResponse;
import com.java.kaboome.data.remote.responses.GroupUsersResponse;
import com.java.kaboome.data.remote.responses.GroupsResponse;
import com.java.kaboome.data.remote.responses.InvitationsResponse;
import com.java.kaboome.data.remote.responses.UserGroupConversationsResponse;
import com.java.kaboome.data.remote.responses.UserGroupResponse;
import com.java.kaboome.data.remote.responses.UserResponse;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.AuthenticationCallback;
import com.java.kaboome.helpers.CognitoAuthenticationHandler;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.data.remote.requests.GroupCreateRequest;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.CreateGroupResponse;
import com.java.kaboome.data.remote.responses.UserGroupsResponse;
import com.java.kaboome.helpers.FirebaseTokenHelper;
import com.java.kaboome.presentation.views.features.BaseActivity;
import com.java.kaboome.presentation.views.features.BaseFragment;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class BackendAPIInterfaceProvider {

    private static final String TAG = "KMBackendAPIIntfcProv";
    private static BackendAPIInterfaceProvider apiServiceProvider;

    private OkHttpClient okHttpClient;
    private HttpLoggingInterceptor loggingInterceptor;
    BackendAPIInterface apiInterface;

//    private BackendAPIInterfaceProvider(String baseUrl,
//                                        long readTimeout,
//                                        long connectTimeout,
//                                        HttpLoggingInterceptor.Level logLevel){
//
//        loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(logLevel);
//
//        final String idTokenForBackendCalls = CognitoHelper.getCurrSession().getIdToken().getJWTToken();
//        Log.d(TAG, "BackendAPIInterfaceProvider: token - "+idTokenForBackendCalls);
//        okHttpClient = new OkHttpClient.Builder()
//                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
//                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
//                .addNetworkInterceptor(loggingInterceptor)
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request newRequest  = chain.request().newBuilder()
//                                .addHeader("Authorization", idTokenForBackendCalls)
//                                .build();
//                        return chain.proceed(newRequest);
//                    }
//                })
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(BackendAPI.baseRemoteUrl)
//                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
//                .build();
//
//        apiInterface = retrofit.create(BackendAPIInterface.class);
//    }

    private BackendAPIInterfaceProvider(String baseUrl,
                                        long readTimeout,
                                        long connectTimeout,
                                        HttpLoggingInterceptor.Level logLevel) {

        loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(logLevel);

//        final String idTokenForBackendCalls = CognitoHelper.getCurrSession().getIdToken().getJWTToken();
  //      Log.d(TAG, "BackendAPIInterfaceProvider: token - " + idTokenForBackendCalls);
        //adding an authenticator because I was seeing errors like the 401 result back from
        //the intent service SyncMessagesFromServer - mthod - getMessagesForGroup
        //the message coming back for the error was - "The incoming token has expired"
        //so, adding the following authenticator to just to try again if that happens.
        //so for now, adding a check in the NetworkHelper should make sure that calls
        //do not come here if the idToken is not valid.
//        okHttpClient = new OkHttpClient.Builder()
//                .authenticator(new Authenticator() {
//                    @Override
//                    public Request authenticate(Route route, Response response) throws IOException {
//                        Log.d(TAG, "authenticate: it failed due to 401?");
////                        if (response.request().header("Authorization") != null) {
////                            return null; // Give up, we've already attempted to authenticate.
////                        }
//                        //"Authorization" header is not null, it just has old token
//                        //should keep a counter
//                        //we need to get the new tokens from the refresh token or let
//                        //aws-sdk for cognito handle it with getSession()
//                        //get token again and make the request again
////                        String idTokenAgain = CognitoHelper.getCurrSession().getIdToken().getJWTToken();
////                        return response.request().newBuilder()
////                                .header("Authorization", idTokenAgain)
////                                .build();
//                        return null;
//                    }
//                })
//                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
//                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
//                .addNetworkInterceptor(loggingInterceptor)
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request newRequest = chain.request().newBuilder()
//                                .addHeader("Authorization", idTokenForBackendCalls)
//                                .build();
//                        return chain.proceed(newRequest);
//                    }
//                })
//                .build();

//        okHttpClient = new OkHttpClient.Builder()
//                .authenticator(new CognitoAuthenticator())
//                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
//                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
//                .addNetworkInterceptor(loggingInterceptor)
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request newRequest = chain.request().newBuilder()
//                                .addHeader("Authorization", idTokenForBackendCalls)
//                                .build();
//                        return chain.proceed(newRequest);
//                    }
//                })
//                .build();

        okHttpClient = new OkHttpClient.Builder()
                .authenticator(new CognitoAuthenticator())
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = null;
                        //the reason I put the below code in try catch is because sometimes
                        //when the app is coming up after a while, there is no current session
                        //for some reason the following code is executed and then fails since there is no current session
                        //it results in an NPE. NPEs or any other exception other than IOException is not handled by Retrofit
                        //and it crashes the app, so I am catching any other exception which is thrown in the process and
                        //wrap it in an IOException and fwd it
                        try {
                            newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", CognitoHelper.getCurrSession().getIdToken().getJWTToken())
                                    .build();
                        } catch (Exception e) {
                            throw new IOException(e);
                        }
                        return chain.proceed(newRequest);
                    }
                })
                .build();

//        Gson builder = new GsonBuilder().disableHtmlEscaping().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BackendAPI.baseRemoteUrl)
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        apiInterface = retrofit.create(BackendAPIInterface.class);
    }

    public static BackendAPIInterfaceProvider getApiServiceProvider(String baseUrl,
                                                                    long readTimeout,
                                                                    long connectTimeout,
                                                                    HttpLoggingInterceptor.Level logLevel) {
        if (apiServiceProvider == null) {
            apiServiceProvider = new BackendAPIInterfaceProvider(baseUrl, readTimeout, connectTimeout, logLevel);
        }
        return apiServiceProvider;
    }


//    public LiveData<ApiResponse<UserGroupsResponse>> getUserGroups(String userId, String token){
//        return apiInterface.getUserGroups(userId, token);
//    }

    public LiveData<ApiResponse<UserGroupsResponse>> getUserGroups(String userId) {
        return apiInterface.getUserGroups(userId);
    }

//    public LiveData<ApiResponse<UserGroup>> getUserGroup(String userId, String groupId) {
//        return apiInterface.getUserGroup(userId, groupId);
//    }

    public Call<ResponseBody> updateUserGroup(String userId, UserGroup userGroup, String action) {
        return apiInterface.updateUserGroup(userId, userGroup, action);
    }

    public LiveData<ApiResponse<GroupUser>> createGroup(String userId, GroupCreateRequest group) {
        return apiInterface.createGroup(userId, group);
    }

    //    public Call<CreateGroupResponse> createGroup(String userId, String token, GroupCreateRequest group){
//        return apiInterface.createGroup(userId, token, group);
//    }
//
    public LiveData<ApiResponse<UserResponse>> getUser(String userId) {
        return apiInterface.getUser(userId);
    }

    //
    public Call<User> updateUserForToken(String userId, User user, String action) {
        return apiInterface.updateUserForToken(userId, user, action);
    }

    public LiveData<ApiResponse<Void>> updateUser(String userId, User user, String action) {
        return apiInterface.updateUser(userId, user, action);
    }

    /**
     *
     * @param groupNameOrId - can be either "GroupName" or "GroupId" for search by either groupName or groupID
     * @param searchText - search text - complete or partial
     * @return
     */
//    public LiveData<ApiResponse<GroupsResponse>> getGroupsMatchByName(String groupNameOrId, String searchText){
//        return apiInterface.getGroupsMatchByName(groupNameOrId, searchText);
//    }

    /**
     * @param groupNameOrId - can be either "GroupName" or "GroupId" for search by either groupName or groupID
     * @param searchText    - search text - complete or partial
     * @return
     */
    public LiveData<ApiResponse<GroupsResponse>> getGroupsMatchByNameOrId(String userId, String searchText, String groupNameOrId) {
        return apiInterface.getGroupsMatchByNameOrId(userId, searchText, groupNameOrId);
    }

    //
//    public Call<List<Group>> getGroupsMatchByName(String groupName, String token){
//        return apiInterface.getGroupsMatchByName(groupName, token);
//    }
//
////    public Call<Group> getGroup(String userId, String groupId, String token){
////        return apiInterface.getGroup(userId, groupId, token);
////    }
//
    public LiveData<ApiResponse<GroupResponse>> getGroup(String userId, String groupId) {
        return apiInterface.getGroup(userId, groupId);
    }

    //
//
//    public Call<ResponseBody> joinGroup(String userId, String groupId, UserGroup group){
//        return apiInterface.joinGroup(userId, groupId, group);
//    }
    public LiveData<ApiResponse<UserGroupResponse>> joinGroup(String userId, String groupId, UserGroup group) {
        return apiInterface.joinGroup(userId, groupId, group);
    }

    //
//    public Call<GroupMessagesResponse> getMessagesForGroup(String userId, String groupId, Long lastAccessedTime){
//        return apiInterface.getMessagesForGroup(userId, groupId, lastAccessedTime);
//    }
    public LiveData<ApiResponse<GroupMessagesResponse>> getMessagesForGroup(String userId, String groupId, Long lastAccessedTime, Long cacheClearTS, int limit, String scanDirection, String sentTo) {
        return apiInterface.getMessagesForGroup(userId, groupId, lastAccessedTime, cacheClearTS, limit, scanDirection, sentTo);
    }

    public Call<GroupMessagesResponse> getMessagesForGroupInBackground(String userId, String groupId, Long lastAccessedTime, Long cacheClearTS, int limit, String scanDirection, String sentTo) {
        return apiInterface.getMessagesForGroupInBackground(userId, groupId, lastAccessedTime, cacheClearTS, limit, scanDirection, sentTo);
    }

    public Call<DeleteMessageResponse> deleteMessage(String userId, String groupId, String messageId) {
        return apiInterface.deleteMessage(userId, groupId, messageId);
    }

    public LiveData<ApiResponse<UserGroupConversationsResponse>> getConversationsForUserGroup(String userId, String groupId) {
        return apiInterface.getUserGroupConversations(userId, groupId);
    }

    public LiveData<ApiResponse<UserGroupConversation>> createUserGroupConversation(String userId, String groupId, ConversationCreateRequest conversationCreateRequest){
        return apiInterface.createUserGroupConversation(userId, groupId, conversationCreateRequest);
    }

    public Call<ResponseBody> updateUserGroupConversation(String userId, String groupId, String conversationId, UserGroupConversation userGroupConversation, String action){
        return apiInterface.updateUserGroupConversation(userId, groupId, conversationId, userGroupConversation, action);
    }

//
//    public LiveData<ApiResponse<MessagesResponse>> getMessagesForGroup(String userId, String groupId, String token, Long lastAccessedTime){
//        return apiInterface.getMessagesForGroup(userId, groupId, token, lastAccessedTime);
//    }
//
//    public Call<ResponseBody> updateGroup(String userId, String groupId, Group group, String action){
//        return apiInterface.updateGroup(userId, groupId, group, action);
//    }

//    public Call<ResponseBody> removeGroup(String userId, String groupId) {
//        return apiInterface.removeGroup(userId, groupId);
//    }
    public LiveData<ApiResponse<Void>> removeGroup(String userId, String groupId) {
        return apiInterface.removeGroup(userId, groupId);
    }


//    public Call<ResponseBody> updateGroupUser(String userId, String groupId, String groupUserId, GroupUser groupUser, String action){
//        return apiInterface.updateGroupUser(userId, groupId, groupUserId, groupUser, action);
//    }

//    public Call<ResponseBody> removeGroupUser(String userId, String groupId, String groupUserId) {
//        return apiInterface.removeGroupUser(userId, groupId, groupUserId);
//    }
    public LiveData<ApiResponse<Void>> removeGroupUser(String userId, String groupId, String groupUserId) {
        return apiInterface.removeGroupUser(userId, groupId, groupUserId);
    }
////

    public LiveData<ApiResponse<Void>> updateGroup(String userId, String groupId, Group group, String action) {
        return apiInterface.updateGroup(userId, groupId, group, action);
    }

    public LiveData<ApiResponse<Void>> updateGroupUser(String userId, String groupId, String groupUserId, GroupUser groupUser, String action) {
        return apiInterface.updateGroupUser(userId, groupId, groupUserId, groupUser, action);
    }

    public LiveData<ApiResponse<GroupUsersResponse>> getGroupUsers(String userId, String groupId) {
        return apiInterface.getGroupUsers(userId, groupId);
    }

    //
//    public Call<ResponseBody> inviteContactsForGroup(String userId, String token, Invitation invitation){
//        return apiInterface.inviteContactsForGroup(userId, token, invitation);
//    }
//
////    public Call<InvitationsResponse> getUserInvitations(String userId, String token){
////        return apiInterface.getUserInvitations(userId, token);
////    }
//
    public LiveData<ApiResponse<InvitationsResponse>> getUserInvitations(String userId) {
        return apiInterface.getUserInvitations(userId);
    }

    //
    public Call<ResponseBody> rejectUserInvitation(String userId, String groupId) {
        return apiInterface.rejectUserInvitation(userId, groupId);
    }

    public LiveData<ApiResponse<InvitationsResponse>> rejectOrCancelUserInvitation(String userId, String groupId) {
        return apiInterface.rejectOrCancelUserInvitation(userId, groupId);
    }

    //    public Call<ResponseBody> createRequest(String userId, String groupId,RequestCreateRequest requestCreateRequest){
//        return apiInterface.createRequest(userId, groupId, requestCreateRequest);
//    }
    public LiveData<ApiResponse<Void>> createRequest(String userId, String groupId, RequestCreateRequest requestCreateRequest) {
        return apiInterface.createRequest(userId, groupId, requestCreateRequest);
    }

    public LiveData<ApiResponse<GroupRequestsResponse>> getGroupRequests(String userId, String groupId) {
        return apiInterface.getGroupRequests(userId, groupId);
    }

    public LiveData<ApiResponse<GroupRequestsResponse>> deleteGroupRequest(String userId, String groupId, RequestDeleteRequest requestDeleteRequest) {
        return apiInterface.deleteGroupRequest(userId, groupId, requestDeleteRequest);
    }

//    public LiveData<ApiResponse<String>> inviteContactsToJoinGroup(String userId, InviteContactsRequest inviteContactsRequest){
//        return apiInterface.inviteContactsToJoinGroup(userId, inviteContactsRequest);
//    }

    public Call<ResponseBody> inviteContactsToJoinGroup(String userId, InviteContactsRequest inviteContactsRequest) {
        return apiInterface.inviteContactsToJoinGroup(userId, inviteContactsRequest);
    }
//    public LiveData<ApiResponse<Void>> inviteContactsToJoinGroup(String userId, InviteContactsRequest inviteContactsRequest) {
//        return apiInterface.inviteContactsToJoinGroup(userId, inviteContactsRequest);
//    }

    public Call<ResponseBody> sendHelpFeedbackMessage(String userId, HelpFeedbackRequest helpFeedbackRequest){
       return apiInterface.sendHelpFeedbackMessage(userId, helpFeedbackRequest);
//       return apiInterface.sendHelpFeedbackMessage(userId, helpFeedbackRequest.getMessageText());
    }

//    public Call<ResponseBody> sendHelpFeedbackMessage(String userId, JsonObject jsonObject){
//        return apiInterface.sendHelpFeedbackMessage(userId, jsonObject);
////       return apiInterface.sendHelpFeedbackMessage(userId, helpFeedbackRequest.getMessageText());
//    }
}

    class CognitoAuthenticator implements Authenticator {

        private static final String TAG = "KMCognitoAuthenticator";

        Request returningRequest = null;
        boolean isRefreshing = false;

        @Override
        public Request authenticate(Route route, final Response response) throws IOException {

            if(response == null){
                return returningRequest;
            }

            CognitoUser user = CognitoHelper.getPool().getCurrentUser();
            if (user == null) {
                return returningRequest;
            }
            String userName = user.getUserId();
            if (userName == null) {
                return returningRequest;
            }
            if (userName != null) {
                CognitoHelper.setUser(userName);
                try {
                    getToken(user, response); //synchronized method
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return returningRequest;

        }

        public synchronized void getToken(CognitoUser user, final Response response) throws InterruptedException {
            if (!isRefreshing) {
                //This is very important to call notify() on the same object that we call wait();
                final CognitoAuthenticator myInstance = this;
                isRefreshing = true;
                Log.d(TAG, "Refreshing token..." );

                //Make async call
                user.getSessionInBackground(new CognitoAuthenticationHandler(new AuthenticationCallback() {

                    @Override
                    public void onFailure(Exception e) {

                        synchronized (myInstance) {
                            isRefreshing = false;
                            returningRequest = null;
                            myInstance.notifyAll();
                            Log.d("refreshToken", "onError .");
                        }
                    }

                    @Override
                    public void onSuccess(CognitoUserSession cognitoUserSession) {
                        synchronized (myInstance) {
                            CognitoHelper.setCurrSession(cognitoUserSession);
                            //check device token, in case it has changed but not captured
                            //user is successfully login, check the fcm token once before going on
                            //dont think we need it here
                            //new device token is created only when the user is either new or has deleted all cache
                            //then when app is launched, new device token will be created before reaching here and
                            //update will be caught much before
//                            FirebaseTokenHelper.updateDeviceToken();
                            //save userId
                            AppConfigHelper.setUserId(cognitoUserSession.getUsername());

                            String idTokenAgain = CognitoHelper.getCurrSession().getIdToken().getJWTToken();
                            returningRequest = response.request().newBuilder()
                                    .header("Authorization", idTokenAgain)
                                    .build();
                            isRefreshing = false;
                            myInstance.notifyAll();
                        }


                    }
                }));
            }

            Log.d("refreshToken", "before wait ." + android.os.Process.getThreadPriority(android.os.Process.myTid()) + this.toString());
            this.wait();
            Log.d("refreshToken", "after wait ." + android.os.Process.getThreadPriority(android.os.Process.myTid()) + this.toString());
        }


    }


//        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
//
//
//            @Override
//            public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
////            AppConfigHelper.setUserLoggedIn(true);
//                Log.d(TAG, " -- Auth Success");
//                CognitoHelper.setCurrSession(cognitoUserSession);
//                String idTokenAgain = CognitoHelper.getCurrSession().getIdToken().getJWTToken();
//                response.request().newBuilder()
//                        .header("Authorization", idTokenAgain)
//                        .build();
//
//            }
//
//            @Override
//            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId){
//                Log.d(TAG, "getAuthenticationDetails: should not come here, throw error");
//                onFailure(new Exception("Fail intentionally, so it goes for login"));
//
//            }
//
//            @Override
//            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
//                Log.d(TAG, "getMFACode: ");
//            }
//
//            @Override
//            public void authenticationChallenge(ChallengeContinuation continuation) {
//                Log.d(TAG, "authenticationChallenge: ");
//            }
//
//
//            @Override
//            public void onFailure(Exception e) {
//
//                Log.d(TAG, "Login failed due to "+e.getMessage()+" in thread "+Thread.currentThread().getName());
//
//
//
//            }
//
//
//        };
//
//    }

//    class CustomAuthenticationHandler implements AuthenticationHandler{
//
//
//
//        @Override
//        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
//            CognitoHelper.setCurrSession(userSession);
//            String idTokenAgain = CognitoHelper.getCurrSession().getIdToken().getJWTToken();
//        }
//
//        @Override
//        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
//            Log.d(TAG, "getAuthenticationDetails: should not come here, throw error");
//            onFailure(new Exception("Fail intentionally, so it goes for login"));
//        }
//
//        @Override
//        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
//            Log.d(TAG, "getMFACode: ");
//        }
//
//        @Override
//        public void authenticationChallenge(ChallengeContinuation continuation) {
//            Log.d(TAG, "authenticationChallenge: ");
//        }
//
//        @Override
//        public void onFailure(Exception exception) {
//            Log.d(TAG, "Login failed due to "+exception.getMessage()+" in thread "+Thread.currentThread().getName());
//        }
//    }
//
//    interface CognitoRefreshSessionCallback{
//        void onSuccess();
//
//        void onFailure();
//    }









