package com.bymjk.txtme.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.bymjk.txtme.Models.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE uid = :uid")
    User getUserByUid(String uid);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();
}

