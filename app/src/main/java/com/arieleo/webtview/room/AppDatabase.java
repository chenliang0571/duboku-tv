package com.arieleo.webtview.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Episode.class, Drama.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VodDao vodDao();
}
