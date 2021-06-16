package com.java.kaboome.presentation.mappers;

import com.java.kaboome.constants.GroupRequestsListStatusConstants;
import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.entities.GroupUserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupRequestModelMapper {

    public static DomainGroupRequest getDomainFromGroupRequestModel(GroupRequestModel groupRequestModel){

        DomainGroupRequest domainGroupRequest = new DomainGroupRequest();

        domainGroupRequest.setUserId(groupRequestModel.getUserId());
        domainGroupRequest.setGroupId(groupRequestModel.getGroupId());
        domainGroupRequest.setUserRole(groupRequestModel.getUserRole());
        domainGroupRequest.setUserAlias(groupRequestModel.getUserAlias());
        domainGroupRequest.setDateRequestMade(groupRequestModel.getDateRequestMade());
        domainGroupRequest.setRequestMessage(groupRequestModel.getRequestMessage());
        domainGroupRequest.setImageUpdateTimestamp(groupRequestModel.getImageUpdateTimestamp());

        return domainGroupRequest;
    }

    public static GroupRequestModel getGroupRequestFromDomainModel(DomainGroupRequest domainGroupRequest){

        GroupRequestModel groupRequestModel = new GroupRequestModel();

        groupRequestModel.setUserId(domainGroupRequest.getUserId());
        groupRequestModel.setGroupId(domainGroupRequest.getGroupId());
        groupRequestModel.setUserRole(domainGroupRequest.getUserRole());
        groupRequestModel.setUserAlias(domainGroupRequest.getUserAlias());
        groupRequestModel.setDateRequestMade(domainGroupRequest.getDateRequestMade());
        groupRequestModel.setRequestMessage(domainGroupRequest.getRequestMessage());
        groupRequestModel.setImageUpdateTimestamp(domainGroupRequest.getImageUpdateTimestamp());

        return groupRequestModel;
    }

    public static List<GroupRequestModel> transformAllFromDomainToModel(DomainResource<List<DomainGroupRequest>> domainGroupRequestsResource) {
        List<DomainGroupRequest> domainGroupRequests = domainGroupRequestsResource.data;

        List<GroupRequestModel> groupRequestModels = new ArrayList<>();

        if (domainGroupRequests != null && !domainGroupRequests.isEmpty()) {
            groupRequestModels = new ArrayList<>();
            for (DomainGroupRequest domainGroupRequest : domainGroupRequests) {
                groupRequestModels.add(getGroupRequestFromDomainModel(domainGroupRequest));
            }
        }
//        else {
//            groupRequestModels = Collections.emptyList();
//        }

        if(domainGroupRequestsResource.status == DomainResource.Status.LOADING){
            GroupRequestModel groupRequestModel = new GroupRequestModel();
            groupRequestModel.setGroupId(GroupRequestsListStatusConstants.LOADING.toString());
            groupRequestModels.add(groupRequestModel);
        }
        else if(domainGroupRequestsResource.status == DomainResource.Status.SUCCESS){
            if(groupRequestModels.isEmpty()){
                GroupRequestModel groupRequestModel = new GroupRequestModel();
                groupRequestModel.setGroupId(GroupRequestsListStatusConstants.NO_REQUESTS.toString());
                groupRequestModels.add(groupRequestModel);
            }
            //else it is already done
        }

        return groupRequestModels;
    }



}
