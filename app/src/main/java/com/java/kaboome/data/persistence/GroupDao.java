package com.java.kaboome.data.persistence;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.data.entities.Group;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class GroupDao {
//    @Insert(onConflict = IGNORE) //shouldnt this be replace too? Search results??
//    long[] insertGroups(Group... group);
//not sure why it was ignore, changing it to replace
    @Insert(onConflict = REPLACE)
    public abstract long[] insertGroups(Group...group);

    @Insert(onConflict = REPLACE)
    public abstract void insertGroup(Group group);


    @Query("SELECT * FROM groups WHERE groupId = :group_id")
    public abstract LiveData<Group> getGroup(String group_id);

    @Query("UPDATE groups SET groupName = :groupName WHERE groupId = :groupId")
    public abstract void updateGroupName(String groupName, String groupId);

    @Query("UPDATE groups SET groupPicLoadingGoingOn = :group_pic_loading_going_on WHERE groupId = :group_id")
    public abstract void updateGroupImageLoadingGoingOn(Boolean group_pic_loading_going_on, String group_id);

    @Query("UPDATE groups SET groupPicUploaded = :group_pic_uploaded WHERE groupId = :group_id")
    public abstract void updateGroupImageUploaded(Boolean group_pic_uploaded, String group_id);

    @Query("UPDATE groups SET groupPicUploaded = :group_pic_uploaded, groupPicLoadingGoingOn = :group_pic_loading_going_on WHERE groupId = :group_id")
    public abstract void updateGroupImageUploadingData(Boolean group_pic_uploaded, Boolean group_pic_loading_going_on, String group_id);

    @Query("UPDATE groups SET groupName = :group_name, groupPicUploaded = :group_pic_uploaded, groupPicLoadingGoingOn = :group_pic_loading_going_on WHERE groupId = :group_id")
    public abstract void updateGroupINameAndmageUploadingData(String group_name, Boolean group_pic_uploaded, Boolean group_pic_loading_going_on, String group_id);

    @Query("UPDATE groups SET privateGroup = :groupPrivacy WHERE groupId = :groupId")
    public abstract void updateGroupPrivacy(boolean groupPrivacy, String groupId);

    @Query("UPDATE groups SET currentUserStatusForGroup = :current_user_status_for_group WHERE groupId = :groupId")
    public abstract void updateCurrentUserStatusForGroup(UserGroupStatusConstants current_user_status_for_group, String groupId);

    @Query("UPDATE groups SET groupDescription = :groupDescription WHERE groupId = :groupId")
    public abstract void updateGroupDescription(String groupDescription, String groupId);

    @Query("UPDATE groups SET expiry = :groupExpiry WHERE groupId = :groupId")
    public abstract void updateGroupExpiry(Long groupExpiry, String groupId);

    @Query("UPDATE groups SET imageUpdateTimestamp = :imageUpdateTS WHERE groupId = :groupId")
    public abstract void updateGroupImageTS(Long imageUpdateTS, String groupId);

    @Query("UPDATE groups SET groupName = :groupName, groupDescription = :groupDescription, createdByAlias = :createdByAlias, privateGroup = :privateGroup,  currentUserStatusForGroup = :currentUserStatusForGroup WHERE groupId = :groupId")
    public abstract void updateFromSearchResults(String groupId, String groupName, String groupDescription, String createdByAlias, Boolean privateGroup, UserGroupStatusConstants currentUserStatusForGroup);

//    @Query("DELETE FROM groups WHERE groupId = :groupId")
//    void deleteGroup(String groupId);

    @Query("UPDATE groups SET isDeleted = :isDeleted WHERE groupId = :groupId")
    public abstract void setGroupToDeleted(Boolean isDeleted, String groupId);

//    @Query("UPDATE groups SET currentUserStatusForGroup = :current_user_status WHERE groupId = :groupId")
//    void updateGroupCurrentStatus(UserGroupStatusConstants current_user_status, String groupId);

    @Query("UPDATE groups SET openToRequests = :open_to_requests WHERE groupId = :groupId")
    public abstract void updateGroupOpenToRequests(Boolean open_to_requests, String groupId);

    @Query("SELECT * FROM groups WHERE lowerCase LIKE :partialOrCompleteGroupName AND NOT coalesce(isDeleted, 0)")
    public abstract LiveData<List<Group>> getGroupsBySearchTextName(String partialOrCompleteGroupName);

    @Query("SELECT * FROM groups WHERE groupId LIKE :partialOrCompleteGroupId AND NOT coalesce(isDeleted, 0)")
    public abstract LiveData<List<Group>> getGroupsBySearchTextId(String partialOrCompleteGroupId);

    @Query("DELETE FROM groups WHERE groupId = :groupId")
    public abstract void deleteGroup(String groupId);

    @Query("DELETE FROM groups WHERE currentUserStatusForGroup NOT IN (:member_statuses)")
    public abstract void deleteUnMemberedGroups(UserGroupStatusConstants[] member_statuses);

    @Transaction
    public long[] deleteAndInsert(Group...groups){
        deleteUnMemberedGroups(new UserGroupStatusConstants[]{UserGroupStatusConstants.ADMIN_MEMBER, UserGroupStatusConstants.REGULAR_MEMBER});
        return insertGroups(groups);
    }
}
