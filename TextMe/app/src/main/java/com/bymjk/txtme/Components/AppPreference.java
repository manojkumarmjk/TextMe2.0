package com.bymjk.txtme.Components;

import android.content.SharedPreferences;
import android.util.Log;

import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.TextMeApplication;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AppPreference {

    private static String sAppPrefName = "appPreferences";

    private User user = new User();

    private ArrayList<User> users = new ArrayList<>();


    public void setUser(User user) {
        this.user = user;

        storeInPreferences();
    }

    public User getUser() {
        return user;
    }

    public void setUsersList(ArrayList<User> users) {
        this.users = users;
        storeInPreferences();
    }

    public ArrayList<User> getUsersList() {
        return users;
    }

    // Loads from preferences
    public static AppPreference loadFromPreferences() {
        SharedPreferences sharedPreferences = TextMeApplication.getInstance().getMainActivity().getSharedPreferences();

        AppPreference appPrefs = null;

        try {
            String strJson = sharedPreferences.getString(sAppPrefName, "");
            Gson gson = new Gson();
            appPrefs = gson.fromJson(strJson, AppPreference.class);
        } catch (Exception e) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(sAppPrefName);
            editor.commit();
        }

        if (appPrefs == null)
            appPrefs = new AppPreference();

        return appPrefs;
    }

    // Stores (persists) in preferences
    public void storeInPreferences() {
        try {
            SharedPreferences.Editor editor = TextMeApplication.getInstance().getMainActivity().getSharedPreferences().edit();
            Gson gson = new Gson();
            String strJson = gson.toJson(this);
            editor.putString(sAppPrefName, strJson);
            editor.commit();
        } catch (Exception e) {
            Log.e("AppPreferences", "AppPreferences Error = ", e);
        }
    }
//
    // clear preferences
    public void clearAppPreferences() {
        try {
            SharedPreferences.Editor editor = TextMeApplication.getInstance().getMainActivity().getSharedPreferences().edit();
            editor.remove(sAppPrefName);
            editor.commit();
        } catch (Exception e) {
            Log.e("Apollo", "Error", e);
        }
    }

}
