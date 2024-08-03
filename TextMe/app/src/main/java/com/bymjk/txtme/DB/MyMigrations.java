package com.bymjk.txtme.DB;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MyMigrations {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table for ChatRoom with the correct schema
            database.execSQL("CREATE TABLE IF NOT EXISTS `chat_rooms` (`roomId` TEXT NOT NULL, `lastMsg` TEXT, `lastMsgTime` INTEGER NOT NULL, `senderId` TEXT, PRIMARY KEY(`roomId`))");
        }
    };
}

