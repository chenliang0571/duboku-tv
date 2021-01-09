package com.arieleo.webtview.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

public class DataAccess {
    private static AppDatabase db;

    public static synchronized AppDatabase getInstance(Context appContext) {
        if (db == null) {
            db = Room.databaseBuilder(appContext,
                    AppDatabase.class, "vod-20200109").build();
        }
        return db;
    }

    public static void insert(Context appContext, Drama... records) {
        AsyncTask.execute(() -> {
        });
    }
}
