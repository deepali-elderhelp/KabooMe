package com.java.kaboome.presentation.viewModelProvider;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.views.features.conversations.viewmodel.AddConversationViewModel;
import com.java.kaboome.presentation.views.features.conversations.viewmodel.ConvsListViewModel;
import com.java.kaboome.presentation.views.features.groupActions.GroupActionsViewModel;
import com.java.kaboome.presentation.views.features.groupInfo.viewmodel.GroupViewModel;
import com.java.kaboome.presentation.views.features.groupMessages.viewmodel.AdminMessagesViewModel;
import com.java.kaboome.presentation.views.features.groupMessages.viewmodel.AdminUserMessagesViewModel;
import com.java.kaboome.presentation.views.features.groupMessages.viewmodel.MessagesViewModel;
import com.java.kaboome.presentation.views.features.groupQRCode.viewmodel.GroupQRViewModel;
import com.java.kaboome.presentation.views.features.requestsList.viewmodel.GroupRequestsListViewModel;

public class CustomViewModelProvider extends ViewModelProvider.NewInstanceFactory {

    private Object[] mParams;

    public CustomViewModelProvider(Object... mParams) {
        this.mParams = mParams;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass == ConvsListViewModel.class){
            return (T) new ConvsListViewModel((Context) mParams[0], (String) mParams[1]);
        }
       if(modelClass == MessagesViewModel.class){
           return (T) new MessagesViewModel((UserGroupModel) mParams[0]);
       }
       else if(modelClass == AdminMessagesViewModel.class){
           return (T) new AdminMessagesViewModel((UserGroupModel) mParams[0]);
       }
       else if(modelClass == AdminUserMessagesViewModel.class){
           return (T) new AdminUserMessagesViewModel((UserGroupConversationModel)mParams[0], (UserGroupModel) mParams[1]);
       }
       else if(modelClass == AddConversationViewModel.class){
           return (T) new AddConversationViewModel((String) mParams[0]);
       }
       else if(modelClass == GroupViewModel.class){
           return (T) new GroupViewModel((String)mParams[0]);
       }
       else if(modelClass == GroupQRViewModel.class){
           return (T) new GroupQRViewModel((String)mParams[0]);
       }
       else if(modelClass == GroupRequestsListViewModel.class){
           return (T) new GroupRequestsListViewModel((String)mParams[0]);
       }
       else if(modelClass == GroupActionsViewModel.class){
           return (T) new GroupActionsViewModel((String)mParams[0]);
       }
       else {
           return super.create(modelClass);
       }
    }
}
