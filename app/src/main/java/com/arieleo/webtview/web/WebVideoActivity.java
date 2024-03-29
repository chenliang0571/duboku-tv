package com.arieleo.webtview.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.room.EmptyResultSetException;

import com.arieleo.webtview.TvSource;
import com.arieleo.webtview.room.Episode;
import com.arieleo.webtview.room.Recent;

import java.io.IOException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WebVideoActivity extends WebBaseActivity {
    private static final String TAG = "WebVideoActivity-DDD";
    private Episode episode;
    private int currentTime = 0;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
                    play(webView, TvSource.jsVideoCmdTemplate(TvSource.JScmd.pause, "null"));
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    play(webView, TvSource.jsVideoCmdTemplate(TvSource.JScmd.play, "null"));
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    play(webView, TvSource.jsVideoCmdTemplate(TvSource.JScmd.backward, "null"));
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    play(webView, TvSource.jsVideoCmdTemplate(TvSource.JScmd.forward, "null"));
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView.setWebChromeClient(new WebVideoActivity.WebChromeClientCustom());
        episode = (Episode) this.getIntent().getSerializableExtra("episode");
        webView.loadUrl(episode.url);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Log.i(TAG, "onCreate: episode = " + episode.title);
        showToast(episode.title);

        jsStartDisposable = TvSource.getScriptResultSubject()
                .subscribeOn(Schedulers.io())
                .filter(pair -> pair.first.equals(TvSource.JScript.jsStart.name()))
                .flatMapSingle(pair -> {
                    WebAppInterface.JsDataResult data = (WebAppInterface.JsDataResult)pair.second;
                    Log.i(TAG, "onCreate: "
                            + TvSource.JScript.jsStart.name()
                            + " " + data.toString());
                    episode.upd = df.format(new Date());
                    return dao.loadHistoryById(episode.url);
                })
                .onErrorResumeNext(error -> {
                    if (error instanceof EmptyResultSetException) {
                        return Observable.just(new Episode());
                    } else {
                        return Observable.error(error);
                    }
                })
                .flatMapSingle(ep -> {
                    Log.d(TAG, "onCreate: loadHistoryById " + " - " + episode);
                    int time = ep.currentTime == null ? 0 : ep.currentTime;
                    episode.currentTime = time;
                    currentTime = time;
                    return dao.insertHistory(episode);
                })
                .flatMapSingle(num -> {
                    Log.i(TAG, "onCreate: insertHistory " + num);
                    Recent recent = new Recent();
                    recent.url = episode.dramaUrl;
                    recent.upd = episode.upd;
                    return dao.insertRecent(recent);
                })
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(num -> {
                            Log.i(TAG, "onCreate: insertRecent " + num);
                            if(currentTime > 0) {
                                play(webView, TvSource.jsVideoCmdTemplate(TvSource.JScmd.set_current_time, currentTime + ""));
                            }
                        },
                        error -> Log.e(TAG, "onCreate: loadHistoryById ", error));

        updateCurrentTimeDisposable = TvSource.getScriptResultSubject()
                .subscribeOn(Schedulers.io())
                .filter(pair -> pair.first.equals(TvSource.JScmd.get_current_time.name()))
                .flatMap(pair -> {
                    WebAppInterface.JsDataResult data = (WebAppInterface.JsDataResult) pair.second;
                    Log.d(TAG, "onCreate: get_current_time " + data.data);
                    return Observable.just(Math.round(Float.parseFloat(data.data)));
                })
                .filter(time -> time > 0)
                .flatMapSingle(time -> {
                    episode.currentTime = time;
                    return dao.insertHistory(episode);
                })
                .subscribe(num -> Log.i(TAG, "onCreate: insertHistory " + episode.url + " " + num),
                        error -> Log.e(TAG, "onCreate: get_current_time/update_current_time", error));

        getCurrentTimeDisposable = TvSource.getVideoSubject()
                .subscribeOn(Schedulers.computation())
                .filter(event -> event.equals("timeupdate"))
                .flatMap(Observable::just)
//                .debounce(1000, TimeUnit.MILLISECONDS)
                .throttleLast(20, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    Log.i(TAG, "onCreate: timeupdate -> get_current_time");
                    play(webView, TvSource.jsVideoCmdTemplate(TvSource.JScmd.get_current_time, "null"));
                }, error -> Log.e(TAG, "onCreate: timeupdate", error) );

        videoEndedDisposable = TvSource.getVideoSubject()
                .subscribeOn(Schedulers.computation())
                .filter(event -> event.equals("ended"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    Log.i(TAG, "onCreate: ended");
                    Intent intent = new Intent();
                    intent.putExtra(TvSource.INTENT_ENDED, "OK");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }, error -> Log.e(TAG, "onCreate: ended", error) );
    }

    private long openedTime = 0;
    private void setCurrentTime(WebView webView) {
        if (System.currentTimeMillis() - openedTime < 500) {
            Log.d(TAG, "ignore setCurrentTime.");
            return;
        } else {
            openedTime = System.currentTimeMillis();
        }
        int number = 0;
        if (currentTime > 0) number = currentTime;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText text = new EditText(this);
        text.setInputType(InputType.TYPE_CLASS_NUMBER);
        text.setText(number + "");
        builder.setTitle("设置进度条（单位秒）").setView(text);
        builder.setPositiveButton("设置", (di, i) -> {
            final String tm = text.getText().toString();
            Log.d(TAG, "onClick: " + tm);
            if (tm.length() > 0) {
                play(webView, TvSource.jsVideoCmdTemplate(TvSource.JScmd.set_current_time, tm));
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
}