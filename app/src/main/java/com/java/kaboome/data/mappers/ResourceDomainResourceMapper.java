package com.java.kaboome.data.mappers;

import com.java.kaboome.data.repositories.Resource;
import com.java.kaboome.domain.entities.DomainResource;

public class ResourceDomainResourceMapper {

    public ResourceDomainResourceMapper() {
    }

    public static DomainResource transform(Resource dataResource) {
        if (dataResource == null) {
            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
        }
        DomainResource.Status statusPassed = null;
        if(dataResource.status == Resource.Status.ERROR){
            statusPassed = DomainResource.Status.ERROR;
        }
        else if(dataResource.status == Resource.Status.SUCCESS){
            statusPassed = DomainResource.Status.SUCCESS;
        }
        else if(dataResource.status == Resource.Status.LOADING){
            statusPassed = DomainResource.Status.LOADING;
        }
        DomainResource domainResource = new DomainResource(statusPassed, dataResource.data, dataResource.message);
        return domainResource;
    }

    public static <T> DomainResource<T> transform(Resource.Status status, T data, String message){
        DomainResource.Status statusPassed = null;
        if(status == Resource.Status.ERROR){
            statusPassed = DomainResource.Status.ERROR;
        }
        else if(status == Resource.Status.SUCCESS){
            statusPassed = DomainResource.Status.SUCCESS;
        }
        else if(status == Resource.Status.LOADING){
            statusPassed = DomainResource.Status.LOADING;
        }
        DomainResource domainResource = new DomainResource(statusPassed, data, message);
        return domainResource;
    }

}
