package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bymjk.txtme.Adapters.TopStatusAdapter;
import com.bymjk.txtme.Models.Status;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.Models.UserStatus;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ActivityStatusBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {

    ActivityStatusBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;

    User user;
    ActionBar actionBar;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        userStatuses = new ArrayList<>();

        actionBar =getSupportActionBar();

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(" Status");
        }

        binding.statusList.showShimmerAdapter();

        binding.bottomNavigationView.setSelectedItemId(R.id.status);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploding Image...");
        dialog.setCancelable(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.statusList.setLayoutManager(layoutManager);


        binding.fabAddStatus.setOnClickListener(view -> {
            ImagePicker.with(StatusActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .galleryOnly()
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        String Users_uid = FirebaseAuth.getInstance().getUid();
        if (Users_uid != null) {
            database.getReference().child("users").child(Users_uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user = snapshot.getValue(User.class);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStatuses.clear();
                    for (DataSnapshot storySnapshot : snapshot .getChildren()){
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();
                        for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }
                        status.setStatuses(statuses);

                        userStatuses.add(status);
                    }
                    binding.statusList.hideShimmerAdapter();
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.status){

                }
                else if(item.getItemId() == R.id.chats){
                    Intent intent = new Intent(StatusActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId() == R.id.calls){
                    Intent intent = new Intent(StatusActivity.this, CallsActivity.class);
                    startActivity(intent);
                    finish();
                }


                return false;
            }
        });

        statusAdapter = new TopStatusAdapter(this,userStatuses);
        binding.statusList.setAdapter(statusAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( data != null) {
            if(data.getData() != null) {

                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String,Object> obj = new HashMap<>();
                                    obj.put("name",userStatus.getName());
                                    obj.put("profileImage",userStatus.getProfileImage());
                                    obj.put("lastUpdated",userStatus.getLastUpdated());

                                    String imgUrl = uri.toString();
                                    Status status = new Status(imgUrl,userStatus.getLastUpdated());

                                    String user_uid_stories = FirebaseAuth.getInstance().getUid();
                                    if (user_uid_stories != null) {

                                        database.getReference()
                                                .child("stories")
                                                .child(user_uid_stories)
                                                .updateChildren(obj);

                                        database.getReference().child("stories")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("statuses")
                                                .push()
                                                .setValue(status);
                                    }
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(StatusActivity.this,MainActivity.class));
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