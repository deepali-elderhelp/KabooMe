package com.java.kaboome.data.persistence;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.java.kaboome.data.entities.User;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Query("SELECT * FROM user WHERE userId = :user_id")
    LiveData<User> getUser(String user_id);

    @Query("UPDATE user SET userName = :user_name WHERE userId = :user_id")
    void updateUserName(String user_name, String user_id);

    @Query("UPDATE user SET email = :user_email WHERE userId = :user_id")
    void updateUserEmail(String user_email, String user_id);

    @Query("UPDATE user SET imageUpdateTimestamp = :image_update_time_stamp WHERE userId = :user_id")
    void updateUserImageTimeStamp(Long image_update_time_stamp, String user_id);
}
