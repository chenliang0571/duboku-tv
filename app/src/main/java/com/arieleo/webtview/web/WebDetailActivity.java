package com.arieleo.webtview.web;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.arieleo.webtview.DetailsActivity;
import com.arieleo.webtview.R;
import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.DataAccess;
import com.arieleo.webtview.room.Drama;
import com.arieleo.webtview.room.Episode;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WebDetailActivity extends FragmentActivity {
    private static final String TAG = "WebDetailActivity-DDD";
    private Drama drama;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        WebView webView = findViewById(R.id.web_view);
//        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, url);

                webView.evaluateJavascript(TvSource.jsLoadEpisodes(), s -> {
                    String json = s.substring(1, s.length() - 1)
                            .replace("\\\"", "\"");
                    Log.d(TAG, "From JS: " + s.length() + " - " + json);
                    gotoActivity(json);
                });
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        drama = (Drama) this.getIntent().getSerializableExtra(TvSource.INTENT_DRAMA);
        webView.loadUrl(drama.url);

        //load history
        DataAccess.getInstance(getApplicationContext())
                .vodDao()
                .loadHistoryByDrama(drama.url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(history -> {
                    Log.d(TAG, "vodDao loadHistoryById " + history.size());
                    for(int i = 0; i < history.size(); i ++) {
                        drama.tag += "\n" + history.get(i).title + " - " + history.get(i).upd
                                + " | " + history.get(i).currentTime;
                    }
                }, err -> err.printStackTrace());
    }

    private void gotoActivity(String s) {
        try {
            Gson gson = new Gson();
            Episode[] data = gson.fromJson(s, Episode[].class);
            Log.d(TAG, "Episodes Object length: " + data.length);
            for (int i=0; i< data.length; i++) {
                data[i].dramaTitle = drama.title;
                data[i].dramaUrl = drama.url;
            }
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(TvSource.INTENT_EPISODES, data);
            intent.putExtra(TvSource.INTENT_DRAMA,
                    this.getIntent().getSerializableExtra(TvSource.INTENT_DRAMA));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}