package com.bymjk.txtme.DB;

import android.content.Context;

import androidx.room.Room;

import com.bymjk.txtme.Components.GenericCallback;
import com.bymjk.txtme.Models.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageRepository {
    private AppDatabase db;
    private MessageDao messageDao;
    private ExecutorService executorService;

    public MessageRepository(Context context) {
        db = AppDatabase.getDatabase(context);
        messageDao = db.messageDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getMessagesByRoomId(String roomId, GenericCallback<List<Message>> callback) {
        executorService.execute(() -> {
            try {
                List<Message> messages = messageDao.getMessagesByRoomId(roomId);
                callback.onSuccess(messages);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
}


