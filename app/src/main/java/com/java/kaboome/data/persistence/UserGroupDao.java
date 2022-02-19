package com.java.kaboome.data.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.java.kaboome.data.entities.UserGroup;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserGroupDao {

    @Insert(onConflict = REPLACE)
    long[] insertUserGroups(UserGroup... userGroup);

    @Insert(onConflict = REPLACE)
    void insertUserGroup(UserGroup userGroup);

    @Query("SELECT * FROM user_groups where userId = :user_id")
    List<UserGroup> getUserGroupsNonLive(String user_id);

    @Query("SELECT * FROM user_groups where userId = :user_id")
    LiveData<List<UserGroup>> getUserGroups(String user_id);

    @Query("SELECT * FROM user_groups where userId = :user_id")
    List<UserGroup> getUserGroupsOnlyCache(String user_id);

    @Query("SELECT * FROM user_groups WHERE userId = :user_id and groupId = :group_id")
    LiveData<UserGroup> getUserGroup(String user_id, String group_id);

    @Query("SELECT * FROM user_groups WHERE userId = :user_id and groupId = :group_id")
    UserGroup getUserGroupData(String user_id, String group_id);

    @Query("UPDATE user_groups SET lastAccessed = :last_accessed WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupLastAccessed(Long last_accessed, String user_id, String group_id);


//    @Query("UPDATE user_groups SET cacheClearTS = :cache_clear_ts WHERE userId = :user_id and groupId = :group_id")
//    void updateUserGroupCacheClearTS(Long cache_clear_ts, String user_id, String group_id);
    @Query("UPDATE user_groups SET cacheClearTS = :cache_clear_ts, lastAccessed = :cache_clear_ts WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupCacheClearTSAndLastAccess(Long cache_clear_ts, String user_id, String group_id);

//    @Query("UPDATE user_groups SET lastMessageCacheTS = :last_message_ts WHERE userId = :user_id and groupId = :group_id")
//    void updateUserGroupLastMessageTS(Long last_message_ts, String user_id, String group_id);

    @Query("SELECT lastAccessed FROM user_groups WHERE userId = :user_id and groupId = :group_id")
    Long getUserGroupLastAccessed(String user_id, String group_id);

    @Query("UPDATE user_groups SET adminsLastAccessed = :last_accessed WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupAdminLastAccessed(Long last_accessed, String user_id, String group_id);

    //@Query("UPDATE user_groups SET adminsCacheClearTS = :cache_clear_ts WHERE userId = :user_id and groupId = :group_id")
    //void updateUserGroupAdminCacheClearTS(Long cache_clear_ts, String user_id, String group_id);

    /**
     * Setting last access along with cache clear TS because if the user is clearing the cache, he is in the group
     * So, might as well update the last access as well. Doing the same on the server as well otherwise it creates problems
     * like the last access is not updated when the cache is cleared...so the next time it causes issues
     */
    @Query("UPDATE user_groups SET adminsCacheClearTS = :cache_clear_ts, adminsLastAccessed = :cache_clear_ts WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupAdminCacheClearTSAndLastAccess(Long cache_clear_ts, String user_id, String group_id);

//    @Query("UPDATE user_groups SET lastMessageCacheTS = :last_message_ts WHERE userId = :user_id and groupId = :group_id")
//    void updateUserGroupLastMessageTS(Long last_message_ts, String user_id, String group_id);



    @Query("UPDATE user_groups SET groupName = :group_name WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupName(String group_name, String user_id, String group_id);

    @Query("UPDATE user_groups SET groupPicLoadingGoingOn = :group_pic_loading_going_on WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupImageLoadingGoingOn(Boolean group_pic_loading_going_on, String user_id, String group_id);

    @Query("UPDATE user_groups SET groupPicUploaded = :group_pic_uploaded WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupImageUploaded(Boolean group_pic_uploaded, String user_id, String group_id);

    @Query("UPDATE user_groups SET privateGroup = :private_group WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupPrivacy(boolean private_group, String user_id, String group_id);

    @Query("UPDATE user_groups SET expiry = :group_expiry WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupExpiry(Long group_expiry, String user_id, String group_id);

    @Query("UPDATE user_groups SET imageUpdateTimestamp = :group_imageTS WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupImageTS(Long group_imageTS, String user_id, String group_id);

    @Query("UPDATE user_groups SET notify = :notify WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupNotify(String notify, String user_id, String group_id);

    @Query("UPDATE user_groups SET groupAdminRole = :user_role, alias = :user_alias WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupRoleAndAlias(String user_role, String user_alias, String user_id, String group_id);

    @Query("UPDATE user_groups SET isDeleted = :is_deleted WHERE userId = :user_id and groupId = :group_id")
    void updateUserGroupIsDeleted(Boolean is_deleted, String user_id, String group_id);

//    @Query("UPDATE user_groups SET adminsLastAccessed = :admins_last_accessed WHERE userId = :user_id and groupId = :group_id")
//    void updateUserGroupAdminsLastAccess(Long admins_last_accessed, String user_id, String group_id);
//
//    @Query("UPDATE user_groups SET adminsCacheClearTS = :admins_cache_clear_TS WHERE userId = :user_id and groupId = :group_id")
//    void updateUserGroupAdminsCacheClearTS(Long admins_cache_clear_TS, String user_id, String group_id);

    @Query("DELETE FROM user_groups WHERE userId = :user_id and groupId = :group_id")
    void deleteUserGroup(String user_id, String group_id);

    @Query("DELETE FROM user_groups WHERE userId = :user_id and isDeleted = :is_deleted")
    void deleteUserGroupsWithIsDeletedTrue(String user_id, Boolean is_deleted);

}
