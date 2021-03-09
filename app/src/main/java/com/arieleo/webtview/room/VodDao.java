package com.arieleo.webtview.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface VodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertVod(Drama... records);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertHistory(Episode record);

    @Query("SELECT * from history where url = :url LIMIT 1")
    Flowable<Episode> loadHistoryById(String url);

    @Query("SELECT * from history where drama_url = :url order by upd desc LIMIT 10")
    Maybe<List<Episode>> loadHistoryByDrama(String url);

    @Query("SELECT distinct dramas.title,dramas.url,dramas.image,dramas.tag," +
            "dramas.pic_text,dramas.category,dramas.more_url,history.upd " +
            "FROM dramas inner join history on dramas.url = history.drama_url " +
            "group by dramas.title order by history.upd desc limit 20")
    Flowable<List<Drama>> findRecent();
}