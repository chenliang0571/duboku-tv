package com.arieleo.webtview.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertRecent(Recent recent);

    @Query("DELETE FROM tv_config")
    Single<Integer> deleteAllTvConfig();

    @Query("SELECT * from history where url = :url LIMIT 1")
    Single<Episode> loadHistoryById(String url);

    @Query("SELECT * from history where drama_url = :url order by upd desc LIMIT 15")
    Flowable<List<Episode>> loadHistoryByDrama(String url);

    @Query("SELECT dramas.title, dramas.url, dramas.url_home, recent.upd, " +
            "dramas.image, dramas.tag, dramas.pic_text, 'recent' as  category " +
            "FROM dramas INNER JOIN recent on recent.url = dramas.url " +
            "WHERE dramas.url_home = :urlHome order by recent.upd desc limit 50")
    Flowable<List<Drama>> findRecent(String urlHome);

    @Query("SELECT count(*) from tv_config")
    Single<Integer> getConfigCount();

    @Query("SELECT * from tv_config")
    Single<List<TvConfig>> getConfig();
}