package com.arieleo.webtview.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface VodDao {
    //https://medium.com/androiddevelopers/room-rxjava-acb0cd4f3757
    //https://developer.android.com/training/data-storage/room/accessing-data#convenience-insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertVod(Drama... records);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertHistory(Episode record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertTvConfig(TvConfig... config);

    @Query("DELETE FROM tv_config")
    Single<Integer> deleteAllTvConfig();

    @Query("UPDATE history SET upd= :upd WHERE url =:url")
    Single<Integer> updateUpd(String url, String upd);

    @Query("UPDATE history SET `current_time` = :currentTime WHERE url =:url")
    Completable updateCurrentTime(String url, String currentTime);

    @Query("SELECT * from history where url = :url LIMIT 1")
    Single<Episode> loadHistoryById(String url);

    @Query("SELECT * from history where drama_url = :url order by upd desc LIMIT 15")
    Maybe<List<Episode>> loadHistoryByDrama(String url);

    @Query("SELECT distinct dramas.title,dramas.url,dramas.image,dramas.tag," +
            "dramas.pic_text,dramas.category,dramas.more_url,history.upd, dramas.url_home " +
            "FROM dramas inner join history on dramas.url = history.drama_url " +
            "WHERE dramas.url_home = :urlHome " +
            "group by dramas.url order by history.upd desc limit 50")
    Flowable<List<Drama>> findRecent(String urlHome);

    @Query("SELECT count(*) from tv_config")
    Single<Integer> getConfigCount();

    @Query("SELECT * from tv_config")
    Single<List<TvConfig>> getConfig();
}