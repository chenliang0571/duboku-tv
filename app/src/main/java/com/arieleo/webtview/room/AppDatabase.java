package com.arieleo.webtview.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

//https://developer.android.com/training/data-storage/room/migrating-db-versions
@Database(entities = {Episode.class, Drama.class, TvConfig.class, Recent.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase db;

    public abstract VodDao vodDao();
    public static synchronized AppDatabase getInstance(Context appContext) {
        if (db == null) {
            db = Room.databaseBuilder(appContext,
                    AppDatabase.class, "vod-20210901")
                    .build();
        }
        return db;
    }
}
