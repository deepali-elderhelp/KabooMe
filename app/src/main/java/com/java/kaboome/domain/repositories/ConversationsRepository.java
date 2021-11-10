package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;

import java.util.List;

public interface ConversationsRepository {

    LiveData<DomainResource<List<DomainUserGroupConversation>>> getConversationsForUserGroups(String groupId);

    List<DomainUserGroupConversation> getConversationsForUserGroupsFromCache(String groupId);

    void updateUserGroupConversationLastAccessed(String groupId, String conversationId, Long newLastAccessed);

    void updateUserGroupConversationCacheClearTS(String groupId, String conversationId, Long newCacheClearTS);

    void updateUserGroupConversationDetails(String groupId, String conversationId, String otherUserName, String otherUserRole, Long userImageTS);

    void deleteUserGroupConversation(String groupId, String conversationId);

    void removeUserGroupConversation(String conversationId, String groupId);

    void addNewConversation(DomainUserGroupConversation domainUserGroupConversation);

    LiveData<DomainUserGroupConversation> getUserGroupConversationFromCache(String groupId, String otherUserId);
}
