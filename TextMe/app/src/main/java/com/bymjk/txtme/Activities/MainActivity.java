package com.bymjk.txtme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bymjk.txtme.Adapters.TopStatusAdapter;
import com.bymjk.txtme.Components.AppPreference;
import com.bymjk.txtme.Components.HelperFunctions;
import com.bymjk.txtme.Components.MyUsersList;
import com.bymjk.txtme.DB.FirebaseToRoomSync;
import com.bymjk.txtme.Models.AvailableUser;
import com.bymjk.txtme.Models.UserStatus;
import com.bymjk.txtme.R;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.Adapters.UsersAdapter;
import com.bymjk.txtme.TextMeApplication;
import com.bymjk.txtme.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<User> users;

    private AppPreference mAppPreferences;

    public static String PREFS_NAME = "prefs";

    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    AvailableUser availableUser;
    ProgressDialog dialog;

    boolean onresume = false;

    private Map<User, Long> userLastMsgTimeMap = new HashMap<>();

    User user;

    ArrayList<String> contactsList;
    ArrayList<String> newContactList;

    String mPhoneNumber;
    String mName;
    String mUid;
    String add91, addNew91;
    ArrayList<User> newUsers;
    FirebaseToRoomSync sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextMeApplication.getInstance().setMainActivity(this);
        Intent intent = getIntent();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        users = Parcels.unwrap(intent.getParcelableExtra("users"));

        mAppPreferences = AppPreference.loadFromPreferences();

        FirebaseMessaging.getInstance()
                .getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                if(FirebaseAuth.getInstance().getUid() != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("token",token);
                    database.getReference()
                            .child("users")
                            .child(FirebaseAuth.getInstance().getUid())
                            .updateChildren(map);

                }
            }
        });

        HelperFunctions helperFunctions = new HelperFunctions();

        helperFunctions.storeUserInLocalDatabase();

        if (FirebaseAuth.getInstance().getUid()!=null ) {
            String myUid = FirebaseAuth.getInstance().getUid();
            sync = new FirebaseToRoomSync(this, myUid);
            sync.sync();
        }

        binding.statusList.setVisibility(View.GONE);
        binding.view3.setVisibility(View.GONE);
        binding.bottomNavigationView.setSelectedItemId(R.id.chats);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);

        contactsList = new ArrayList<>();
        newContactList = new ArrayList<>();

//        getContactList();

        getActualUser();



        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploding Image...");
        dialog.setCancelable(false);



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


//        binding.statusList.showShimmerAdapter();

//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
//        binding.statusList.setLayoutManager(layoutManager);
//
//        statusAdapter = new TopStatusAdapter(this,userStatuses);
//        binding.statusList.setAdapter(statusAdapter);


//        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                users.clear();
//                for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                    if (user != null) {
//                        User user = snapshot1.getValue(User.class);
//                        if (user != null && !user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
//                            users.add(user);
//                        }
//                    }
//                }
//                binding.recyclerview.hideShimmerAdapter();
//                usersAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        ArrayList<User> newUsers = TextMeApplication.getInstance().getMainActivity().getAppPreferences().getUsersList();
//        if (newUsers.size()>0) {
//            usersAdapter = new UsersAdapter(this,newUsers);
//            binding.recyclerview.setAdapter(usersAdapter);
//            binding.recyclerview.showShimmerAdapter();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    binding.recyclerview.hideShimmerAdapter();
//                }
//            },2500);
//            usersAdapter.notifyDataSetChanged();
//        }
//        else {
//            usersAdapter = new UsersAdapter(this,users);
//            binding.recyclerview.setAdapter(usersAdapter);
//            binding.recyclerview.showShimmerAdapter();
//        }

        newUsers = TextMeApplication.getInstance().getMainActivity().getAppPreferences().getUsersList();

        fetchUsersAndTimestamps();

        String UserId = auth.getUid();

        if (UserId != null) {
            database.getReference().child("users").child(UserId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user = snapshot.getValue(User.class);
                            if (user != null) {
                                mPhoneNumber = user.getPhoneNumber();
                                mName = user.getName();
                                mUid = auth.getUid();
                                availableUser = new AvailableUser(mName, mPhoneNumber, mUid);
                                database.getReference()
                                        .child("availableUser")
                                        .child(mPhoneNumber)
                                        .setValue(availableUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                            }
                                        });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

//        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    userStatuses.clear();
//                    for (DataSnapshot storySnapshot : snapshot .getChildren()){
//                        UserStatus status = new UserStatus();
//                        status.setName(storySnapshot.child("name").getValue(String.class));
//                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
//                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));
//
//                        ArrayList<Status> statuses = new ArrayList<>();
//                        for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
//                            Status sampleStatus = statusSnapshot.getValue(Status.class);
//                            statuses.add(sampleStatus);
//                        }
//                        status.setStatuses(statuses);
//
//                        userStatuses.add(status);
//                    }
//                    binding.statusList.hideShimmerAdapter();
//                    statusAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.status){
                    Intent intent = new Intent(MainActivity.this,StatusActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.chats){

                }
                else if(item.getItemId() == R.id.calls){
                    Intent intent = new Intent(MainActivity.this, CallsActivity.class);
                    startActivity(intent);
                }


                return false;
            }
        });
    }

    private void getContactList() {
        Cursor cursor = MainActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (cursor.moveToNext()) {
            if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) >= 0) {
                @SuppressLint("Range")
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (contactsList.contains(phone)) {
                    cursor.moveToNext();
                } else {
                    contactsList.add(phone);

                }
            }
        }

        for (String element : contactsList) {

            // If this element is not present in newList
            // then add it
            if (element.length() == 10) {
                addNew91 = "+91"+element;
                if (!newContactList.contains(addNew91)) {
                    newContactList.add(addNew91);
                }
            }
            else {
                if (!newContactList.contains(element)) {
                    newContactList.add(element);
                }
            }
        }

//        getActualUser();
        Log.d("contact: ", String.valueOf(contactsList.size()));

    }

    @SuppressLint({"CheckResult", "NotifyDataSetChanged"})
    private void getActualUser() {

        if(users == null) {
            if (!sync.getAllUsers(true).isEmpty()) {
                users = new ArrayList<>(sync.getAllUsers(true));
                binding.recyclerview.hideShimmerAdapter();
                if (usersAdapter != null) {
                    usersAdapter.notifyDataSetChanged();
                }
            } else if (!sync.getAllUsers(false).isEmpty()) {
                ArrayList<User> userArrayList = new ArrayList<>();
                FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<String> userIds = sync.getAllUserIDs();

                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);

                                for (String uid : userIds) {
                                    if (user != null && user.getUid().equals(uid) && !user.getUid().equals(FirebaseAuth.getInstance().getUid()) ) {
                                        userArrayList.add(user);
                                    }
                                }
                            }

                            users = new ArrayList<>(userArrayList);
                            usersAdapter = new UsersAdapter(MainActivity.this, users);
                            binding.recyclerview.setAdapter(usersAdapter);
                            if (usersAdapter != null) {
                                usersAdapter.notifyDataSetChanged();
                            }

                            TextMeApplication.getInstance().getMainActivity().getAppPreferences().setUsersList(userArrayList);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                sync.usersSubject.subscribe(myUsers -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            users = myUsers;
                            usersAdapter = new UsersAdapter(MainActivity.this, users);
                            binding.recyclerview.setAdapter(usersAdapter);
                            if (usersAdapter != null) {
                                usersAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                });
            }
        } else {
            usersAdapter = new UsersAdapter(this, users);
            binding.recyclerview.setAdapter(usersAdapter);
            binding.recyclerview.hideShimmerAdapter();
            if (usersAdapter != null) {
                usersAdapter.notifyDataSetChanged();
            }
        }

        /*
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);

                        for (String number : newContactList){
                            if (user != null && user.getPhoneNumber().equals(number) && !user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                                users.add(user);
                            }
                        }
                    }
                    Log.d("actual: ", String.valueOf(users.size()));
                    MyUsersList myUsersList = MyUsersList.getInstance();
                    myUsersList.setList(users);
                    TextMeApplication.getInstance().getMainActivity().getAppPreferences().setUsersList(users);
                    binding.recyclerview.hideShimmerAdapter();
                    if(usersAdapter!=null) {
                        usersAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

    }

    private void fetchUsersAndTimestamps() {
        if (users != null && !users.isEmpty()) {
            fetchLastMsgTimestamps(users);
        }
    }

    private void fetchLastMsgTimestamps(ArrayList<User> userList) {
        for (User user : userList) {
            String senderId = FirebaseAuth.getInstance().getUid();
            String senderRoom = senderId + user.getUid();

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long timeStamp = 0;
                            if (snapshot.exists() && snapshot.child("lastMsgTime").getValue(Long.class) != null) {
                                timeStamp = snapshot.child("lastMsgTime").getValue(Long.class);
                            }

                            if (onresume){
                                userLastMsgTimeMap.remove(user);
                                userLastMsgTimeMap.put(user, timeStamp);
                            }else {
                                userLastMsgTimeMap.put(user, timeStamp);
                            }

                            if (userLastMsgTimeMap.size() == (!newUsers.isEmpty() ? newUsers.size() : users.size())) {
                                sortUsersByTimestamp();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FirebaseError", "Error fetching timestamp: " + error.getMessage());
                        }
                    });
        }
    }

    private void sortUsersByTimestamp() {
        List<Map.Entry<User, Long>> sortedEntries = new ArrayList<>(userLastMsgTimeMap.entrySet());

        Collections.sort(sortedEntries, new Comparator<Map.Entry<User, Long>>() {
            @Override
            public int compare(Map.Entry<User, Long> entry1, Map.Entry<User, Long> entry2) {
                return Long.compare(entry2.getValue(), entry1.getValue());
            }
        });

        ArrayList<User> sortedUsers = new ArrayList<>();
        for (Map.Entry<User, Long> entry : sortedEntries) {
            sortedUsers.add(entry.getKey());
        }

        // Update the adapter with the sorted user list
        usersAdapter = new UsersAdapter(this, sortedUsers);
        binding.recyclerview.setAdapter(usersAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                usersAdapter.notifyDataSetChanged();
                onresume = true;
            }
        }, 100);
    }

    public SharedPreferences getSharedPreferences() {
        return this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    public AppPreference getAppPreferences() { return mAppPreferences; }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if( data != null) {
//            if(data.getData() != null) {
//
//                dialog.show();
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                Date date = new Date();
//                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");
//                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()){
//                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    UserStatus userStatus = new UserStatus();
//                                    userStatus.setName(user.getName());
//                                    userStatus.setProfileImage(user.getProfileImage());
//                                    userStatus.setLastUpdated(date.getTime());
//
//                                    HashMap<String,Object> obj = new HashMap<>();
//                                    obj.put("name",userStatus.getName());
//                                    obj.put("profileImage",userStatus.getProfileImage());
//                                    obj.put("lastUpdated",userStatus.getLastUpdated());
//
//                                    String imgUrl = uri.toString();
//                                    Status status = new Status(imgUrl,userStatus.getLastUpdated());
//
//                                    String user_uid_stories = FirebaseAuth.getInstance().getUid();
//                                    if (user_uid_stories != null) {
//
//                                        database.getReference()
//                                                .child("stories")
//                                                .child(user_uid_stories)
//                                                .updateChildren(obj);
//
//                                        database.getReference().child("stories")
//                                                .child(FirebaseAuth.getInstance().getUid())
//                                                .child("statuses")
//                                                .push()
//                                                .setValue(status);
//                                    }
//                                    dialog.dismiss();
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//        }
//
//    }

    @Override
    protected void onResume() {
        super.onResume();

//        ArrayList<User> newUsers = TextMeApplication.getInstance().getMainActivity().getAppPreferences().getUsersList();
//        fetchLastMsgTimestamps(newUsers);

        String currentId = FirebaseAuth.getInstance().getUid();
        if (currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("Online");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        if (currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("Offline");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.search) {

        }
        if(item.getItemId()==R.id.settings){
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
        }
        if(item.getItemId()==R.id.invite){

        }
        if(item.getItemId()==R.id.group){
            startActivity(new Intent(MainActivity.this,GroupChatActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


}