package com.arieleo.webtview.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "history")
public class Episode implements Serializable {
    static final long serialVersionUID = 727561609932127631L;
    @NonNull
    @PrimaryKey
    public String url;
    @NonNull
    public String title;
    @ColumnInfo(name = "drama_url")
    public String dramaUrl;
    public String upd;
    @ColumnInfo(name = "current_time", defaultValue = "0")
    public Integer currentTime;

    @Override
    public String toString() {
        return "Episode{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", dramaUrl='" + dramaUrl + '\'' +
                ", upd='" + upd + '\'' +
                ", currentTime='" + currentTime + '\'' +
                '}';
    }
}
