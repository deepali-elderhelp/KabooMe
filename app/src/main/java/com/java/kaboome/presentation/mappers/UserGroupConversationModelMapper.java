package com.java.kaboome.presentation.mappers;

import android.util.Log;

import com.java.kaboome.constants.GroupListStatusConstants;
import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainGroupUnreadData;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class takes a UserGroup Domain object and converts it into a
 * UserGroupModel object and vice versa
 */
public class UserGroupConversationModelMapper {

    private static final String TAG = "KMUGConvModelMapper";

    //you should order the list here based upon last message etc.
//    public static List<UserGroupConversationModel> transformAll(DomainResource<List<DomainUserGroupConversation>> groupsResource, List<DomainGroupUnreadData> unreadData){
//
//        List<DomainUserGroupConversation> conversations = groupsResource.data;
//
//        Log.d(TAG, "transformAll: status is "+groupsResource.status);
//
//        List<UserGroupConversationModel> viewConvsList = new ArrayList<>();
//
//        for(DomainUserGroupConversation domainUserGroupConversation: conversations){
//
//            //if group is deleted, do not add it to the list
//            if(domainUserGroupConversation.getDeleted() == null ||  domainUserGroupConversation.getDeleted() != true) {
//
//                UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
//                userGroupConversationModel.setUserId(domainUserGroupConversation.getUserId());
//                userGroupConversationModel.setGroupId(domainUserGroupConversation.getGroupId());
//                userGroupConversationModel.setOtherUserId(domainUserGroupConversation.getOtherUserId());
//                userGroupConversationModel.setOtherUserName(domainUserGroupConversation.getOtherUserName());
//                userGroupConversationModel.setOtherUserRole(domainUserGroupConversation.getOtherUserRole());
//                userGroupConversationModel.setIsOtherUserAdmin(domainUserGroupConversation.getIsOtherUserAdmin());
//                userGroupConversationModel.setCacheClearTS(domainUserGroupConversation.getCacheClearTS());
//                userGroupConversationModel.setLastAccessed(domainUserGroupConversation.getLastAccessed());
//                userGroupConversationModel.setDeleted(domainUserGroupConversation.getDeleted());
//                userGroupConversationModel.setImageUpdateTimestamp(domainUserGroupConversation.getImageUpdateTimestamp());
//
//                DomainGroupUnreadData domainGroupUnreadData = getGroupUnreadData(domainUserGroupConversation.getGroupId(), unreadData);
//                if (domainGroupUnreadData != null) {
//                    String separator = (domainGroupUnreadData.getLastMessageSentBy()!= null && !domainGroupUnreadData.getLastMessageSentBy().isEmpty())? ":":"";
//                    userGroupConversationModel.setUnreadCount(domainGroupUnreadData.getCountOfUnreadMessages());
//                    userGroupConversationModel.setLastMessageText(domainGroupUnreadData.getLastMessageSentBy() +separator+domainGroupUnreadData.getLastMessageText());
//                }
//                viewConvsList.add(userGroupConversationModel);
//            }
//        }
//
//        if(groupsResource.status == DomainResource.Status.LOADING){
//            UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
//            userGroupConversationModel.setGroupId(GroupListStatusConstants.LOADING.toString());
//            viewConvsList.add(userGroupConversationModel);
//        }
//        else if(groupsResource.status == DomainResource.Status.SUCCESS){
//            if(conversations.isEmpty()){
//                UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
//                userGroupConversationModel.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
//                viewConvsList.add(userGroupConversationModel);
//            }
//            //else it is already done
//        }
////        else if(groupsResource.status == DomainResource.Status.ERROR){
////            UserGroupModel userGroupModel = new UserGroupModel();
////            userGroupModel.setGroupId(GroupListStatusConstants.ERROR.toString());
////            viewConvsList.add(userGroupModel);
////        }
//
//        return viewConvsList;
//    }


    public static List<UserGroupConversationModel> transformAll(DomainResource<List<DomainUserGroupConversation>> groupsResource, List<DomainGroupUnreadData> unreadData){

        List<DomainUserGroupConversation> convs = groupsResource.data;

        Log.d(TAG, "transformAll: status is "+groupsResource.status);

        List<UserGroupConversationModel> viewConvsList = new ArrayList<>();

        for(DomainUserGroupConversation domainUserGroupConversation: convs){

            //if group is deleted, do not add it to the list
            if(domainUserGroupConversation.getDeleted() == null ||  domainUserGroupConversation.getDeleted() != true) {

                UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
                userGroupConversationModel.setUserId(domainUserGroupConversation.getUserId());
                userGroupConversationModel.setGroupId(domainUserGroupConversation.getGroupId());
                userGroupConversationModel.setOtherUserId(domainUserGroupConversation.getOtherUserId());
                userGroupConversationModel.setOtherUserName(domainUserGroupConversation.getOtherUserName());
                userGroupConversationModel.setOtherUserRole(domainUserGroupConversation.getOtherUserRole());
                userGroupConversationModel.setIsOtherUserAdmin(domainUserGroupConversation.getIsOtherUserAdmin());
                userGroupConversationModel.setCacheClearTS(domainUserGroupConversation.getCacheClearTS());
                userGroupConversationModel.setLastAccessed(domainUserGroupConversation.getLastAccessed());
                userGroupConversationModel.setDeleted(domainUserGroupConversation.getDeleted());
                userGroupConversationModel.setImageUpdateTimestamp(domainUserGroupConversation.getImageUpdateTimestamp());

                DomainGroupUnreadData domainGroupUnreadData = getGroupUnreadData(domainUserGroupConversation.getGroupId(), unreadData);
                if (domainGroupUnreadData != null) {
                    String separator = (domainGroupUnreadData.getLastMessageSentBy()!= null && !domainGroupUnreadData.getLastMessageSentBy().isEmpty())? ":":"";
                    userGroupConversationModel.setUnreadCount(domainGroupUnreadData.getCountOfUnreadMessages());
                    userGroupConversationModel.setLastMessageText(domainGroupUnreadData.getLastMessageSentBy() +separator+domainGroupUnreadData.getLastMessageText());
                }
                viewConvsList.add(userGroupConversationModel);
            }
        }

        if(groupsResource.status == DomainResource.Status.LOADING){
            UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
            userGroupConversationModel.setGroupId(GroupListStatusConstants.LOADING.toString());
            viewConvsList.add(userGroupConversationModel);
        }
        else if(groupsResource.status == DomainResource.Status.SUCCESS){
            if(convs.isEmpty() || allConversationsDeleted(convs)){
                UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
                userGroupConversationModel.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
                viewConvsList.add(userGroupConversationModel);
            }
            //else it is already done
        }

        return viewConvsList;
    }

    public static List<UserGroupConversationModel> transformAllFromDomain(DomainResource<List<DomainUserGroupConversation>> groupsResource){

            List<DomainUserGroupConversation> convs = groupsResource.data;

            Log.d(TAG, "transformAll: status is "+groupsResource.status);

            List<UserGroupConversationModel> viewConvsList = new ArrayList<>();

            for(DomainUserGroupConversation domainUserGroupConversation: convs){

                //if group is deleted, do not add it to the list
                if(domainUserGroupConversation.getDeleted() == null ||  domainUserGroupConversation.getDeleted() != true) {

                    UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
                    userGroupConversationModel.setUserId(domainUserGroupConversation.getUserId());
                    userGroupConversationModel.setGroupId(domainUserGroupConversation.getGroupId());
                    userGroupConversationModel.setOtherUserId(domainUserGroupConversation.getOtherUserId());
                    userGroupConversationModel.setOtherUserName(domainUserGroupConversation.getOtherUserName());
                    userGroupConversationModel.setOtherUserRole(domainUserGroupConversation.getOtherUserRole());
                    userGroupConversationModel.setIsOtherUserAdmin(domainUserGroupConversation.getIsOtherUserAdmin());
                    userGroupConversationModel.setCacheClearTS(domainUserGroupConversation.getCacheClearTS());
                    userGroupConversationModel.setLastAccessed(domainUserGroupConversation.getLastAccessed());
                    userGroupConversationModel.setDeleted(domainUserGroupConversation.getDeleted());
                    userGroupConversationModel.setImageUpdateTimestamp(domainUserGroupConversation.getImageUpdateTimestamp());

                    viewConvsList.add(userGroupConversationModel);
                }
            }

            if(groupsResource.status == DomainResource.Status.LOADING){
                UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
                userGroupConversationModel.setGroupId(GroupListStatusConstants.LOADING.toString());
                userGroupConversationModel.setLastMessageSentAt(Long.MIN_VALUE);
                viewConvsList.add(userGroupConversationModel);
            }
            else if(groupsResource.status == DomainResource.Status.SUCCESS){
                if(convs.isEmpty() || allConversationsDeleted(convs)){
                    UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
                    userGroupConversationModel.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
                    viewConvsList.add(userGroupConversationModel);
                }
                //else it is already done
            }

            return viewConvsList;
        }


    public static DomainGroupUnreadData getGroupUnreadData(String groupId, List<DomainGroupUnreadData> allUnreadData){

        if(groupId ==null || groupId.isEmpty() || allUnreadData == null || allUnreadData.isEmpty())
            return null;
        for(DomainGroupUnreadData unreadData : allUnreadData){
            if(groupId.equals(unreadData.getGroupId()))
                return  unreadData;
        }
        return null;

    }



    public static DomainUserGroupConversation getDomainFromUserModel(UserGroupConversationModel userGroupConversationModel){

        DomainUserGroupConversation domainUserGroupConversation = new DomainUserGroupConversation();
        domainUserGroupConversation.setUserId(userGroupConversationModel.getUserId());
        domainUserGroupConversation.setGroupId(userGroupConversationModel.getGroupId());
        domainUserGroupConversation.setOtherUserId(userGroupConversationModel.getOtherUserId());
        domainUserGroupConversation.setOtherUserName(userGroupConversationModel.getOtherUserName());
        domainUserGroupConversation.setOtherUserRole(userGroupConversationModel.getOtherUserRole());
        domainUserGroupConversation.setIsOtherUserAdmin(userGroupConversationModel.getIsOtherUserAdmin());
        domainUserGroupConversation.setCacheClearTS(userGroupConversationModel.getCacheClearTS());
        domainUserGroupConversation.setLastAccessed(userGroupConversationModel.getLastAccessed());
        domainUserGroupConversation.setDeleted(userGroupConversationModel.getDeleted());
        domainUserGroupConversation.setImageUpdateTimestamp(userGroupConversationModel.getImageUpdateTimestamp());

        return domainUserGroupConversation;
    }

    public static UserGroupConversationModel getUserModelFromDomain(DomainUserGroupConversation domainUserGroupConversation){
        if (domainUserGroupConversation == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }
        UserGroupConversationModel userGroupConversation = new UserGroupConversationModel();
        userGroupConversation.setUserId(domainUserGroupConversation.getUserId());
        userGroupConversation.setGroupId(domainUserGroupConversation.getGroupId());
        userGroupConversation.setOtherUserId(domainUserGroupConversation.getOtherUserId());
        userGroupConversation.setOtherUserName(domainUserGroupConversation.getOtherUserName());
        userGroupConversation.setOtherUserRole(domainUserGroupConversation.getOtherUserRole());
        userGroupConversation.setIsOtherUserAdmin(domainUserGroupConversation.getIsOtherUserAdmin());
        userGroupConversation.setCacheClearTS(domainUserGroupConversation.getCacheClearTS());
        userGroupConversation.setLastAccessed(domainUserGroupConversation.getLastAccessed());
        userGroupConversation.setDeleted(domainUserGroupConversation.getDeleted());
        userGroupConversation.setImageUpdateTimestamp(domainUserGroupConversation.getImageUpdateTimestamp());


        return userGroupConversation;
    }

    private static boolean allConversationsDeleted(List<DomainUserGroupConversation> conversations){
        if(conversations == null) return true;

        for(DomainUserGroupConversation conversation : conversations){
            if(!conversation.getDeleted()){
                return false;
            }
        }

        return true;
    }
}
