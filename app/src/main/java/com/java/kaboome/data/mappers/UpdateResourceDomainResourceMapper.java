package com.java.kaboome.data.mappers;

import com.java.kaboome.data.repositories.UpdateResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;

public class UpdateResourceDomainResourceMapper {

    public UpdateResourceDomainResourceMapper() {
    }

    public static DomainUpdateResource transform(UpdateResource dataResource) {
        if (dataResource == null) {
            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
        }
        DomainUpdateResource.Status statusPassed = null;
        if(dataResource.status == UpdateResource.Status.ERROR){
            statusPassed = DomainUpdateResource.Status.ERROR;
        }
        else if(dataResource.status == UpdateResource.Status.SUCCESS){
            statusPassed = DomainUpdateResource.Status.SUCCESS;
        }
        else if(dataResource.status == UpdateResource.Status.UPDATING){
            statusPassed = DomainUpdateResource.Status.UPDATING;
        }
        DomainUpdateResource domainResource = new DomainUpdateResource(statusPassed, dataResource.data, dataResource.message);
        return domainResource;
    }

    public static <T> DomainUpdateResource<T> transform(UpdateResource.Status status, T data, String message){
        DomainUpdateResource.Status statusPassed = null;
        if(status == UpdateResource.Status.ERROR){
            statusPassed = DomainUpdateResource.Status.ERROR;
        }
        else if(status == UpdateResource.Status.SUCCESS){
            statusPassed = DomainUpdateResource.Status.SUCCESS;
        }
        else if(status == UpdateResource.Status.UPDATING){
            statusPassed = DomainUpdateResource.Status.UPDATING;
        }
        DomainUpdateResource domainResource = new DomainUpdateResource(statusPassed, data, message);
        return domainResource;
    }

}
