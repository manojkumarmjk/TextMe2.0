package com.bymjk.txtme.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bymjk.txtme.Models.Message;
import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Query("SELECT * FROM messages WHERE massageId = :messageId")
    Message getMessageById(String messageId);

    @Query("SELECT * FROM messages WHERE senderId = :senderId ORDER BY timestamp ASC")
    List<Message> getMessagesBySenderId(String senderId);

    @Query("SELECT * FROM messages")
    List<Message> getAllMessages();

    @Query("SELECT * FROM messages WHERE massageId IN (SELECT messageId FROM room_message_mapping WHERE roomId = :roomId) ORDER BY timestamp ASC")
    List<Message> getMessagesByRoomId(String roomId);

    @Update
    void update(Message message);

    @Query("DELETE FROM messages WHERE massageId = :messageId")
    void deleteByMessageId(String messageId);
}

