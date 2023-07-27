package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String UserId;
    User user;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        actionBar =getSupportActionBar();

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(" Settings");
        }

//        setSupportActionBar(binding.toolbar3);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        UserId = auth.getUid();

        if (UserId != null) {
            database.getReference().child("users").child(UserId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user = snapshot.getValue(User.class);

                            if (user != null) {
                                if (user.getName() != null) {
                                    binding.userName.setText(user.getName());
                                }
                                if (user.getAbout() != null) {
                                    binding.userAbout.setText(user.getAbout());
                                } else {
                                    binding.userAbout.setText("Hey There! I am using TextMe");
                                    user.setAbout("Hey There ! I am using TextMe");
                                }
                                if (user.getProfileImage() != null) {
                                    Glide.with(SettingsActivity.this).load(user.getProfileImage())
                                            .placeholder(R.drawable.avatar)
                                            .into(binding.userProfileImage);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


        binding.updateProfileTab.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, MyProfileActivity.class);
            intent.putExtra("userName", user.getName());
            intent.putExtra("userAbout", user.getAbout());
            intent.putExtra("userPhone", user.getPhoneNumber());
            intent.putExtra("userProfile", user.getProfileImage());
            startActivity(intent);
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}