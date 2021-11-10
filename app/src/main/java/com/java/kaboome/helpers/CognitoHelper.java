package com.java.kaboome.helpers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.ListPrincipalPoliciesRequest;
import com.amazonaws.services.iot.model.ListPrincipalPoliciesResult;
import com.amazonaws.services.iot.model.Policy;
import com.java.kaboome.constants.AWSConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CognitoHelper {

    private static final String TAG = "KMCognitoHelper";

    /**
     * App secret associated with your app id - if the App id does not have an associated App secret,
     * set the App secret to null.
     * e.g. clientSecret = null;
     */
    private static final String clientSecret = null;

    /**
     * Set Your User Pools region.
     * e.g. if your user pools are in US East (N Virginia) then set cognitoRegion = Regions.US_EAST_1.
     */
    private static final Regions cognitoRegion = Regions.US_WEST_2;


    private static CognitoHelper cognitoHelper;
    private static CognitoUserPool userPool;
    private static String user;

    // User details from the service
    private static CognitoUserSession currSession;
    private static CognitoUserDetails userDetails;

    private static CognitoCachingCredentialsProvider credentialsProvider;

    public static void init(Context context) {

        if (cognitoHelper != null && userPool != null) {
            return;
        }

        if (cognitoHelper == null) {
            cognitoHelper = new CognitoHelper();
        }

        if (userPool == null) {

            // Create a user pool with default ClientConfiguration
            userPool = new CognitoUserPool(context, AWSConstants.COGNITO_USER_POOL_ID.toString(), AWSConstants.COGNITO_CLIENT_ID.toString(), clientSecret, cognitoRegion);

//            ClientConfiguration clientConfiguration = new ClientConfiguration();
//            clientConfiguration.setConnectionTimeout(5000); // 5 seconds
//            clientConfiguration.setSocketTimeout(5000);
//            clientConfiguration.setMaxErrorRetry(0);

//            userPool = new CognitoUserPool(context, AWSConstants.COGNITO_USER_POOL_ID.toString(), AWSConstants.COGNITO_CLIENT_ID.toString(), clientSecret, clientConfiguration, cognitoRegion);

//            userPool = new CognitoUserPool(context,)

            // This will also work
            /*
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
            cipClient.setRegion(Region.getRegion(cognitoRegion));
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cipClient);
            */


        }

    }

    public static CognitoUserPool getPool() {
        return userPool;
    }

    public static String getCurrUser() {
        return user;
    }

    public static void setUser(String newUser) {
        user = newUser;
    }

    public static void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }

    public static CognitoUserSession getCurrSession() {
        return currSession;
    }

    public static void setUserDetails(CognitoUserDetails details) {
        userDetails = details;
        //  refreshWithSync();
    }

    public static CognitoUserDetails getUserDetails() {
        return userDetails;
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e(TAG, " -- Error: " + exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if (temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if (temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return formattedString;
    }

    //this method calls the callback on the main thread
    //so, doesn't matter which thread you call it on
    public static void getCredentialsProvider(final CredentialsHandler callback) throws Exception {
        if (callback == null) {
            throw new Exception("callback is null");
        }

        if (!getCurrSession().isValid()) {
            //the token has expired, first user needs to get valid session to continue
            init(AppConfigHelper.getContext());
            CognitoUser user = CognitoHelper.getPool().getCurrentUser();
            if (user == null) {
                throw new Exception("Cognito user is null");
            }
            String userName = user.getUserId();
            if (userName == null) {
                throw new Exception("Cognito username is null");
            }
            if (userName != null) {
                CognitoHelper.setUser(userName);
                user.getSessionInBackground(new CognitoAuthenticationHandler(new AuthenticationCallback() {

                    @Override
                    public void onFailure(Exception exception) {
                        callback.onFailure(exception);
                    }

                    @Override
                    public void onSuccess(CognitoUserSession cognitoUserSession) {
                        CognitoHelper.setCurrSession(cognitoUserSession);
                        //save userId
                        AppConfigHelper.setUserId(cognitoUserSession.getUsername());
                        getCredentialsAfterSession(callback);

                    }
                }));

            }


        }
        else{
            getCredentialsAfterSession(callback);
        }
    }

        public static void getCredentialsAfterSession(final CredentialsHandler callback){
            //if the credentialsProvider is not null, that means the old credentialsProvider object is created
            //we do not need to create it again, but if it is null, then we create is new setting all the login data etc.
            if(credentialsProvider == null){
                credentialsProvider = new CognitoCachingCredentialsProvider(
                        AppConfigHelper.getContext(), // Context
                        AWSConstants.COGNITO_IDENTITY_POOL_ID.toString(), // Identity Pool ID
                        Regions.US_WEST_2 // Region
                );

                // If the user is authenticated through login with Amazon, you can set the map
                // of token to the provider - Cognito in this case
                String token = CognitoHelper.getCurrSession().getIdToken().getJWTToken();
                Map<String, String> logins = new HashMap<String, String>();
                logins.put(AWSConstants.COGNITO_LOGIN_TOKEN_PROVIDER.toString(), token);
                credentialsProvider.setLogins(logins);
            }
            else{
                //credentials exist, but make sure they have the right cognito token in their login params
                String credentialsToken = credentialsProvider.getLogins().get(AWSConstants.COGNITO_LOGIN_TOKEN_PROVIDER.toString());
                String cognitoToken = CognitoHelper.getCurrSession().getIdToken().getJWTToken();
                if(!credentialsToken.equalsIgnoreCase(cognitoToken)){
                    credentialsProvider.getLogins().put(AWSConstants.COGNITO_LOGIN_TOKEN_PROVIDER.toString(), cognitoToken);
                }
            }




            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Handler handler = new Handler(AppConfigHelper.getContext().getMainLooper());
                    Runnable returnCallback;
                    try {
                        credentialsProvider.getCredentials();
                        returnCallback = new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(credentialsProvider);
                            }
                        };
                    } catch (final Exception e) {
                        returnCallback = new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(e);
                            }
                        };
                    }

                    handler.post(returnCallback);

                }

            }).start();
        }


    public static void setIoTPolicy(final AWSIotClient iotClient, final IoTPolicyHelper callback) throws Exception {

        if (callback == null) {
            throw new Exception("callback is null");
        }

        if(AppConfigHelper.isIotPolicyAttached()){
            callback.onSuccess(); //policy is already attached, return now
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(AppConfigHelper.getContext().getMainLooper());
                Runnable returnCallback;
                try {


                    ListPrincipalPoliciesRequest listPrincipalPoliciesRequest = new ListPrincipalPoliciesRequest();
                    listPrincipalPoliciesRequest.setRequestCredentials(credentialsProvider.getCredentials());
                    listPrincipalPoliciesRequest.setAscendingOrder(true);
                    listPrincipalPoliciesRequest.setMarker("");
                    listPrincipalPoliciesRequest.setPageSize(new Integer(1));
                    listPrincipalPoliciesRequest.setPrincipal(credentialsProvider.getIdentityId());

                    ListPrincipalPoliciesResult result = iotClient.listPrincipalPolicies(listPrincipalPoliciesRequest);
                    List<Policy> listPolicies = result.getPolicies();

                    boolean policyAlreadyAttached = false;

                    //ideally if policy is already attached, it will not even come here
                    //but in case user data is wiped out and the global variable is reset
                    //then it might come here
                    for (Policy policy : listPolicies) {
                        if (policy.getPolicyName().equals("TestIoTPolicy")) {
                            policyAlreadyAttached = true;
                            break;
                        }
                    } //end for

                    if(policyAlreadyAttached){
                        //it already has it attached, do nothing
                        returnCallback = new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess();
                            }
                        };
                    }
                    else{
                        //it comes here only when the policy is not attached, attach it
                        AttachPrincipalPolicyRequest attachPrincipalPolicyRequest = new AttachPrincipalPolicyRequest();
                        attachPrincipalPolicyRequest.setPolicyName("TestIoTPolicy");
                        attachPrincipalPolicyRequest.setPrincipal(credentialsProvider.getIdentityId());

                        iotClient.attachPrincipalPolicy(attachPrincipalPolicyRequest);


                        returnCallback = new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess();
                            }
                        };
                    }
                }
                catch (final Exception e) {
                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    };
                }


                handler.post(returnCallback);

            }

        }).start();


    }
}
