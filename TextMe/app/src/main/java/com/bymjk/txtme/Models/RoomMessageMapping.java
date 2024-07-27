package com.bymjk.txtme.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room_message_mapping")
public class RoomMessageMapping {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String roomId;
    private String messageId;

    public RoomMessageMapping() {
    }

    public RoomMessageMapping(int id, String roomId, String messageId) {
        this.id = id;
        this.roomId = roomId;
        this.messageId = messageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}

