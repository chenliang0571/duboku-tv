package com.arieleo.webtview;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class WebAppInterface {
    private static final String TAG = "WebAppInterface";
    Context mContext;

    public WebAppInterface(Context c) {
        mContext = c;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void putMeta(String data) {
        String msg = "empty";
        try {
            JSONArray obj = new JSONArray(data);
            msg = "size:" + obj.length();
            showToast(msg);
            Log.d(TAG, data);

            //intent
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}