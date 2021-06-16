package com.java.kaboome.presentation.mappers;

import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.presentation.entities.UserModel;

public class UserModelMapper {

    public static UserModel transformFromDomainToModel(DomainResource<DomainUser> input) {

        UserModel userModel = new UserModel();

        if(input.data != null){
            DomainUser domainUser = input.data;
            userModel.setUserId(domainUser.getUserId());
            userModel.setUserName(domainUser.getUserName());
//            userModel.setDateUserCreated(domainUser.getDateUserCreated());
            userModel.setPhoneNumber(domainUser.getPhoneNumber());
            userModel.setEmail(domainUser.getEmail());
            userModel.setImageUpdateTimestamp(domainUser.getImageUpdateTimestamp());
        }

        if(input.status == DomainResource.Status.LOADING){
           userModel.setStatus("Loading");
        }
        if(input.status == DomainResource.Status.SUCCESS){
            userModel.setStatus("Success");
        }
        if(input.status == DomainResource.Status.ERROR){
            userModel.setStatus("Error");
        }

        return userModel;


    }

    public static DomainUser transformFromModelToDomain(UserModel userModel) {

        DomainUser domainUser = new DomainUser();

        if(userModel != null){

            domainUser.setUserId(userModel.getUserId());
            domainUser.setUserName(userModel.getUserName());
            domainUser.setPhoneNumber(userModel.getPhoneNumber());
            domainUser.setEmail(userModel.getEmail());
            domainUser.setImageUpdateTimestamp(userModel.getImageUpdateTimestamp());
        }

        return domainUser;


    }









}
