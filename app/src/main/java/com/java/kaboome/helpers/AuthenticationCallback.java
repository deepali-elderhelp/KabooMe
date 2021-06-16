package com.java.kaboome.helpers;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;

public interface AuthenticationCallback {


    void onFailure(Exception e);

    void onSuccess(CognitoUserSession userSession);
}
