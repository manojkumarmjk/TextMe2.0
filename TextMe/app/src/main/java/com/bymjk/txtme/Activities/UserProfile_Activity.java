package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ActivityChatsBinding;
import com.bymjk.txtme.databinding.ActivityUserProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile_Activity extends AppCompatActivity {

    ActivityUserProfileBinding binding;

    String profile;
    String name;
    String phoneNumber;
    String uid, senderId, senderRoom ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }




        name = getIntent().getStringExtra("name");
        phoneNumber = getIntent().getStringExtra("phonenumber");




        binding.UserName.setText(name);
        binding.phoneNumber.setText(phoneNumber);
        binding.audioCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityCompat.requestPermissions(UserProfile_Activity.this, new String[]{Manifest.permission.CALL_PHONE}, 100);

                FirebaseDatabase.getInstance().getReference()
                        .child("availableUser")
                        .child(phoneNumber)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    uid =  snapshot.child("uid").getValue(String.class);
                                    senderId = FirebaseAuth.getInstance().getUid();

                                    if (uid != null) {
                                        senderRoom = senderId + uid;
                                    }

                                    SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a");
                                    sdfTime.setTimeZone(TimeZone.getDefault());
                                    String currentTime = sdfTime.format(new Date());
                                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM");
                                    sdfDate.setTimeZone(TimeZone.getDefault());
                                    currentTime = "at " + currentTime + " on " + sdfDate.format(new Date());


                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("time", currentTime);

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("calls")
                                            .child(senderRoom)
                                            .updateChildren(map);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(phone_intent);
            }
        });

        profile = getIntent().getStringExtra("image");
        Glide.with(UserProfile_Activity.this)
                .load(profile)
                .placeholder(R.drawable.avatar)
                .into(binding.UserprofileImage);

        binding.chatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile_Activity.this,ChatsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(100, permissions, grantResults);
        if (requestCode == 100)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }
}