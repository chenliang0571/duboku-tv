package com.arieleo.webtview.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "recent")
public class Recent implements Serializable {
    static final long serialVersionUID = 727561667527098564L;
    @NonNull
    @PrimaryKey
    public String url;
    @NonNull
    public String upd;

    @Override
    public String toString() {
        return "Drama{" +
                ", url='" + url + '\'' +
                ", upd='" + upd + '\'' +
                '}';
    }
}