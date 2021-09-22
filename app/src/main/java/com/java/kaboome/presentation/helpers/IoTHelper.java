package com.java.kaboome.presentation.helpers;

import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttMessageDeliveryCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.google.gson.Gson;
import com.java.kaboome.constants.AWSConstants;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.CredentialsHandler;
import com.java.kaboome.helpers.IoTPolicyHelper;
import com.java.kaboome.presentation.entities.IoTMessage;
import com.java.kaboome.presentation.viewModelProvider.SingleMutableLiveEvent;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.PublishMessageCallback;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * This class helps with connection to IoT
 * Subscribe to a group
 * UnSubscribe to a group etc.
 */
public class IoTHelper {

    private static final String TAG = "KMIotHelper";
    private static IoTHelper iotHelper;
    private MutableLiveData<Boolean> connectionEstablished = new MutableLiveData<>();
    private MutableLiveData<Boolean> subscriptionDone = new MutableLiveData<>();
//    private MutableLiveData<IoTMessage> iotMessageReceived = new MutableLiveData<>();
    private SingleMutableLiveEvent<IoTMessage> iotMessageReceived = new SingleMutableLiveEvent<>();
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AWSIotClient mIotAndroidClient;
    private AWSIotMqttManager mqttManager;
    private AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus currentStatus;
    private boolean attemptingGettingCredentials = false;
//    private IoTMessage lastMessage = null;


    private IoTHelper(){
        connectionEstablished.setValue(false);
        subscriptionDone.setValue(false);
    }

    public static IoTHelper getInstance() {
        if(iotHelper == null){
            iotHelper = new IoTHelper();
        }
        return  iotHelper;
    }


    public void connectToIoT(final String topicName, final boolean recreateClient) {
        Log.d(TAG, "connectToIoT: Topic Name - "+topicName);
        try {
            CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                @Override
                public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
                    Log.d(TAG, "Successful retrieval of CredentialsProvider");

                    credentialsProvider = credentialsProviderReturned;
                    //you got the credentialsProvider
                    //check if the IoT Policy is attached, if not, then attach it


                    Region region = Region.getRegion(Regions.US_WEST_2);
                    mIotAndroidClient = new AWSIotClient(credentialsProvider);
                    mIotAndroidClient.setRegion(region);

                    try{

                        CognitoHelper.setIoTPolicy(mIotAndroidClient, new IoTPolicyHelper() {
                            @Override
                            public void onSuccess() {
                                //set the AppConfig variable to set
                                AppConfigHelper.setIotPolicyAttached(true);
                                connectToMQTT(topicName, recreateClient);
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Log.d(TAG, "Failed to get attached policies");
//                                callback.onErrorGettingPolicyAttached(exception);
                                connectionEstablished.postValue(false);
                            }
                        });
                    }
                    catch (Exception e){
                        Log.d(TAG, "No callback passed to the setIoTPolicy()");
                        connectionEstablished.postValue(false);
//                        callback.onErrorGettingPolicyAttached(e);
                    }


                }

                @Override
                public void onFailure(Exception exception) {
                    Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);
                    connectionEstablished.postValue(false);
//                    callback.onErrorGettingCredentials(exception);

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "No callback passed to getCredentials()");
            connectionEstablished.setValue(false);
//            callback.onOtherErrors(e);
            e.printStackTrace();
        }
    }


    private void connectToMQTT(String topicName, boolean recreateClient){

//        if(!NetworkHelper.isOnline()){
//            return;
//        }
        //connect to mqtt
        // MQTT Client
        if(mqttManager == null || recreateClient){

//            if(mqttManager != null){
//                try {
//                    mqttManager.unsubscribeTopic(groupId);
//                    mqttManager.disconnect();
//                } catch (Exception e) {
////                    e.printStackTrace();
//                    //do nothing, we are just making sure that the connection is gone
//                }
//            }
            //do not need to create again if it is already connected
            //My current thoughts are that the client id should be the user id
            //reason being at any point, a client id can have only one session
            //if another broker connection is being formed for the same client id, then the old one
            //is disconnected and this one is started - that's what we really want.
            //at any one point of time, one client/device/user can only connect to one session/chat group

            //coming back in Aug 2021 -  verifying this that keeping the client id as the user id
            //is a good idea -  it is unique for each user - no multiple devices allowed anyways
//            String unique_clientId = UUID.randomUUID().toString();
//            String unique_clientId = AppConfigHelper.getUserId()+groupId;
//            String unique_clientId = AppConfigHelper.getUserId()+groupId+System.nanoTime();
            //just for testing since I am getting error messages related to the mqtt client connection
            //based upon the client id, I am testing by keeping the client id as just the user id
            //so it is same wherever this user creates the mqtt connection from
            //and if it tries to create again, the old one is dropped and the new one is kept

//            String unique_clientId = AppConfigHelper.getUserId()+topicName+System.nanoTime();
            //coming back in Aug 2021 -  verifying this that keeping the client id as the user id
            //is a good idea -  it is unique for each user - no multiple devices allowed anyways
            String unique_clientId = AppConfigHelper.getUserId();
            Log.d(TAG, "clientId - "+unique_clientId);
//            mqttManager = new AWSIotMqttManager(unique_clientId, AWSConstants.IOT_ENDPOINT.toString());
            mqttManager = AWSIotMqttManager.from(Region.getRegion(Regions.US_WEST_2), AWSIotMqttManager.ClientId.fromString(unique_clientId), AWSIotMqttManager.Endpoint.fromString(AWSConstants.IOT_ENDPOINT.toString()));


            // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
            // MQTT pings every 10 seconds.
//            mqttManager.setKeepAlive(10);
            //not keeping it 10 seconds - too little
            //making it default which is 300seconds - 5 minutes
//            mqttManager.setAutoResubscribe(false); //we want to resubscribe ourselves
            mqttManager.setAutoResubscribe(true); //not sure why it was false
            mqttManager.setMaxAutoReconnectAttempts(3); //do not want to keep retrying
            //so reconnecting is okay, the AWSIoTManager even handles resubscribing.
            //It fails if the credentials are also expired by then. Then there is no
            //fruits of trying to reconnect. It is going to fail
            //but it takes forever long time to say that the connection is lost
            //it keeps saying that the status is "Reconnecting" till all the reconnecting
            //attempts are given up - so really, how do we know that the reconnecting should be stopped
            //because the credentials have expired...hmmm??
//            mqttManager.setAutoReconnect(false); //let's handle reconnect ourselves
            //let's wait - let's try to see if reconnecting or connection lost can check for
            //credentials

        }


        try {
            mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(TAG, "Throwable came - "+throwable);
                    currentStatus = status;
                    Log.d(TAG, "Status = " + String.valueOf(status));
                    if (status == AWSIotMqttClientStatus.Connected) {
                        connectionEstablished.postValue(true);
                        //connection has happened, now subscribe
//                        callback.onSuccess();

                    } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                        connectionEstablished.postValue(false);
                        //try connecting again
                        Log.d(TAG, "Connection lost, try again");
                        //also make sure that the credentials are good
                        makeSureCredentialsValid();
                        if(throwable.getMessage().contains("Timed out")){
                            mqttManager = null;
                        }

                    }
                    else if (status == AWSIotMqttClientStatus.Reconnecting) {
                        connectionEstablished.postValue(false);
                        //try connecting again
                        Log.d(TAG, "Connection broken, trying to reconnect");
                        //also make sure that the credentials are good
                        makeSureCredentialsValid();

                    }

                }
            });
        }
        catch (final Exception e) {
            connectionEstablished.postValue(false);
            Log.e(TAG, "MQTT Connection error.", e);
//            callback.onErrorGettingMqttConnection(e);
            //go back in this case
            //Toast message of the error here
            //remember you are in UI thread
        }
    }

    public void subscribeToGroup(String topic) {
        subscribeToTopic(topic);
    }

    public AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus getCurrentStatus() {
        return currentStatus;
    }

    private void makeSureCredentialsValid(){
        if(attemptingGettingCredentials){
            return;
        }
        attemptingGettingCredentials = true;
        try {
            CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                @Override
                public void onSuccess(CognitoCachingCredentialsProvider credentialsProvider) {
                    Log.d(TAG, "onSuccess: getting credentials");
                    attemptingGettingCredentials = false;
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.d(TAG, "onFailure: getting credentials");
                    attemptingGettingCredentials = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            attemptingGettingCredentials = false;
        }
    }

    private void subscribeToTopic(String topicName){
        //this method is being called on the UI Thread

//        if(!NetworkHelper.isOnline()){
//            return;
//        }

        if(topicName == null || topicName.isEmpty())
            return;

        if(mqttManager == null ){
            return; //connection not there, cannot subscribe
        }

        //connectionEstablished is false
        if(!connectionEstablished.getValue()){
            return;
        }

        //todo: change Cazt to app name after AWS IoT change
        String topic = "Cazt/"+topicName;
        Log.d(TAG, "subscribeToTopic: "+topic);

        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            //print out thread name, not sure if already running on UI thread
                            Log.d(TAG, "Thread in subscribe "+Thread.currentThread().getId());
                            try {

                                String messageText = new String(data, "UTF-8");
                                //get the message object from the json received

                                IoTMessage messageObject = new Gson().fromJson(messageText, IoTMessage.class);
                                iotMessageReceived.postValue(messageObject);

                            } catch (UnsupportedEncodingException e) {
                                Log.e(TAG, "Message encoding error.", e);
                            }

                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "Error while subscribeToTopic: "+e.getMessage());
            e.printStackTrace();
            //this exception could be the result of connection already broken by the broker
            //but the client is still assuming that the connection is here or could be that the
            //credentials are expired.
            //when user comes here, the best way to handle this is to form the connection again.
            //but the connection needs afresh...even new mqttClient
//            mqttManager = null;
            connectToIoT(topicName, true);


        }

    }

    public void unsubscribeFromTopic(String topicName){

        String topic = "Cazt/"+topicName;
        try {
            if (mqttManager != null) {
                mqttManager.unsubscribeTopic(topic);
            }
            //set iotMessageReceived to null
            iotMessageReceived.setValue(null);
        }
        catch (Exception e) {
            Log.e(TAG, "Unsubscribe error for group "+topicName, e);
        }
    }

    public void disconnectToIoT(){

        try {
            if(mqttManager != null){
                mqttManager.disconnect();
                connectionEstablished.setValue(false);
            }

        } catch (Exception e) {
            Log.e(TAG, "Disconnect error.", e);
        }
    }


//    public MutableLiveData<Boolean> getGroupSubscriptionDone() {
//        return subscriptionDone;
//    }

    public MutableLiveData<Boolean> getConnectionEstablished() {
        return connectionEstablished;
    }

    public MutableLiveData<IoTMessage> getIotMessageReceived() {
        return iotMessageReceived;
    }


    public void publishIoTMessage(final IoTMessage ioTMessage, final String userId, final PublishMessageCallback callback) {

        String topic = MessageGroupsHelper.getTopicName(ioTMessage, userId);
        if(topic == null){
            topic = ioTMessage.getGroupId();
        }
//        String groupId = ioTMessage.getGroupId();
        //get Gson to convert this object into JSON
        final String message_json = new Gson().toJson(ioTMessage);

        //todo: change Cazt to app name after AWS IoT change
        final String topicName = "Cazt/"+topic;
        Log.d(TAG, "publish to topic - "+topicName);

        try {
            if(mqttManager != null){
//                mqttManager.publishString(message_json, topic, AWSIotMqttQos.QOS0);
                mqttManager.publishString(message_json, topicName, AWSIotMqttQos.QOS0, new AWSIotMqttMessageDeliveryCallback() {
                    @Override
                    public void statusChanged(MessageDeliveryStatus status, Object userData) {
                        if(status == MessageDeliveryStatus.Success){
                            Log.d(TAG, "MessageDelivered");
                            Log.d(TAG, "Message published is "+message_json);
                            //the thread this call comes back on is not main thread...it is some internal network thread used by the mqtt call
                            //so, any action that needs main thread, like observing something, toast etc. needs to move back to main thread
                            AppExecutors2.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    callback.publishSuccessful();
                                }
                            });

                        }
                        if(status == MessageDeliveryStatus.Fail){
                            Log.d(TAG, "MessageFailed");
//                            lastMessage = ioTMessage;
//                            mqttManager = null;

                            //the thread this call comes back on is not main thread...it is some internal network thread used by the mqtt call
                            //so, any action that needs main thread, like observing something, toast etc. needs to move back to main thread
//                            AppExecutors2.getInstance().mainThread().execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    connectToIoT(ioTMessage.getGroupId(), true);
//                                    callback.publishFailed();
//                                }
//                            });
                            AppExecutors2.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    connectToIoT(topicName, true);
                                    callback.publishFailed();
                                }
                            });

                        }
                    }
                }, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Publish error.", e);
        }

    }


    public void resetConnectionEstablished() {
        connectionEstablished.setValue(false);
    }

//    public IoTMessage getLastMessage() {
//        return lastMessage;
//    }
//
//    public void setLastMessage(IoTMessage lastMessage) {
//        this.lastMessage = lastMessage;
//    }
}
