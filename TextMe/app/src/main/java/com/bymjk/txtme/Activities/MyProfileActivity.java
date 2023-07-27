package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ActivityMyProfileBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class MyProfileActivity extends AppCompatActivity {

    ActivityMyProfileBinding binding;
    ActionBar actionBar;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;

    int flag;

    String mName, mAbout, mProfile, mPhone, mUid;
    String newName, newAbout, newProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        actionBar =getSupportActionBar();

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(" Profile");
        }

        mName = getIntent().getStringExtra("userName");
        mAbout = getIntent().getStringExtra("userAbout");
        mPhone = getIntent().getStringExtra("userPhone");
        mProfile = getIntent().getStringExtra("userProfile");
        mUid = auth.getUid();


        binding.tvName.setText(mName);
        binding.tvAbout.setText(mAbout);
        binding.tvPhone.setText(mPhone);
        Glide.with(MyProfileActivity.this)
                .load(mProfile)
                .placeholder(R.drawable.avatar)
                .into(binding.profileimage);

        binding.editProfileImage.setOnClickListener(view -> {
            binding.editAboutLayout.setEnabled(false);
            binding.editNameLayout.setEnabled(false);
            binding.saveBtn.setVisibility(View.VISIBLE);

            ImagePicker.with(MyProfileActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .galleryOnly()
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
            flag = 1;
        });

        binding.editNameLayout.setOnClickListener(view -> {
            binding.tvName.setVisibility(View.INVISIBLE);
            binding.etName.setVisibility(View.VISIBLE);
            binding.saveBtn.setVisibility(View.VISIBLE);
            binding.editAboutLayout.setEnabled(false);
            binding.editProfileImage.setEnabled(false);
            flag = 2;

        });

        binding.editAboutLayout.setOnClickListener(view -> {
            binding.tvAbout.setVisibility(View.INVISIBLE);
            binding.etAbout.setVisibility(View.VISIBLE);
            binding.saveBtn.setVisibility(View.VISIBLE);
            binding.editNameLayout.setEnabled(false);
            binding.editProfileImage.setEnabled(false);
            flag = 3;
        });

        binding.saveBtn.setOnClickListener(view -> {
            if (flag == 1) {
                saveData(newProfile, 1);
            }
            else if (flag == 2) {
                newName = binding.etName.getText().toString().trim();
                saveData(newName, 2);
            }
            else if (flag == 3){
                newAbout = binding.etAbout.getText().toString().trim();
                saveData(newAbout,3);
            }
        });



    }

    private void saveData(String newData, int select) {


            if (select == 1){

                Toast.makeText(MyProfileActivity.this, "Your Profile Image Saved Successfully", Toast.LENGTH_SHORT).show();

            }
            else if (select == 2){
                User user = new User(mUid,newData,mPhone, mAbout,mProfile);
                database.getReference()
                        .child("users")
                        .child(mUid)
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                binding.saveBtn.setVisibility(View.INVISIBLE);
                                binding.editAboutLayout.setEnabled(true);
                                binding.editProfileImage.setEnabled(true);
                                binding.tvName.setText(newData);
                                binding.tvName.setVisibility(View.VISIBLE);
                                binding.etName.setVisibility(View.INVISIBLE);
                                Toast.makeText(MyProfileActivity.this, "Your Name Saved Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else if (select == 3){

                User user = new User(mUid,mName,mPhone,newData,mProfile);
                database.getReference()
                        .child("users")
                        .child(mUid)
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                binding.saveBtn.setVisibility(View.INVISIBLE);
                                binding.editNameLayout.setEnabled(true);
                                binding.editProfileImage.setEnabled(true);
                                binding.tvAbout.setText(newData);
                                binding.tvAbout.setVisibility(View.VISIBLE);
                                binding.etAbout.setVisibility(View.INVISIBLE);
                                Toast.makeText(MyProfileActivity.this, "Your Description Saved Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( data != null) {
            if(data.getData() != null) {
                selectedImage = data.getData();
                binding.profileimage.setImageURI(selectedImage);
                if (selectedImage != null) {
                    StorageReference reference = storage.getReference().child("Profile").child(Objects.requireNonNull(auth.getUid()));
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        newProfile = uri.toString();
                                        User user = new User(mUid,mName,mPhone, mAbout,newProfile);
                                        database.getReference()
                                                .child("users")
                                                .child(mUid)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        binding.saveBtn.setVisibility(View.INVISIBLE);
                                                        binding.editNameLayout.setEnabled(true);
                                                        binding.editAboutLayout.setEnabled(true);
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }

    }
}