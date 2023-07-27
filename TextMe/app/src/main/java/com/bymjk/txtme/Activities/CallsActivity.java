package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.bymjk.txtme.Adapters.CallingViewAdapter;
import com.bymjk.txtme.Adapters.UsersAdapter;
import com.bymjk.txtme.Components.HelperFunctions;
import com.bymjk.txtme.Components.MyUsersList;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ActivityCallsBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class CallsActivity extends AppCompatActivity {

    ActivityCallsBinding binding;
    ActionBar actionBar;

    ArrayList<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        users = MyUsersList.getList();

        actionBar =getSupportActionBar();
        ActivityCompat.requestPermissions(CallsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 100);

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(" Calls ");
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.allCallingUsers.setLayoutManager(layoutManager);

        CallingViewAdapter callingViewAdapter = new CallingViewAdapter(this, users);
        binding.allCallingUsers.setAdapter(callingViewAdapter);

        binding.bottomNavigationView.setSelectedItemId(R.id.calls);

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.status){
                    Intent intent = new Intent(CallsActivity.this,StatusActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId() == R.id.chats){
                    Intent intent = new Intent(CallsActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId() == R.id.calls){

                }


                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(CallsActivity.this,MainActivity.class));
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

//    @Override
//    public boolean onSupportNavigateUp() {
//        // handle the up button press here
//
//        Intent intent = new Intent(CallsActivity.this,MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//
//        return super.onSupportNavigateUp();
//    }

}