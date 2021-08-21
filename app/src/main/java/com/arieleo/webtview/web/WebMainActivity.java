package com.arieleo.webtview.web;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arieleo.webtview.MainActivity;
import com.arieleo.webtview.R;
import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.AppDatabase;
import com.arieleo.webtview.room.Drama;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class WebMainActivity extends WebBaseActivity {
    private static final String TAG = "WebMainActivity-DDD";
    private Drama[] recent;

    @Override
    WebViewClient getCustomWebClient() {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished: " + url);
                webView.evaluateJavascript(TvSource.jsInject(), res -> {
                    if (res != null && res.contains("ok")) {
                        play(webView, TvSource.jsRunTemplate(TvSource.JScript.jsLoadMeta));
                    }
                });
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        };
    }

    @Override
    void processJsLoadMeta(String s) {
        String json = s.substring(1, s.length() - 1)
                .replace("\\\"", "\"");
        Drama[] data = gson.fromJson(json, Drama[].class);
        Log.i(TAG, "processJsLoadMeta: Dramas " + data.length);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TvSource.INTENT_DRAMAS, data);
        if (recent != null && recent.length > 0) {
            intent.putExtra(TvSource.INTENT_RECENT, recent);
        }
        startActivity(intent);
        finish();
    }

    @Override
    void processJsLoadEpisodes(String str) {

    }

    @Override
    void processJsSearchResults(String str) {

    }

    @Override
    void processJsStart(String s) {

    }

    @Override
    void processJsGetCurrentTime(String s) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findRecentDisposable = TvSource.initialize(this)
                .flatMap(title -> {
                    if (title.length() > 0) {
                        Log.d(TAG, "TvSource.title: " + TvSource.title());
                        return AppDatabase.getInstance(getApplicationContext())
                                .vodDao().findRecent(TvSource.urlHome());
                    } else {
                        throw new Exception(getString(R.string.config_not_found));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dramas -> {
                    webView.loadUrl(TvSource.urlHome());
                    Log.d(TAG, "vodDao: findRecent " + dramas.size());
                    for (int i = 0; i < dramas.size(); i++) {
                        dramas.get(i).category = "recent";
                    }
                    recent = dramas.toArray(new Drama[0]);
                }, error -> Log.e(TAG, "onCreate: initialize", error));
    }
}