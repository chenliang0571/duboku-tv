package com.arieleo.webtview.web;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.arieleo.webtview.R;
import com.arieleo.webtview.room.AppDatabase;
import com.arieleo.webtview.room.VodDao;

import io.reactivex.disposables.Disposable;

public abstract class WebBaseActivity extends FragmentActivity {
    private static final String TAG = "WebBaseActivity-DDD";
    //    private final PublishSubject<KeyEvent> keyboardSubject =  PublishSubject.create();
    protected final Handler handler = new Handler();
    protected ProgressBar spinner;
    protected WebView webView;
    protected VodDao dao;
    protected Disposable loadHistoryDisposable;
    protected Disposable findRecentDisposable;
    protected Disposable loadHistoryByIdDisposable;
    protected Disposable updateCurrentTimeDisposable;
    protected Disposable jsLoadMetaDisposable;
    protected Disposable jsLoadEpisodesDisposable;
    protected Disposable jsSearchResultsDisposable;
    protected Disposable jsStartDisposable;
    protected Disposable getCurrentTimeDisposable;

    abstract WebViewClient getCustomWebClient();

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
        handler.removeCallbacksAndMessages(null);
        for(Disposable disposable : new Disposable[] {
                loadHistoryDisposable, 
                findRecentDisposable, 
                loadHistoryByIdDisposable,
                updateCurrentTimeDisposable,
                jsLoadMetaDisposable,
                jsLoadEpisodesDisposable,
                jsSearchResultsDisposable,
                jsStartDisposable,
                getCurrentTimeDisposable,
        } ) {
            if(disposable != null && !disposable.isDisposed()) {
                Log.i(TAG, "onDestroy: disposable");
                disposable.dispose();
            }
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
        try {
            webView.evaluateJavascript(script, s -> {
                Log.d(TAG, "evaluateJavascript: " + s);
                if (s != null && s.contains("func-not-found")) {
                    Log.d(TAG, "replay: JS");
                    handler.postDelayed(() -> play(webView, script), 1000);
                } else if (s != null && s.contains("-error-")) {
                    Toast.makeText(this, "ERROR:" + s, Toast.LENGTH_LONG).show();
                }
//                else if (TvSource.jsRunTemplate(TvSource.JScript.jsStart).equals(script)
//                        && s.contains("jsStart-video-started-")) {
//                    processJsStart(s);
//                } else if (s.contains("jsVideoCMD-get_current_time-")) {
//                    processJsGetCurrentTime(s);
//                    handler.postDelayed(() -> play(webView, script), 30000);
//                } else if (TvSource.jsRunTemplate(TvSource.JScript.jsLoadMeta).equals(script)) {
//                    processJsLoadMeta(s);
//                } else if (TvSource.jsRunTemplate(TvSource.JScript.jsLoadEpisodes).equals(script)) {
//                    processJsLoadEpisodes(s);
//                } else if (TvSource.jsRunTemplate(TvSource.JScript.jsSearchResults).equals(script)) {
//                    processJsSearchResults(s);
//                }
            });
        } catch (Exception e) {
            showToast("ERROR:" + e.getMessage());
            Log.e(TAG, "play: Exception", e);
        }
    }

    class WebChromeClientCustom extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;
        private final int FULL_SCREEN_SETTING = View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE;

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
            getWindow().getDecorView().setSystemUiVisibility(FULL_SCREEN_SETTING);
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
            getWindow().getDecorView().setSystemUiVisibility(FULL_SCREEN_SETTING);
        }
    }
}
