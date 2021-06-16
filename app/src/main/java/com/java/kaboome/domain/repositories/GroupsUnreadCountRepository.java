//package com.java.kaboome.domain.repositories;
//
//import androidx.lifecycle.LiveData;
//
//import com.java.kaboome.domain.entities.DomainGroupUnreadData;
//import com.java.kaboome.domain.entities.DomainMessage;
//
//import java.util.List;
//
//public interface GroupsUnreadCountRepository {
//
//    LiveData<List<DomainGroupUnreadData>> getGroupsUnreadCountList();
//
//    LiveData<DomainGroupUnreadData> getGroupUnreadCount(String groupId);
//
//    void addNewUnreadMessage(DomainMessage domainMessage);
//
//    void resetGroupsUnreadCountList(String groupId);
//
//}
