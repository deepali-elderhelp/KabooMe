package com.java.kaboome.presentation.helpers;

public class ErrorMessageHelper {

    public static String getErrorMessage(String errorMessageFromServer){
        if(errorMessageFromServer == null || errorMessageFromServer.isEmpty()){
            return "";
        }
        if(errorMessageFromServer.contains("500")){
            return "Sorry, something went wrong at the server.";
        }
        if(errorMessageFromServer.contains("521")){
            return "Sorry, the group has already expired or deleted by the Admin";
        }
        if(errorMessageFromServer.contains("531")){
            return "Sorry, the group is closed for new requests";
        }
        return "Sorry, something went wrong";
    }
}
