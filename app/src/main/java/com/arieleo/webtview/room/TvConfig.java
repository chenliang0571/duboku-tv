package com.arieleo.webtview.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "tv_config")
public class TvConfig implements Serializable {
    static final long serialVersionUID = 727562127616099330L;
    @NonNull
    @PrimaryKey
    public String title;
    @SerializedName("url_home")
    @ColumnInfo(name = "url_home")
    public String urlHome;
    @SerializedName("url_search")
    @ColumnInfo(name = "url_search")
    public String urlSearch;
    @SerializedName("episode_direction")
    @ColumnInfo(name = "episode_direction")
    public String episodeDirection;
    @SerializedName("js_get_video_iframe")
    @ColumnInfo(name = "js_get_video_iframe")
    public String jsGetVideoIframe;
    @SerializedName("js_clear_tag")
    @ColumnInfo(name = "js_clear_tag")
    public String jsClearTag;
    @SerializedName("js_load_meta")
    @ColumnInfo(name = "js_load_meta")
    public String jsLoadMeta;
    @SerializedName("js_search_results")
    @ColumnInfo(name = "js_search_results")
    public String jsSearchResults;
    @SerializedName("js_load_episodes")
    @ColumnInfo(name = "js_load_episodes")
    public String jsLoadEpisodes;

    @Override
    public String toString() {
        return "TvConfig{" +
                "title='" + title + '\'' +
                ", urlHome='" + urlHome + '\'' +
                ", urlSearch='" + urlSearch + '\'' +
                ", episodeDirection='" + episodeDirection + '\'' +
                ", jsGetVideoIframe='" + jsGetVideoIframe + '\'' +
                ", jsClearTag='" + jsClearTag + '\'' +
                ", jsLoadMeta='" + jsLoadMeta + '\'' +
                ", jsSearchResults='" + jsSearchResults + '\'' +
                ", jsLoadEpisodes='" + jsLoadEpisodes + '\'' +
                '}';
    }
}
