package com.bymjk.txtme.DB;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.bymjk.txtme.Components.GenericCallback;
import com.bymjk.txtme.Models.ChatRoom;
import com.bymjk.txtme.Models.Message;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.TextMeApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private AppDatabase db;
    private UserDao userDao;
    private ChatRoomDao chatRoomDao;
    private ExecutorService executorService;

    public UserRepository(Context context) {
        db = AppDatabase.getDatabase(context);
        userDao = db.userDao();
        chatRoomDao = db.chatRoomDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getAllUsers(GenericCallback<List<User>> callback) {
        executorService.execute(() -> {
            try {
                List<User> user = userDao.getAllUsers();
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getUserFromUID(String uID, GenericCallback<User> callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByUid(uID);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getUsersByChatRoom(GenericCallback<ArrayList<User>> callback){
        executorService.execute(() -> {
            try {
                List<ChatRoom> chatRooms = chatRoomDao.getAllChatRooms();
                ArrayList<User> users =  getUsersFromChatRoomIds(chatRooms);
                callback.onSuccess(users);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }


    private ArrayList<User> getUsersFromChatRoomIds(List<ChatRoom> chatRooms){
        ArrayList<User> users = new ArrayList<>();
        List<User> userList = userDao.getAllUsers();
        for (ChatRoom chatRoomItem :  chatRooms){
            for (User userItem : userList) {
                if (chatRoomItem.getRoomId().endsWith(userItem.getUid())){
                    users.add(userItem);
                }
            }
        }
        TextMeApplication.getInstance().getMainActivity().getAppPreferences().setUsersList(users);
        return users;
    }

    public void getAllChatRooms(GenericCallback<ArrayList<ChatRoom>> callback){
        executorService.execute(() -> {
            try {
                List<ChatRoom> chatRooms = chatRoomDao.getAllChatRooms();
                callback.onSuccess(new ArrayList<>(chatRooms));
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getChatRoomByRoomID(String roomId, GenericCallback<ChatRoom> callback){
        executorService.execute(() -> {
            try {
                ChatRoom chatRoom = chatRoomDao.getChatRoomById(roomId);
                callback.onSuccess(chatRoom);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

}
