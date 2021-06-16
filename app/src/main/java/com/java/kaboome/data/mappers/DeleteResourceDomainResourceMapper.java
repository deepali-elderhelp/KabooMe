package com.java.kaboome.data.mappers;

import com.java.kaboome.data.repositories.DeleteResource;
import com.java.kaboome.data.repositories.UpdateResource;
import com.java.kaboome.domain.entities.DomainDeleteResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;

public class DeleteResourceDomainResourceMapper {

    public DeleteResourceDomainResourceMapper() {
    }

    public DomainDeleteResource transform(DeleteResource dataResource) {
        if (dataResource == null) {
            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
        }
        DomainDeleteResource.Status statusPassed = null;
        if(dataResource.status == DeleteResource.Status.ERROR){
            statusPassed = DomainDeleteResource.Status.ERROR;
        }
        else if(dataResource.status == DeleteResource.Status.SUCCESS){
            statusPassed = DomainDeleteResource.Status.SUCCESS;
        }
        else if(dataResource.status == DeleteResource.Status.DELETING){
            statusPassed = DomainDeleteResource.Status.DELETING;
        }
        DomainDeleteResource domainDeleteResource = new DomainDeleteResource(statusPassed, dataResource.data, dataResource.message);
        return domainDeleteResource;
    }

    public static <T> DomainDeleteResource<T> transform(DeleteResource.Status status, T data, String message){
        DomainDeleteResource.Status statusPassed = null;
        if(status == DeleteResource.Status.ERROR){
            statusPassed = DomainDeleteResource.Status.ERROR;
        }
        else if(status == DeleteResource.Status.SUCCESS){
            statusPassed = DomainDeleteResource.Status.SUCCESS;
        }
        else if(status == DeleteResource.Status.DELETING){
            statusPassed = DomainDeleteResource.Status.DELETING;
        }
        DomainDeleteResource domainResource = new DomainDeleteResource(statusPassed, data, message);
        return domainResource;
    }

}
