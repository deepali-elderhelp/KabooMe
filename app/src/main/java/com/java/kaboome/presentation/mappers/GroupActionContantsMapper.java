package com.java.kaboome.presentation.mappers;

import com.java.kaboome.constants.GroupActionConstants;

public class GroupActionContantsMapper {

    public static GroupActionConstants getConstant(String action){

        return GroupActionConstants.get(action);
    }



}
