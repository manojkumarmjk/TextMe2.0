package com.bymjk.txtme.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bymjk.txtme.Models.RoomMessageMapping;

import java.util.List;

@Dao
public interface RoomMessageMappingDao {
    @Insert
    void insert(RoomMessageMapping roomMessageMapping);

    @Query("SELECT messageId FROM room_message_mapping WHERE roomId = :roomId")
    List<String> getMessageIdsByRoomId(String roomId);

    @Query("SELECT * FROM room_message_mapping WHERE roomId = :roomId AND messageId = :messageId")
    RoomMessageMapping getMapping(String roomId, String messageId);

    @Query("SELECT DISTINCT roomId FROM room_message_mapping")
    List<String> getAllRoomIds();

    @Query("DELETE FROM room_message_mapping WHERE roomId = :roomId AND messageId = :messageId")
    void deleteByRoomIdAndMessageId(String roomId, String messageId);
}

