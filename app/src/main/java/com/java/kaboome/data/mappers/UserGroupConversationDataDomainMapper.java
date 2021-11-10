package com.java.kaboome.data.mappers;

import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class gets a data specific UserGroup Entity and converts it
 * to a domain specific UserGroup Entity
 * So basically UserGroup --> DomainUserGroup
 */
public class UserGroupConversationDataDomainMapper {

    public UserGroupConversationDataDomainMapper() {
    }

    public static DomainUserGroupConversation transform(UserGroupConversation userGroupConversation) {
        if (userGroupConversation == null) {
//            throw new IllegalArgumentException("Cannot transform a null value");
            return null;
        }
        DomainUserGroupConversation domainUserGroupConversation = new DomainUserGroupConversation();
        domainUserGroupConversation.setUserId(userGroupConversation.getUserId());
        domainUserGroupConversation.setGroupId(userGroupConversation.getGroupId());
        domainUserGroupConversation.setOtherUserId(userGroupConversation.getOtherUserId());
        domainUserGroupConversation.setOtherUserName(userGroupConversation.getOtherUserName());
        domainUserGroupConversation.setOtherUserRole(userGroupConversation.getOtherUserRole());
        domainUserGroupConversation.setIsOtherUserAdmin(userGroupConversation.getIsOtherUserAdmin());
        domainUserGroupConversation.setCacheClearTS(userGroupConversation.getCacheClearTS());
        domainUserGroupConversation.setLastAccessed(userGroupConversation.getLastAccessed());
        domainUserGroupConversation.setDeleted(userGroupConversation.getDeleted());
        domainUserGroupConversation.setImageUpdateTimestamp(userGroupConversation.getImageUpdateTimestamp());

        return domainUserGroupConversation;
    }

    public static UserGroupConversation transformFromDomain(DomainUserGroupConversation domainUserGroupConversation) {
        if (domainUserGroupConversation == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }
        UserGroupConversation userGroupConversation = new UserGroupConversation();
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

    public static List<DomainUserGroupConversation> transform(List<UserGroupConversation> userGroupConvCollection) {
        List<DomainUserGroupConversation> domainUserGroupConvCollection;

        if (userGroupConvCollection != null && !userGroupConvCollection.isEmpty()) {
            domainUserGroupConvCollection = new ArrayList<>();
            for (UserGroupConversation userGroupConversation : userGroupConvCollection) {
                domainUserGroupConvCollection.add(transform(userGroupConversation));
            }
        } else {
            domainUserGroupConvCollection = Collections.emptyList();
        }

        return domainUserGroupConvCollection;
    }
}
