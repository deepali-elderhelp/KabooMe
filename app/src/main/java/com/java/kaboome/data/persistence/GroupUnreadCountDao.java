//package com.java.kaboome.data.persistence;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.Query;
//
//
//import com.java.kaboome.data.entities.GroupUnreadCount;
//
//import java.util.List;
//
//import static androidx.room.OnConflictStrategy.REPLACE;
//
///**
// * A little misleading name, other than unreadcount, it also has info on last message text
// */
//@Dao
//public interface GroupUnreadCountDao {
//
//    @Insert(onConflict = REPLACE)
//    void insertGroup(GroupUnreadCount groupUnreadCount);
//
//    @Query("SELECT * FROM group_unread_count")
//    LiveData<List<GroupUnreadCount>> getGroupsAndCounts();
//
//    @Query("SELECT * FROM group_unread_count WHERE groupId = :group_id")
//    GroupUnreadCount getGroupUnread(String group_id);
//
//    @Query("SELECT * FROM group_unread_count WHERE groupId = :group_id")
//    LiveData<GroupUnreadCount> getGroupUnreadCountLiveData(String group_id);
//
//    @Query("UPDATE group_unread_count SET countOfUnreadMessages = countOfUnreadMessages + 1, lastMessageText = :lastMessageTextPassed, lastMessageSentBy = :lastMessageSentByPassed WHERE groupId = :group_id")
//    void incrementGroupUnreadCount(String group_id, String lastMessageTextPassed, String lastMessageSentByPassed);
//
//    @Query("UPDATE group_unread_count SET countOfUnreadMessages = 0, lastMessageText = :lastMessageText, lastMessageSentBy = :lastMessageSentByPassed WHERE groupId = :group_id")
//    void resetGroupUnreadCountAndLastMessage(String group_id, String lastMessageText, String lastMessageSentByPassed);
//
//    @Query("UPDATE group_unread_count SET countOfUnreadMessages = 0 WHERE groupId = :group_id")
//    void resetGroupUnreadCountOnly(String group_id);
//
////    @Query("UPDATE group_unread_count SET countOfUnreadMessages = 0 WHERE groupId = :group_id")
////    void resetGroupUnreadLastMessageOnly(String group_id);
//
//
//}
