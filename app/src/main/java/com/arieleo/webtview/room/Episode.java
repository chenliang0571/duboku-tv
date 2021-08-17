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
    @NonNull
    @ColumnInfo(name = "url_home")
    public String urlHome;
    @ColumnInfo(name = "drama_url")
    public String dramaUrl;
    @ColumnInfo(name = "drama_title")
    public String dramaTitle;
    @NonNull
    public String upd;

    @Override
    public String toString() {
        return "Episode{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", urlHome='" + urlHome + '\'' +
                ", dramaUrl='" + dramaUrl + '\'' +
                ", dramaTitle='" + dramaTitle + '\'' +
                ", upd='" + upd + '\'' +
                '}';
    }
}
