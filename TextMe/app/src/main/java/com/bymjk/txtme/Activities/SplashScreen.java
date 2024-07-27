package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bymjk.txtme.BuildConfig;
import com.bymjk.txtme.Components.FirebaseClientImplementation;
import com.bymjk.txtme.Components.HelperFunctions;
import com.bymjk.txtme.Components.UpdateDialog;
import com.bymjk.txtme.DB.FirebaseToRoomSync;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
import com.bymjk.txtme.TextMeApplication;
import com.bymjk.txtme.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.parceler.Parcels;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    ActivitySplashScreenBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseClientImplementation firebaseClientImpl = new FirebaseClientImplementation();
    String newVersion, newUrl;
    long newTimestamp;
    long downloadId;

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    int daysDiffToUpdate;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        firebaseClientImpl.getAppVersion();

        firebaseClientImpl.appVersionUpdate.subscribe(appVersion -> {
            newVersion = appVersion.getAppVersion();
            newUrl = appVersion.getAppUrl();
            newTimestamp = appVersion.getAppUpdateTimestamp();

            daysDiffToUpdate = HelperFunctions.getDaysDiff(newTimestamp,System.currentTimeMillis());

            if (BuildConfig.VERSION_NAME.equals(newVersion)) {
                goToAnotherActivity();
            }
            else {
                UpdateDialog updateDialog = new UpdateDialog();
                updateDialog.ShowUpdateDialog(this, appVersion, daysDiffToUpdate,SplashScreen.this);
            }
        });



    }

    public void goToAnotherActivity(){
        if (auth.getCurrentUser() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    intent.putExtra("users", Parcels.wrap(getAllUsers()));
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, PhonenumberActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);

        }
    }

    private ArrayList<User> getAllUsers(){

        ArrayList<User> users = new ArrayList<>();
        String myUid = FirebaseAuth.getInstance().getUid();
        FirebaseToRoomSync sync  = new FirebaseToRoomSync(this, myUid);
        if (!sync.getAllUsers(true).isEmpty()) {
            users = new ArrayList<>(sync.getAllUsers(true));
            return users;
        } else if(!sync.getAllUsers(false).isEmpty()){
            users = new ArrayList<>(sync.getAllUsers(false));
            return users;
        }

        return users;
    }

}