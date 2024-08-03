package com.bymjk.txtme.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bymjk.txtme.Models.ChatRoom;
import java.util.List;

@Dao
public interface ChatRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatRoom chatRoom);

    @Query("SELECT * FROM chat_rooms WHERE roomId = :roomId")
    ChatRoom getChatRoomById(String roomId);

    @Query("SELECT * FROM chat_rooms ORDER BY lastMsgTime DESC")
    List<ChatRoom> getAllChatRooms();

    @Query("DELETE FROM chat_rooms WHERE roomId = :roomId")
    void deleteByRoomId(String roomId);
}

