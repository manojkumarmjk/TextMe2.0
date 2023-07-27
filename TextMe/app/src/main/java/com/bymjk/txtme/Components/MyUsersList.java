package com.bymjk.txtme.Components;

import com.bymjk.txtme.Models.User;

import java.util.ArrayList;

public class MyUsersList {

    private ArrayList<User> myList;
    private static MyUsersList instance;

    private MyUsersList() {
        myList = new ArrayList<>();
    }

    public static MyUsersList getInstance() {
        if (instance == null) {
            instance = new MyUsersList();
        }
        return instance;
    }

    public static ArrayList<User> getList() {
        return instance.myList;
    }

    public void setList(ArrayList<User> list) {
        myList = list;
    }

}
