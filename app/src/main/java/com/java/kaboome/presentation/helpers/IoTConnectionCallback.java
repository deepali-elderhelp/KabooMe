package com.java.kaboome.presentation.helpers;

import com.java.kaboome.presentation.entities.IoTMessage;

public interface IoTConnectionCallback {
    void onConnectionSuccessful();

    void onConnectionFailure(Exception exception);

//    void onGroupSubsription();

    void onMessageArrived(IoTMessage message);

    void onDisconnectDone();
}
