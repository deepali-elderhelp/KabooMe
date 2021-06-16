package com.java.kaboome.data.mappers;

import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainGroupUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupRequestDataDomainMapper {

    public static DomainGroupRequest transformFromGroupRequest(GroupRequest groupRequest){

        if (groupRequest == null) {
            throw new IllegalArgumentException("Cannot transformFromGroupRequest a null value");
        }

        DomainGroupRequest domainGroupRequest = new DomainGroupRequest();


        domainGroupRequest.setUserId(groupRequest.getUserId());
        domainGroupRequest.setGroupId(groupRequest.getGroupId());
        domainGroupRequest.setUserRole(groupRequest.getUserRole());
        domainGroupRequest.setUserAlias(groupRequest.getUserAlias());
        domainGroupRequest.setDateRequestMade(groupRequest.getDateRequestMade());
        domainGroupRequest.setRequestMessage(groupRequest.getRequestMessage());
        domainGroupRequest.setImageUpdateTimestamp(groupRequest.getImageUpdateTimestamp());

        return domainGroupRequest;
    }


    public static GroupRequest transformFromDomainRequest(DomainGroupRequest domainGroupRequest){

        if (domainGroupRequest == null) {
            throw new IllegalArgumentException("Cannot transformFromDomainGroupRequest a null value");
        }

        GroupRequest groupRequest = new GroupRequest();


        groupRequest.setUserId(domainGroupRequest.getUserId());
        groupRequest.setGroupId(domainGroupRequest.getGroupId());
        groupRequest.setUserRole(domainGroupRequest.getUserRole());
        groupRequest.setUserAlias(domainGroupRequest.getUserAlias());
        groupRequest.setDateRequestMade(domainGroupRequest.getDateRequestMade());
        groupRequest.setRequestMessage(domainGroupRequest.getRequestMessage());
        groupRequest.setImageUpdateTimestamp(domainGroupRequest.getImageUpdateTimestamp());

        return groupRequest;
    }

    public static List<DomainGroupRequest> transform(List<GroupRequest> groupRequestsCollection) {
        List<DomainGroupRequest> domainGroupRequestsCollection;

        if (groupRequestsCollection != null && !groupRequestsCollection.isEmpty()) {
            domainGroupRequestsCollection = new ArrayList<>();
            for (GroupRequest groupRequest : groupRequestsCollection) {
                domainGroupRequestsCollection.add(transformFromGroupRequest(groupRequest));
            }
        } else {
            domainGroupRequestsCollection = Collections.emptyList();
        }

        return domainGroupRequestsCollection;
    }


}
