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
    private static String urlHome = TvDuboku.urlHome();
    private static HashMap<String, TvData> config;

    static {
        config = new HashMap<>();
        config.put(TvDuboku.urlHome(), new TvData(TvDuboku.title(), TvDuboku.urlHome(),
                TvDuboku.urlSearch(), TvDuboku.jsStart(),
                TvDuboku.jsPlay(), TvDuboku.jsPause(),
                TvDuboku.jsForward(), TvDuboku.jsBackward(),
                TvDuboku.jsLoadMeta(), TvDuboku.jsSearchResults(),
                TvDuboku.jsLoadEpisodes()));
        config.put(TvOlevod.urlHome(), new TvData(TvOlevod.title(), TvOlevod.urlHome(),
                TvOlevod.urlSearch(), TvOlevod.jsStart(),
                TvOlevod.jsPlay(), TvOlevod.jsPause(),
                TvOlevod.jsForward(), TvOlevod.jsBackward(),
                TvOlevod.jsLoadMeta(), TvOlevod.jsSearchResults(),
                TvOlevod.jsLoadEpisodes()));
    }
    public static void setSharedPreferencesUrlHome(Context context, String urlHome) {
        SharedPreferences sharedPref = context.getSharedPreferences("arieleo.key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("HOME_URL", urlHome);
        boolean res = editor.commit();
        Log.i(TAG, "setSharedPreferencesUrlHome urlHome =" + urlHome + "; result =" + res);
    }

    public static void initialize(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("arieleo.key", Context.MODE_PRIVATE);
        urlHome = sharedPref.getString("HOME_URL", "");
        Log.i(TAG, "initialize urlHome =" + urlHome);
        if(urlHome.length() == 0) urlHome = TvDuboku.urlHome();
    }

    public static String title() {
        return config.get(urlHome).title;
    }

    public static String urlHome() {
        return config.get(urlHome).urlHome;
    }

    public static String urlSearch() {
        return config.get(urlHome).urlSearch;
    }

    public static String jsStart() {
        return config.get(urlHome).jsStart;
    }

    public static String jsPlay() {
        return config.get(urlHome).jsPlay;
    }

    public static String jsPause() {
        return config.get(urlHome).jsPause;
    }

    public static String jsForward() {
        return config.get(urlHome).jsForward;
    }

    public static String jsBackward() {
        return config.get(urlHome).jsBackward;
    }

    public static String jsLoadMeta() {
        return config.get(urlHome).jsLoadMeta;
    }

    public static String jsSearchResults() {
        return config.get(urlHome).jsSearchResults;
    }

    public static String jsLoadEpisodes() {
        return config.get(urlHome).jsLoadEpisodes;
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
