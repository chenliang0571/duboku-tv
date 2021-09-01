package com.arieleo.webtview.web;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arieleo.webtview.DetailsActivity;
import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.Drama;
import com.arieleo.webtview.room.Episode;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WebDetailActivity extends WebBaseActivity {
    private static final String TAG = "WebDetailActivity-DDD";
    private Drama drama;

    @Override
    WebViewClient getCustomWebClient() {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished: " + url);

                webView.evaluateJavascript(TvSource.jsInject(), res -> {
                    if (res != null && res.contains("ok")) {
                        play(webView, TvSource.jsRunTemplate(TvSource.JScript.jsLoadEpisodes));
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

        drama = (Drama) this.getIntent().getSerializableExtra(TvSource.INTENT_DRAMA);
        webView.loadUrl(drama.url);

        jsLoadEpisodesDisposable = TvSource.getScriptResultSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(pair -> pair.first.equals(TvSource.JScript.jsLoadEpisodes.name()))
                .subscribe(pair -> {
                    Episode[] data = (Episode[]) pair.second;
                    Log.i(TAG, "onCreate: jsLoadEpisodes: "
                            + TvSource.JScript.jsLoadEpisodes.name()
                            + " " + data.length);
                    for (Episode datum : data) {
                        datum.dramaUrl = drama.url;
                    }
                    Intent intent = new Intent(this, DetailsActivity.class);
                    intent.putExtra(TvSource.INTENT_EPISODES, data);
                    intent.putExtra(TvSource.INTENT_DRAMA,
                            this.getIntent().getSerializableExtra(TvSource.INTENT_DRAMA));
                    startActivity(intent);
                    finish();
                } );
    }
}