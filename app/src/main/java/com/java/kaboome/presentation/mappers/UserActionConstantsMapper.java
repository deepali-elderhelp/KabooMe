package com.java.kaboome.presentation.mappers;

import com.java.kaboome.constants.UserActionConstants;

public class UserActionConstantsMapper {

    public static UserActionConstants getConstant(String action){

        return UserActionConstants.get(action);
    }



}
