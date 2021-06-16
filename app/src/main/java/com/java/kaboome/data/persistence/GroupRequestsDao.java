package com.java.kaboome.data.persistence;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.entities.Invitation;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public abstract class GroupRequestsDao {

    @Insert(onConflict = REPLACE)
    public abstract long[] insertGroupRequests(GroupRequest... groupRequests);

    @Insert(onConflict = REPLACE)
    public abstract void insertGroupRequest(GroupRequest groupRequest);

    @Query("SELECT * FROM group_requests where groupId = :group_id")
    public abstract LiveData<List<GroupRequest>> getGroupRequests(String group_id);

    @Query("SELECT * FROM group_requests WHERE userId = :user_id and groupId = :group_id")
    public abstract LiveData<GroupRequest> getGroupRequest(String user_id, String group_id);

    @Query("SELECT * FROM group_requests WHERE groupId = :group_id")
    public abstract List<GroupRequest> getGroupRequestsSingle(String group_id);

    @Query("SELECT * FROM group_requests order by groupId asc")
    public abstract LiveData<List<GroupRequest>> getAllGroupsAllRequests();

//    @Query("UPDATE group_users SET notify = :notify WHERE userId = :userId and groupId = :groupId")
//    void updateGroupUserNotification(String userId, String groupId, String notify);
//
//    @Query("UPDATE group_users SET userName = :alias, role = :role WHERE userId = :userId and groupId = :groupId")
//    void updateGroupUserAliasAndRole(String userId, String groupId, String alias, String role);
//
//    @Query("UPDATE group_users SET isAdmin = :isAdmin WHERE userId = :userId and groupId = :groupId")
//    void updateGroupUserIsAdmin(String userId, String groupId, String isAdmin);

    @Query("DELETE FROM group_requests where userId = :user_id and groupId = :group_id")
    public abstract void deleteRequest(String user_id, String group_id);

    @Query("DELETE FROM group_requests")
    public abstract void deleteAllRequests();

    @Query("DELETE FROM group_requests where groupId = :group_id")
    public abstract void deleteAllRequestsForGroup(String group_id);

    //ideally, not sure if transaction is needed here
    //As per Room doc, insert and delete are synchronous anyways
    //only Query is async and also only when it is returning a LiveData or Flowable
    //Our delete is a query and returns void, so it should be synchronous anyways,
    //but not taking chances and wrapped the whole thing in a transaction
    //so, any time new requests come, all the old ones are deleted and the new ones from the server
    //are inserted.

    @Transaction
    public long[] deleteAndInsert(String group_id, GroupRequest...requests){
        deleteAllRequestsForGroup(group_id);
        return insertGroupRequests(requests);
    }
}
