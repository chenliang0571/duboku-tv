package com.arieleo.webtview.web;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.room.EmptyResultSetException;

import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.Episode;

import java.io.IOException;
import java.net.URLConnection;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WebVideoActivity extends WebBaseActivity {
    private static final String TAG = "WebVideoActivity-DDD";
    private static final int FULL_SCREEN_SETTING = View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE;
    private Episode episode;
    private String currentTime = null;

    @Override
    WebViewClient getCustomWebClient() {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, url);
                webView.evaluateJavascript(TvSource.jsInject(), res -> {
                    if (res != null && res.contains("ok")) {
                        play(webView, TvSource.jsRunTemplate(TvSource.JScript.jsStart));
                        hideSpinner();
                    }
                });
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("assets".equals(uri.getHost())) {
                    Log.i(TAG, uri.toString());
                    try {
                        return new WebResourceResponse(
                                URLConnection.guessContentTypeFromName(uri.getPath()),
                                "utf-8",
                                WebVideoActivity.this.getAssets().open(uri.toString().substring(15)));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "shouldInterceptRequest " + e);
                    }
                }
                return null;
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                Log.d(TAG, "onUnhandledKeyEvent: " + event.getKeyCode());
            }
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    return false;
                }
                Log.d(TAG, "shouldOverrideKeyEvent: " + event.getKeyCode());
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    showToast("上键暂停，下键播放，左键回退，右键快进，菜单键设置时间");
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU
                        || event.getKeyCode() == KeyEvent.KEYCODE_TAB) {
                    setCurrentTime(webView);
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    play(webView, TvSource.jsVideoCmdTemplate("pause", "null"));
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    play(webView, TvSource.jsVideoCmdTemplate("play", "null"));
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    play(webView, TvSource.jsVideoCmdTemplate("backward", "null"));
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    play(webView, TvSource.jsVideoCmdTemplate("forward", "null"));
                    return true;
                } else {
                    return false;
                }
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

    }

    @Override
    void processJsStart(String s) {
        episode.upd = s.replace("jsStart-video-started-", "").replace("\"", "");
        episode.urlHome = TvSource.urlHome();
        loadHistoryByIdDisposable = dao.loadHistoryById(episode.url)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(error -> {
                    if (error instanceof EmptyResultSetException) {
                        return Single.just(new Episode());
                    } else {
                        return Single.error(error);
                    }
                })
                .flatMap(ep -> {
                    currentTime = ep.currentTime;
                    if (currentTime == null) {
                        Log.i(TAG, "play: insert History " + " - " + episode);
                        return dao.insertHistory(episode);
                    } else {
                        Log.i(TAG, "play: update History " + episode.url + episode.upd);
                        return dao.updateUpd(episode.url, episode.upd);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(num -> Log.i(TAG, "play: history = " + num),
                        error -> Log.e(TAG, "play: loadHistoryById", error));
        play(webView, TvSource.jsVideoCmdTemplate("get_current_time", "null"));
    }

    @Override
    void processJsGetCurrentTime(String s) {
        String time = s.replace("jsVideoCMD-get_current_time-", "")
                .replace("\"", "");
        try {
            if (Float.parseFloat(time) > 0) {
                updateCurrentTimeDisposable = dao.updateCurrentTime(episode.url, time)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> Log.d(TAG, "vodDao updateCurrentTime " + episode.url + " - " + time),
                                error -> Log.e(TAG, "play: updateCurrentTime", error));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e(TAG, "jsGetCurrentTime NumberFormatException " + time);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView.setWebChromeClient(new WebVideoActivity.WebChromeClientCustom());
        episode = (Episode) this.getIntent().getSerializableExtra("episode");
        webView.loadUrl(episode.url);
    }


    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    private long openedTime = 0;
    private void setCurrentTime(WebView webView) {
        if (System.currentTimeMillis() - openedTime < 500) {
            Log.d(TAG, "ignore setCurrentTime.");
            return;
        } else {
            openedTime = System.currentTimeMillis();
        }
        String number = "0";
        try {
            if (currentTime != null && Float.parseFloat(currentTime) > 0) {
                number = currentTime;
            }
        } catch (NumberFormatException e) {
            number = "0";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText text = new EditText(this);
        text.setInputType(InputType.TYPE_CLASS_NUMBER);
        text.setText(number);
        builder.setTitle("设置进度条（单位秒）").setView(text);
        builder.setPositiveButton("设置", (di, i) -> {
            final String tm = text.getText().toString();
            Log.d(TAG, "onClick: " + tm);
            if (tm.length() > 0) {
                play(webView, TvSource.jsVideoCmdTemplate("set_current_time", tm));
            } else {
                Toast.makeText(this, "ERROR currentTime", Toast.LENGTH_LONG).show();
            }
            di.dismiss();
        });
        builder.setNegativeButton("取消", (di, i) -> {
            Log.d(TAG, "setNegativeButton ");
            di.dismiss();
        });
        builder.create().show();
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