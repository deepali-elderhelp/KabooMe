package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum GroupActionConstants {

    UPDATE_GROUP_NAME("updateGroupName"),
    UPDATE_GROUP_DESC("updateGroupDesc"),
    UPDATE_GROUP_EXPIRY("updateGroupExpiry"),
    UPDATE_GROUP_IMAGE("updateGroupImage"),
    UPDATE_GROUP_NAME_PRIVACY_IMAGE("updateGroupNamePrivacyImage"),
    UPDATE_GROUP_REQUESTS_SETTING("updateGroupRequestsSetting"),
    UPDATE_GROUP_USER_ROLE_AND_ALIAS("updateGroupUserRoleAndAlias"),
    UPDATE_GROUP_USER_IMAGE("updateGroupUserImage"),
    UPDATE_GROUP_CURRENT_STATUS("updateGroupCurrentStatus"),
    UPDATE_GROUP_ROLE_AND_ALIAS_AND_IMAGE("updateGroupUserRoleAliasAndImage"),
    UPDATE_GROUP_CONV_ROLE_AND_ALIAS_AND_IMAGE("updateGroupUserRoleAndAlias_conv"),
    UPDATE_GROUP_USER_NOTIFICATION("updateGroupUserNotification"),
    UPDATE_GROUP_USERS_TO_ADMIN("updateGroupUsersToAdmin"),
//    UPDATE_GROUP_ADMINS_LAST_ACCESS("updateGroupAdminsLastAccess"),
//    UPDATE_GROUP_ADMINS_CACHE_CLEAR("updateGroupAdminsCacheClear"),
    REMOVE_GROUP_FOR_ALL("groupRemoveForAll"),
    REMOVE_GROUP_FOR_OTHER_USER("groupRemoveForOtherUser"),
    REMOVE_GROUP_FOR_USER("groupRemoveForCurrentUser"),
    REMOVE_GROUP_CONV_FOR_USER("updateGroupUserIsAdmin_conv");


    private String action;

    private static final Map<String,GroupActionConstants> GROUP_ACTION_CONSTANTS_MAP;

    GroupActionConstants(String value) {
        this.action = value;
    }

    public String getAction() {
        return this.action;
    }


    @Override
    public String toString() {
        return this.action;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String,GroupActionConstants> map = new ConcurrentHashMap<String, GroupActionConstants>();
        for (GroupActionConstants instance : GroupActionConstants.values()) {
            map.put(instance.getAction(),instance);
        }
        GROUP_ACTION_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static GroupActionConstants get (String name) {
        return GROUP_ACTION_CONSTANTS_MAP.get(name);
    }



}


