package com.arieleo.webtview;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public final class TvSource {
    private static final String TAG = "TvSource-DDD";
    public static final String INTENT_DRAMA = "drama";
    public static final String INTENT_DRAMAS = "dramas";
    public static final String INTENT_EPISODE = "episode";
    public static final String INTENT_EPISODES = "episodes";
    public static final String INTENT_SEARCH = "search";
    public static final String INTENT_RECENT = "recent";
    private static String title = TvDuboku.title();
    private static HashMap<String, TvData> config;

    static {
        config = new HashMap<>();
        config.put(TvDuboku.title(), new TvData(TvDuboku.title(), TvDuboku.urlHome(),
                TvDuboku.urlSearch(), TvDuboku.jsStart(),
                TvDuboku.jsPlay(), TvDuboku.jsPause(),
                TvDuboku.jsForward(), TvDuboku.jsBackward(),
                TvDuboku.jsLoadMeta(), TvDuboku.jsSearchResults(),
                TvDuboku.jsLoadEpisodes(), TvDuboku.jsSetCurrentTime(),
                TvDuboku.jsGetCurrentTime()));
        config.put(TvOlevod.title(), new TvData(TvOlevod.title(), TvOlevod.urlHome(),
                TvOlevod.urlSearch(), TvOlevod.jsStart(),
                TvOlevod.jsPlay(), TvOlevod.jsPause(),
                TvOlevod.jsForward(), TvOlevod.jsBackward(),
                TvOlevod.jsLoadMeta(), TvOlevod.jsSearchResults(),
                TvOlevod.jsLoadEpisodes(), TvOlevod.jsSetCurrentTime(),
                TvOlevod.jsGetCurrentTime()));
    }
    public static boolean setSharedPreferencesUrlHome(Context context, String title) {
        TvData tv = config.get(title);
        if(tv != null && !tv.title.equals(TvSource.title)) {
            SharedPreferences sharedPref = context.getSharedPreferences("arieleo.key", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("HOME_TITLE", title);
            boolean res = editor.commit();
            Log.i(TAG, "setSharedPreferencesUrlHome title =" + title + "; result =" + res);
            return res;
        } else {
            return false;
        }
    }

    public static void initialize(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("arieleo.key", Context.MODE_PRIVATE);
        title = sharedPref.getString("HOME_TITLE", "");
        Log.i(TAG, "initialize title =" + title);
        if(title.length() == 0) title = TvDuboku.title();
    }

    public static String title() {
        return config.get(title).title;
    }

    public static String urlHome() {
        return config.get(title).urlHome;
    }

    public static String urlSearch() {
        return config.get(title).urlSearch;
    }

    public static String jsStart() {
        return config.get(title).jsStart;
    }

    public static String jsPlay() {
        return config.get(title).jsPlay;
    }

    public static String jsPause() {
        return config.get(title).jsPause;
    }

    public static String jsForward() {
        return config.get(title).jsForward;
    }

    public static String jsBackward() {
        return config.get(title).jsBackward;
    }

    public static String jsLoadMeta() {
        return config.get(title).jsLoadMeta;
    }

    public static String jsSearchResults() {
        return config.get(title).jsSearchResults;
    }

    public static String jsLoadEpisodes() {
        return config.get(title).jsLoadEpisodes;
    }

    public static String jsSetCurrentTime() {
        return config.get(title).jsSetCurrentTime;
    }

    public static String jsGetCurrentTime() {
        return config.get(title).jsGetCurrentTime;
    }

    public static final String JsTest = "(function() {\n" +
            "    Android.showToast(\"test\");\n" +
            "    return new Date().toString();\n" +
            "})()";
    public static final String JsPlayOrStop = "(function() {\n" +
            "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
            "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
            "    if(!iframe) return 'iframe-null';\n" +
            "    const video = iframe.contentWindow.document.querySelector('video');\n" +
            "    console.log('video ' + (video?video.tagName:'null'));\n" +
            "    if(video) {" +
            "       video.scrollIntoView();" +
            "       if(video.paused) {" +
            "           video.play();" +
            "           return 'play'\n" +
            "       } else {" +
            "           video.pause();" +
            "           return 'pause'\n" +
            "       }" +
            "    } else {\n" +
            "       return 'video-null'\n" +
            "    }" +
            "})()";
}
