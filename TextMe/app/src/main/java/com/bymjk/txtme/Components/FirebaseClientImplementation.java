package com.bymjk.txtme.Components;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bymjk.txtme.Models.AppVersion;
import com.bymjk.txtme.Models.AvailableUser;
import com.bymjk.txtme.Models.Features;
import com.bymjk.txtme.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class FirebaseClientImplementation {

    public BehaviorSubject<User> userDetails = BehaviorSubject.create();
    public BehaviorSubject<AppVersion> appVersionUpdate = BehaviorSubject.create();
    public BehaviorSubject<AvailableUser> availableUser = BehaviorSubject.create();
    public BehaviorSubject<String> messages = BehaviorSubject.create();
    public BehaviorSubject<String> calls = BehaviorSubject.create();
    public BehaviorSubject<String> statuses = BehaviorSubject.create();


    public void getUserDetails(String uid){
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            userDetails.onNext(user);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void postAvailableUser(String mPhoneNumber, AvailableUser availableUser){

        FirebaseDatabase.getInstance().getReference()
                .child("availableUser")
                .child(mPhoneNumber)
                .setValue(availableUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });
    }

    public void getAppVersion(){
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("appUpdate");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AppVersion appVersion;

                        Features features = snapshot.child("features").getValue(Features.class);
                        long timestamp;

                        Long timestamp1 = snapshot.child("timestamp").getValue(Long.class);
                        if (timestamp1 != null) {
                            timestamp = timestamp1;
                            // do something with timestampValue
                        }
                        else {
                            timestamp = System.currentTimeMillis();
                        }
                        int totalFeatures;
                        Integer totalFeatures1 = snapshot.child("totalFeatures").getValue(Integer.class);
                        if (totalFeatures1 != null) {
                            totalFeatures = totalFeatures1;
                            // do something with timestampValue
                        }
                        else {
                            totalFeatures = 0;
                        }
                        String url = snapshot.child("url").getValue(String.class);
                        String version = snapshot.child("version").getValue(String.class);

                        appVersion = new AppVersion(features,timestamp,totalFeatures,url,version);
                        appVersionUpdate.onNext(appVersion);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase Error",error.getMessage());
                    }
                });

    }




}
