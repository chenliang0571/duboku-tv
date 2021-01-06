package com.arieleo.webtview;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

public class WebVideoActivity extends FragmentActivity {
    private static final String TAG = "WebVideoActivity";
    private static final int FULL_SCREEN_SETTING = View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        WebView webView = findViewById(R.id.web_view);
        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36 Edg/87.0.664.66");
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebContentsDebuggingEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, url);

                play(webView,TVduboku.JsPlayOrStop);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        webView.setWebChromeClient(new WebVideoActivity.WebChromeClientCustom());
        Episode episode = (Episode) this.getIntent().getSerializableExtra("episode");
        webView.loadUrl(episode.link);
    }

    protected void play(WebView webView, String script) {
        final Handler handler = new Handler();
        handler.postDelayed(() -> webView.evaluateJavascript(script, s -> {
            Log.d(TAG, "From JS: " + s.length() + " - " + s);
            if(s.contains("null")) {
                Log.d(TAG, "play: JsPlayOrStop");
                play(webView, script);
            } else if("\"big\"".equals(s)) {
                //Failed to execute 'requestFullScreen' on 'Element': API can only be initiated by a user gesture.
//                Log.d(TAG, "play: JsFullScreen");
//                play(webView, TVduboku.JsFullScreen);
            }
        }), 2000);
    }

    class WebChromeClientCustom extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        @Override
        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow()
                    .getDecorView())
                    .addView(this.mCustomView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            WebVideoActivity.this.getWindow().getDecorView().setSystemUiVisibility(FULL_SCREEN_SETTING);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            this.mCustomView.setOnSystemUiVisibilityChangeListener(visibility -> updateControls());
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        }

        void updateControls() {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.mCustomView.getLayoutParams();
            params.bottomMargin = 0;
            params.topMargin = 0;
            params.leftMargin = 0;
            params.rightMargin = 0;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            this.mCustomView.setLayoutParams(params);
            WebVideoActivity.this.getWindow().getDecorView().setSystemUiVisibility(FULL_SCREEN_SETTING);
        }
    }
}