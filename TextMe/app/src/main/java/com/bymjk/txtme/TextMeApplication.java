package com.bymjk.txtme;

import android.app.Application;
import android.content.Context;

import com.bymjk.txtme.Activities.MainActivity;

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
}
