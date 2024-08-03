package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bymjk.txtme.databinding.ActivityPhonenumberBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;

public class PhonenumberActivity extends AppCompatActivity {

    ActivityPhonenumberBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhonenumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkLocationPermissions();

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding.phonebox.requestFocus();

       binding.continuebtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(PhonenumberActivity.this,OTPActivity.class);
               intent.putExtra("phonenumber", binding.phonebox.getText().toString());
               startActivity(intent);
           }
       });
    }


    private void checkLocationPermissions() {
        String[] permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        } else {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        }

        // Check if any of the required permissions are not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            // Request the missing permissions
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, start accessing the location
            } else {
                // Permission was denied, show a message to the user
                Toast.makeText(this, "Location permission is required for this app to work", Toast.LENGTH_SHORT).show();
            }
        }
    }

}