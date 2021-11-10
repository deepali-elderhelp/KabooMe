package com.java.kaboome.data.repositories;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.remote.requests.HelpFeedbackRequest;
import com.java.kaboome.domain.repositories.HelpRepository;
import com.java.kaboome.helpers.AppConfigHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataHelpRepository implements HelpRepository {

    private static final String TAG = "DataHelpRepository";
    private static DataHelpRepository instance;

    private DataHelpRepository() {
    }

    public static DataHelpRepository getInstance(){
        if(instance == null){
            instance = new DataHelpRepository();
        }
        return instance;
    }

    @Override
    public Void postHelpFeedbackMessage(String subject, String messageText, boolean contactAllowed) {
        HelpFeedbackRequest helpFeedbackRequest = new HelpFeedbackRequest(subject, messageText, contactAllowed);
        sendHelpFeedbackMessage(helpFeedbackRequest);
        return null;
    }

    private void sendHelpFeedbackMessage(final HelpFeedbackRequest helpFeedbackRequest){

        new NetworkBoundNoReturn(AppExecutors2.getInstance()){

            @NonNull
            @Override
            protected Call<ResponseBody> createCall() {
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("subject", helpFeedbackRequest.getSubject());
//                jsonObject.addProperty("messageText", helpFeedbackRequest.getMessageText());
//                jsonObject.addProperty("allowedContact", helpFeedbackRequest.getAllowedContact());


                return AppConfigHelper.getBackendApiServiceProvider().sendHelpFeedbackMessage(AppConfigHelper.getUserId(), helpFeedbackRequest);
            }
        };
    }
}
