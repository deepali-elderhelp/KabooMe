package com.java.kaboome.data.persistence;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

import com.java.kaboome.data.converters.StringTSMapConverter;
import com.java.kaboome.data.converters.UserGroupStatusConstantsConverter;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.entities.Invitation;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.entities.User;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.entities.UserGroupConversation;


//@Database(entities = {UserGroup.class, User.class, Invitation.class, Group.class, GroupUser.class, Message.class, GroupUnreadCount.class}, version = 1)
@Database(entities = {User.class, UserGroup.class, Group.class, GroupUser.class, Invitation.class, GroupRequest.class, Message.class, UserGroupConversation.class}, version = 1)
@TypeConverters({UserGroupStatusConstantsConverter.class})
public abstract class KabooMeDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "kaboome_db";

    private static KabooMeDatabase instance;

    public static KabooMeDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    KabooMeDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract UserGroupDao getUserGroupDao();

    public abstract UserDao getUserDao();

    public abstract InvitiationDao getInvitationDao();

    public abstract MessageDao getMessageDao();

//    public abstract GroupUnreadCountDao getGroupUnreadCountDao();

    public abstract GroupDao getGroupDao();

    public abstract GroupUserDao getGroupUserDao();

    public abstract GroupRequestsDao getGroupRequestsDao();

    public abstract UserGroupConversationDao getUserGroupConversationsDao();
}
