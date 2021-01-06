package com.arieleo.webtview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class WebMainActivity extends FragmentActivity {
    private static final String TAG = "WebActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        WebView webView = findViewById(R.id.web_view);
        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, url);

                webView.evaluateJavascript(TVduboku.JsTest, s -> {
                    Log.d(TAG, "From JS: " + s);
                });
                webView.evaluateJavascript(TVduboku.JsLoadMeta, s -> {
                    String json = s.substring(1, s.length() - 1)
                            .replace("\\\"", "\"");
                    Log.d(TAG, "From JS: " + s.length() + " - " + json);
                    gotoActivity(json);
                });
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        webView.loadUrl("https://www.duboku.tv/");
    }

    private void gotoActivity(String s) {
        try {
            Gson gson = new Gson();
            Meta[] data = gson.fromJson(s, Meta[].class);
            Log.d(TAG, "Meta Object length: " + data.length);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("meta", data);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    class Meta implements Serializable {
        static final long serialVersionUID = 727561609867586888L;
        String category;
        String link;
        List<Item> items;

        @Override
        public String toString() {
            return "Meta{" +
                    "category='" + category + '\'' +
                    ", link='" + link + '\'' +
                    ", items=" + items +
                    '}';
        }
    }
}