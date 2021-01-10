package com.arieleo.webtview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.arieleo.webtview.room.DataAccess;
import com.arieleo.webtview.room.Drama;
import com.google.gson.Gson;

import io.reactivex.schedulers.Schedulers;

public class WebMainActivity extends FragmentActivity {
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

//                webView.evaluateJavascript(TVduboku.JsTest, s -> {
//                    Log.d(TAG, "From JS: " + s);
//                });
                webView.evaluateJavascript(TVduboku.JsLoadMeta, s -> {
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
        webView.loadUrl(TVduboku.UrlHome);

        DataAccess.getInstance(getApplicationContext())
                .vodDao().findRecent()
                .subscribeOn(Schedulers.io())
                .subscribe((dramas) -> {
                            Log.d(TAG, "vodDao: findRecent " + dramas.size());
                            for (int i = 0; i < dramas.size(); i++) {
                                dramas.get(i).category = "recent";
                            }
                            recent = dramas.toArray(new Drama[0]);
                        },
                        err -> err.printStackTrace());
    }

    Drama[] recent;

    private void gotoActivity(String s) {
        try {
            Gson gson = new Gson();
            Drama[] data = gson.fromJson(s, Drama[].class);
            Log.d(TAG, "Dramas Object length: " + data.length);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(TVduboku.IntentDramas, data);
            if(recent != null && recent.length > 0) {
                intent.putExtra(TVduboku.IntentRecent, recent);
            }
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}