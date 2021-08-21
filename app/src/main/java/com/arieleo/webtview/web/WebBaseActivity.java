package com.arieleo.webtview.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.arieleo.webtview.R;
import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.AppDatabase;
import com.arieleo.webtview.room.VodDao;
import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;

public abstract class WebBaseActivity extends FragmentActivity {
    private static final String TAG = "WebBaseActivity-DDD";
    //    private final PublishSubject<KeyEvent> keyboardSubject =  PublishSubject.create();
    protected final Handler handler = new Handler();
    protected ProgressBar spinner;
    protected WebView webView;
    protected VodDao dao;
    protected Gson gson = new Gson();
    protected Disposable loadHistoryDisposable;
    protected Disposable findRecentDisposable;
    protected Disposable loadHistoryByIdDisposable;
    protected Disposable updateCurrentTimeDisposable;

    abstract WebViewClient getCustomWebClient();

    abstract void processJsLoadMeta(String s);

    abstract void processJsLoadEpisodes(String s);

    abstract void processJsSearchResults(String s);

    abstract void processJsStart(String s);

    abstract void processJsGetCurrentTime(String s);

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);
        spinner = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);

        spinner.setVisibility(View.VISIBLE);
        dao = AppDatabase.getInstance(getApplicationContext()).vodDao();
//        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
//        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36 Edg/87.0.664.66");
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(getCustomWebClient());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        handler.removeCallbacksAndMessages(null);
        if (loadHistoryDisposable != null && !loadHistoryDisposable.isDisposed()) {
            loadHistoryDisposable.dispose();
        }
        if (findRecentDisposable != null && !findRecentDisposable.isDisposed()) {
            findRecentDisposable.dispose();
        }
        if (loadHistoryByIdDisposable != null && !loadHistoryByIdDisposable.isDisposed()) {
            loadHistoryByIdDisposable.dispose();
        }
        if (updateCurrentTimeDisposable != null && !updateCurrentTimeDisposable.isDisposed()) {
            updateCurrentTimeDisposable.dispose();
        }
        super.onDestroy();
    }

    protected void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }

    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    protected void play(WebView webView, String script) {
        Log.v(TAG, "play: " + script);
        webView.evaluateJavascript(script, s -> {
            try {
                Log.d(TAG, "evaluateJavascript: " + s);
                if (s.contains("func-not-found")) {
                    Log.d(TAG, "replay: JS");
                    handler.postDelayed(() -> play(webView, script), 1000);
                } else if (TvSource.jsRunTemplate(TvSource.JScript.jsStart).equals(script)
                        && s.contains("jsStart-video-started-")) {
                    processJsStart(s);
                } else if (s.contains("jsVideoCMD-get_current_time-")) {
                    processJsGetCurrentTime(s);
                    handler.postDelayed(() -> play(webView, script), 30000);
                } else if (TvSource.jsRunTemplate(TvSource.JScript.jsLoadMeta).equals(script)) {
                    processJsLoadMeta(s);
                } else if (TvSource.jsRunTemplate(TvSource.JScript.jsLoadEpisodes).equals(script)) {
                    processJsLoadEpisodes(s);
                } else if (TvSource.jsRunTemplate(TvSource.JScript.jsSearchResults).equals(script)) {
                    processJsSearchResults(s);
                }
            } catch (Exception e) {
                Toast.makeText(this, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }
}
