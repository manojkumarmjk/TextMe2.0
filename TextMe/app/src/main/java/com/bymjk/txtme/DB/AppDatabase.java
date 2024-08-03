package com.bymjk.txtme.DB;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.bymjk.txtme.Models.ChatRoom;
import com.bymjk.txtme.Models.Message;
import com.bymjk.txtme.Models.RoomMessageMapping;
import com.bymjk.txtme.Models.User;


@Database(entities = {User.class, Message.class, RoomMessageMapping.class, ChatRoom.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract MessageDao messageDao();
    public abstract RoomMessageMappingDao roomMessageMappingDao();
    public abstract ChatRoomDao chatRoomDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "chat_database")
                            .addMigrations(MyMigrations.MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

