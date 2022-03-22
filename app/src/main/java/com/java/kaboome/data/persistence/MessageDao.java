package com.java.kaboome.data.persistence;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.java.kaboome.data.entities.Message;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {

    @Insert(onConflict = REPLACE)
    long[] insertMessages(Message... messages);

    @Insert(onConflict = REPLACE)
    void insertMessage(Message message);

    @Insert(onConflict = IGNORE)
    void insertMessageIfDoesNotExist(Message message);

//    @Query("SELECT * FROM messages where groupId = :group_id")
//    LiveData<List<Message>> getMessagesForGroup(String group_id);

//    @Query("SELECT * FROM messages where groupId = :group_id and sentAt < :last_sent ORDER BY sentAt DESC LIMIT 15")
//    LiveData<List<Message>> getMessagesForGroupBeforeLastSent(String group_id, long last_sent);
//
    @Query("SELECT * FROM messages WHERE messageId = :message_id")
    Message getMessage(String message_id);
//
//    @Query("SELECT * FROM messages where groupId = :group_id ORDER BY sentAt ASC")
//    DataSource.Factory<Integer, Message> getMessagesForGroup(String group_id);

//    @Query("SELECT * FROM messages where groupId = :group_id ORDER BY sentAt ASC")
//    List<Message> getAllMessagesForGroup(String group_id);

//    @Query("SELECT * FROM messages where groupId = :group_id ORDER BY sentAt DESC LIMIT 1")
//    Message getLastMessageForGroup(String group_id);



    @Query("SELECT * FROM messages where groupId = :group_id and sentTo = 'InterAdmin' ORDER BY sentAt DESC LIMIT 1")
    Message getLastInterAdminMessageForGroup(String group_id);

//    @Query("SELECT * FROM messages where groupId = :group_id and sentTo = :userId ORDER BY sentAt DESC LIMIT 1")
//    Message getLastUserAdminMessageForGroup(String group_id, String userId);

//    @Query("SELECT * FROM messages where groupId = :group_id AND (NOT coalesce(isDeletedLocally, 0) OR NOT coalesce(isDeleted, 0)) ORDER BY sentAt DESC LIMIT 1")
@Query("SELECT * FROM messages where groupId = :group_id AND NOT (coalesce(isDeletedLocally, 0) OR coalesce(isDeleted, 0)) ORDER BY sentAt DESC LIMIT 1")
    LiveData<Message> getLastMessageForWholeGroupAsLiveData(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id and sentTo = 'Group' AND NOT (coalesce(isDeletedLocally, 0) OR coalesce(isDeleted, 0)) ORDER BY sentAt DESC LIMIT 1")
    LiveData<Message> getLastMessageForOnlyGroupAsLiveData(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id and sentTo != 'Group' AND NOT (coalesce(isDeletedLocally, 0) OR coalesce(isDeleted, 0)) ORDER BY sentAt DESC LIMIT 1")
    LiveData<Message> getLastMessageForAllConvsAsLiveData(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = :user_id AND NOT (coalesce(isDeletedLocally, 0) OR coalesce(isDeleted, 0)) ORDER BY sentAt DESC LIMIT 1")
    LiveData<Message> getLastMessageForConvAsLiveData(String group_id, String user_id);

//    @Query("SELECT * FROM messages where groupId = :group_id ORDER BY sentAt DESC LIMIT 1")
//    Message getLastMessageForWholeGroup(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND NOT (coalesce(isDeletedLocally, 0) OR coalesce(isDeleted, 0)) ORDER BY sentAt DESC LIMIT 1")
    Message getLastMessageForWholeGroup(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id and sentTo = 'Group' AND NOT (coalesce(isDeletedLocally, 0) OR coalesce(isDeleted, 0))ORDER BY sentAt DESC LIMIT 1")
    Message getLastMessageForOnlyGroup(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id and sentTo != 'Group' AND NOT (coalesce(isDeletedLocally, 0) OR coalesce(isDeleted, 0))ORDER BY sentAt DESC LIMIT 1")
    Message getLastMessageForAllConvs(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = :user_id AND NOT (coalesce(isDeletedLocally, 0) OR coalesce(isDeleted, 0)) ORDER BY sentAt DESC LIMIT 1")
    Message getLastMessageForConv(String group_id, String user_id);

    @Query("SELECT * FROM messages where groupId = :group_id ORDER BY sentAt DESC LIMIT 1")
    Message getLastMessageForWholeGroupIncludingDeleted(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id and sentTo = 'Group' ORDER BY sentAt DESC LIMIT 1")
    Message getLastMessageForOnlyGroupIncludingDeleted(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id and sentTo != 'Group' ORDER BY sentAt DESC LIMIT 1")
    Message getLastMessageForAllConvsIncludingDeleted(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = :user_id ORDER BY sentAt DESC LIMIT 1")
    Message getLastMessageForConvIncludingDeleted(String group_id, String user_id);



//    @Query("SELECT * FROM messages where groupId = :group_id AND sentAt > :lastAccessTime ORDER BY sentAt ASC")
//    LiveData<List<Message>> getGroupMessagesSentAfter(String group_id, Long lastAccessTime);

//    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = :user_id AND sentAt > :lastAccessTime ORDER BY sentAt ASC")
//    LiveData<List<Message>> getConversationMessagesSentAfter(String group_id, String user_id, Long lastAccessTime);

//    @Query("SELECT * FROM messages where groupId = :group_id AND sentAt > :lastAccessTime ORDER BY sentAt ASC")
//    List<Message> getGroupMessagesSentAfterCache(String group_id, Long lastAccessTime);

//    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = :user_id AND sentAt > :lastAccessTime ORDER BY sentAt ASC")
//    List<Message> getConversationMessagesSentAfterCache(String group_id, String user_id, Long lastAccessTime);



    @Query("SELECT * FROM messages where groupId = :group_id AND unread = 0")
    LiveData<List<Message>> getWholeGroupNetUnreadMessagesLiveData(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND unread = 0")
    List<Message> getWholeGroupNetUnreadMessages(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = :sent_to AND unread = 0")
    LiveData<List<Message>> getGroupConversationNetUnreadMessagesLiveData(String group_id, String sent_to);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = :sent_to AND unread = 0")
    List<Message> getGroupConversationNetUnreadMessages(String group_id, String sent_to);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo != 'Group' AND unread = 0")
    LiveData<List<Message>> getGroupAllConversationNetUnreadMessagesLiveData(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo != 'Group' AND unread = 0")
    List<Message> getGroupAllConversationNetUnreadMessages(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = 'Group' AND unread = 0")
    LiveData<List<Message>> getOnlyGroupNetUnreadMessagesLiveData(String group_id);

    @Query("SELECT * FROM messages where groupId = :group_id AND sentTo = 'Group' AND unread = 0")
    List<Message> getOnlyGroupNetUnreadMessages(String group_id);



    @Delete
    void delete(Message message);

    @Query("DELETE FROM messages where groupId = :group_id and sentTo = 'Group'")
    void clearMessagesOfGroup(String group_id);

    @Query("DELETE FROM messages where groupId = :group_id and sentTo = :otherUserId")
    void clearMessagesOfGroupConversation(String group_id, String otherUserId);

    @Query("UPDATE messages SET messageText = :deleteMessageText WHERE messageId = :message_id")
    void deleteMessageText(String message_id, String deleteMessageText);

    @Query("UPDATE messages SET waitingToBeDeleted = :waiting_to_be_deleted WHERE messageId = :message_id")
    void updateMessageWaitingToBeDeletedStatus(String message_id, boolean waiting_to_be_deleted);

//    @Query("UPDATE messages SET hasAttachment = :has_attachments, attachmentUploaded = :attachment_uploaded, attachmentDownloaded = :attachment_downloaded, attachmentPath = :attachment_path where messageId = :message_id")
//    void updateMessageAttachmentData(String message_id, boolean has_attachments, boolean attachment_uploaded, boolean attachment_downloaded, String attachment_path);

    @Query("UPDATE messages SET hasAttachment = :has_attachments, attachmentUploaded = :attachment_uploaded,  attachmentLoadingGoingOn = :isLoading, attachmentMime = :mime_type, attachmentUri = :attachment_uri where messageId = :message_id")
    void updateMessageAttachmentData(String message_id, boolean has_attachments, boolean attachment_uploaded, boolean isLoading, String mime_type, String attachment_uri);

    @Query("UPDATE messages SET attachmentLoadingGoingOn = :attachment_loading_going_on where messageId = :message_id")
    void updateMessageAttachmentUploadStatus(String message_id, boolean attachment_loading_going_on);

    @Query("UPDATE messages SET loadingProgress = :loading_progress where messageId = :message_id")
    void updateMessageLoadingProgress(String message_id, int loading_progress);

    @Query("UPDATE messages SET unread = 1 where groupId = :group_id and sentTo = :sent_to")
    void updateMessagesToRead(String group_id, String sent_to);

//    @Query("UPDATE messages SET isDeleted = 1, messageText = :message_text where groupId = :group_id and sentTo = :sent_to and messageId = :message_id")
//    void setLocalMessageToDelete(String message_text, String group_id, String sent_to, String message_id);

    @Query("UPDATE messages SET isDeletedLocally = 1, messageText = :message_text where groupId = :group_id and sentTo = :sent_to and messageId = :message_id")
    void setLocalMessageToDelete(String message_text, String group_id, String sent_to, String message_id);


    //UPDATE recipes SET title = :title, publisher = :publisher, image_url = :image_url, social_rank = :social_rank " +
    //            "WHERE recipe_id = :recipe_id
//    @Query("UPDATE messages")
//    void deleteMessage(String message_id);

    /**
     * @Query(
     *     "SELECT id, name, description,created " +
     *             "FROM   (SELECT id, name, description, created, created AS sort " +
     *             "        FROM   reports " +
     *             "
     *             "        UNION " +
     *             "        SELECT '00000000-0000-0000-0000-000000000000' as id, Substr(created, 0, 9) as name, '' as description, Substr(created, 0, 9) || '000000' AS created, Substr(created, 0, 9) || '256060' AS sort " +
     *             "        FROM   reports " +
     *             "
     *             "        GROUP  BY Substr(created, 0, 9)) " +
     *             "ORDER  BY sort DESC ")
     */

//    @Query(
//            "SELECT groupId, messageId, sentAt, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "FROM (SELECT groupId, messageId, sentAt, sentAt AS sort, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "FROM messages where groupId = :group_id "+
//                    "UNION "+
//                    "SELECT 'DateHeaderGroup' as groupId, messageId as messageId, sentAt as sentAt , sentAt as sort, '00' as sentBY, '00' as alias, '00' as isAdmin, '00' as role, 0 as notify, '00' as messageText, 'false' as waitingToBeDeleted, 'false' as  uploadedToServer "+
//                    "FROM messages where groupId = :group_id "+
//                    "GROUP BY substr(cast(sentAt as text), 1, 5))"+
//                    "ORDER BY sort ASC"
//    )
//    @Query(
//            "SELECT groupId, messageId, sentAt, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "FROM (" +
//                    "SELECT 'DateHeaderGroup' as groupId, messageId as messageId, sentAt as sentAt , (sentAt-1) as sort, sentBy as sentBy, alias as alias, isAdmin as isAdmin, role as role, notify as notify, messageText as messageText, waitingToBeDeleted as waitingToBeDeleted, uploadedToServer as  uploadedToServer "+
//                    "FROM messages where groupId = :group_id "+
//                    "GROUP BY substr(cast(sentAt as text), 1, 5)"+
//                    "UNION "+
//                    "SELECT groupId, messageId, sentAt, sentAt AS sort, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "FROM messages where groupId = :group_id )"+
//                    "ORDER BY sort ASC"
//    )
//    @Query(
//            "SELECT groupId, messageId, sentAt, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "FROM (" +
//                    "SELECT 'DateHeaderGroup' as groupId, messageId as messageId, sentAt as sentAt , (sentAt-1) as sort, sentBy as sentBy, strftime('%Y %m %d', datetime(sentAt/1000, 'unixepoch'), 'localtime') as alias, isAdmin as isAdmin, strftime('%Y %m %d', sentAt/1000, 'unixepoch') as role, notify as notify, messageText as messageText, waitingToBeDeleted as waitingToBeDeleted, uploadedToServer as  uploadedToServer "+
//                    "FROM messages where groupId = :group_id "+
//                    "GROUP BY strftime('%Y %m %d', sentAt/1000, 'unixepoch')"+
//                    "UNION "+
//                    "SELECT groupId, messageId, sentAt, sentAt AS sort, sentBy, strftime('%Y %m %d', datetime(sentAt/1000, 'unixepoch'), 'localtime') as alias, isAdmin, strftime('%Y %m %d', sentAt/1000, 'unixepoch') as role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "FROM messages where groupId = :group_id )"+
//                    "ORDER BY sort ASC"
//    )
    //Last Working
//    @Query(
//            "SELECT groupId, messageId, sentAt, sort, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "FROM (" +
//                    "SELECT 'DateHeaderGroup' as groupId, messageId as messageId, sentAt as sentAt , (sentAt-1) as sort, sentBy as sentBy, strftime('%Y %m %d', datetime(sentAt/1000, 'unixepoch'), 'localtime') as alias, isAdmin as isAdmin, role as role, notify as notify, messageText as messageText, waitingToBeDeleted as waitingToBeDeleted, uploadedToServer as  uploadedToServer "+
//                    "FROM messages where groupId = :group_id "+
//                    "GROUP BY strftime('%Y %m %d', datetime(sentAt/1000, 'unixepoch'), 'localtime')"+
//                    "UNION "+
//                    "SELECT groupId, messageId, sentAt, sentAt AS sort, sentBy,  strftime('%Y %m %d', datetime(sentAt/1000, 'unixepoch'), 'localtime') as alias, isAdmin, role as role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "FROM messages where groupId = :group_id )"+
//                    "ORDER BY sort DESC"
//    )
//
//
//    DataSource.Factory<Integer, Message> getMessagesForGroup(String group_id);

    /**
     *
     * @param group_id
     * @return
     *
     * After a long time, came to the knowledge that min(sentAt)-1 is very important, otherwise, the date sorting for the new dates(new messages arriving)
     * was messing up since now the header was having a date which was not the minimum and hence it was showing in wrong place
     * Tried to create, debug in online sql editor, with creating a table with dummy data and reproducing the error there.
     * Finally, reproduced the issue there and viola!! after fixing that there, tried it here and it worked!!
     * Link to sql online editor -
     * https://sqliteonline.com/
     */
//    @Query(
//
//            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer " +
//                    "FROM messages where groupId = :group_id "+
//                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
//                    "  UNION " +
//                    "  select groupId, messageId, sentAt, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer "+
//                    "  FROM messages where groupId = :group_id "+
//                    "  ORDER BY sentAt DESC"
//
//    )

    //added attachments fields

//    @Query(
//
//            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentDownloaded, attachmentPath " +
//                    "FROM messages where groupId = :group_id "+
//                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
//                    "  UNION " +
//                    "  select groupId, messageId, sentAt, sentBy, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentDownloaded, attachmentPath "+
//                    "  FROM messages where groupId = :group_id "+
//                    "  ORDER BY sentAt DESC"
//
//    )
//    DataSource.Factory<Integer, Message> getMessagesForGroup(String group_id);

//    @Query(
//
//            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime " +
//                    "FROM messages where groupId = :group_id "+
//                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
//                    "  UNION " +
//                    "  select groupId, messageId, sentAt, sentBy, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime "+
//                    "  FROM messages where groupId = :group_id "+
//                    "  ORDER BY sentAt DESC"
//
//    )
//    DataSource.Factory<Integer, Message> getMessagesForGroup(String group_id);

    @Query(

            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, tnBlob " +
                    "FROM messages where groupId = :group_id "+
                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
                    "  UNION " +
                    "  select groupId, messageId, sentAt, sentBy, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, tnBlob "+
                    "  FROM messages where groupId = :group_id "+
                    "  ORDER BY sentAt DESC"

    )
    DataSource.Factory<Integer, Message> getMessagesForGroup(String group_id);

//    @Query(
//
//            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, tnBlob " +
//                    "FROM messages where groupId = :group_id and sentTo = 'Group'"+
//                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
//                    "  UNION " +
//                    "  select groupId, messageId, sentAt, sentBy, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, tnBlob "+
//                    "  FROM messages where groupId = :group_id and sentTo = 'Group'"+
//                    "  ORDER BY sentAt DESC"
//
//    )
//    DataSource.Factory<Integer, Message> getGroupMessagesForGroup(String group_id);

//    @Query(
//
//            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, attachmentUri, tnBlob, unread " +
//                    "FROM messages where groupId = :group_id and sentTo = 'Group'"+
//                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
//                    "  UNION " +
//                    "  select groupId, messageId, sentAt, sentBy, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, attachmentUri, tnBlob, unread "+
//                    "  FROM messages where groupId = :group_id and sentTo = 'Group'"+
//                    "  ORDER BY sentAt DESC"
//
//    )
//    DataSource.Factory<Integer, Message> getGroupMessagesForGroup(String group_id);


    @Query(

            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, sentTo, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, attachmentUri, tnBlob, unread, sentToImageTS, sentToUserName, sentToUserRole " +
                    "FROM messages where groupId = :group_id and sentTo = 'Group' AND NOT coalesce(isDeletedLocally, 0)"+
                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
                    "  UNION " +
                    "  select groupId, messageId, sentAt, sentBy, sentTo, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, attachmentUri, tnBlob, unread, sentToImageTS, sentToUserName, sentToUserRole "+
                    "  FROM messages where groupId = :group_id and sentTo = 'Group'AND NOT coalesce(isDeletedLocally, 0)"+
                    "  ORDER BY sentAt DESC"

    )
    DataSource.Factory<Integer, Message> getGroupMessagesForGroup(String group_id);


    @Query(

            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, sentTo, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, attachmentUri, tnBlob, unread, sentToImageTS, sentToUserName, sentToUserRole " +
                    "FROM messages where groupId = :group_id  and sentTo = 'InterAdmin' AND NOT coalesce(isDeletedLocally, 0)"+
                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
                    "  UNION " +
                    "  select groupId, messageId, sentAt, sentBy, sentTo, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, attachmentUri, tnBlob, unread, sentToImageTS, sentToUserName, sentToUserRole "+
                    "  FROM messages where groupId = :group_id and sentTo = 'InterAdmin' AND NOT coalesce(isDeletedLocally, 0)"+
                    "  ORDER BY sentAt DESC"

    )
    DataSource.Factory<Integer, Message> getAdminMessagesForGroup(String group_id);

    @Query(

            "select  'DateHeaderGroup' as groupId, messageId, (min(sentAt)-1) as sentAt, sentBy, sentTo, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, attachmentUri, tnBlob, sentToImageTS, sentToUserName, sentToUserRole " +
                    "FROM messages where groupId = :group_id  and sentTo = :user_id AND NOT coalesce(isDeletedLocally, 0)"+
                    "  group by substr(CAST(date(sentAt/1000, 'unixepoch', 'localtime') as text), 1, 10)" +
                    "  UNION " +
                    "  select groupId, messageId, sentAt, sentBy, sentTo, sentByImageTS, alias, isAdmin, role, notify, messageText, waitingToBeDeleted, unread, isDeleted, uploadedToServer, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, loadingProgress, attachmentExtension, attachmentMime, attachmentUri, tnBlob, sentToImageTS, sentToUserName, sentToUserRole "+
                    "  FROM messages where groupId = :group_id and sentTo = :user_id AND NOT coalesce(isDeletedLocally, 0)"+
                    "  ORDER BY sentAt DESC"

    )
    DataSource.Factory<Integer, Message> getUserAdminMessagesForGroup(String group_id, String user_id);



}
