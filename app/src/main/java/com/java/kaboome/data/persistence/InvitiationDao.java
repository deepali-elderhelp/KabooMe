package com.java.kaboome.data.persistence;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.java.kaboome.data.entities.Invitation;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public abstract class InvitiationDao {
    @Insert(onConflict = REPLACE)
    public abstract  long[] insertInvitations(Invitation... invitations);

    @Insert(onConflict = REPLACE)
    public abstract void insertInvitation(Invitation invitation);

    @Query("SELECT * FROM user_invitations")
    public abstract LiveData<List<Invitation>> getInvitations();

    @Query("SELECT * FROM user_invitations where groupId = :group_id")
    public abstract LiveData<Invitation> getInvitation(String group_id);

    @Query("DELETE FROM user_invitations where invitationStatus = :invitation_status")
    public abstract void deleteInvitations(String invitation_status);

    @Query("DELETE FROM user_invitations where groupId = :group_id")
    public abstract void deleteInvitationForGroup(String group_id);

    @Query("DELETE FROM user_invitations")
    public abstract void deleteAllInvitations();

    //ideally, not sure if transaction is needed here
    //As per Room doc, insert and delete are synchronous anyways
    //only Query is async and also only when it is returning a LiveData or Flowable
    //Our delete is a query and returns void, so it should be synchronous anyways,
    //but not taking chances and wrapped the whole thing in a transaction
    //so, any time new invitations come, all the old ones are deleted and the new ones from the server
    //are inserted.

    @Transaction
    public long[] deleteAndInsert(Invitation...invitations){
        deleteAllInvitations();
        return insertInvitations(invitations);
    }

//    @Query("SELECT * FROM user_invitations WHERE userId = :user_id and groupId = :group_id")
//    LiveData<Invitation> getInvitation(String user_id, String group_id);
}
