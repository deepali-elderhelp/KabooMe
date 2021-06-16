package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;

import java.util.List;

public class GetConversationsListUseCase extends BaseUseCase<DomainResource<List<DomainUserGroupConversation>>, GetConversationsListUseCase.Params> {

    private static final String TAG = "KMGetConvsListUseCase";

    private ConversationsRepository conversationsRepository;

    public GetConversationsListUseCase(ConversationsRepository conversationsRepository) {
        this.conversationsRepository = conversationsRepository;
    }

    @Override
    protected LiveData<DomainResource<List<DomainUserGroupConversation>>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting group convs list");
        return conversationsRepository.getConversationsForUserGroups(params.groupId);
    }

    public static final class Params {

        private final String groupId;


        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetConversationsListUseCase.Params getConversationForGroup(String groupId) {
            return new GetConversationsListUseCase.Params(groupId);
        }
    }

}
