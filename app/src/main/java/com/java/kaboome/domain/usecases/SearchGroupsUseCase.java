package com.java.kaboome.domain.usecases;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupsListRepository;

import java.util.List;

public class SearchGroupsUseCase extends BaseUseCase<DomainResource<List<DomainGroup>>, SearchGroupsUseCase.Params> {

    private static final String TAG = "KMSearchGroupsUseCase";
    private GroupsListRepository groupsListRepository;

    public SearchGroupsUseCase(GroupsListRepository groupsListRepository) {
        this.groupsListRepository = groupsListRepository;
    }


    @Override
    protected LiveData<DomainResource<List<DomainGroup>>> executeUseCase(SearchGroupsUseCase.Params params) {
        return this.groupsListRepository.getGroupsList(params.searchByGroupNameOrId, params.searchText, params.goToServer);
    }


    public static final class Params {

        private final String searchByGroupNameOrId;
        private final String searchText;
        private final boolean goToServer;


        private Params(String searchByGroupNameOrId, String searchText, boolean goToServer) {
            this.searchByGroupNameOrId = searchByGroupNameOrId;
            this.searchText = searchText;
            this.goToServer = goToServer;
        }

        public static SearchGroupsUseCase.Params searchInfo(String searchByGroupNameOrId, String searchText, boolean goToServer) {
            return new SearchGroupsUseCase.Params(searchByGroupNameOrId, searchText, goToServer);
        }
    }
}
