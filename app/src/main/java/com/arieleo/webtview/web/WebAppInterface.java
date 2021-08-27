package com.arieleo.webtview.web;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.Drama;
import com.arieleo.webtview.room.Episode;
import com.google.gson.Gson;

import java.util.HashMap;

public class WebAppInterface {
    private static final String TAG = "WebAppInterface-DDD";
    private static final Gson gson = new Gson();
    private static final HashMap<String, Class> clsMap = new HashMap<>();
    Context mContext;

    public static class JsDataResult {
        public int code;
        public String data;
        public String error;

        @Override
        public String toString() {
            return "JsStartResult{" +
                    "code=" + code +
                    ", data='" + data + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }
    public WebAppInterface(Context c) {
        mContext = c;
        clsMap.put(TvSource.JScript.jsLoadMeta.name(), Drama[].class);
        clsMap.put(TvSource.JScript.jsLoadEpisodes.name(), Episode[].class);
        clsMap.put(TvSource.JScript.jsSearchResults.name(), Drama[].class);
        clsMap.put(TvSource.JScript.jsStart.name(), JsDataResult.class);
        clsMap.put(TvSource.JScmd.get_current_time.name(), JsDataResult.class);
        clsMap.put(TvSource.JScmd.set_current_time.name(), JsDataResult.class);
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * video.addEventListener
     * @param event canplay / durationchange / ended / error / pause / play / timeupdate
     *              inject-script-ready
     */
    @JavascriptInterface
    public void onEvent(String event) {
        Log.v(TAG, "onEvent : " + event);
        TvSource.getVideoSubject().onNext(event);
    }

    /**
     * jsRunTemplate / jsVideoCmdTemplate
     * @param cmd jsStart / jsLoadMeta / jsLoadEpisodes / jsSearchResults
     *            play / pause / forward / backward / get_current_time / set_current_time
     * @param data json
     */
    @JavascriptInterface
    public void sendData(String cmd, String data) {
        try {
            Object obj = gson.fromJson(data, clsMap.get(cmd) != null ? clsMap.get(cmd) : Object.class);
            Log.v(TAG, "sendData: " + obj);
            TvSource.getScriptResultSubject().onNext(new Pair<>(cmd, obj));
        } catch (Exception err) {
            Toast.makeText(mContext, "sendData: " + cmd, Toast.LENGTH_SHORT).show();
        }
    }
}