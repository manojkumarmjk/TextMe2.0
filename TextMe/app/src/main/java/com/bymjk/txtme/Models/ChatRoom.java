package com.bymjk.txtme.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_rooms")
public class ChatRoom {
    @PrimaryKey
    @NonNull
    private String roomId;
    private String lastMsg;
    private long lastMsgTime;
    private String senderId;

    // Getters and setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}

