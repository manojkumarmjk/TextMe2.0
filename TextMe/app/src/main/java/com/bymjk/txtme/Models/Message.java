package com.bymjk.txtme.Models;

import com.bymjk.txtme.Components.HelperFunctions;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {

    public enum MessageType {
        AUDIO("audio"),
        VIDEO("video"),
        IMAGE("image"),
        DOCS("docs"),
        DATE_SEPARATOR("date_separator"),
        GENERAL_ANNOUNCEMENT("general_announcement"),
        MARKETING_MESSAGE("marketing_message"),
        SUPPORT_MESSAGE("support_message"),
        LOCATION("location"),
        WEATHER("weather"),
        OTP_AUTH("otp_auth"),
        CONTACT_DETAIL("contact_detail"),
        POLL("poll"),
        TEXT("text"); // For general text messages

        private final String value;

        MessageType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static MessageType fromString(String value) {
            for (MessageType type : MessageType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown message type: " + value);
        }
    }

    @PrimaryKey
    @NonNull private String massageId = "";
    private String massage = "";
    private String senderId = "";
    private String imageUrl = "";
    private long timestamp = 0;
    private int feeling = -1;
    private int messageStatus = 0;
    private String messageType = MessageType.TEXT.getValue(); // Default type is TEXT

    public Message() {
    }

    public Message(String massage, String senderId, long timestamp, int messageStatus, String messageType) {
        this.massage = massage;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageStatus = messageStatus;
        this.messageType = messageType;
    }

    public String getMassageId() {
        return massageId;
    }

    public void setMassageId(String massageId) {
        this.massageId = massageId;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getDay() {
        return HelperFunctions.getFormattedDate(this.timestamp);
    }
}

