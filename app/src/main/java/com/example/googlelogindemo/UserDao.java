package com.example.googlelogindemo;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface UserDao {


    @Insert
    Long register(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM User WHERE userName =:username AND password =:password")
    LiveData<User> login(String username, String password);


    @Query("SELECT * FROM User WHERE userId =:userid")
    LiveData<User> userDetails(Long userid);



}
