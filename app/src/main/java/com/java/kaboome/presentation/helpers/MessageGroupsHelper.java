package com.java.kaboome.presentation.helpers;

import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.presentation.entities.IoTMessage;

public class MessageGroupsHelper {

    public static String sentToBasedUponMessageGroupsConstants(MessageGroupsConstants messageGroupsConstants, String userId){
        if(messageGroupsConstants == null){
            return "Group";
        }
        if(messageGroupsConstants.equals(MessageGroupsConstants.GROUP_MESSAGES)){
            return "Group";
        }
        if(messageGroupsConstants.equals(MessageGroupsConstants.ADMIN_MESSAGES)){
            return "InterAdmin";
        }
        else{
            return userId;
        }

    }

    public static MessageGroupsConstants getMessageGroupConstantBySentTo(String sentTo){
        if(sentTo == null){
            return MessageGroupsConstants.GROUP_MESSAGES;
        }
        if(sentTo.equals(MessageGroupsConstants.GROUP_MESSAGES.getStatus())){
            return MessageGroupsConstants.GROUP_MESSAGES;
        }
        if(sentTo.equals(MessageGroupsConstants.ADMIN_MESSAGES.getStatus())){
            return MessageGroupsConstants.ADMIN_MESSAGES;
        }
        else{
            return MessageGroupsConstants.USER_ADMIN_MESSAGES;
        }
    }

    public static String getTopicName(IoTMessage message, String userId){
        if(message == null){
            return null;
        }
        if(message.getSentTo().equals(MessageGroupsConstants.GROUP_MESSAGES.getStatus())){
            return message.getGroupId();
        }
        if(message.getSentTo().equals(MessageGroupsConstants.ADMIN_MESSAGES.getStatus())){
            return message.getGroupId()+"_InterAdmin";
        }
        else{
            return userId+"_"+message.getGroupId()+"_Admin";
        }
    }

    public static String getTopicName(String groupId, String userId, MessageGroupsConstants messageGroupsConstants){
        if(groupId == null || messageGroupsConstants == null){
            return null;
        }
        if(messageGroupsConstants == MessageGroupsConstants.GROUP_MESSAGES){
            return groupId;
        }
        if(messageGroupsConstants == MessageGroupsConstants.ADMIN_MESSAGES){
            return groupId+"_InterAdmin";
        }
        if(messageGroupsConstants == MessageGroupsConstants.USER_ADMIN_MESSAGES){
            return userId+"_"+groupId+"_Admin";
        }
        return null;
    }
}
