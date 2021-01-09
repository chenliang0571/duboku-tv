package com.arieleo.webtview.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface VodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertVod(Drama... records);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertHistory(Episode record);

    @Update
    Completable updateEpisodes(Drama record);

    @Query("SELECT * from history where url = :url LIMIT 1")
    Flowable<Episode> loadHistoryById(String url);

    @Query("SELECT distinct dramas.* FROM dramas " +
            "inner join history on dramas.url = history.drama_url " +
            "order by history.upd desc limit 20")
    Flowable<List<Drama>> findRecent();

//    @Query("SELECT * FROM history order by upd desc LIMIT :num")
//    Flowable<List<Drama>> findRecent(int num);

//    @Query("SELECT * FROM book " +
//            "INNER JOIN loan ON loan.book_id = book.id " +
//            "INNER JOIN user ON user.id = loan.user_id " +
//            "WHERE user.name LIKE :userName")
//    public List<Book> findBooksBorrowedByNameSync(String userName);
}