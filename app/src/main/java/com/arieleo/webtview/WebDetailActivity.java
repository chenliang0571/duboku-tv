package com.arieleo.webtview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;

public class WebDetailActivity extends FragmentActivity {
    private static final String TAG = "WebDetailActivity";

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

                webView.evaluateJavascript(TVduboku.JsLoadEpisodes, s -> {
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

        Item item = (Item) this.getIntent().getSerializableExtra(DetailsActivity.MOVIE);
        webView.loadUrl(item.href);
    }

    private void gotoActivity(String s) {
        try {
            Gson gson = new Gson();
            Episode[] data = gson.fromJson(s, Episode[].class);
            Log.d(TAG, "Episodes Object length: " + data.length);
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("episodes", data);
            intent.putExtra(DetailsActivity.MOVIE, this.getIntent().getSerializableExtra(DetailsActivity.MOVIE));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}