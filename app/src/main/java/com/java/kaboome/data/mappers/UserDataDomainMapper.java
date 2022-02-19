package com.java.kaboome.data.mappers;


import com.java.kaboome.constants.UserActionConstants;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.User;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainUser;

public class UserDataDomainMapper {

    public static DomainUser transformFromUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot transformFromUser a null value");
        }

        DomainUser domainUser = new DomainUser();
        domainUser.setUserId(user.getUserId());
        domainUser.setUserName(user.getUserName());
        domainUser.setDateUserCreated(user.getDateUserCreated());
        domainUser.setPhoneNumber(user.getPhoneNumber());
        domainUser.setEmail(user.getEmail());
        domainUser.setImageUpdateTimestamp(user.getImageUpdateTimestamp());
        domainUser.setUserPicUploaded(user.getUserPicUploaded());
        domainUser.setUserPicLoadingGoingOn(user.getUserPicLoadingGoingOn());

        return domainUser;
    }

    public static User transformFromDomain(DomainUser domainUser) {
        if (domainUser == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }

        User user = new User();

        user.setUserId(domainUser.getUserId());
        user.setUserName(domainUser.getUserName());
        user.setDateUserCreated(domainUser.getDateUserCreated());
        user.setPhoneNumber(domainUser.getPhoneNumber());
        user.setEmail(domainUser.getEmail());
        user.setImageUpdateTimestamp(domainUser.getImageUpdateTimestamp());
        user.setUserPicUploaded(domainUser.getUserPicUploaded());
        user.setUserPicLoadingGoingOn(domainUser.getUserPicLoadingGoingOn());

        return user;
    }

    public static User transformFromDomain(DomainUser domainUser, String action){
        if (domainUser == null || action == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain or action a null value");
        }

        User user = new User();

        user.setUserId(domainUser.getUserId());

        if(UserActionConstants.UPDATE_USER_NAME.getAction().equals(action)){
            user.setUserName(domainUser.getUserName());
            user.setImageUpdateTimestamp(0L); //so that it does not pass null to server - that causes invalid json at API Gateway
        }
        if(UserActionConstants.UPDATE_USER_EMAIL.getAction().equals(action)){
            user.setEmail(domainUser.getEmail());
            user.setImageUpdateTimestamp(0L); //so that it does not pass null to server - that causes invalid json at API Gateway
        }
        if(UserActionConstants.UPDATE_USER_PROFILE_IMAGE_TS.getAction().equals(action)){
            user.setUserPicLoadingGoingOn(domainUser.getUserPicLoadingGoingOn());
            user.setUserPicUploaded(domainUser.getUserPicUploaded());
            user.setImageUpdateTimestamp(domainUser.getImageUpdateTimestamp());
        }
        if(UserActionConstants.UPDATE_USER_PROFILE_IMAGE_NO_TS.getAction().equals(action)){
            user.setImageUpdateTimestamp(domainUser.getImageUpdateTimestamp());
            user.setUserPicLoadingGoingOn(domainUser.getUserPicLoadingGoingOn());
            user.setUserPicUploaded(domainUser.getUserPicUploaded());
        }

        return user;

    }


}
