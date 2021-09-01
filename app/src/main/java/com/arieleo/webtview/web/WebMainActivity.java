package com.arieleo.webtview.web;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arieleo.webtview.MainActivity;
import com.arieleo.webtview.R;
import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.AppDatabase;
import com.arieleo.webtview.room.Drama;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WebMainActivity extends WebBaseActivity {
    private static final String TAG = "WebMainActivity-DDD";

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
                .filter(pair -> pair.first.equals(TvSource.JScript.jsLoadMeta.name()))
                .flatMapSingle(pair -> {
                    Drama[] data = (Drama[]) pair.second;
                    for (Drama drama : data) {
                        drama.urlHome = TvSource.urlHome();
                    }
                    return AppDatabase.getInstance(getApplicationContext())
                            .vodDao().insertVod(data).map(num -> new Pair<List, Pair>(num, pair));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    Drama[] data = (Drama[]) pair.second.second;
                    Log.i(TAG, "onCreate: jsLoadMeta: "
                            + TvSource.JScript.jsLoadMeta.name()
                            + " " + data.length + ", insertVod size = " + pair.first.size());
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(TvSource.INTENT_DRAMAS, data);
                    startActivity(intent);
                    finish();
                } );

        initializeDisposable =  TvSource.initialize(this)
                .flatMap(title -> {
                    if (title.length() > 0) {
                        Log.d(TAG, "TvSource.title: " + TvSource.title());
                        return Single.just(TvSource.title());
                    } else {
                        throw new Exception(getString(R.string.config_not_found));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(title -> {
                    showToast(title);
                    webView.loadUrl(TvSource.urlHome());
                }, error -> {
                    Log.e(TAG, "onCreate: initialize", error);
                    showToast("ERROR: " + error);
                });
    }
}