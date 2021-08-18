package com.arieleo.webtview.web;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.arieleo.webtview.R;
import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.DataAccess;
import com.arieleo.webtview.room.Episode;
import com.arieleo.webtview.room.VodDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WebVideoActivity extends FragmentActivity {
    private static final String TAG = "WebVideoActivity-DDD";
    private static final int FULL_SCREEN_SETTING = View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE;
    private Episode episode;
    private VodDao dao;
    private String currentTime = null;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        dao = DataAccess.getInstance(getApplicationContext()).vodDao();

        WebView webView = findViewById(R.id.web_view);
//        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36 Edg/87.0.664.66");
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, url);
                play(webView, TvSource.jsStart());
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
                    play(webView, TvSource.jsPause());
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    play(webView, TvSource.jsPlay());
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    play(webView, TvSource.jsBackward());
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    play(webView, TvSource.jsForward());
                    return true;
                } else {
                    return false;
                }
            }
        });
        webView.setWebChromeClient(new WebVideoActivity.WebChromeClientCustom());
        episode = (Episode) this.getIntent().getSerializableExtra("episode");
        Log.d(TAG, "onCreate: episode" + episode);
        webView.loadUrl(episode.url);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    protected void play(WebView webView, String script) {
        webView.evaluateJavascript(script, s -> {
            Log.d(TAG, "From JS: " + s.length() + " - " + s);
            if (s.contains("null")) {
                Log.d(TAG, "play: JS");
                handler.postDelayed(() -> play(webView, script), 1000);
            } else if (s.contains("video-start-")) {
                episode.upd = s.replace("video-start-", "").replace("\"", "");
                episode.urlHome = TvSource.urlHome();
                dao.loadHistoryById(episode.url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMapCompletable(ep -> {
                            currentTime = ep.currentTime;
                            return dao.updateUpd(episode.url, episode.upd);
                        })
                        .subscribe(() -> {
                                    if (currentTime == null) {
                                        dao.insertHistory(episode)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(() -> Log.d(TAG, "vodDao insert History " + episode), err -> err.printStackTrace());
                                    } else {
                                        Log.d(TAG, "vodDao update History " + episode.url + episode.upd);
                                    }
                                },
                                err -> err.printStackTrace());
                play(webView, TvSource.jsGetCurrentTime());
            } else if (s.contains("current-time-")) {
                //update to db
                String time = s.replace("current-time-", "").replace("\"", "");
                try {
                    if (Float.parseFloat(time) > 0) {
                        dao.updateCurrentTime(episode.url, time)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> Log.d(TAG, "vodDao updateCurrentTime " + episode.url + " - " + time),
                                        err -> err.printStackTrace());
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Log.e(TAG, "jsGetCurrentTime NumberFormatException " + time);
                }
                handler.postDelayed(() -> play(webView, script), 30000);
            }
        });
    }

    private long openedTime = 0;
    private void setCurrentTime(WebView webView) {
        if(System.currentTimeMillis() - openedTime < 500) {
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
        } catch (NumberFormatException e) { }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText text = new EditText(this);
        text.setInputType(InputType.TYPE_CLASS_NUMBER);
        text.setText(number);
        builder.setTitle("设置进度条（单位秒）").setView(text);
        builder.setPositiveButton("设置", (di, i) -> {
            final String tm = text.getText().toString();
            Log.d(TAG, "onClick: " + tm);
            if (tm.length() > 0) {
                play(webView, TvSource.jsSetCurrentTime().replace("Number.NaN", tm));
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