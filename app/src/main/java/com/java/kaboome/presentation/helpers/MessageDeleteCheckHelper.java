package com.java.kaboome.presentation.helpers;

import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.AppConfigHelper;

public class MessageDeleteCheckHelper {

    public static boolean canDeleteMessage(Message message, String isUserAdmin){
        //return true if user is the creator of the message
        //or if user is an admin of the group
        if(AppConfigHelper.getUserId().equals(message.getSentBy())){
            return true;
        }
        if(isUserAdmin != null && Boolean.parseBoolean(isUserAdmin)){
            return true;
        }
        return false;
    }
}
