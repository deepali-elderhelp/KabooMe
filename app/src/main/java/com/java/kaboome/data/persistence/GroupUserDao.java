package com.java.kaboome.data.persistence;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.java.kaboome.data.entities.GroupUser;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface GroupUserDao {

    @Insert(onConflict = REPLACE)
    long[] insertGroupUsers(GroupUser... groupUsers);

    @Insert(onConflict = REPLACE)
    void insertGroupUser(GroupUser groupUser);

    @Query("SELECT * FROM group_users where groupId = :group_id AND NOT coalesce(isDeleted, 0)")
    LiveData<List<GroupUser>> getGroupUsers(String group_id);

    @Query("SELECT * FROM group_users WHERE userId = :user_id and groupId = :group_id")
    LiveData<GroupUser> getGroupUsers(String user_id, String group_id);

    @Query("SELECT * FROM group_users WHERE userId = :user_id and groupId = :group_id")
    GroupUser getGroupUserFromCache(String user_id, String group_id);

    @Query("UPDATE group_users SET notify = :notify WHERE userId = :userId and groupId = :groupId")
    void updateGroupUserNotification(String userId, String groupId, String notify);

    @Query("UPDATE group_users SET groupUserPicUploaded = :group_user_pic_uploaded WHERE userId = :userId and groupId = :group_id")
    void updateGroupUserImageUploaded(String userId, String group_id, Boolean group_user_pic_uploaded);

    @Query("UPDATE group_users SET groupUserPicUploaded = :group_user_pic_uploaded, groupUserPicLoadingGoingOn = :group_user_pic_loading_going_on WHERE userId = :userId and groupId = :group_id")
    void updateGroupUserImageUploadingData(String userId, String group_id, Boolean group_user_pic_uploaded, Boolean group_user_pic_loading_going_on);

    @Query("UPDATE group_users SET userName = :alias, role = :role, groupUserPicUploaded = :group_user_pic_uploaded, groupUserPicLoadingGoingOn = :group_user_pic_loading_going_on, imageUpdateTimestamp = :image_update_timestamp WHERE userId = :userId and groupId = :group_id")
    void updateGroupUserAliasRoleAndmageUploadingData(String userId, String group_id, String alias, String role, Boolean group_user_pic_uploaded, Boolean group_user_pic_loading_going_on, Long image_update_timestamp);

    @Query("UPDATE group_users SET userName = :alias, role = :role, groupUserPicUploaded = :group_user_pic_uploaded, groupUserPicLoadingGoingOn = :group_user_pic_loading_going_on WHERE userId = :userId and groupId = :group_id")
    void updateGroupUserAliasRoleAndmageUploadingDataNoTS(String userId, String group_id, String alias, String role, Boolean group_user_pic_uploaded, Boolean group_user_pic_loading_going_on);

    @Query("UPDATE group_users SET userName = :alias, role = :role, imageUpdateTimestamp = :image_update_timestamp WHERE userId = :userId and groupId = :groupId")
    void updateGroupUserAliasAndRole(String userId, String groupId, String alias, String role, Long image_update_timestamp);

    @Query("UPDATE group_users SET isAdmin = :isAdmin WHERE userId = :userId and groupId = :groupId")
    void updateGroupUserIsAdmin(String userId, String groupId, String isAdmin);

    @Query("UPDATE group_users SET isDeleted = :is_deleted WHERE userId = :userId and groupId = :groupId")
    void updateGroupUserIsDeleted(Boolean is_deleted, String userId, String groupId);

    @Query("DELETE FROM group_users where userId = :user_id and groupId = :group_id")
    void removeGroupUser(String user_id, String group_id);
}
