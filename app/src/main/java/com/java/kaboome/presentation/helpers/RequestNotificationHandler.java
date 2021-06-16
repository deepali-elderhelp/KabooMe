package com.java.kaboome.presentation.helpers;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.java.kaboome.data.repositories.DataGroupRequestRepository;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.domain.usecases.AddLocalRequestUseCase;
import com.java.kaboome.domain.usecases.DeleteLocalRequestUseCase;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.mappers.GroupRequestModelMapper;

public class RequestNotificationHandler extends BaseNotificationHandler {

    private static final String TAG = "KMRequestNotifHandler";
    private Notification summaryNotification;
    private DeleteLocalRequestUseCase deleteLocalRequestUseCase;
    private AddLocalRequestUseCase addLocalRequestUseCase;
    private GroupRequestRepository groupRequestRepository;
    private boolean requestCancel = false;


    public RequestNotificationHandler(RemoteMessage remoteMessage, Context context, boolean requestCancel) {
        super(remoteMessage, context);
        groupRequestRepository = DataGroupRequestRepository.getInstance();
        deleteLocalRequestUseCase = new DeleteLocalRequestUseCase(groupRequestRepository);
        addLocalRequestUseCase = new AddLocalRequestUseCase(groupRequestRepository);
        this.requestCancel = requestCancel;
    }

    @Override
    public void handleNotification() {

        String remote_message_data = new Gson().toJson(remoteMessage.getData());
        Log.d(TAG, "Message Data: " + remote_message_data);

        //get the request to be handled
        GroupRequestModel groupRequestModel = new Gson().fromJson(remote_message_data, GroupRequestModel.class);

        //check if the request needs to be cancelled
        if (requestCancel) {
            //you need to delete the request from the cache
            deleteLocalRequestUseCase.execute(DeleteLocalRequestUseCase.Params.deleteRequest(groupRequestModel.getUserId(), groupRequestModel.getGroupId()));

        }
        else{
            //you need to add the request to the cache
            addLocalRequestUseCase.execute(AddLocalRequestUseCase.Params.addRequest(GroupRequestModelMapper.getDomainFromGroupRequestModel(groupRequestModel)));

        }


    }


}
