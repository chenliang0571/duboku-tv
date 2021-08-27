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
import io.reactivex.schedulers.Schedulers;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jsLoadMetaDisposable = TvSource.getScriptResultSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(pair -> pair.first.equals(TvSource.JScript.jsLoadMeta.name()))
                .subscribe(pair -> {
                    Drama[] data = (Drama[]) pair.second;
                    Log.i(TAG, "onCreate: jsLoadMeta: "
                            + TvSource.JScript.jsLoadMeta.name()
                            + " " + data.length);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(TvSource.INTENT_DRAMAS, data);
                    if (recent != null && recent.length > 0) {
                        intent.putExtra(TvSource.INTENT_RECENT, recent);
                    }
                    startActivity(intent);
                    finish();
                } );

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
                .subscribe(recent -> {
                    webView.loadUrl(TvSource.urlHome());
                    Log.d(TAG, "vodDao: findRecent " + recent.size());
                    for (int i = 0; i < recent.size(); i++) {
                        recent.get(i).category = "recent";
                    }
                    this.recent = recent.toArray(new Drama[0]);
                }, error -> Log.e(TAG, "onCreate: initialize", error));
    }
}