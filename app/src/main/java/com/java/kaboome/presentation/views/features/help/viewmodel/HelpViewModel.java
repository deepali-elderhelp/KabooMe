package com.java.kaboome.presentation.views.features.help.viewmodel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.data.repositories.DataGroupsListRepository;
import com.java.kaboome.data.repositories.DataHelpRepository;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupsListRepository;
import com.java.kaboome.domain.repositories.HelpRepository;
import com.java.kaboome.domain.usecases.SearchGroupsUseCase;
import com.java.kaboome.domain.usecases.SendHelpFeedbackUseCase;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.mappers.GroupModelMapper;

import java.util.List;


public class HelpViewModel extends ViewModel {

    private static final String TAG = "KMHelpViewModel";
    private SendHelpFeedbackUseCase sendHelpFeedbackUseCase;
    private HelpRepository helpRepository;

    public HelpViewModel() {
        helpRepository = DataHelpRepository.getInstance();
        sendHelpFeedbackUseCase = new SendHelpFeedbackUseCase(helpRepository);
    }

    public void sendHelpFeedbackMessage(String subject, String messageText, boolean contactAllowed){
        sendHelpFeedbackUseCase.execute(SendHelpFeedbackUseCase.Params.sendHelpFeedbackMessage(subject, messageText, contactAllowed));
    }

}
















