package com.bymjk.txtme.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import org.parceler.Parcel;

@Parcel
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull private String uid = "";
    private String name;
    private String phoneNumber;
    private String profileImage;
    private String about;
    private String token;

    public User() {
    }

    public User(String uid, String name, String phoneNumber, String about, String profileImage) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.about = about;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


