package com.java.kaboome.data.persistence;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.data.entities.UserGroupConversation;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public abstract class UserGroupConversationDao {

    @Insert(onConflict = REPLACE)
    public abstract long[] insertUserGroupConversations(UserGroupConversation... userGroupConversations);

    @Insert(onConflict = IGNORE)
    public abstract void insertUserGroupConversation(UserGroupConversation userGroupConversation);

    @Query("SELECT * FROM user_group_conversation where groupId = :group_id and userId = :user_id")
    public abstract LiveData<List<UserGroupConversation>> getUserGroupConversations(String group_id, String user_id);

    @Query("SELECT * FROM user_group_conversation where groupId = :group_id and userId = :user_id")
    public abstract List<UserGroupConversation> getNonLiveUserGroupConversations(String group_id, String user_id);
//
//    @Query("SELECT * FROM user_group_conversation WHERE userId = :user_id and groupId = :group_id and conversationId = :conversation_id")
//    public abstract LiveData<UserGroupConversation> getUserGroupConversation(String user_id, String group_id, String conversation_id);

    @Query("SELECT * FROM user_group_conversation WHERE groupId = :group_id")
    public abstract LiveData<List<UserGroupConversation>> getUserGroupConversations(String group_id);

    @Query("UPDATE user_group_conversation SET lastAccessed = :last_accessed WHERE userId = :user_id and groupId = :group_id and otherUserId = :conversationId")
    public abstract void updateUserGroupConversationLastAccessed(Long last_accessed, String user_id, String group_id, String conversationId);

    @Query("UPDATE user_group_conversation SET cacheClearTS = :new_cache_clear_TS WHERE userId = :user_id and groupId = :group_id and otherUserId = :conversation_id")
    public abstract void updateUserGroupConversationCacheClearTS(Long new_cache_clear_TS, String user_id, String conversation_id, String group_id);

    @Query("UPDATE user_group_conversation SET otherUserName = :other_user_name and otherUserRole = :other_user_role and imageUpdateTimestamp = :image_update_ts WHERE userId = :user_id and groupId = :group_id and otherUserId = :conversation_id")
    public abstract void updateUserGroupConversationDetails(String other_user_name, String other_user_role, Long image_update_ts, String user_id, String conversation_id, String group_id);

    @Query("UPDATE user_group_conversation SET isDeleted = :is_deleted WHERE userId = :user_id and groupId = :group_id and otherUserId = :conversation_id")
    public abstract void deleteUserGroupConversation(Boolean is_deleted, String user_id, String conversation_id, String group_id);

    @Query("DELETE FROM user_group_conversation WHERE userId = :user_id and groupId = :group_id and otherUserId = :conversation_id")
    public abstract void removeUserGroupConversation(String user_id, String conversation_id, String group_id);

//    @Query("SELECT * FROM user_group_conversation WHERE groupId = :group_id")
//    public abstract LiveData<List<UserGroupConversation>> getLiveDataUserGroupConversations(String group_id);

}
