//package com.java.kaboome.data.repositories;
//
//import androidx.arch.core.util.Function;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.Transformations;
//
//import com.java.kaboome.data.entities.GroupUnreadCount;
//import com.java.kaboome.data.executors.AppExecutors2;
//import com.java.kaboome.data.mappers.GroupUnreadDomainUnreadMapper;
//import com.java.kaboome.data.persistence.GroupUnreadCountDao;
//import com.java.kaboome.domain.entities.DomainGroupUnreadData;
//import com.java.kaboome.domain.entities.DomainMessage;
//import com.java.kaboome.domain.repositories.GroupsUnreadCountRepository;
//import com.java.kaboome.helpers.AppConfigHelper;
//
//import java.util.List;
//
//public class DataGroupsUnreadCountRepository implements GroupsUnreadCountRepository {
//
//    private static final String TAG = "KMDataGrpsUnredCountRepo";
//
//    private static DataGroupsUnreadCountRepository instance;
//    private GroupUnreadCountDao groupUnreadCountDao;
//
//    public DataGroupsUnreadCountRepository() {
//        groupUnreadCountDao = AppConfigHelper.getKabooMeDatabaseInstance().getGroupUnreadCountDao();
//    }
//
//    public static DataGroupsUnreadCountRepository getInstance(){
//        if(instance == null){
//            instance = new DataGroupsUnreadCountRepository();
//        }
//        return instance;
//    }
//
//    @Override
//    public LiveData<List<DomainGroupUnreadData>> getGroupsUnreadCountList() {
//        return Transformations.map(groupUnreadCountDao.getGroupsAndCounts(), new Function<List<GroupUnreadCount>, List<DomainGroupUnreadData>>() {
//            @Override
//            public List<DomainGroupUnreadData> apply(List<GroupUnreadCount> input) {
//                return GroupUnreadDomainUnreadMapper.transform(input);
//            }
//        });
//    }
//
//    @Override
//    public LiveData<DomainGroupUnreadData> getGroupUnreadCount(final String groupId) {
//        return Transformations.map(groupUnreadCountDao.getGroupUnreadCountLiveData(groupId), new Function<GroupUnreadCount, DomainGroupUnreadData>() {
//            @Override
//            public DomainGroupUnreadData apply(GroupUnreadCount input) {
//                if(input == null) {//the unread count for group is not there yet
//                    return new DomainGroupUnreadData(groupId, 0, "", "");
//                }
//                else{
//                    return GroupUnreadDomainUnreadMapper.transform(input);
//                }
//
//            }
//        });
//    }
//
//    /**
//     * Expecting this to be called from fcm service and user while sending a message in the group
//     * @param domainMessage
//     */
//    @Override
//    public void addNewUnreadMessage(DomainMessage domainMessage) {
//        //add the message to unreadcount db
//        insertNewMessageInGroupUnread(domainMessage.getGroupId(), domainMessage.getMessageText(), domainMessage.getAlias());
//    }
//
//    @Override
//    public void resetGroupsUnreadCountList(String groupId) {
//        resetUnreadCountListForGroup(groupId);
//    }
//
//
//    private void resetUnreadCountListForGroup(final String groupId) {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                GroupUnreadCount groupUnreadCount = groupUnreadCountDao.getGroupUnread(groupId);
//                if(groupUnreadCount == null){
//                    groupUnreadCountDao.insertGroup(new GroupUnreadCount(groupId, 0, "", ""));
//                }
//                else{
//                    groupUnreadCountDao.resetGroupUnreadCountOnly(groupId);
//                }
//            }
//        });
//    }
//
//    private void insertNewMessageInGroupUnread(final String groupId, final String lastMessageText, final String lastMessageSentBy){
//
//        //see if the group Id exists
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                GroupUnreadCount groupUnreadCount = groupUnreadCountDao.getGroupUnread(groupId);
//                if(groupUnreadCount == null){
//                    groupUnreadCountDao.insertGroup(new GroupUnreadCount(groupId, 1, lastMessageText, lastMessageSentBy));
//                }
//                else{
//                    groupUnreadCountDao.incrementGroupUnreadCount(groupId, lastMessageText, lastMessageSentBy);
//                }
//            }
//        });
//
//    }
//}
//
