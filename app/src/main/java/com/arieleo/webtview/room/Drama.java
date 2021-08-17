package com.arieleo.webtview.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "dramas")
public class Drama implements Serializable {
    static final long serialVersionUID = 727561609867527564L;
    @NonNull
    public String title;
    @NonNull
    @PrimaryKey
    public String url;
    @NonNull
    @ColumnInfo(name = "url_home")
    public String urlHome;
    public String image;
    public String tag;
    @ColumnInfo(name = "pic_text")
    public String picText;
    @NonNull
    public String category;
    @ColumnInfo(name = "more_url")
    public String moreUrl;
    public String upd;
    @Ignore
    public String episodes;

    @Override
    public String toString() {
        return "Drama{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", urlHome='" + urlHome + '\'' +
                ", image='" + image + '\'' +
                ", tag='" + tag + '\'' +
                ", picText='" + picText + '\'' +
                ", category='" + category + '\'' +
                ", moreUrl='" + moreUrl + '\'' +
                ", upd='" + upd + '\'' +
                ", episodes='" + episodes + '\'' +
                '}';
    }
}