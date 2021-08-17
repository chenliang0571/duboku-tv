package com.arieleo.webtview;

public class TvData {
    TvData(String title, String urlHome,
           String urlSearch, String jsStart,
           String jsPlay, String jsPause,
           String jsForward, String jsBackward,
           String jsLoadMeta, String jsSearchResults,
           String jsLoadEpisodes) {
        this.title = title;
        this.urlHome = urlHome;
        this.urlSearch = urlSearch;
        this.jsStart = jsStart;
        this.jsPlay = jsPlay;
        this.jsPause = jsPause;
        this.jsForward = jsForward;
        this.jsBackward = jsBackward;
        this.jsLoadMeta = jsLoadMeta;
        this.jsSearchResults = jsSearchResults;
        this.jsLoadEpisodes = jsLoadEpisodes;
    }

    String title;
    String urlHome;
    String urlSearch;
    String jsStart;
    String jsPlay;
    String jsPause;
    String jsForward;
    String jsBackward;
    String jsLoadMeta;
    String jsSearchResults;
    String jsLoadEpisodes;
}
