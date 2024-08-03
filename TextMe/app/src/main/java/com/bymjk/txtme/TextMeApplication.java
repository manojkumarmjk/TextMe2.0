package com.bymjk.txtme;

import android.app.Application;
import android.content.Context;

import com.bymjk.txtme.Activities.MainActivity;
import com.bymjk.txtme.Adapters.UsersAdapter;
import com.bymjk.txtme.Components.GenericCallback;
import com.bymjk.txtme.DB.UserRepository;
import com.bymjk.txtme.Models.User;

import java.util.ArrayList;

public class TextMeApplication extends Application {

    private static TextMeApplication instance;
    private MainActivity mMainActivity;

    public TextMeApplication() {
        instance = this;
    }
    public void setMainActivity(MainActivity mainActivity){
        mMainActivity = mainActivity ;
    }
    public static Context getContext() {
        return instance;
    }
    public static TextMeApplication getInstance() {
        return instance;
    }
    public MainActivity getMainActivity(){
        return mMainActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize your application here
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        saveUsersInPreference();
    }

    private void saveUsersInPreference(){
        UserRepository userRepository = new UserRepository(this);

        userRepository.getUsersByChatRoom(new GenericCallback<ArrayList<User>>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

}
