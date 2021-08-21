package com.arieleo.webtview.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Episode.class, Drama.class, TvConfig.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase db;

    public abstract VodDao vodDao();
    public static synchronized AppDatabase getInstance(Context appContext) {
        if (db == null) {
            db = Room.databaseBuilder(appContext,
                    AppDatabase.class, "vod-20210819")
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return db;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `tv_config` (" +
                    "`title` TEXT, " +
                    "`url_home` TEXT, " +
                    "`url_search` TEXT, " +
                    "`js_get_video_iframe` TEXT, " +
                    "`js_clear_tag` TEXT, " +
                    "`js_load_meta` TEXT, " +
                    "`js_search_results` TEXT, " +
                    "`js_load_episodes` TEXT, " +
                    "PRIMARY KEY(`title`))");
        }
    };
}
