package com.arieleo.webtview.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.Drama;
import com.google.gson.Gson;

public class WebSearchActivity extends WebBaseActivity {
    private static final String TAG = "WebSearchActivity-DDD";

    @Override
    WebViewClient getCustomWebClient() {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished: " + url);
                webView.evaluateJavascript(TvSource.jsInject(), res -> {
                    if (res != null && res.contains("ok")) {
                        play(webView, TvSource.jsRunTemplate(TvSource.JScript.jsSearchResults));
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

    }

    @Override
    void processJsLoadEpisodes(String s) {

    }

    @Override
    void processJsSearchResults(String s) {
        String json = s.substring(1, s.length() - 1)
                .replace("\\\"", "\"");
        Log.d(TAG, "From JS: " + s.length() + " - " + json);
        Intent intent = new Intent();
        Gson gson = new Gson();
        Drama[] data = gson.fromJson(json, Drama[].class);
        intent.putExtra(TvSource.INTENT_SEARCH, data);
        setResult(Activity.RESULT_OK, intent);
        finish();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText text = new EditText(this);
        builder.setTitle("关键字搜索").setView(text);
        builder.setPositiveButton("搜索", (di, i) -> {
            final String wd = text.getText().toString();
            Log.d(TAG, "onClick: " + wd);
            if (wd.length() > 0) {
                webView.loadUrl(TvSource.urlSearch() + wd);
            } else {
                Toast.makeText(this, "ERROR keyword", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        builder.setNegativeButton("取消", (di, i) -> {
            Log.d(TAG, "setNegativeButton ");
            setResult(Activity.RESULT_OK);
            finish();
        });
        builder.create().show();
    }
}