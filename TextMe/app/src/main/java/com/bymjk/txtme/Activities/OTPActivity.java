package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bymjk.txtme.DB.FirebaseToRoomSync;
import com.bymjk.txtme.Models.AvailableUser;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OnOtpCompletionListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;

//    ArrayList<AvailableUser> availableUsersList;
//
//    AvailableUser availableUser1;
    String verificationId;

//    String mName, mPhone, mUid;

    //ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        /*dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP ...");
        dialog.setCancelable(false);
        dialog.show();*/

        String phoneNumber = getIntent().getStringExtra("phonenumber");
        String phonenumber_ccode = "+91" + phoneNumber;
        binding.phonelbl.setText("Verify " + phonenumber_ccode);


        binding.otpView.requestFocus();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phonenumber_ccode)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        //Log.d("code", "onCodeSent:" + verifyId);
                        //dialog.dismiss();
                        verificationId = verifyId;
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        // OTPActivity.this.enableUserManuallyInputCode();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                try {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    Log.d("code", "onCodeSent - otp:" + otp);
                    Log.d("code", "onCodeSent - verification id:" + verificationId);
                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

//                            database.getReference().child("availableUser").child(phonenumber_ccode)
//                                    .addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            availableUser1 = snapshot.getValue(AvailableUser.class);
//                                            if (availableUser1 != null) {
//                                                mName = availableUser1.getName();
//                                                mPhone = availableUser1.getPhone();
//                                                mUid = availableUser1.getUid();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//                        }
                                String uid = auth.getUid();
                                if (uid != null) {
                                    database.getReference().child("availableUser").child(phonenumber_ccode)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    // Result will be holded Here

//                                            availableUsersList.clear();
//                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                                                AvailableUser availableUser = snapshot1.getValue(AvailableUser.class);
//                                                if (availableUser != null) {
//                                                    availableUsersList.add(availableUser);
//                                                }
//                                            }

                                                    Intent intent;
//                                            AvailableUser availableUser = new AvailableUser(mName, phonenumber_ccode, );
                                                    if (snapshot.exists()) {
                                                        intent = new Intent(OTPActivity.this, MainActivity.class);
                                                        intent.putExtra("users", Parcels.wrap(getAllUsers()));
                                                    } else {
                                                        intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                                                    }
                                                    startActivity(intent);
                                                    finishAffinity();
                                                    Toast.makeText(OTPActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(OTPActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (Exception e){
                    e.printStackTrace();
                }
        }
    });

}

    private ArrayList<User> getAllUsers(){

        ArrayList<User> users = new ArrayList<>();
        String myUid = FirebaseAuth.getInstance().getUid();
        FirebaseToRoomSync sync  = new FirebaseToRoomSync(this, myUid);
        if (!sync.getAllUsers(true).isEmpty()) {
            users = new ArrayList<>(sync.getAllUsers(true));
            return users;
        }

        return users;
    }

}