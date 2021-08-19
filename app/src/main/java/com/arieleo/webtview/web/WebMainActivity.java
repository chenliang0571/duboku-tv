package com.arieleo.webtview.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.arieleo.webtview.MainActivity;
import com.arieleo.webtview.R;
import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.DataAccess;
import com.arieleo.webtview.room.Drama;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WebMainActivity extends FragmentActivity {
    private static final String TAG = "WebActivity-DDD";
    private Drama[] recent;
    private Disposable findRecentDisposable;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        TvSource.initialize(this);
        Log.d(TAG, "TvSource.urlHome: " + TvSource.urlHome());

        WebView webView = findViewById(R.id.web_view);
//        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, url);
                webView.evaluateJavascript(TvSource.jsLoadMeta(), s -> {
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
        webView.loadUrl(TvSource.urlHome());

        findRecentDisposable = DataAccess.getInstance(getApplicationContext())
                .vodDao().findRecent(TvSource.urlHome())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((dramas) -> {
                            Log.d(TAG, "vodDao: findRecent " + dramas.size());
                            for (int i = 0; i < dramas.size(); i++) {
                                dramas.get(i).category = "recent";
                            }
                            recent = dramas.toArray(new Drama[0]);
                        },
                        Throwable::printStackTrace);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        if(findRecentDisposable != null && !findRecentDisposable.isDisposed()) {
            findRecentDisposable.dispose();
        }
        super.onStop();
    }

    private void gotoActivity(String s) {
        try {
            Gson gson = new Gson();
            Drama[] data = gson.fromJson(s, Drama[].class);
            Log.d(TAG, "Dramas Object length: " + data.length);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(TvSource.INTENT_DRAMAS, data);
            if(recent != null && recent.length > 0) {
                intent.putExtra(TvSource.INTENT_RECENT, recent);
            }
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}