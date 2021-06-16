package com.java.kaboome.data.converters;

import androidx.room.TypeConverter;

import com.java.kaboome.constants.UserGroupStatusConstants;

public class UserGroupStatusConstantsConverter {

        @TypeConverter
        public static UserGroupStatusConstants fromString(String action) {
            return action == null ? null : UserGroupStatusConstants.get(action);
        }

        @TypeConverter
        public static String toString(UserGroupStatusConstants userGroupStatusConstants) {
            return userGroupStatusConstants == null ? null : userGroupStatusConstants.getAction();
        }
}
