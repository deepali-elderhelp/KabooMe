//package com.java.kaboome.data.mappers;
//
//import com.java.kaboome.data.entities.GroupUnreadCount;
//import com.java.kaboome.domain.entities.DomainGroupUnreadData;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class GroupUnreadDomainUnreadMapper {
//
//    public static DomainGroupUnreadData transform(GroupUnreadCount groupUnreadCount) {
//        if (groupUnreadCount == null) {
//            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
//        }
//
//        DomainGroupUnreadData domainGroupUnreadCount =  new DomainGroupUnreadData();
//        domainGroupUnreadCount.setGroupId(groupUnreadCount.getGroupId());
//        domainGroupUnreadCount.setCountOfUnreadMessages(groupUnreadCount.getCountOfUnreadMessages());
//        domainGroupUnreadCount.setLastMessageText(groupUnreadCount.getLastMessageText());
//        domainGroupUnreadCount.setLastMessageSentBy(groupUnreadCount.getLastMessageSentBy());
//
//        return domainGroupUnreadCount;
//    }
//
//    public static List<DomainGroupUnreadData> transform(List<GroupUnreadCount> groupUnreadCountList) {
//        List<DomainGroupUnreadData> domainGroupUnreadCountList;
//
//        if (groupUnreadCountList != null && !groupUnreadCountList.isEmpty()) {
//            domainGroupUnreadCountList = new ArrayList<>();
//            for (GroupUnreadCount groupUnreadCount : groupUnreadCountList) {
//                domainGroupUnreadCountList.add(transform(groupUnreadCount));
//            }
//        } else {
//            domainGroupUnreadCountList = Collections.emptyList();
//        }
//
//        return domainGroupUnreadCountList;
//    }
//}
