package com.bymjk.txtme.DB;

import com.bymjk.txtme.Components.MyUsersList;
import com.bymjk.txtme.Models.Message;
import com.bymjk.txtme.Models.RoomMessageMapping;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.TextMeApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.room.Room;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class FirebaseToRoomSync {

    private AppDatabase db;
    private MessageDao messageDao;
    private UserDao userDao;
    private RoomMessageMappingDao roomMessageMappingDao;
    private String myUid;
    private ExecutorService executorService;
    public BehaviorSubject<ArrayList<User>> usersSubject = BehaviorSubject.create();

    public FirebaseToRoomSync(Context context, String myUid) {
        db = Room.databaseBuilder(context, AppDatabase.class, "chat_database").build();
        userDao = db.userDao();
        messageDao = db.messageDao();
        roomMessageMappingDao = db.roomMessageMappingDao();
        this.myUid = myUid;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void sync() {

        roomsAndMessagesSync();

    }

    private void allUsersSync(){

        ExecutorService userExecutorService =  Executors.newSingleThreadExecutor();
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userExecutorService.execute(() -> {

                    List<String> userIds = getAllUserIDs();

                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);

                            for (String uid : userIds) {
                                if (user != null && user.getUid().equals(uid) && !user.getUid().equals(FirebaseAuth.getInstance().getUid()) && userDao.getUserByUid(uid) == null ) {
                                    userDao.insert(user);
                                }
                            }
                        }

                        usersSubject.onNext(new ArrayList<>(userDao.getAllUsers()));

                        ArrayList<User> userArrayList = new ArrayList<>(userDao.getAllUsers());

                        TextMeApplication.getInstance().getMainActivity().getAppPreferences().setUsersList(userArrayList);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void roomsAndMessagesSync(){
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                executorService.execute(() -> {
                    for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                        String roomId = chatSnapshot.getKey();
                        if (roomId != null && roomId.startsWith(myUid)) {
                            for (DataSnapshot messageSnapshot : chatSnapshot.child("massages").getChildren()) {

                                Message message = messageSnapshot.getValue(Message.class);

                                String messageId = messageSnapshot.getKey();
                                String imageUrl = messageSnapshot.child("imageUrl").getValue(String.class);
                                int feeling = messageSnapshot != null && messageSnapshot.child("feeling") != null ? messageSnapshot.child("feeling").getValue(Integer.class) != null ? messageSnapshot.child("feeling").getValue(Integer.class) : -1 : -1;

                                if (message != null && messageDao.getMessageById(messageId) == null) {
                                    message.setMassageId(messageId);
                                    message.setImageUrl(imageUrl);
                                    message.setFeeling(feeling);
                                    if (messageDao.getMessageById(messageId) == null) {
                                        messageDao.insert(message);
                                    }
                                }


                                if (roomMessageMappingDao.getMapping(roomId, messageId) == null) {
                                    RoomMessageMapping roomMessageMapping = new RoomMessageMapping();
                                    roomMessageMapping.setRoomId(roomId);
                                    roomMessageMapping.setMessageId(messageId);
                                    if (roomMessageMappingDao.getMapping(roomId, messageId) == null) {
                                        roomMessageMappingDao.insert(roomMessageMapping);
                                    }
                                }
                            }
                        }
                    }
                    allUsersSync();
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private List<String> getAllRoomIds() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<String>> future = executorService.submit(() -> roomMessageMappingDao.getAllRoomIds());
        return future.get();
    }

    public List<String> getAllUserIDs(){
        List<String> userID = new ArrayList<>();
        try {
            List<String> roomIds = getAllRoomIds();
            for (String roomId : roomIds) {
                try {
                    String friendUid = FirebaseToRoomSync.extractFriendUid(roomId, myUid);
                    Log.d("RoomId", "Room ID: " + roomId);
                    Log.d("UserUid", "User UID: " + myUid);
                    Log.d("FriendUid", "Friend UID: " + friendUid);
                    userID.add(friendUid);
                } catch (IllegalArgumentException e) {
                    Log.e("RoomId", "Invalid Room ID: " + roomId, e);
                }
                try {
                    String friendUidBack = extractFriendUidBack(roomId, myUid);
                    Log.d("RoomId", "Room ID: " + roomId);
                    Log.d("UserUid", "User UID: " + myUid);
                    Log.d("FriendUidBack", "Friend UID: " + friendUidBack);
                    userID.add(friendUidBack);
                } catch (IllegalArgumentException e) {
                    Log.e("RoomId", "Invalid Room ID: " + roomId, e);
                }

                return new ArrayList<>(userID);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userID;
    }

    private static String extractFriendUid(String roomId, String userUid) {
        if (roomId.startsWith(userUid)) {
            return roomId.substring(userUid.length());
        } else {
            throw new IllegalArgumentException("Room ID does not start with user UID");
        }
    }

    public static String extractFriendUidBack(String roomId, String userUid) {
        if (roomId.endsWith(userUid)) {
            return roomId.substring(0, roomId.length() - userUid.length());
        } else {
            throw new IllegalArgumentException("Room ID does not end with user UID");
        }
    }

    private List<User> findAllUserInDB() throws Exception{
        Future<List<User>> future = executorService.submit(() -> userDao.getAllUsers());
        return future.get();
    }

    public ArrayList<User> getAllUsers(boolean fromDB) {

        if (fromDB) {
            try {
                ArrayList<User> userArrayList = new ArrayList<>(findAllUserInDB());
                return userArrayList;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        } else {
            ArrayList<User> userArrayList = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<String> userIds = getAllUserIDs();

                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);

                                for (String uid : userIds) {
                                    if (user != null && user.getUid().equals(uid) && !user.getUid().equals(FirebaseAuth.getInstance().getUid()) ) {
                                        userArrayList.add(user);
                                    }
                                }
                            }

                            usersSubject.onNext(userArrayList);

                            TextMeApplication.getInstance().getMainActivity().getAppPreferences().setUsersList(userArrayList);

                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return new ArrayList<>();
     }

}

