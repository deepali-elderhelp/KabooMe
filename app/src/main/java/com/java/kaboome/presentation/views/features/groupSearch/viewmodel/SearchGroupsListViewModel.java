package com.java.kaboome.presentation.views.features.groupSearch.viewmodel;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.repositories.DataGroupsListRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUnreadData;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupsListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.domain.usecases.GetUserGroupsListOnlyLocalUseCase;
import com.java.kaboome.domain.usecases.GetUserGroupsListUseCase;
import com.java.kaboome.domain.usecases.SearchGroupsUseCase;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.mappers.GroupModelMapper;
import com.java.kaboome.presentation.mappers.UserGroupModelMapper;

import java.util.List;


public class SearchGroupsListViewModel extends ViewModel {

    private static final String TAG = "KMSearchGroupsListVM";
    private SearchGroupsUseCase searchGroupsUseCase;
    private MediatorLiveData<List<GroupModel>> groups = new MediatorLiveData<>();
    private GroupsListRepository groupsListRepository;
    private LiveData<DomainResource<List<DomainGroup>>> repositorySource;


    // query extras
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
        private int pageNumber;
    private String query;
    private boolean cancelRequest;
//    private long requestStartTime;


    public SearchGroupsListViewModel() {
        groupsListRepository = DataGroupsListRepository.getInstance();
        searchGroupsUseCase = new SearchGroupsUseCase(groupsListRepository);
    }

    public MediatorLiveData<List<GroupModel>> getGroups() {
        return groups;
    }

    public void searchGroupsByText(String searchByGroupNameOrId, String searchText, boolean goToServer){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            isQueryExhausted = false;
            getGroupsBySearchText(searchByGroupNameOrId, searchText, goToServer);
        }
    }

    public void searchNextPage(String searchByGroupNameOrId, String searchText,  boolean goToServer ){
        if(!isQueryExhausted && !isPerformingQuery){
            pageNumber++;
            getGroupsBySearchText(searchByGroupNameOrId, searchText, goToServer);
        }
    }

    private void getGroupsBySearchText(String searchByGroupNameOrId, String searchText, final boolean goToServer) {

        cancelRequest = false;
        isPerformingQuery = true;

        //TODO: implement page number search
        groups.removeSource(repositorySource); //if any old hanging there
        repositorySource = searchGroupsUseCase.execute(SearchGroupsUseCase.Params.searchInfo(searchByGroupNameOrId, searchText, goToServer));

        groups.addSource(repositorySource, new Observer<DomainResource<List<DomainGroup>>>() {
            @Override
            public void onChanged(DomainResource<List<DomainGroup>> listDomainResource) {
                if (listDomainResource != null) {


                    if (listDomainResource.status == DomainResource.Status.SUCCESS) {

                        isPerformingQuery = false;

                        if (listDomainResource.data != null) {
                            if (listDomainResource.data.size() == 0) {
                                Log.d(TAG, "no groups found...");
                                groups.setValue(GroupModelMapper.transformAllGroupDomainResourceToModels(listDomainResource, goToServer));
//                                searchResult.setValue(listDomainResource);

                            } else {
                                Log.d(TAG, "There are groups...");
                                for(DomainGroup group: listDomainResource.data){
                                    Log.d(TAG, "Group Name - "+group.getGroupName()+" and private - "+group.isPrivateGroup());
                                }
                                groups.setValue(GroupModelMapper.transformAllGroupDomainResourceToModels(listDomainResource, goToServer));
//                                searchResult.setValue(listDomainResource);


                            }
                        }
                        groups.removeSource(repositorySource);
                    } else if (listDomainResource.status == DomainResource.Status.LOADING) {
                        if (listDomainResource.data != null) {
                            if (listDomainResource.data.size() <= 0) {
                                Log.d(TAG, "no groups cached...");
                                groups.setValue(GroupModelMapper.transformAllGroupDomainResourceToModels(listDomainResource, goToServer));


                            } else {
                                Log.d(TAG, "There are groups cached...");
                                groups.setValue(GroupModelMapper.transformAllGroupDomainResourceToModels(listDomainResource, goToServer));
//                                searchResult.setValue(listDomainResource);
                            }

                        }
                    } else if (listDomainResource.status == DomainResource.Status.ERROR) {
                        isPerformingQuery = false;

                        groups.setValue(GroupModelMapper.transformAllGroupDomainResourceToModels(listDomainResource, goToServer));
//                        searchResult.setValue(listDomainResource);
                        groups.removeSource(repositorySource);
                    }
                } else {
                    groups.removeSource(repositorySource);
                }
            }
        });


    }
}
















