package com.arieleo.webtview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.arieleo.webtview.room.Drama;
import com.google.gson.Gson;

public class WebSearchActivity extends FragmentActivity {
    private static final String TAG = "WebActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        WebView webView = findViewById(R.id.web_view);
//        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, url);

                webView.evaluateJavascript(TVduboku.JsSearchResults, s -> {
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

        search(webView);
    }

    private void search(WebView webView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText text = new EditText(this);

        builder.setTitle("关键字搜索").setView(text);
        builder.setPositiveButton("搜索", (di, i) -> {
            final String wd = text.getText().toString();
            Log.d(TAG, "onClick: " + wd);
            if (wd != null && wd.length() > 0) {
                webView.loadUrl(TVduboku.UrlSearch + wd);
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

    private void gotoActivity(String s) {
        try {
            Intent intent = new Intent();
            Gson gson = new Gson();
            Drama[] data = gson.fromJson(s, Drama[].class);
            intent.putExtra(TVduboku.IntentSearch, data);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}