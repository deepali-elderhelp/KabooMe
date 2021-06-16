package com.java.kaboome.presentation.views.features;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {

    private static final String TAG = "KMBaseViewModel";

//    MutableLiveData<Boolean> userLoggedIn = new MutableLiveData<>();

    ConnectivityLiveData connectivityLiveData;

//    public MutableLiveData<Boolean> getUserLoggedIn() {
//        return userLoggedIn;
//    }
//
//    public void setUserLoggedIn(Boolean userLoggedInBoolean) {
//        userLoggedIn.setValue(userLoggedInBoolean);
//    }

    public void setConnectivityLiveData(ConnectivityLiveData connectivityLiveData) {
        this.connectivityLiveData = connectivityLiveData;
    }

    public ConnectivityLiveData getConnectivityLiveData() {
        return connectivityLiveData;
    }
}
