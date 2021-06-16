package com.java.kaboome.helpers;

import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class CognitoAuthenticationHandler implements AuthenticationHandler {

    private static final String TAG = "KMCognitoAuthHandler";



    private AuthenticationCallback authenticationCallback;

    public CognitoAuthenticationHandler(AuthenticationCallback authenticationCallback) {
        this.authenticationCallback = authenticationCallback;
    }

    @Override
    public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
        authenticationCallback.onSuccess(userSession);
    }

    @Override
    public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
        Log.d(TAG, "getAuthenticationDetails: should not come here, throw error");
        onFailure(new Exception("Fail intentionally, so it goes for login"));
    }

    @Override
    public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
        Log.d(TAG, "getMFACode: ");
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation continuation) {
        Log.d(TAG, "authenticationChallenge: ");
    }

    @Override
    public void onFailure(Exception exception) {
        Log.d(TAG, "Login failed due to "+exception.getMessage());
        authenticationCallback.onFailure(exception);

    }
}


